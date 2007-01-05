/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.engine.compiler;

import static com.hp.hpl.jena.sdb.core.JoinType.INNER;
import static com.hp.hpl.jena.sdb.core.JoinType.LEFT;

import java.sql.SQLException;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.query.core.Var;
import com.hp.hpl.jena.query.engine.Binding;
import com.hp.hpl.jena.query.engine.QueryIterator;
import com.hp.hpl.jena.query.engine1.ExecutionContext;

import com.hp.hpl.jena.sdb.core.JoinType;
import com.hp.hpl.jena.sdb.core.SDBRequest;
import com.hp.hpl.jena.sdb.core.sqlexpr.S_Equal;
import com.hp.hpl.jena.sdb.core.sqlexpr.SqlColumn;
import com.hp.hpl.jena.sdb.core.sqlexpr.SqlExpr;
import com.hp.hpl.jena.sdb.core.sqlexpr.SqlExprList;
import com.hp.hpl.jena.sdb.core.sqlnode.*;
import com.hp.hpl.jena.sdb.sql.RS;
import com.hp.hpl.jena.sdb.sql.SDBExceptionSQL;

public class QC
{
    private static Log log = LogFactory.getLog(QC.class) ;
    
    public static SqlNode innerJoin(SDBRequest request, SqlNode left, SqlNode right)
    {
        if ( left == null )
            return right ; 
        return join(request, INNER, left, right, null) ; 
    }

    public static SqlNode leftJoin(SDBRequest request, SqlNode left, SqlNode right)
    {
        if ( left == null )
            return right ; 
        return join(request, LEFT, left, right, null) ; 
    }

    public static SqlNode leftJoinCoalesce(SDBRequest request, String alias, SqlNode left, SqlNode right, Set<Var> coalesceVars)
    {
        SqlJoin sqlJoin = join(request, LEFT, left, right, coalesceVars) ;
        return SqlCoalesce.create(alias, sqlJoin, coalesceVars) ;
    }
    
//    private static String sqlNodeName(SqlNode sNode)
//    {
//        if ( sNode == null )            return "<null>" ;
//        if ( sNode.isProject() )        return "Project" ;
//        if ( sNode.isRestrict() )       return "Restrict/"+sqlNodeName(sNode.asRestrict().getSubNode()) ;
//        if ( sNode.isTable() )          return "Table" ;
//        if ( sNode.isInnerJoin() )      return "JoinInner" ;
//        if ( sNode.isLeftJoin() )       return "Joinleft" ;
//        if ( sNode.isCoalesce() )       return "Coalesce" ;
//        return "<unknown>" ;
//    }
    
    // Join/LeftJoin two subexpressions, calculating the join conditions in the process
    // If a coalesce (LeftJoin) then don't equate left and right vars of the same name.
    // An SqlCoalesce is a special case of LeftJoin where ignoreVars!=null
    private static SqlJoin join(SDBRequest request, 
                                JoinType joinType, 
                                SqlNode left, SqlNode right,
                                Set<Var> ignoreVars)
    {
        SqlExprList conditions = new SqlExprList() ;
        
        if ( joinType == JoinType.INNER )
            // If it's a LeftJoin, leave the left filter on the LHS.
            left = removeRestrict(left, conditions) ;
        
        if ( joinType == JoinType.LEFT )
            // Make RHS scope optionals 
            ;
        
        right = removeRestrict(right, conditions) ;
        
        for ( Var v : left.getIdScope().getVars() )
        {
            if ( right.getIdScope().hasColumnForVar(v) )
            {
                if ( ignoreVars == null || ! ignoreVars.contains(v) )
                {
                    SqlColumn leftCol = left.getIdScope().getColumnForVar(v).getColumn() ;
                    SqlColumn rightCol = right.getIdScope().getColumnForVar(v).getColumn() ;
                    
                    SqlExpr c = new S_Equal(leftCol, rightCol) ;
                    conditions.add(c) ;
                    c.addNote("Join var: "+v) ; 
                }
            }
        }
        
        SqlJoin join = SqlJoin.create(joinType, left, right, null) ;
        join.addConditions(conditions) ;
        return join ;
    }
    
    private static SqlNode removeRestrict(SqlNode sqlNode, SqlExprList conditions)
    {
        if ( ! sqlNode.isRestrict() ) 
            return sqlNode ;
        
        SqlRestrict restrict = sqlNode.asRestrict() ;
        SqlNode subNode = restrict.getSubNode() ;
        if ( ! subNode.isTable() && ! subNode.isInnerJoin() )
            return sqlNode ;
        conditions.addAll(restrict.getConditions()) ;
        subNode.addNotes(restrict.getNotes()) ;
        return subNode ;
    }
    
    public static boolean PrintSQL = false ;
    
    public static QueryIterator exec(OpSQL opSQL, SDBRequest request, Binding binding, ExecutionContext execCxt)
    {
        String sqlStmt = toSqlString(opSQL, request) ;
        
        if ( PrintSQL )
            System.out.println(sqlStmt) ;
        
        try {
            java.sql.ResultSet jdbcResultSet = request.getStore().getConnection().execQuery(sqlStmt) ;
            if ( false )
                // Destructive
                RS.printResultSet(jdbcResultSet) ;
            try {
                return opSQL.getBridge().assembleResults(jdbcResultSet, binding, execCxt) ;
            } finally { jdbcResultSet.close() ; }
        } catch (SQLException ex)
        {
            throw new SDBExceptionSQL("SQLException in executing SQL statement", ex) ;
        }
    }

    public static String toSqlString(OpSQL opSQL, 
                                     SDBRequest request)
    {
        SqlNode sqlNode = opSQL.getSqlNode() ;
        String sqlStmt = request.getStore().getSQLGenerator().generateSQL(sqlNode) ;
        return sqlStmt ; 
    }
    
    /** Find the variables needed out of this query.
     * If we don't do sorting in-DB, then we need the ORDER BY variables as well. 
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Var> queryOutVars(Query query)
    {
        //Class : VarList LinkedHashSet<Var>
        List<Var> vars = new ArrayList<Var>() ;
        
        // Project variables
        List<String> list = (List<String>)query.getResultVars() ;
        if ( list.size() == 0 )
            LogFactory.getLog(QC.class).warn("No project variables") ;
        for ( String vn  : list )
            vars.add(Var.alloc(vn)) ;
        
        // Add the ORDER BY variables
        List<SortCondition> orderConditions = (List<SortCondition>)query.getOrderBy() ;
        if ( orderConditions != null )
        {
            for ( SortCondition sc : orderConditions )
            {
                List<Var> x = (List<Var>)sc.getExpression().getVarsMentioned() ;
                for ( Var v :  x )
                {
                    if ( ! vars.contains(v) )
                        vars.add(v) ;
                }
            }
        }
        return vars ;
    }
}

/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
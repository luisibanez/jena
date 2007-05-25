/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package arq.cmd;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.algebra.AlgebraGenerator;
import com.hp.hpl.jena.sparql.algebra.AlgebraGeneratorQuad;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.DataSourceImpl;
import com.hp.hpl.jena.sparql.engine.Plan;
import com.hp.hpl.jena.sparql.engine.QueryEngineBase;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.util.IndentedWriter;
import com.hp.hpl.jena.sparql.util.Utils;

/** Utilities for queries and plans
 * 
 * @author Andy Seaborne
 * @version $Id: QueryUtils.java,v 1.20 2007/02/08 18:05:01 andy_seaborne Exp $
 */

public class QueryUtils
{
    public static void printPlan(Query query, QueryExecution qe)
    {
        System.err.println("QueryUtils.printPlan: Need s converting") ;
//        if ( qe instanceof QueryEngineBase )
//        {
//            QueryEngineBase qeb = (QueryEngineBase)qe ;
//            // Ensure there is some kind of dataset
//
//            SerializationContext sCxt = new SerializationContext(query) ;
//            IndentedWriter out = new IndentedWriter(System.out) ;
//            
//            if ( !qeb.hasDatasetOrDescription() )
//                qeb.setDataset(new DataSourceImpl()) ;
//            Plan plan = qeb.getPlan() ;
//            plan.output(out, sCxt) ;
//            out.flush();
//            return ;
//        }
//        System.err.println("printPlan: Unknown engine type: "+Utils.className(qe)) ;
    }

    
    
    public static void printQuery(Query query)
    {
        IndentedWriter out = new IndentedWriter(System.out) ;
        printQuery(out, query) ;
    }
    
    public static void printQuery(IndentedWriter out, Query query)
    {
        printQuery(out, query, Syntax.defaultSyntax) ;
    }
    
    public static void printQuery(IndentedWriter out, Query query, Syntax syntax)
    {
        query.serialize(out, syntax) ;
        out.flush() ;
    }

    public static void printOp(Query query)
    {
        IndentedWriter out = new IndentedWriter(System.out) ;
        printOp(out, query) ;
    }
    
    public static void printOp(IndentedWriter out, Query query)
    {
        Op op = AlgebraGenerator.compileQuery(query) ;
        SerializationContext sCxt = new SerializationContext(query) ;
        op.output(out, sCxt) ;
        out.flush();
    }    

    public static void printQuad(Query query)
    {
        IndentedWriter out = new IndentedWriter(System.out) ;
        printQuad(out, query) ;
    }
    
    public static void printQuad(IndentedWriter out, Query query)
    {
        Op op = AlgebraGeneratorQuad.compileQuery(query) ;
        SerializationContext sCxt = new SerializationContext(query) ;
        op.output(out, sCxt) ;
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
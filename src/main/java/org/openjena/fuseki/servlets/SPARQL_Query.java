/*
 * (c) Copyright 2010 Epimorphics Ltd.
 * All rights reserved.
 * [See end of file]
 */

package org.openjena.fuseki.servlets;

import static java.lang.String.format ;
import static org.openjena.fuseki.Fuseki.serverlog ;
import static org.openjena.fuseki.HttpNames.paramAccept ;
import static org.openjena.fuseki.HttpNames.paramCallback ;
import static org.openjena.fuseki.HttpNames.paramDefaultGraphURI ;
import static org.openjena.fuseki.HttpNames.paramForceAccept ;
import static org.openjena.fuseki.HttpNames.paramNamedGraphURI ;
import static org.openjena.fuseki.HttpNames.paramOutput1 ;
import static org.openjena.fuseki.HttpNames.paramOutput2 ;
import static org.openjena.fuseki.HttpNames.paramQuery ;
import static org.openjena.fuseki.HttpNames.paramQueryRef ;
import static org.openjena.fuseki.HttpNames.paramStyleSheet ;

import java.util.Arrays ;
import java.util.Enumeration ;
import java.util.HashSet ;
import java.util.Set ;

import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.openjena.atlas.io.IndentedLineBuffer ;
import org.openjena.fuseki.DEF ;
import org.openjena.fuseki.HttpNames ;
import org.openjena.fuseki.migrate.WebIO ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.DatasetFactory ;
import com.hp.hpl.jena.query.Query ;
import com.hp.hpl.jena.query.QueryException ;
import com.hp.hpl.jena.query.QueryExecution ;
import com.hp.hpl.jena.query.QueryExecutionFactory ;
import com.hp.hpl.jena.query.QueryFactory ;
import com.hp.hpl.jena.query.ResultSet ;
import com.hp.hpl.jena.query.ResultSetFactory ;
import com.hp.hpl.jena.query.Syntax ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.shared.Lock ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.resultset.SPARQLResult ;

public class SPARQL_Query extends SPARQL_ServletBase
{
    private static Logger log = LoggerFactory.getLogger(SPARQL_Query.class) ;
    
    private class HttpActionQuery extends HttpAction {
        public HttpActionQuery(long id, DatasetGraph dsg, HttpServletRequest request, HttpServletResponse response, boolean verbose)
        {
            super(id, dsg, request, response, verbose) ;
        }
    }
    
    public SPARQL_Query(boolean verbose)
    { super(PlainRequestFlag.DIFFERENT, verbose) ; }

    public SPARQL_Query()
    { this(false) ; }

    // Choose REST verbs to support.
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    { doCommon(request, response) ; }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    { doCommon(request, response) ; }
    
    @Override
    protected void perform(long id, DatasetGraph dsg, HttpServletRequest request, HttpServletResponse response)
    {
        validate(request) ;
        HttpActionQuery action = new HttpActionQuery(id, dsg, request, response, verbose_debug) ;
        execute(action) ;
    }

    @Override
    protected void requestNoQueryString(HttpServletRequest request, HttpServletResponse response)
    {
        log.warn("Service Description / SPARQL Query") ;
        errorNotFound("Service Description") ;
    }

    // (1) Param to constructor.
    // (2) DRY : to super class.
    static String[] tails = { HttpNames.ServiceQuery, HttpNames.ServiceQueryAlt } ;
    
    @Override
    protected String mapRequestToDataset(String uri)
    {
        for ( String tail : tails )
        {
            String x = mapRequestToDataset(uri, tail) ;
            if ( x != null )
                return x ;
        }
        return uri ; 
    }
    
    protected String mapRequestToDataset(String uri, String tail)
    {
        if ( uri.endsWith(tail) )
            return uri.substring(0, uri.length()-tail.length()) ;
        return null ;
    }

    
    /*
     *                 @SuppressWarnings("unchecked")
                Enumeration<String> en = request.getParameterNames() ;
                if ( en.hasMoreElements() )
                    errorBadRequest("No request parameters allowed") ;
                return ;

     */
    
    // All the params we support
    private static String[] params_ = {  paramQuery, paramDefaultGraphURI, paramNamedGraphURI, 
                                        paramQueryRef,
                                        paramStyleSheet,
                                        paramAccept,
                                        paramOutput1, paramOutput2, 
                                        paramCallback, 
                                        paramForceAccept } ;
    private static Set<String> params = new HashSet<String>(Arrays.asList(params_)) ;
    
    private void validate(HttpServletRequest request)
    {
        String incoming = request.getContentType() ;
        if ( incoming != null )
        {
        
            if ( DEF.contentTypeSPARQLQuery.equals(incoming) )
                error(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unofficial DEF.contentTypeSPARQLQuery not supported") ;
                
            if ( ! DEF.contentTypeForm.equals(incoming) )
                error(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported: "+incoming) ;
        }
        
        // GET/POST of a form at this point.
        
        
        String queryStr = request.getParameter(HttpNames.paramQuery) ;
        
        if ( queryStr == null )
            errorBadRequest("SPARQL Query: No query specificied (no 'query=' found)") ;

        @SuppressWarnings("unchecked")
        Enumeration<String> en = request.getParameterNames() ;
        for ( ; en.hasMoreElements() ; )
        {
            String name = en.nextElement() ;
            if ( ! params.contains(name) )
                errorBadRequest("SPARQL Query: Unrecognize request parameter: "+name) ;
        }
    }

    private void execute(HttpActionQuery action)
    {
        String queryString = action.request.getParameter(paramQuery) ;
        String queryStringLog = formatForLog(queryString) ;
        serverlog.info(format("[%d] Query = %s", action.id, queryString));
        Query query = null ;
        try {
            // NB syntax is ARQ (a superset of SPARQL)
            query = QueryFactory.create(queryString, Syntax.syntaxARQ) ;
            queryStringLog = formatForLog(query) ;
        } catch (QueryException ex)
        {
            errorBadRequest("Parse error: \n"+queryString +"\n\r" + ex.getMessage()) ;
        }
        
        if ( query.hasDatasetDescription() )
            errorBadRequest("Query has FROM/FROM NAMED") ;
        
        // Assumes no streaming.
        try {
            action.lock.enterCriticalSection(Lock.READ) ;
            SPARQLResult result = executeQuery(action, query, queryStringLog) ;
            sendResults(action, result) ;
        } finally { action.lock.leaveCriticalSection() ; }

    }

    protected QueryExecution createQueryExecution(Query query, Dataset dataset)
    {
        return QueryExecutionFactory.create(query, dataset) ;
    }

    private SPARQLResult executeQuery(HttpActionQuery action, Query query, String queryStringLog)
    {
        Dataset dataset = DatasetFactory.create(action.dsg) ;
        QueryExecution qexec = createQueryExecution(query, dataset) ;

        // call back.
        
        if ( query.isSelectType() )
        {
            // Force some query execute now.
            ResultSet rs = qexec.execSelect() ;

            
            // Do this to force the query to do something that should touch any underlying database,
            // and hence ensure the communications layer is working.  MySQL can time out after  
            // 8 hours of an idle connection
            rs.hasNext() ;

            // TEMP until streaming sorted out.
            rs = ResultSetFactory.copyResults(rs) ;
            
            // Old way - heavyweight
            //rs = ResultSetFactory.copyResults(rs) ;
            serverlog.info(format("[%d] OK/select", action.id)) ;
            return new SPARQLResult(rs) ;
        }

        if ( query.isConstructType() )
        {
            Model model = qexec.execConstruct() ;
            serverlog.info(format("[%d] OK/construct", action.id)) ;
            return new SPARQLResult(model) ;
        }

        if ( query.isDescribeType() )
        {
            Model model = qexec.execDescribe() ;
            serverlog.info("[%d] OK/describe: "+queryStringLog) ;
            return new SPARQLResult(model) ;
        }

        if ( query.isAskType() )
        {
            boolean b = qexec.execAsk() ;
            serverlog.info("[%d] OK/ask: "+queryStringLog) ;
            return new SPARQLResult(b) ;
        }

        errorBadRequest("Unknown query type - "+queryStringLog) ;
        return null ;
    }

    protected void sendResults(HttpActionQuery action, SPARQLResult result)
    {
        if ( result.isResultSet() )
            ResponseQuery.doResponseResultSet(result.getResultSet(), null, action.request, action.response) ;
        else if ( result.isGraph() )
            ResponseQuery.doResponseModel(result.getModel(), action.request, action.response) ;
        else if ( result.isBoolean() )
            // Make different?
            ResponseQuery.doResponseResultSet(null, result.getBooleanResult(), action.request, action.response) ;
        else
            errorOccurred("Unknown or invalid result type") ;
    }
    
    private String formatForLog(Query query)
    {
        IndentedLineBuffer out = new IndentedLineBuffer() ;
        out.setFlatMode(true) ;
        query.serialize(out) ;
        return out.asString() ;
    }
        
    /**
     * @param queryURI
     * @return
     */
    private String getRemoteString(String queryURI)
    {
        return WebIO.exec_get(queryURI) ;
    }

}

/*
 * (c) Copyright 2010 Epimorphics Ltd.
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
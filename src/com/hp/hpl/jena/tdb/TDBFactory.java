/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.tdb;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.pgraph.GraphBPlusTree;
import com.hp.hpl.jena.tdb.pgraph.GraphBTree;
import com.hp.hpl.jena.tdb.pgraph.PGraphBase;
import com.hp.hpl.jena.tdb.pgraph.assembler.PGraphAssemblerVocab;

public class TDBFactory
{
    static { TDB.init(); }
    
    // ----- TEMP
    public static Graph createGraphBPT(Location loc)
    {
        return GraphBPlusTree.create(loc) ;
    }
    public static Graph createGraphBT(Location loc)
    {
        return GraphBTree.create(loc) ;
    }
    // -----
    
    /** Read the file and assembler a model, of type TDB persistent graph */ 
    public static Model assembleModel(String assemblerFile)
    {
        return (Model)AssemblerUtils.build(assemblerFile, PGraphAssemblerVocab.PGraphType) ;
    }
    
    /** Read the file and assembler a model, of type TDB persistent graph */ 
    public static Graph assembleGraph(String assemblerFile)
    {
        Model m = assembleModel(assemblerFile) ;
        Graph g = (PGraphBase)m.getGraph() ;
        return g ;
    }

    /** Create a model, at the given location */
    public static Model createModel(String dir)
    {
        return ModelFactory.createModelForGraph(createGraph(dir)) ;
    }

    /** Create a graph, at the given location */
    public static Graph createGraph(String dir)
    {
        Location loc = new Location(dir) ;
        return createGraph(loc) ;
    }
    
    /** Create a model, at the given location */
    public static Model createModel(Location loc)
    {
        return ModelFactory.createModelForGraph(createGraph(loc)) ;
    }

    /** Create a graph, at the given location */
    public static Graph createGraph(Location loc)       { return _createGraph(loc) ; }
    
    /** Create a TDB model backed by an in-memory block manager. For testing. */  
    public static Model createModel()
    {
        return ModelFactory.createModelForGraph(createGraph()) ;
    }
    
    /** Create a TDB graph backed by an in-memory block manager. For testing. */  
    public static Graph createGraph()   { return _createGraph() ; }
    
    // Switch on Graph implementation
    
    //IndexFactory idxFactory = new IndexFactoryBPlusTree(loc, BlockSize) ;
    
    
    private static Graph _createGraph()
    {
        String indexType = TDB.getContext().getAsString(TDB.symIndexType, "BPlusTree") ;

        if (indexType.equalsIgnoreCase("BPlusTree")) 
            return GraphBPlusTree.create() ;
        if (indexType.equalsIgnoreCase("BTree")) 
            return GraphBTree.create() ;
        throw new TDBException("Unrecognized index type: " + indexType) ;
    }

    private static Graph _createGraph(Location loc)
    {
        String indexType = TDB.getContext().getAsString(TDB.symIndexType, "BPlusTree") ;
        if (indexType.equalsIgnoreCase("BPlusTree")) 
            return GraphBPlusTree.create(loc) ;
        if (indexType.equalsIgnoreCase("BTree")) 
            return GraphBTree.create(loc) ;
        throw new TDBException("Unrecognized index type: " + indexType) ;
    }
// private static Graph _createGraph() { return GraphBTree.create() ; }
// private static Graph _createGraph(Location loc) { return GraphBTree.create(loc) ; }
    
}

/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
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
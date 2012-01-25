/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.hpl.jena.tdb;

import org.openjena.atlas.logging.Log ;

import com.hp.hpl.jena.graph.Graph ;
import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.DatasetFactory ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.ModelFactory ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.core.Transactional ;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils ;
import com.hp.hpl.jena.sparql.engine.optimizer.reorder.ReorderLib ;
import com.hp.hpl.jena.sparql.engine.optimizer.reorder.ReorderTransformation ;
import com.hp.hpl.jena.tdb.assembler.VocabTDB ;
import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB ;
import com.hp.hpl.jena.tdb.sys.SystemTDB ;
import com.hp.hpl.jena.tdb.sys.TDBMaker ;
import com.hp.hpl.jena.tdb.transaction.DatasetGraphTransaction ;

/** Public factory for creating objects datasets backed by TDB storage */
public class TDBFactory
{
    private TDBFactory() {} 
    
    /** Read the file and assembler a dataset */ 
    public static Dataset assembleDataset(String assemblerFile)
    {
        return (Dataset)AssemblerUtils.build(assemblerFile, VocabTDB.tDatasetTDB) ;
    }
    
    /** Create or connect to a TDB-backed dataset */ 
    public static Dataset createDataset(String dir)
    { return createDataset(new Location(dir)) ; }

    /** Create or connect to a TDB-backed dataset */ 
    public static Dataset createDataset(Location location)
    { return createDataset(createDatasetGraph(location)) ; }

    /** Create or connect to a TDB dataset backed by an in-memory block manager. For testing.*/ 
    public static Dataset createDataset()
    { return createDataset(createDatasetGraph()) ; }

    /** Create a dataset around a DatasetGraphTDB */ 
    private static Dataset createDataset(DatasetGraph datasetGraph)
    { return DatasetFactory.create(datasetGraph) ; }
    
    // Meaningless unless there is only one in-memory dataset */
    //    /** Create a TDB model for named model for an in-memory */  
    //    public static Graph createNamedGraph(String name)
    //    { return createDataset().getNamedModel(name) ; }

    /** Create or connect to a TDB-backed dataset (graph-level) */
    public static DatasetGraph createDatasetGraph(String directory)
    { return createDatasetGraph(new Location(directory)) ; }

    /** Create or connect to a TDB-backed dataset (graph-level) */
    public static DatasetGraph createDatasetGraph(Location location)
    { return _createDatasetGraph(location) ; }

    /** Create a TDB-backed dataset (graph-level) in memory (for testing) */
    public static DatasetGraph createDatasetGraph()
    {
        return _createDatasetGraph() ;
    }
    
    /** Create a TDB-backed DatasetGraph directly over the storage in memory (not transactional) (testing only) */  
    public static DatasetGraphTDB createDatasetGraphBase()
    {
        return TDBMaker._createDatasetGraph() ;
    }

    /** Create a TDB-backed Dataset directly over the storage in memory (not transactional) (testing only) */  
    public static Dataset createDatasetBase()
    {
        return DatasetFactory.create(TDBMaker._createDatasetGraph()) ;
    }

    private static DatasetGraph _createDatasetGraph(Location location)
    { 
        DatasetGraphTDB dsg = TDBMaker._createDatasetGraph(location) ;
        if ( dsg instanceof Transactional)
        {
            Log.info(TDBFactory.class, "TDBMaker returns a transactional DatasetGraphs") ; 
            // v0.9.0 - doesn't happen but this prepares for a possible future.
            return dsg ;
        }
        // Not transactional - add the switching wrapper.
        return asTransactional(dsg) ; }
    
    private static DatasetGraph _createDatasetGraph()
    {
        // Make silent by setting the optimizer to the no-opt
        ReorderTransformation rt = SystemTDB.defaultOptimizer ;
        if ( rt == null )
            SystemTDB.defaultOptimizer = ReorderLib.identity() ;
        DatasetGraphTDB dsg = TDBMaker._createDatasetGraph() ;
        SystemTDB.defaultOptimizer  = rt ;
        return asTransactional(dsg) ;
    }
    
    /** By default, TDBFactory returns Datasets and DatasetGraphs that can be used in
     *  transactions.  To force a return to TDB 0.8.x behaviour of returning 
     *  Datasets and DatasetGraphs attached directly to the storage, set this
     *  to false.  Warning: it's global. 
     */
    
    public static boolean MAKE_TRANSACTIONAL_DATASETS = true ; 
    
    private static DatasetGraph asTransactional(DatasetGraphTDB dsg)
    {
        if ( MAKE_TRANSACTIONAL_DATASETS )
            return new DatasetGraphTransaction(dsg) ;
        else
            return dsg ;
    }

    /** Return the location of a dataset if it is backed by TDB, else null */ 
    public static Location location(Dataset dataset)
    {
        return TDBFactoryTxn.location(dataset) ;
    }

    /** Return the location of a dataset if it is backed by TDB, else null */ 
    public static Location location(DatasetGraph dataset)
    {
        return TDBFactoryTxn.location(dataset) ;
    }

    /** Read the file and assembler a graph, of type TDB persistent graph
     *  @deprecated Assemble a Dataset and use the default graph.
     */
    @Deprecated 
    public static Graph assembleGraph(String assemblerFile)
    {
        Model m = assembleModel(assemblerFile) ;
        Graph g = m.getGraph() ;
        return g ;
    }

    /** Read the file and assembler a model, of type TDB persistent graph
     *  @deprecated Assemble a Dataset and use the default model.
     */
    @Deprecated 
    public static Model assembleModel(String assemblerFile)
    {
        return (Model)AssemblerUtils.build(assemblerFile, VocabTDB.tGraphTDB) ;
    }

    /** Create a model, at the given location.
     *  It is better to create a dataset and get the default model from that.
     *  This Model is not connected to the TDB transaction system.
     *  @deprecated Create a Dataset and use the default model.
     */
    @Deprecated
    public static Model createModel(Location loc)
    {
        return ModelFactory.createModelForGraph(createGraph(loc)) ;
    }

    /** Create a model, at the given location 
     *  It is better to create a dataset and get the default model from that.
     *  This Model is not connected to the TDB transaction system. 
     *  @deprecated Create a Dataset and get the default model.
     */
    @Deprecated
    public static Model createModel(String dir)
    {
        return ModelFactory.createModelForGraph(createGraph(dir)) ;
    }

    /** Create a TDB model backed by an in-memory block manager. For testing. */
    @Deprecated
    
    public static Model createModel()
    { return ModelFactory.createModelForGraph(createGraph()) ; }

    /** Create a TDB model for named model
     * It is better to create a dataset and get the named model from that.
     * This Model is not connected to the TDB transaction system.
     *  @deprecated Create a Dataset and get the name model.
     */
    @Deprecated
    public static Model createNamedModel(String name, String location)
    { return createDataset(location).getNamedModel(name) ; }

    /** Create a TDB model for named model.
     * It is better to create a dataset and get the named model from that.
     * This Model is not connected to the TDB transaction system.
     *  @deprecated Create a Dataset and get the name model.
     */  
    @Deprecated
    public static Model createNamedModel(String name, Location location)
    { return createDataset(location).getNamedModel(name) ; }

    /** Create a graph, at the given location 
     * @deprecated Create a DatasetGraph and use the default graph.
     */
    @Deprecated
    public static Graph createGraph(Location loc)       { return createDatasetGraph(loc).getDefaultGraph() ; }

    /** Create a graph, at the given location 
     * @deprecated Create a DatasetGraph and use the default graph.
     */
    @Deprecated
    public static Graph createGraph(String dir)
    {
        Location loc = new Location(dir) ;
        return createGraph(loc) ;
    }
    
    /** Create a TDB graph backed by an in-memory block manager. For testing. */  
    @Deprecated
    public static Graph createGraph()   { return createDatasetGraph().getDefaultGraph() ; }

    /** Create a TDB graph for named graph
     * @deprecated Create a DatasetGraph and get the name graph from that.
     */  
    @Deprecated
    public static Graph createNamedGraph(String name, String location)
    { return createDatasetGraph(location).getGraph(Node.createURI(name)) ; }
    
    /** Create a TDB graph for named graph
     * @deprecated Create a DatasetGraph and get the name graph from that.
     */  
    @Deprecated
    public static Graph createNamedGraph(String name, Location location)
    { return createDatasetGraph(location).getGraph(Node.createURI(name)) ; }
}

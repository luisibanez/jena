/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import java.io.FileInputStream ;
import java.io.IOException ;
import java.io.InputStream ;
import java.util.Arrays ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import junit.framework.TestCase ;
import org.junit.runner.JUnitCore ;
import org.junit.runner.Result ;
import atlas.iterator.Filter ;
import atlas.junit.TextListener2 ;
import atlas.lib.Tuple ;
import atlas.logging.Log ;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.query.* ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.riot.JenaReaderTurtle2 ;
import com.hp.hpl.jena.sparql.algebra.Algebra ;
import com.hp.hpl.jena.sparql.algebra.Op ;
import com.hp.hpl.jena.sparql.algebra.Transformer ;
import com.hp.hpl.jena.tdb.TC_TDB ;
import com.hp.hpl.jena.tdb.TDB ;
import com.hp.hpl.jena.tdb.TDBFactory ;
import com.hp.hpl.jena.tdb.base.block.BlockMgr ;
import com.hp.hpl.jena.tdb.base.block.BlockMgrFactory ;
import com.hp.hpl.jena.tdb.base.file.FileSet ;
import com.hp.hpl.jena.tdb.base.file.MetaFile ;
import com.hp.hpl.jena.tdb.base.record.RecordFactory ;
import com.hp.hpl.jena.tdb.index.RangeIndex ;
import com.hp.hpl.jena.tdb.index.bplustree.BPlusTree ;
import com.hp.hpl.jena.tdb.index.bplustree.BPlusTreeParams ;
import com.hp.hpl.jena.tdb.junit.QueryTestTDB ;
import com.hp.hpl.jena.tdb.nodetable.NodeTable ;
import com.hp.hpl.jena.tdb.solver.reorder.ReorderLib ;
import com.hp.hpl.jena.tdb.solver.reorder.ReorderTransformation ;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.sys.Names ;
import com.hp.hpl.jena.tdb.sys.SetupTDB ;
import com.hp.hpl.jena.tdb.sys.SystemTDB ;
import com.hp.hpl.jena.tdb.sys.TDBMaker ;

public class RunTDB
{
    static { Log.setLog4j() ; }
    static String divider = "----------" ;
    static String nextDivider = null ;
    static void divider()
    {
        if ( nextDivider != null )
            System.out.println(nextDivider) ;
        nextDivider = divider ;
    }

    public static void main(String ... args) throws IOException
    {
        tdb.turtle.main("D.ttl") ; System.exit(0) ;

        tdbquery("--tdb=tdb.ttl", "--set=tdb:logExec=info", "SELECT * {GRAPH ?g { ?s ?p ?o}}") ;
        System.exit(0) ;


        Dataset ds = TDBFactory.createDataset("DB") ;
        DatasetGraphTDB dsg = (DatasetGraphTDB)(ds.asDatasetGraph()) ;

        final NodeTable nodeTable = dsg.getQuadTable().getNodeTupleTable().getNodeTable() ;
        final NodeId target = nodeTable.getNodeIdForNode(Node.createURI("http://example/graph2")) ;

        System.out.println("Filter: "+target) ;
        
        Filter<Tuple<NodeId>> filter = new Filter<Tuple<NodeId>>() {
            
            public boolean accept(Tuple<NodeId> item)
            {
                
                // Reverse the lookup as a demo
                Node n = nodeTable.getNodeForNodeId(target) ;
                
                //System.err.println(item) ;
                if ( item.size() == 4 && item.get(0).equals(target) )
                {
                    System.out.println("Reject: "+item) ;
                    return false ;
                }
                System.out.println("Accept: "+item) ;
                return true ;
            } } ;
            
            
        TDB.getContext().set(SystemTDB.symTupleFilter, filter) ;

        String qs = "SELECT * { { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }}" ;
        //String qs = "SELECT * { GRAPH ?g { ?s ?p ?o } }" ;
        
        tdbquery("--tdb=tdb.ttl", qs) ;
    }

    // Switching on index type to build.
    // Replaces IndexFactory.
    
    interface RangeIndexMaker
    {
        // No record factory - no details of range expected.
        public RangeIndex createRangeIndex(FileSet fileset) ;
    }
    
    static class Registries
    {
        static Map<String, RangeIndexMaker> regIndexMakers = new HashMap<String, RangeIndexMaker>() ;
        static 
        {
            // Needs remo
            regIndexMakers.put("bplustree", new RangeIndexM_BPT()) ;
        }
    }
    
    
    class RangeIndexM implements RangeIndexMaker
    {
        public RangeIndex createRangeIndex(FileSet fileset)
        {
            MetaFile metafile = fileset.getMetaFile() ;
            String filetype = metafile.getProperty("tdb.file.type", "rangeindex") ;
            if ( filetype.equals("rangeindex") )
                SetupTDB.error(null, "Expected a range index for '"+"tdb.file.type"+"' got:"+filetype) ;
            
            String impl = metafile.getProperty("tdb.file.impl", "bplustree") ;
            RangeIndexMaker rim = Registries.regIndexMakers.get(impl) ;
            if ( rim == null )
                SetupTDB.error(null, "No such implementation: "+impl) ;
            return rim.createRangeIndex(fileset) ;
        }
        
    }
    
    static class RangeIndexM_BPT implements RangeIndexMaker
    {
        private int readCacheSize ;
        private int writeCacheSize ;
        
        public RangeIndexM_BPT()
        {
            this(SystemTDB.BlockReadCacheSize, SystemTDB.BlockWriteCacheSize) ;
        }
        
        public RangeIndexM_BPT(int readCacheSize, int writeCacheSize)
        {
            this.readCacheSize = readCacheSize ;
            this.writeCacheSize = writeCacheSize ;
        }
        
        public RangeIndex createRangeIndex(FileSet fileset)
        {
            MetaFile metafile = fileset.getMetaFile() ;
            RecordFactory recordFactory = SetupTDB.makeRecordFactory(metafile, "tdb.bplustree.record", -1, -1) ;
            
            String blkSizeStr = metafile.getOrSetDefault("tdb.bplustree.blksize", Integer.toString(SystemTDB.BlockSize)) ; 
            int blkSize = SetupTDB.parseInt(blkSizeStr, "Bad block size") ;
            
            // IndexBuilder.getBPlusTree().newRangeIndex(fs, recordFactory) ;
            // Does not set order.
            
            int calcOrder = BPlusTreeParams.calcOrder(blkSize, recordFactory.recordLength()) ;
            String orderStr = metafile.getOrSetDefault("tdb.bplustree.order", Integer.toString(calcOrder)) ;
            int order = SetupTDB.parseInt(orderStr, "Bad order for B+Tree") ;
            if ( order != calcOrder )
                SetupTDB.error(null, "Wrong order (" + order + "), calculated = "+calcOrder) ;

            RangeIndex rIndex = createBPTree(fileset, order, blkSize, readCacheSize, writeCacheSize, recordFactory) ;
            metafile.flush() ;
            return rIndex ;
        }
        
        /** Knowing all the parameters, create a B+Tree */
        public static RangeIndex createBPTree(FileSet fileset, int order, 
                                              int blockSize,
                                              int readBlockCacheSize, int writeBlockCacheSize,
                                              RecordFactory factory)
        {
            // ---- Checking
            if (blockSize < 0 && order < 0) throw new IllegalArgumentException("Neither blocksize nor order specified") ;
            if (blockSize >= 0 && order < 0) order = BPlusTreeParams.calcOrder(blockSize, factory.recordLength()) ;
            if (blockSize >= 0 && order >= 0)
            {
                int order2 = BPlusTreeParams.calcOrder(blockSize, factory.recordLength()) ;
                if (order != order2) throw new IllegalArgumentException("Wrong order (" + order + "), calculated = "
                                                                        + order2) ;
            }
        
            // Iffy - does not allow for slop.
            if (blockSize < 0 && order >= 0)
            {
                // Only in-memory.
                blockSize = BPlusTreeParams.calcBlockSize(order, factory) ;
            }
        
            BPlusTreeParams params = new BPlusTreeParams(order, factory) ;
            BlockMgr blkMgrNodes = BlockMgrFactory.create(fileset, Names.bptExt1, blockSize, 
                                                          readBlockCacheSize, writeBlockCacheSize) ;
            BlockMgr blkMgrRecords = BlockMgrFactory.create(fileset, Names.bptExt2, blockSize, 
                                                            readBlockCacheSize, writeBlockCacheSize) ;
            return BPlusTree.attach(params, blkMgrNodes, blkMgrRecords) ;
        }

        
    }
    
    public static void turtle2() throws IOException
    {
        // Also tdb.turtle.
        //        TDB.init();
        //        RDFWriter w = new JenaWriterNTriples2() ;
        //        Model model = FileManager.get().loadModel("D.ttl") ;
        //        w.write(model, System.out, null) ;
        //        System.exit(0) ;

        InputStream input = new FileInputStream("D.ttl") ;
        JenaReaderTurtle2.parse(input) ;
        System.out.println("END") ;
        System.exit(0) ;

    }
    
     public static void rewrite()
    {
        ReorderTransformation reorder = null ;
        if ( false )
            reorder = ReorderLib.fixed() ;
        else
        {
            reorder = ReorderLib.weighted("stats.sse") ;
        }
        Query query = QueryFactory.read("Q.rq") ;
        Op op = Algebra.compile(query) ;
        System.out.println(op) ;
        
        op = Transformer.transform(new TransformReorderBGP(reorder), op) ;
        System.out.println(op) ;
        System.exit(0) ;
    }
    
    private static void query(String str, Dataset dataset)
    {
        query(str, dataset, null) ;
    }
    
    private static void query(String str, Dataset dataset, QuerySolution qs)
    {
        System.out.println(str) ; 
        Query q = QueryFactory.create(str, Syntax.syntaxARQ) ;
        QueryExecution qexec = QueryExecutionFactory.create(q, dataset, qs) ;
        ResultSet rs = qexec.execSelect() ;
        ResultSetFormatter.out(rs) ;
        qexec.close() ;
    }
    
    private static void query(String str, Model model)
    {
        Query q = QueryFactory.create(str, Syntax.syntaxARQ) ;
        QueryExecution qexec = QueryExecutionFactory.create(q, model) ;
        ResultSet rs = qexec.execSelect() ;
        ResultSetFormatter.out(rs) ;
        qexec.close() ;
    }
    
    private static void test()
    {
        String testNum = "2" ;
        String dir = "testing/UnionGraph/" ;
        List<String> dftGraphs = Arrays.asList(dir+"data-dft.ttl") ;
        List<String> namedGraphs = Arrays.asList(dir+"data-1.ttl", dir+"data-2.ttl") ;
        String queryFile = dir+"merge-"+testNum+".rq" ;
        ResultSet rs = ResultSetFactory.load(dir+"merge-"+testNum+"-results.srx") ;
        
        TestCase t = new QueryTestTDB("Test", null, "uri", dftGraphs, namedGraphs, rs, queryFile, TDBMaker.memFactory) ;
        JUnitCore runner = new org.junit.runner.JUnitCore() ;
        runner.addListener(new TextListener2(System.out)) ;
        
        TC_TDB.beforeClass() ;
        Result result = runner.run(t) ;
        TC_TDB.afterClass() ;
    }
    
    
    
    private static void tdbquery(String... args)
    {
        tdb.tdbquery.main(args) ;
        System.exit(0) ;
    }
    
    private static void tdbloader(String... args)
    {
        tdb.tdbloader.main(args) ; 
        System.exit(0) ;
    }
    
    private static void tdbdump(String... args)
    {
        tdb.tdbdump_OLD.main(args) ; 
        System.exit(0) ;
    }
    
    private static void tdbtest(String...args)
    {
        tdb.tdbtest.main(args) ;
        System.exit(0) ;
    }
    
    private static void tdbconfig(String... args) 
    {
        tdb.tdbconfig.main(args) ;
        System.exit(0) ;
    }
}

/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
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
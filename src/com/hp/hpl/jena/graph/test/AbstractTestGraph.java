/*
  (c) Copyright 2003, Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: AbstractTestGraph.java,v 1.50 2004-11-04 15:05:38 chris-dollin Exp $i
*/

package com.hp.hpl.jena.graph.test;

import com.hp.hpl.jena.util.HashUtils;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.graph.query.*;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.shared.*;

import java.util.*;

/**
    AbstractTestGraph provides a bunch of basic tests for something that
    purports to be a Graph. The abstract method getGraph must be overridden
    in subclasses to deliver a Graph of interest. 
    
 	@author kers
*/
public /* abstract */ class AbstractTestGraph extends GraphTestBase
    {
    public AbstractTestGraph( String name )
        { super( name ); }
        
    /**
        Returns a Graph to take part in the test. Must be overridden in
        a subclass.
    */
    // public abstract Graph getGraph();
    
    public Graph getGraph() { return new GraphMem(); }
    
    public Graph getGraphWith( String facts )
        {
        Graph g = getGraph();
        graphAdd( g, facts );
        return g;    
        }
        
    /**
        This test case was generated by Ian and was caused by GraphMem
        not keeping up with changes to the find interface. 
    */
    public void testFindAndContains()
        {
        Graph g = getGraph();
        Node r = Node.create( "r" ), s = Node.create( "s" ), p = Node.create( "P" );
        g.add( Triple.create( r, p,  s ) );
        assertTrue( g.contains( r, p, Node.ANY ) );
        assertTrue( g.find( r, p, Node.ANY ).hasNext() );
        }
        
    public void testFindByFluidTriple()
        {
        Graph g = getGraph();
        g.add( triple( "x y z ") );
        assertTrue( g.find( triple( "?? y z" ) ).hasNext() );
        assertTrue( g.find( triple( "x ?? z" ) ).hasNext() );
        assertTrue( g.find( triple( "x y ??" ) ).hasNext() );
        }
        
    public void testContainsConcrete()
        {
        Graph g = getGraph();
        graphAdd( g, "s P o; _x _R _y; x S 0" );
        assertTrue( g.contains( triple( "s P o" ) ) );
        assertTrue( g.contains( triple( "_x _R _y" ) ) );
        assertTrue( g.contains( triple( "x S 0" ) ) );
    /* */
        assertFalse( g.contains( triple( "s P Oh" ) ) );
        assertFalse( g.contains( triple( "S P O" ) ) );
        assertFalse( g.contains( triple( "s p o" ) ) );
        assertFalse( g.contains( triple( "_x _r _y" ) ) );
        assertFalse( g.contains( triple( "x S 1" ) ) );
        }
        
    public void testContainsFluid()
        {
        Graph g = getGraph();
        graphAdd( g, "x R y; a P b" );
        assertTrue( g.contains( triple( "?? R y" ) ) );
        assertTrue( g.contains( triple( "x ?? y" ) ) );
        assertTrue( g.contains( triple( "x R ??" ) ) );
        assertTrue( g.contains( triple( "?? P b" ) ) );
        assertTrue( g.contains( triple( "a ?? b" ) ) );
        assertTrue( g.contains( triple( "a P ??" ) ) );
        assertTrue( g.contains( triple( "?? R y" ) ) );
    /* */
        assertFalse( g.contains( triple( "?? R b" ) ) );
        assertFalse( g.contains( triple( "a ?? y" ) ) );
        assertFalse( g.contains( triple( "x P ??" ) ) );
        assertFalse( g.contains( triple( "?? R x" ) ) );
        assertFalse( g.contains( triple( "x ?? R" ) ) );
        assertFalse( g.contains( triple( "a S ??" ) ) );
        }
        
    /**
        test  isEmpty - moved from the QueryHandler code.
    */
    public void testIsEmpty()
        {
        Graph g = getGraph();
        if (canBeEmpty( g ))
            {
            assertTrue( g.isEmpty() );
            g.add( Triple.create( "S P O" ) );
            assertFalse( g.isEmpty() );
            g.add( Triple.create( "A B C" ) );
            assertFalse( g.isEmpty() );
            g.add( Triple.create( "S P O" ) );
            assertFalse( g.isEmpty() );
            g.delete( Triple.create( "S P O" ) );
            assertFalse( g.isEmpty() );
            g.delete( Triple.create( "A B C" ) );
            assertTrue( g.isEmpty() );
            }
        }
        

    public void testAGraph()
        {
        String title = this.getClass().getName();
        Graph g = getGraph();
        int baseSize = g.size();
        graphAdd( g, "x R y; p S q; a T b" );
    /* */
        assertContainsAll( title + ": simple graph", g, "x R y; p S q; a T b" );
        assertEquals( title + ": size", baseSize + 3, g.size() );
        graphAdd( g, "spindizzies lift cities; Diracs communicate instantaneously" );
        assertEquals( title + ": size after adding", baseSize + 5, g.size() );
        g.delete( triple( "x R y" ) );
        g.delete( triple( "a T b" ) );
        assertEquals( title + ": size after deleting", baseSize + 3, g.size() );
        assertContainsAll( title + ": modified simple graph", g, "p S q; spindizzies lift cities; Diracs communicate instantaneously" );
        assertOmitsAll( title + ": modified simple graph", g, "x R y; a T b" );
    /* */ 
        ClosableIterator it = g.find( null, node("lift"), null );
        assertTrue( title + ": finds some triple(s)", it.hasNext() );
        assertEquals( title + ": finds a 'lift' triple", triple("spindizzies lift cities"), it.next() );
        assertFalse( title + ": finds exactly one triple", it.hasNext() );
        }

//    public void testStuff()
//        {
////        testAGraph( "StoreMem", new GraphMem() );
////        testAGraph( "StoreMemBySubject", new GraphMem() );
////        String [] empty = new String [] {};
////        Graph g = graphWith( "x R y; p S q; a T b" );
////    /* */
////        assertContainsAll( "simple graph", g, "x R y; p S q; a T b" );
////        graphAdd( g, "spindizzies lift cities; Diracs communicate instantaneously" );
////        g.delete( triple( "x R y" ) );
////        g.delete( triple( "a T b" ) );
////        assertContainsAll( "modified simple graph", g, "p S q; spindizzies lift cities; Diracs communicate instantaneously" );
////        assertOmitsAll( "modified simple graph", g, "x R y; a T b" );
//        }
                                      
    /**
        Test that Graphs have transaction support methods, and that if they fail
        on some g they fail because they do not support the operation.
    */
    public void testHasTransactions()
        {
        Graph g = getGraph();
        TransactionHandler th = g.getTransactionHandler();
        th.transactionsSupported();
        try { th.begin(); } catch (UnsupportedOperationException x) {}
        try { th.abort(); } catch (UnsupportedOperationException x) {}
        try { th.commit(); } catch (UnsupportedOperationException x) {}
    /* */
        Command cmd = new Command() 
            { public Object execute() { return null; } };
        try { th.executeInTransaction( cmd ); } 
        catch (UnsupportedOperationException x) {}
        }

    static final Triple [] tripleArray = tripleArray( "S P O; A R B; X Q Y" );

    static final List tripleList = Arrays.asList( tripleArray( "i lt j; p equals q" ) );
        
    static final Triple [] setTriples = tripleArray
        ( "scissors cut paper; paper wraps stone; stone breaks scissors" );
        
    static final Set tripleSet = HashUtils.createSet( Arrays.asList( setTriples ) );
                
    public void testBulkUpdate()
        {
        Graph g = getGraph();
        BulkUpdateHandler bu = g.getBulkUpdateHandler();
        Graph items = graphWith( "pigs might fly; dead can dance" );
        int initialSize = g.size();
    /* */
        bu.add( tripleArray );
        testContains( g, tripleArray );
        testOmits( g, tripleList );
    /* */
        bu.add( tripleList );
        testContains( g, tripleList );
        testContains( g, tripleArray );
    /* */
        bu.add( tripleSet.iterator() );
        testContains( g, tripleSet.iterator() );
        testContains( g, tripleList );
        testContains( g, tripleArray );
    /* */
        bu.add( items );
        testContains( g, items );
        testContains( g, tripleSet.iterator() );
        testContains( g, tripleArray );
        testContains( g, tripleList );
    /* */
        bu.delete( tripleArray );
        testOmits( g, tripleArray );
        testContains( g, tripleList );
        testContains( g, tripleSet.iterator() );
        testContains( g, items );
    /* */
        bu.delete( tripleSet.iterator() );
        testOmits( g, tripleSet.iterator() );
        testOmits( g, tripleArray );
        testContains( g, tripleList );
        testContains( g, items );
    /* */
        bu.delete( items );
        testOmits( g, tripleSet.iterator() );
        testOmits( g, tripleArray );
        testContains( g, tripleList );
        testOmits( g, items ); 
    /* */
        bu.delete( tripleList );
        assertEquals( "graph has original size", initialSize, g.size() );
        }
        
    public void testBulkAddWithReification()
        {        
        testBulkAddWithReification( false );
        testBulkAddWithReification( true );
        }
        
    public void testBulkAddWithReificationPreamble()
        {
        Graph g = getGraph();
        xSPO( g.getReifier() );
        assertFalse( getReificationTriples( g.getReifier() ).isEmpty() );    
        }
        
    public void testBulkAddWithReification( boolean withReifications )
        {
        Graph graphToUpdate = getGraph();
        BulkUpdateHandler bu = graphToUpdate.getBulkUpdateHandler();
        Graph graphToAdd = graphWith( "pigs might fly; dead can dance" );
        Reifier updatedReifier = graphToUpdate.getReifier();
        Reifier addedReifier = graphToAdd.getReifier();
        xSPOyXYZ( addedReifier );
        bu.add( graphToAdd, withReifications );
        assertIsomorphic
            ( 
            withReifications ? getReificationTriples( addedReifier ) : graphWith( "" ), 
            getReificationTriples( updatedReifier ) 
            );
        }
        
    protected void xSPOyXYZ( Reifier r )
        {
        xSPO( r );
        r.reifyAs( Node.create( "y" ), Triple.create( "X Y Z" ) );       
        }

    protected void aABC( Reifier r )
        { r.reifyAs( Node.create( "a" ), Triple.create( "A B C" ) ); }
        
    protected void xSPO( Reifier r )
        { r.reifyAs( Node.create( "x" ), Triple.create( "S P O" ) ); }
        
    public void testRemove()
        { 
        testRemove( "S ?? ??", "S ?? ??" );
        testRemove( "S ?? ??", "?? P ??" );
        testRemove( "S ?? ??", "?? ?? O" );
        testRemove( "?? P ??", "S ?? ??" );
        testRemove( "?? P ??", "?? P ??" );
        testRemove( "?? P ??", "?? ?? O" );
        testRemove( "?? ?? O", "S ?? ??" );
        testRemove( "?? ?? O", "?? P ??" );
        testRemove( "?? ?? O", "?? ?? O" );
        }
    
    public void testRemove( String findRemove, String findCheck )
        {
        Graph g = getGraphWith( "S P O" );
        ExtendedIterator it = g.find( Triple.create( findRemove ) );
        try 
            {
            it.next(); it.remove(); it.close();
            assertFalse( g.contains( Triple.create( findCheck ) ) );
            }
        catch (UnsupportedOperationException e)
            { assertFalse( g.getCapabilities().iteratorRemoveAllowed() ); }
        }
    
    public void testBulkRemoveWithReification()
        {        
        testBulkUpdateRemoveWithReification( true );
        testBulkUpdateRemoveWithReification( false );
        }
        
    public void testBulkUpdateRemoveWithReification( boolean withReifications )
        {
        Graph g = getGraph();
        BulkUpdateHandler bu = g.getBulkUpdateHandler();
        Graph items = graphWith( "pigs might fly; dead can dance" );
        Reifier gr = g.getReifier(), ir = items.getReifier();
        xSPOyXYZ( ir );
        xSPO( gr ); aABC( gr ); 
        bu.delete( items, withReifications );
        Graph answer = graphWith( "" );
        Reifier ar = answer.getReifier();
        if (withReifications)
            aABC( ar ); 
        else
            {
            xSPO( ar );
            aABC( ar );
            }
        assertIsomorphic( getReificationTriples( ar ), getReificationTriples( gr ) );
        }
                                        
    public void testHasCapabilities()
        {
        Graph g = getGraph();
        Capabilities c = g.getCapabilities();
        boolean sa = c.sizeAccurate();
        boolean aaSome = c.addAllowed();
        boolean aaAll = c.addAllowed( true );
        boolean daSome = c.deleteAllowed();
        boolean daAll = c.deleteAllowed( true );
        boolean cbe = c.canBeEmpty();
        }
        
    public void testFind()
        {
        Graph g = getGraph();
        graphAdd( g, "S P O" );
        assertTrue( g.find( Node.ANY, null, null ).hasNext() );
        assertTrue( g.find( null, Node.ANY, null ).hasNext() );
        }
        
    public void testFind2()
        {
         Graph g = getGraphWith( "S P O" );   
         TripleIterator waitingForABigRefactoringHere = null;
         ExtendedIterator it = g.find( Node.ANY, Node.ANY, Node.ANY );
        }

    protected boolean canBeEmpty( Graph g )
        { return g.isEmpty(); }
        
    public void testEventRegister()
        {
        Graph g = getGraph();
        GraphEventManager gem = g.getEventManager();
        assertSame( gem, gem.register( new RecordingListener() ) );
        }
        
    /**
        Test that we can safely unregister a listener that isn't registered.
    */
    public void testEventUnregister()
        {
        getGraph().getEventManager().unregister( L );
        }
        
    /**
        Handy triple for test purposes.
    */
    protected Triple SPO = Triple.create( "S P O" );
    protected RecordingListener L = new RecordingListener();
    
    /**
        Utility: get a graph, register L with its manager, return the graph.
    */
    protected Graph getAndRegister( GraphListener gl )
        {
        Graph g = getGraph();
        g.getEventManager().register( gl );
        return g;
        }
        
    public void testAddTriple()
        {
        Graph g = getAndRegister( L );
        g.add( SPO );
        L.assertHas( new Object[] {"add", g, SPO} );
        }
        
    public void testDeleteTriple()
        {        
        Graph g = getAndRegister( L );
        g.delete( SPO );
        L.assertHas( new Object[] { "delete", g, SPO} );
        }
        
    public void testTwoListeners()
        {
        RecordingListener L1 = new RecordingListener();
        RecordingListener L2 = new RecordingListener();
        Graph g = getGraph();
        GraphEventManager gem = g.getEventManager();
        gem.register( L1 ).register( L2 );
        g.add( SPO );
        L2.assertHas( new Object[] {"add", g, SPO} );
        L1.assertHas( new Object[] {"add", g, SPO} );
        }
        
    public void testUnregisterWorks()
        {
        Graph g = getGraph();
        GraphEventManager gem = g.getEventManager();
        gem.register( L ).unregister( L );
        g.add( SPO );
        L.assertHas( new Object[] {} );
        }
        
    public void testRegisterTwice()
        {
        Graph g = getAndRegister( L );
        g.getEventManager().register( L );
        g.add( SPO );
        L.assertHas( new Object[] {"add", g, SPO, "add", g, SPO} );
        }
        
    public void testUnregisterOnce()
        {
        Graph g = getAndRegister( L );
        g.getEventManager().register( L ).unregister( L );
        g.delete( SPO );
        L.assertHas( new Object[] {"delete", g, SPO} );
        }
        
    public void testBulkAddArrayEvent()
        {
        Graph g = getAndRegister( L );
        Triple [] triples = tripleArray( "x R y; a P b" );
        g.getBulkUpdateHandler().add( triples );
        L.assertHas( new Object[] {"add[]", g, triples} );
        }
      
    public void testBulkAddList()
        {
        Graph g = getAndRegister( L );
        List elems = Arrays.asList( tripleArray( "bells ring loudly; pigs might fly" ) );
        g.getBulkUpdateHandler().add( elems );
        L.assertHas( new Object[] {"addList", g, elems} );
        }
    
    public void testBulkDeleteArray()
        {
        Graph g = getAndRegister( L );
        Triple [] triples = tripleArray( "x R y; a P b" );
        g.getBulkUpdateHandler().delete( triples );
        L.assertHas( new Object[] {"delete[]", g, triples} );
        }
        
    public void testBulkDeleteList()
        {
        Graph g = getAndRegister( L );
        List elems = Arrays.asList( tripleArray( "bells ring loudly; pigs might fly" ) );
        g.getBulkUpdateHandler().delete( elems );
        L.assertHas( new Object[] {"deleteList", g, elems} );
        }
        
    public void testBulkAddIterator()
        {
        Graph g = getAndRegister( L ); 
        Triple [] triples = tripleArray( "I wrote this; you read that; I wrote this" );
        g.getBulkUpdateHandler().add( asIterator( triples ) );
        L.assertHas( new Object[] {"addIterator", g, Arrays.asList( triples )} );
        }
        
    public void testBulkDeleteIterator()
        {
        Graph g = getAndRegister( L );
        Triple [] triples = tripleArray( "I wrote this; you read that; I wrote this" );
        g.getBulkUpdateHandler().delete( asIterator( triples ) );
        L.assertHas( new Object[] {"deleteIterator", g, Arrays.asList( triples )} );
        }
        
    public Iterator asIterator( Triple [] triples )
        { return Arrays.asList( triples ).iterator(); }
    
    public void testBulkAddGraph()
        {
        Graph g = getAndRegister( L );
        Graph triples = graphWith( "this type graph; I type slowly" );
        g.getBulkUpdateHandler().add( triples );
        L.assertHas( new Object[] {"addGraph", g, triples} );
        }
        
    public void testBulkDeleteGraph()
        {        
        Graph g = getAndRegister( L );
        Graph triples = graphWith( "this type graph; I type slowly" );
        g.getBulkUpdateHandler().delete( triples );
        L.assertHas( new Object[] {"deleteGraph", g, triples} );
        }
    
    public void testGeneralEvent()
        {
        Graph g = getAndRegister( L );
        Object value = new int[]{};
        g.getEventManager().notifyEvent( g, value );
        L.assertHas( new Object[] { "someEvent", g, value } );
        }
    
    public void testRemoveAllEvent()
        {
        Graph g = getAndRegister( L );
        g.getBulkUpdateHandler().removeAll();
        L.assertHas( new Object[] { "someEvent", g, GraphEvents.removeAll } );        
        }
    
    public void testRemoveSomeEvent()
        {
        Graph g = getAndRegister( L );
        Node S = node( "S" ), P = node( "?P" ), O = node( "??" );
        g.getBulkUpdateHandler().remove( S, P, O );
        Object event = GraphEvents.remove( S, P, O );
        L.assertHas( new Object[] { "someEvent", g, event } );        
        }
    
    /**
     * Test that nodes can be found in all triple positions.
     * However, testing for literals in subject positions is suppressed
     * at present to avoid problems with InfGraphs which try to prevent
     * such constructs leaking out to the RDF layer.
     */
    public void testContainsNode()
        {
        Graph g = getGraph();
        graphAdd( g, "a P b; _c _Q _d; a 11 12" );
        QueryHandler qh = g.queryHandler();
        assertTrue( qh.containsNode( node( "a" ) ) );
        assertTrue( qh.containsNode( node( "P" ) ) );
        assertTrue( qh.containsNode( node( "b" ) ) );
        assertTrue( qh.containsNode( node( "_c" ) ) );
        assertTrue( qh.containsNode( node( "_Q" ) ) );
        assertTrue( qh.containsNode( node( "_d" ) ) );
//        assertTrue( qh.containsNode( node( "10" ) ) );
        assertTrue( qh.containsNode( node( "11" ) ) );
        assertTrue( qh.containsNode( node( "12" ) ) );
    /* */
        assertFalse( qh.containsNode( node( "x" ) ) );
        assertFalse( qh.containsNode( node( "_y" ) ) );
        assertFalse( qh.containsNode( node( "99" ) ) );
        }
        
    public void testRemoveAll()
        { 
        testRemoveAll( "" );
        testRemoveAll( "a R b" );
        testRemoveAll( "c S d; e:ff GGG hhhh; _i J 27; Ell Em 'en'" );
        }
    
    public void testRemoveAll( String triples )
        {
        Graph g = getGraph();
        graphAdd( g, triples );
        g.getBulkUpdateHandler().removeAll();
        assertTrue( g.isEmpty() );
        }
    
    /**
     	Test cases for RemoveSPO(); each entry is a triple (add, remove, result).
     	<ul>
     	<li>add - the triples to add to the graph to start with
     	<li>remove - the pattern to use in the removal
     	<li>result - the triples that should remain in the graph
     	</ul>
    */
    protected String[][] cases =
    	{
                { "x R y", "x R y", "" },
                { "x R y; a P b", "x R y", "a P b" },
                { "x R y; a P b", "?? R y", "a P b" },
                { "x R y; a P b", "x R ??", "a P b" },
                { "x R y; a P b", "x ?? y", "a P b" },      
                { "x R y; a P b", "?? ?? ??", "" },       
                { "x R y; a P b; c P d", "?? P ??", "x R y" },       
                { "x R y; a P b; x S y", "x ?? ??", "a P b" },                 
    	};
    
    /**
     	Test that remove(s, p, o) works, in the presence of inferencing graphs that
     	mean emptyness isn't available. This is why we go round the houses and
     	test that expected ~= initialContent + addedStuff - removed - initialContent.
    */
    public void testRemoveSPO()
        {
        for (int i = 0; i < cases.length; i += 1)
            for (int j = 0; j < 3; j += 1)
                {
                Graph content = getGraph();
                Graph baseContent = copy( content );
                graphAdd( content, cases[i][0] );
                Triple remove = triple( cases[i][1] );
                Graph expected = graphWith( cases[i][2] );
                content.getBulkUpdateHandler().remove( remove.getSubject(), remove.getPredicate(), remove.getObject() );
                Graph finalContent = remove( copy( content ), baseContent );
                assertIsomorphic( cases[i][1], expected, finalContent );
                }
        }
    
    protected void add( Graph toUpdate, Graph toAdd )
        {
        toUpdate.getBulkUpdateHandler().add( toAdd );
        }
    
    protected Graph remove( Graph toUpdate, Graph toRemove )
        {
        toUpdate.getBulkUpdateHandler().delete( toRemove );
        return toUpdate;
        }
    
    protected Graph copy( Graph g )
        {
        Graph result = Factory.createDefaultGraph();
        result.getBulkUpdateHandler().add( g );
        return result;
        }
    
    protected Graph getClosed()
        {
        Graph result = getGraph();
        result.close();
        return result;
        }
        
//    public void testClosedDelete()
//        {
//        try { getClosed().delete( triple( "x R y" ) ); fail( "delete when closed" ); }
//        catch (ClosedException c) { /* as required */ }
//        }
//        
//    public void testClosedAdd()
//        {
//        try { getClosed().add( triple( "x R y" ) ); fail( "add when closed" ); }
//        catch (ClosedException c) { /* as required */ }
//        }
//        
//    public void testClosedContainsTriple()
//        {
//        try { getClosed().contains( triple( "x R y" ) ); fail( "contains[triple] when closed" ); }
//        catch (ClosedException c) { /* as required */ }
//        }
//        
//    public void testClosedContainsSPO()
//        {
//        Node a = Node.ANY;
//        try { getClosed().contains( a, a, a ); fail( "contains[SPO] when closed" ); }
//        catch (ClosedException c) { /* as required */ }
//        }
//        
//    public void testClosedFindTriple()
//        {
//        try { getClosed().find( triple( "x R y" ) ); fail( "find [triple] when closed" ); }
//        catch (ClosedException c) { /* as required */ }
//        }
//        
//    public void testClosedFindSPO()
//        {
//        Node a = Node.ANY;
//        try { getClosed().find( a, a, a ); fail( "find[SPO] when closed" ); }
//        catch (ClosedException c) { /* as required */ }
//        }
//        
//    public void testClosedSize()
//        {
//        try { getClosed().size(); fail( "size when closed (" + this.getClass() + ")" ); }
//        catch (ClosedException c) { /* as required */ }
//        }
        
    }


/*
    (c) Copyright 2003 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/*
 	(c) Copyright 2005, Hewlett-Packard Development Company, LP
 	All rights reserved.
 	[See end of file]
*/

package com.hp.hpl.jena.rdf.model.impl;

import java.util.*;
import java.util.Map;

import com.hp.hpl.jena.graph.test.GraphTestBase;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.*;
import com.hp.hpl.jena.util.CollectionFactory;
import com.hp.hpl.jena.vocabulary.*;

/**
     ModelSpecFactory is the [new] class for delivering new ModelSpec objects 
     described by their RDF specifications. The ModelSpec objects are created
     by ModelSpecCreator objects found in a ModelSpecCreatorRegistry, where they
     are identified by their RDF types as found in the RDF description. 
     ModelSpecFactory finds [if necessary] the root of the description, finds the
     most specific type of that root which is a subclass of JenaModelSpec.ModelSpec, and
     invokes the corresponding creator object.
     
     <p>ModelSpecFactory has no instance methods.
     
     @author kers
*/
public class ModelSpecFactory
    {
    /**
        The registry which is used when none is supplied to ModelSpecFactory.
    */
    protected static ModelSpecCreatorRegistry defaultRegistry = ModelSpecCreatorRegistry.instance;
    
    /**
        Answer a ModelSpec as described by the resource <code>root</code> in the model 
        <code>m</code>, using the creator registry <code>registry</code>. If there is
        no such creator, a <code>BadDescriptionException</code> is thrown.
    */
    protected static ModelSpec create( ModelSpecCreatorRegistry registry, Model m, Resource root )
        { Resource type = findSpecificType( root, JenaModelSpec.ModelSpec );
        ModelSpecCreator sc = registry.getCreator( type );
        if (sc == null) throw new BadDescriptionException( "no model-spec creator found for " + type, m );
        return sc.create( root, m ); }
    
    /**
        As per <code>create(ModelSpecCreatorRegistry,Model,Resource), with the Resource
        being the unique subject of <code>m</code> which has type <code>JenaModelSpec.ModelSpec</code>.
    */
    public static ModelSpec createSpec( ModelSpecCreatorRegistry registry, Model m )
        { Model full = withSchema( m ); 
        return create( registry, full, findRootByType( full, JenaModelSpec.ModelSpec ) ); }
    
    /**
        As per <code>create(ModelSpecCreatorRegistry,Model,Resource), with the Registry
        being the default instance of ModelSpecCreatorRegistry.
    */
    public static ModelSpec createSpec( Model m, Resource root )
        { Model full = withSchema( m ); 
        return create( defaultRegistry, full, (Resource) root.inModel( full ) ); }

    /**
        As per <code>create(ModelSpecCreatorRegistry,Model,Resource), with the Registry
        being the default Registry and the Resource being the unique subject of
        <code>m</code> with type <code>JenaModelSpec.ModelSpec</code>.
    */
    public static ModelSpec createSpec( Model m )
        { Model full = withSchema( m );  
//         Map map = CollectionFactory.createHashedMap();
//         System.err.println( "create spec from: " + GraphTestBase.nice( m.getGraph(), map ) + "\n which fills to " + GraphTestBase.nice( full.getGraph(), map ) );
        return create( defaultRegistry, full, findRootByType( full, JenaModelSpec.ModelSpec ) ); }

    /**
        Answer a wrapping of <code>m</code> as an RDFS model using the JenaModelSpec schema.
    */
    public static Model withSchema( Model m )
        // { return ModelFactory.createRDFSModel( JenaModelSpec.getSchema(), m ); }
        { return withSpecSchema( m ); }

    /**
        Answer the unique subject of <code>m</code> which has type <code>type</code>.
        If there is no such subject, throw a <code>BadDescriptionException</code>.
    */
    public static Resource findRootByType( Model m, Resource type )
        { StmtIterator it = m.listStatements( null, RDF.type, type );
        if (!it.hasNext()) 
            {
            // System.err.println( ">> " + GraphTestBase.nice( m.getGraph(), new HashMap() ) );
            throw new BadDescriptionNoRootException( m, type );
            }
        Resource root = it.nextStatement().getSubject();
        if (it.hasNext()) throw new BadDescriptionMultipleRootsException( m, type );
        return root; }
    
    /**
        Answer the "most specific" type of root in desc which is an instance of type.
        We assume a single inheritance thread starting with that type. The model
        should contain the subclass closure (ie either be complete, or an inference
        model which will generate completeness).
        
        @param root the subject whose type is to be found
        @param type the base type for the search
        @return T such that (root type T) and if (root type T') then (T' subclassof T)
    */
    public static Resource findSpecificType( Resource root, Resource type )
        { StmtIterator it = root.listProperties( RDF.type );
        Model desc = root.getModel();
        while (it.hasNext())
            { Resource candidate = it.nextStatement().getResource();
            if (desc.contains( candidate, RDFS.subClassOf, type )) type = candidate; }
        return type; }

    public static Model withSpecSchema( Model m )
        {
        Model result = ModelFactory.createDefaultModel();
        Model schema = ModelFactory.createRDFSModel( JenaModelSpec.getSchema() );
        result.add( m );
        for (StmtIterator it = schema.listStatements( null, RDFS.subClassOf, (RDFNode) null ); it.hasNext();)
            { 
            Statement s = it.nextStatement();
            if (s.getSubject().getNameSpace().equals( JenaModelSpec.baseURI ) && s.getResource().getNameSpace().equals( JenaModelSpec.baseURI ))
                result.add( s ); 
            }        
        for (StmtIterator it = schema.listStatements( null, RDFS.domain, (RDFNode) null ); it.hasNext();)
            {
            Statement s = it.nextStatement();
            for (StmtIterator x = m.listStatements( null, (Property) s.getSubject().as( Property.class ), (RDFNode) null ); x.hasNext();)
                {
                Statement t = x.nextStatement();
                result.add( t.getSubject(), RDF.type, s.getObject() );
                // System.err.println( "++ added " + t.getSubject() + " type " + s.getObject() );
                for (StmtIterator foo = schema.listStatements( s.getResource(), RDFS.subClassOf, (RDFNode) null ); foo.hasNext();)
                    {
                    result.add( t.getSubject(), RDF.type, foo.nextStatement().getObject() );
                    }
                }
            }
        addSupertypes( m, result, schema );
        addSupertypes( m, result, m );
        // System.err.println( ">> result: " + GraphTestBase.nice( result.getGraph(), new HashMap() ) );
        return result;
        }

    protected static void addSupertypes( Model m, Model result, Model schema )
        {
        for (StmtIterator it = m.listStatements( null, RDF.type, (RDFNode) null ); it.hasNext();)
            {
            Statement s = it.nextStatement();
            for (StmtIterator bat = schema.listStatements( s.getResource(), RDFS.subClassOf, (RDFNode) null ); bat.hasNext();)
                result.add( s.getSubject(), RDF.type, bat.nextStatement().getObject() );
            }
        }

    }


/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
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
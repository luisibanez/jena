/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            10 Feb 2003
 * Filename           $RCSfile: OWLProfile.java,v $
 * Revision           $Revision: 1.7 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2003-04-07 09:34:37 $
 *               by   $Author: ian_dickinson $
 *
 * (c) Copyright 2002-2003, Hewlett-Packard Company, all rights reserved.
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////
package com.hp.hpl.jena.ontology.impl;


// Imports
///////////////
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.enhanced.*;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;

import java.util.*;



/**
 * <p>
 * Ontology language profile implementation for the Full variant of the OWL 2002/07 language.
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: OWLProfile.java,v 1.7 2003-04-07 09:34:37 ian_dickinson Exp $
 */
public class OWLProfile
    extends AbstractProfile
{
    // Constants
    //////////////////////////////////


    // Instance variables
    //////////////////////////////////


    // Constructors
    //////////////////////////////////


    // External signature methods
    //////////////////////////////////

    public String   NAMESPACE() {                   return OWL.NAMESPACE; }

    public Resource CLASS() {                       return OWL.Class; }
    public Resource RESTRICTION() {                 return OWL.Restriction; }
    public Resource THING() {                       return OWL.Thing; }
    public Resource NOTHING() {                     return OWL.Nothing; }
    public Resource OBJECT_PROPERTY() {             return OWL.ObjectProperty; }
    public Resource DATATYPE_PROPERTY() {           return OWL.DatatypeProperty; }
    public Resource TRANSITIVE_PROPERTY() {         return OWL.TransitiveProperty; }
    public Resource SYMMETRIC_PROPERTY() {          return OWL.SymmetricProperty; }
    public Resource FUNCTIONAL_PROPERTY() {         return OWL.FunctionalProperty; }
    public Resource INVERSE_FUNCTIONAL_PROPERTY() { return OWL.InverseFunctionalProperty; }
    public Resource ALL_DIFFERENT() {               return OWL.AllDifferent; }
    public Resource ONTOLOGY() {                    return OWL.Ontology; }
    public Resource DEPRECATED_CLASS() {            return OWL.DeprecatedClass; }
    public Resource DEPRECATED_PROPERTY() {         return OWL.DeprecatedProperty; }
    public Resource ANNOTATION_PROPERTY() {         return OWL.AnnotationProperty; }
    
    

    public Property EQUIVALENT_PROPERTY() {         return OWL.equivalentProperty; }
    public Property EQUIVALENT_CLASS() {            return OWL.equivalentClass; }
    public Property DISJOINT_WITH() {               return OWL.disjointWith; }
    public Property SAME_INDIVIDUAL_AS() {          return OWL.sameIndividualAs; }
    public Property SAME_AS() {                     return OWL.sameAs; }
    public Property DIFFERENT_FROM() {              return OWL.differentFrom; }
    public Property DISTINCT_MEMBERS() {            return OWL.distinctMembers; }
    public Property UNION_OF() {                    return OWL.unionOf; }
    public Property INTERSECTION_OF() {             return OWL.intersectionOf; }
    public Property COMPLEMENT_OF() {               return OWL.complementOf; }
    public Property ONE_OF() {                      return OWL.oneOf; }
    public Property ON_PROPERTY() {                 return OWL.onProperty; }
    public Property ALL_VALUES_FROM() {             return OWL.allValuesFrom; }
    public Property HAS_VALUE() {                   return OWL.hasValue; }
    public Property SOME_VALUES_FROM() {            return OWL.someValuesFrom; }
    public Property MIN_CARDINALITY() {             return OWL.minCardinality; }
    public Property MAX_CARDINALITY() {             return OWL.maxCardinality; }
    public Property CARDINALITY() {                 return OWL.cardinality; }
    public Property INVERSE_OF() {                  return OWL.inverseOf; }
    public Property IMPORTS() {                     return OWL.imports; }
    public Property PRIOR_VERSION() {               return OWL.priorVersion; }
    public Property BACKWARD_COMPATIBLE_WITH() {    return OWL.backwardCompatibleWith; }
    public Property INCOMPATIBLE_WITH() {           return OWL.incompatibleWith; }
    public Property SUB_PROPERTY_OF() {             return RDFS.subPropertyOf; }
    public Property SUB_CLASS_OF() {                return RDFS.subClassOf; }
    public Property DOMAIN() {                      return RDFS.domain; }
    public Property RANGE() {                       return RDFS.range; }

    // Annotations    
    public Property VERSION_INFO() {                return OWL.versionInfo; }
    public Property LABEL() {                       return RDFS.label; }
    public Property COMMENT() {                     return RDFS.comment; }
    public Property SEE_ALSO() {                    return RDFS.seeAlso; }
    public Property IS_DEFINED_BY() {               return RDFS.isDefinedBy; }
    
    
    protected Resource[][] aliasTable() {
        return new Resource[][] {
            { OWL.sameAs, OWL.sameIndividualAs }
        };
    }
    
    /** The only first-class axiom type in OWL is AllDifferent */ 
    public Iterator getAxiomTypes() {
        return Arrays.asList(
            new Resource[] {
                OWL.AllDifferent
            }
        ).iterator();
    }

    /** The annotation properties of OWL */
    public Iterator getAnnotationProperties() {
        return Arrays.asList(
            new Resource[] {
                OWL.versionInfo,
                RDFS.label, 
                RDFS.seeAlso,
                OWL.comment,
                RDFS.isDefinedBy
            }
        ).iterator();
    }
    
    public Iterator getClassDescriptionTypes() {
        return Arrays.asList(
            new Resource[] {
                OWL.Class,
                OWL.Restriction
            }
        ).iterator();
    }


    /**
     * <p>
     * Answer true if the given graph supports a view of this node as the given 
     * language element, according to the semantic constraints of the profile.
     * If strict checking on the ontology model is turned off, this check is
     * skipped.
     * </p>
     * 
     * @param n A node to test
     * @param g The enhanced graph containing <code>n</code>, which is assumed to
     * be an {@link OntModel}.
     * @param type A class indicating the facet that we are testing against.
     * @return True if strict checking is off, or if <code>n</code> can be 
     * viewed according to the facet resource <code>res</code>
     */
    public boolean isSupported( Node n, EnhGraph g, Class type ) {
        if (g instanceof OntModel) {
            OntModel m = (OntModel) g;
            
            if (!m.strictMode()) {
                // checking turned off
                return true;
            }
            else {
                // lookup the profile check for this resource
                SupportsCheck check = (SupportsCheck) s_supportsChecks.get( type );
                
                // a check must be defined for the test to succeed
                return (check != null)  && check.doCheck( n, g );  
            }
        }
        else {
            return false;
        }
    }

    // Internal implementation methods
    //////////////////////////////////


    //==============================================================================
    // Inner class definitions
    //==============================================================================

    /** Helper class for doing syntactic/semantic checks on a node */
    protected static class SupportsCheck
    {
        public boolean doCheck( Node n, EnhGraph g ) {
            return true;
        }
    }
    
    
    // Table of check data
    //////////////////////
    
    private static Object[][] s_supportsCheckTable = new Object[][] {
        // Resource (key),              check method
        {  AllDifferent.class,          new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.AllDifferent.asNode() );
                                            }
                                        }
        },
        {  AnnotationProperty.class,    new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                for (Iterator i = ((OntModel) g).getProfile().getAnnotationProperties();  i.hasNext(); ) {
                                                    if (((Resource) i.next()).asNode().equals( n )) {
                                                        // a built-in annotation property
                                                        return true;
                                                    }
                                                }
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.AnnotationProperty.asNode() );
                                            }
                                        }
        },
        { Axiom.class,                  new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                // node will support being an Axiom facet if it has rdf:type owl:AllDifferent or equivalent
                                                for (Iterator i = ((OntModel) g).getProfile().getAxiomTypes();  i.hasNext(); ) {
                                                    if (g.asGraph().contains( n, RDF.type.asNode(), ((Resource) i.next()).asNode() )) {
                                                        // node has a recognised axiom type
                                                        return true;
                                                    }
                                                }

                                                return false;
                                            }
                                        }
        },
        {  ClassDescription.class,      new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.Class.asNode() ) ||
                                                       g.asGraph().contains( n, RDF.type.asNode(), OWL.Restriction.asNode() );
                                            }
                                        }
        },
        {  DatatypeProperty.class,      new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.DatatypeProperty.asNode() );
                                            }
                                        }
        },
        {  ObjectProperty.class,        new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.ObjectProperty.asNode() );
                                            }
                                        }
        },
        {  FunctionalProperty.class,    new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.FunctionalProperty.asNode() );
                                            }
                                        }
        },
        {  InverseFunctionalProperty.class, new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.InverseFunctionalProperty.asNode() ) &&
                                                !g.asGraph().contains( n, RDF.type.asNode(), OWL.DatatypeProperty.asNode() );
                                            }
                                        }
        },
        {  ObjectProperty.class,        new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.ObjectProperty.asNode() );
                                            }
                                        }
        },
        {  OntClass.class,              new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.Class.asNode() );
                                            }
                                        }
        },
        {  OntList.class,               new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return n.equals( RDF.nil.asNode() )  ||
                                                       g.asGraph().contains( n, RDF.type.asNode(), RDF.List.asNode() );
                                            }
                                        }
        },
        {  Ontology.class,              new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.Ontology.asNode() );
                                            }
                                        }
        },
        {  OntProperty.class,           new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), RDF.Property.asNode() ) ||
                                                       g.asGraph().contains( n, RDF.type.asNode(), OWL.ObjectProperty.asNode() ) ||
                                                       g.asGraph().contains( n, RDF.type.asNode(), OWL.DatatypeProperty.asNode() ) ||
                                                       g.asGraph().contains( n, RDF.type.asNode(), OWL.AnnotationProperty.asNode() );
                                            }
                                        }
        },
        {  Restriction.class,           new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.Restriction.asNode() );
                                            }
                                        }
        },
        {  SymmetricProperty.class,     new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.SymmetricProperty.asNode() ) &&
                                                       !g.asGraph().contains( n, RDF.type.asNode(), OWL.DatatypeProperty.asNode() );
                                            }
                                        }
        },
        {  TransitiveProperty.class,    new SupportsCheck() {
                                            public boolean doCheck( Node n, EnhGraph g ) {
                                                return g.asGraph().contains( n, RDF.type.asNode(), OWL.TransitiveProperty.asNode() ) &&
                                                       !g.asGraph().contains( n, RDF.type.asNode(), OWL.DatatypeProperty.asNode() );
                                            }
                                        }
        },
    };


    // Static variables
    //////////////////////////////////

    /** Map from resource to syntactic/semantic checks that a node can be seen as the given facet */
    protected static HashMap s_supportsChecks = new HashMap();
    
    static {
        // initialise the map of supports checks from a table of static data
        for (int i = 0;  i < s_supportsCheckTable.length;  i++) {
            s_supportsChecks.put( s_supportsCheckTable[i][0], s_supportsCheckTable[i][1] );
        }
    }

}




/*
    (c) Copyright Hewlett-Packard Company 2002-2003
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


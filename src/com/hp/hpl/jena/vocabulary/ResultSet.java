/*
  (c) Copyright 2003, Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: ResultSet.java,v 1.6 2003-08-27 13:08:11 andy_seaborne Exp $
*/

/* Vocabulary Class generated by Jena vocabulary generator
 */
package com.hp.hpl.jena.vocabulary;

import com.hp.hpl.jena.rdf.model.*;

/** ResultSet vocabulary class for namespace http://jena.hpl.hp.com/2003/03/result-set#
 */
public class ResultSet {

    protected static final String uri ="http://jena.hpl.hp.com/2003/03/result-set#";

    /** returns the URI for this schema
     * @return the URI for this schema
     */
    public static String getURI() {
          return uri;
    }


    public static final Resource ResultSolution = ResourceFactory.createResource(uri + "ResultSolution" );
    public static final Resource ResultBinding = ResourceFactory.createResource(uri + "ResultBinding" );
    public static final Resource ResultSet = ResourceFactory.createResource(uri + "ResultSet" );
    public static final Property value = ResourceFactory.createProperty(uri + "value" );
    public static final Property resultVariable = ResourceFactory.createProperty(uri + "resultVariable" );
    public static final Property variable = ResourceFactory.createProperty(uri + "variable" );
    public static final Property size = ResourceFactory.createProperty(uri + "size" );
    public static final Property binding = ResourceFactory.createProperty(uri + "binding" );
    public static final Property solution = ResourceFactory.createProperty(uri + "solution" );
    public static final Resource undefined = ResourceFactory.createResource(uri + "undefined" ) ;
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
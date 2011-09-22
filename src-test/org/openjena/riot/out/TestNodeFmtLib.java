/*
 * (c) Copyright 2011 Epimorphics Ltd.
 * All rights reserved.
 * [See end of file]
 */

package org.openjena.riot.out;

import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.util.NodeFactory ;
import com.hp.hpl.jena.vocabulary.RDF ;

import org.junit.Test ;
import org.openjena.atlas.junit.BaseTest ;

public class TestNodeFmtLib extends BaseTest
{
    // : is 3A 
    // - is 2D
    
    // BNode labels.
    
    @Test public void encode_01() { testenc("abc", "Babc") ; }
    @Test public void encode_02() { testenc("-", "BX2D") ; }
    @Test public void encode_03() { testenc("abc:def-ghi", "BabcX3AdefX2Dghi") ; }
    @Test public void encode_04() { testenc("01X", "B01XX") ; }
    @Test public void encode_05() { testenc("-X", "BX2DXX") ; }

    @Test public void rt_01() {  testencdec("a") ; }
    @Test public void rt_02() {  testencdec("") ; }
    @Test public void rt_03() {  testencdec("abc") ; }
    @Test public void rt_04() {  testencdec("000") ; }
    @Test public void rt_05() {  testencdec("-000") ; }
    @Test public void rt_06() {  testencdec("X-") ; }
    @Test public void rt_07() {  testencdec("-123:456:xyz") ; }

    
    private void testenc(String input, String expected)
    {
        String x = NodeFmtLib.encodeBNodeLabel(input) ;
        assertEquals(expected, x) ;
    }
    
    private void testencdec(String input)
    {
        String x = NodeFmtLib.encodeBNodeLabel(input) ;
        String y = NodeFmtLib.decodeBNodeLabel(x) ;
        assertEquals(input, y) ;
    }

    @Test public void fmtNode_01() { test ("<a>", "<a>") ; }
    @Test public void fmtNode_02() { test ("<"+RDF.getURI()+"type>", "rdf:type") ; }
    @Test public void fmtNode_03() { test ("'123'^^xsd:integer", "123") ; }
    @Test public void fmtNode_04() { test ("'abc'^^xsd:integer", "\"abc\"^^xsd:integer") ; }
    
    private static void test(String node, String output)
    { test(NodeFactory.parseNode(node) , output) ; }
    
    private static void test(Node node, String output)
    {
        String x = NodeFmtLib.str(node) ;
        assertEquals(output, x) ;
    }
}

/*
 * (c) Copyright 2011 Epimorphics Ltd.
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
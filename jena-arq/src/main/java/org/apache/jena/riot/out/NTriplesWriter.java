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

package org.apache.jena.riot.out;

import java.io.OutputStream ;
import java.util.Iterator ;

import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.atlas.lib.Sink ;
import org.apache.jena.riot.system.Prologue ;
import org.apache.jena.riot.system.SyntaxLabels ;

import com.hp.hpl.jena.graph.Graph ;
import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.graph.Triple ;

public class NTriplesWriter
{
    public static void write(OutputStream out, Graph graph)
    {
        write(out, graph.find(Node.ANY, Node.ANY, Node.ANY)) ;
    }
    
    public static void write(OutputStream out, Iterator<Triple> iter)
    {
        Prologue prologue = Prologue.create(null, null) ; // (null, graph.getPrefixMapping()) ;
        //NodeToLabel.createBNodeByLabelEncoded() ;
        Sink<Triple> sink = new SinkTripleOutput(out, prologue, SyntaxLabels.createNodeToLabel()) ;
        Iter.sendToSink(iter, sink) ;
    }
}

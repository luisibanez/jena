/*
  (c) Copyright 2002, 2003, Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: PredicateBase.java,v 1.4 2003-08-27 13:00:59 andy_seaborne Exp $
*/

package com.hp.hpl.jena.graph.query;

/**
    PredicateBase provides a base class for implementations of Predicate.
    <code>evaluateBool</code> is left abstract - who knows what the core meaning of
    a predicate is - but a default implementation of <code>and</code> is supplied which
    does the "obvious thing": it's <code>evaluateBool</code> is the &amp;&amp; of the two
    component Predicate's <code>evaluateBool</code>s.
    
    @author kers
*/

public abstract class PredicateBase implements Predicate
    {
    /** evaluate truth value in this domain: deferred to subclasses */
    public abstract boolean evaluateBool( Domain d );

    /** L.and(R).evaluateBool(D) = L.evaluateBool(D) && R.evaluateBool(D) */
    public Predicate and( final Predicate other )
        {
        return new PredicateBase()
            {
            public boolean evaluateBool( Domain d )
                { return PredicateBase.this.evaluateBool( d ) && other.evaluateBool( d ); }
            };
        }
    }

/*
    (c) Copyright 2002, 2003 Hewlett-Packard Development Company, LP
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
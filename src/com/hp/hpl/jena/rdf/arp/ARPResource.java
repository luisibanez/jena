/*
 *  (c) Copyright 2001  Hewlett-Packard Development Company, LP
 *  All rights reserved.
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
 
 * * $Id: ARPResource.java,v 1.5 2003-12-05 17:46:34 jeremy_carroll Exp $
   
   AUTHOR:  Jeremy J. Carroll
*/
/*
 * ARPResource.java
 *
 * Created on June 25, 2001, 9:57 PM
 */

package com.hp.hpl.jena.rdf.arp;



/**
 *
 * @author  jjc
 */
class ARPResource implements  AResource {
    // Constants cribbed from com.megginson.sax.rdf.RDFFilter
    private final static String RDFNS =
	"http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static int RDFNS_LENGTH = RDFNS.length();
    private final static String XMLNS =
	"http://www.w3.org/XML/1998/namespace";


				// Element names
    private static AResource RDF_STATEMENT ; 
    static URIReference RDF_BAG;  
    static URIReference RDF_SEQ ;  
    static URIReference RDF_ALT ; 
    private static String RDF_LI ; 
    private static AResource RDF_TYPE ;
    private static AResource RDF_SUBJECT ;
    private static AResource RDF_PREDICATE ;
    private static AResource RDF_OBJECT ;
    static {
        try {
        RDF_STATEMENT =
	new URIReference((RDFNS + "Statement").intern()); 
    RDF_BAG =
	new URIReference((RDFNS + "Bag").intern());  
    RDF_SEQ =
	new URIReference((RDFNS + "Seq").intern());  
    RDF_ALT =
	new URIReference((RDFNS + "Alt").intern()); 
    RDF_LI =
	(RDFNS + "li").intern(); 
    RDF_TYPE =
	new URIReference((RDFNS + "type").intern());
    RDF_SUBJECT =
	new URIReference((RDFNS + "subject").intern());
    RDF_PREDICATE =
	new URIReference((RDFNS + "predicate").intern());
    RDF_OBJECT =
	new URIReference((RDFNS + "object").intern());
        }
        catch ( MalformedURIException e ) {
            System.err.println("Internal error: " + e.toString());
            e.printStackTrace();
        }
    }
    
    private ARPFilter arp;
    
    private String nodeID = null;
    
    // The uri is the document uri.
    // If the uri is null then
    // we are an anonymous resource , number genId.
    private String uri;     // maybe the 
    private ARPResource bag = null;
    static private int genIdCounter = 0;
    final private int genId = genIdCounter++;
    private int liCounter = 1;
    static String dummy = "http://jena.hpl.hp.com/arp/not/a/real/uri/";
	static String nullDummy = "nullpointerexception://jena.hpl.hp.com/arp/";
    
    ARPResource(ARPFilter parent) {
        arp = parent;
    }
    
    void setBagId(String id) {
        bag = new ARPResource(arp);
        bag.setAbout(id);
        bag.setType(RDF_BAG);
    } 
    
    void setAbout(URIReference uri){
        setAbout(uri.getURI());
    }
    
    void setAbout(String uri){
        this.uri = uri.startsWith( dummy ) ? uri.substring( dummy.length() ) : uri;
    }
    
    void setLiObject(Object v, String reify) {
        setPredObject( rdf_n(liCounter++), v, reify(reify) );
    }
  
    void setPredicateObject(AResource p, ARPString v) {
        setPredObject(p,v,null);
    }
    
    void setPredicateObject(AResource p, Object v, String reify) {
        setPredObject(p,v,reify(reify));
    }
    
    AResource reify(String reify) {
        ARPResource reification = null;
        if ( reify != null ) {
                reification = new ARPResource(arp);
                reification.setAbout(reify);
        }
        return reification;
    }
    
     
    void setType(URIReference r) {
        setPredObject(RDF_TYPE, r, null );
    }
    
    private void setPredObject( AResource pred, Object obj, AResource reify ) {
        // The basic triple.
        triple( this, pred, obj );
        
        // Deal with bag before reification
        if ( bag != null ) {
            if ( reify == null )
                reify = new ARPResource(arp);
            bag.setLiObject( reify, null );
           // tripleRes( bag, rdf_n(bag.liCounter++), reify ); 
            // Don't take the short cut - it misses stuff.
        }
        
        if ( reify != null ) {
            tripleRes( reify, RDF_TYPE, RDF_STATEMENT );
            tripleRes( reify, RDF_SUBJECT, this );
            tripleRes( reify, RDF_PREDICATE, pred );
            triple( reify, RDF_OBJECT, obj );
        }
        
    }
    
    private void triple( AResource subj, AResource pred, Object obj ) {
        int sw = 0;
        if ( obj instanceof AResource ) {
            sw += 1;
        }
        if ( obj instanceof ALiteral ) {
            sw += 2;
        }
        switch (sw) {
            case 1:
                tripleRes(subj,pred,(AResource)obj);
                break;
            case 2:
                tripleLit(subj,pred,(ALiteral)obj);
                break;
            case 0:
            case 3:
            default:
                throw new RuntimeException("Assertion failure: sw = " + sw);
        }
    }
    
    private void tripleRes(final AResource s,final AResource p,final AResource o ) {
        arp.statementHandler.statement(s,p,o);
        
    }
    
    private void tripleLit( AResource s, final AResource p, final ALiteral o ) {
           arp.statementHandler.statement(s,p,o);
    }
    
    static private URIReference _rdf_n[] = new URIReference[0];
    
    static private URIReference rdf_n(int i) {
        if (i>=_rdf_n.length) {
            int newLength = (i+10)*3/2;
            URIReference new_rdf_n[] = new URIReference[newLength];
            System.arraycopy(_rdf_n,0,new_rdf_n,0,_rdf_n.length);
            for (int j=_rdf_n.length;j<newLength;j++) {
                try {
                new_rdf_n[j] = new URIReference(RDFNS+"_"+j);
                }
                catch (MalformedURIException disaster) {
                    throw new RuntimeException("Impossible: RDF_nnn handling logic error.");
                }
            }
            _rdf_n = new_rdf_n;
        }
        return _rdf_n[i];
    }
    
    static int isRDF_N(AResource r) {
        if ( !r.isAnonymous() ) {
            String uri = r.getURI();
            if ( uri.length() > RDFNS_LENGTH && uri.charAt(RDFNS_LENGTH) == '_' ) {
                try {
                    String sub = uri.substring(RDFNS_LENGTH+1);
                    int rslt = Integer.parseInt(sub);
                    if ( rslt>0 && uri.startsWith(RDFNS) ) {
                        return rslt;
                    }
                }
                catch (NumberFormatException e) {
                }
            }
        }
        return -1;
    }
    
    // AResource interface.
    public boolean isAnonymous() {
        return uri == null;
    }
    
    public String getAnonymousID() {
        return nodeID==null
                ? ( "ARP:" + Integer.toString(genId) )
                : nodeID;
    }
    
    void setNodeId(String s) {
    	nodeID = s;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public String toString() {
        return uri==null?"_"+genId:uri;
    }
    
    public int hashCode() {
        if ( uri==null )
            return genId;
        else
            return uri.hashCode();
    }
    
    public boolean equals(Object o) {
        if ( o == null || !(o instanceof AResource))
            return false;
        AResource a=(AResource)o;
        if ( uri == null ) 
            return a.isAnonymous() && a.getAnonymousID().equals(getAnonymousID());
        else
            return (!a.isAnonymous()) && uri.equals(a.getURI());
    }
    
    private Object userData;
    
    public Object getUserData() {
    	if ( uri != null )
    	  throw new IllegalStateException("User data only supported on blank nodes");
        if ( nodeID != null )
           return arp.getUserData(nodeID);
        else
           return userData;
    }
    
    public void setUserData(Object d) {
    	if ( uri != null )
    	  throw new IllegalStateException("User data only supported on blank nodes");
     	if ( nodeID == null ) 
            userData = d;
        else
            arp.setUserData(nodeID,d);
    }

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.arp.AResource#hasNodeID()
	 */
	public boolean hasNodeID() {
		return nodeID!=null;
	}
    
}

/* Generated By:JavaCC: Do not edit this line. TurtleParser.java */
/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 */

package com.hp.hpl.jena.n3.turtle.parser ;

import com.hp.hpl.jena.n3.turtle.ParserBase ;
import com.hp.hpl.jena.graph.* ;

public class TurtleParser extends ParserBase implements TurtleParserConstants {

// --- Entry point
  final public void parse() throws ParseException {
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PREFIX:
      case INTEGER:
      case DECIMAL:
      case DOUBLE:
      case STRING_LITERAL1:
      case STRING_LITERAL2:
      case STRING_LITERAL_LONG1:
      case STRING_LITERAL_LONG2:
      case Q_IRIref:
      case QNAME_NS:
      case QNAME:
      case BLANK_NODE_LABEL:
      case VAR1:
      case VAR2:
      case LPAREN:
      case NIL:
      case LBRACE:
      case LBRACKET:
      case ANON:
      case PLUS:
      case MINUS:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      Statement();
    }
    jj_consume_token(0);
  }

  final public void Statement() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PREFIX:
      Directive();
      break;
    case INTEGER:
    case DECIMAL:
    case DOUBLE:
    case STRING_LITERAL1:
    case STRING_LITERAL2:
    case STRING_LITERAL_LONG1:
    case STRING_LITERAL_LONG2:
    case Q_IRIref:
    case QNAME_NS:
    case QNAME:
    case BLANK_NODE_LABEL:
    case VAR1:
    case VAR2:
    case LPAREN:
    case NIL:
    case LBRACE:
    case LBRACKET:
    case ANON:
    case PLUS:
    case MINUS:
      TriplesSameSubject();
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(DOT);
  }

  final public void Directive() throws ParseException {
                     Token t ; Node n ;
    jj_consume_token(PREFIX);
    t = jj_consume_token(QNAME_NS);
    n = Q_IRI_REF();
      String s = fixupPrefix(t.image, t.beginLine, t.beginColumn) ;
      setPrefix(s, n.getURI()) ;
  }

// N3

// ---- TRIPLES
// <<<<< SPARQL extract
  final public void TriplesSameSubject() throws ParseException {
                              Node s ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
    case DECIMAL:
    case DOUBLE:
    case STRING_LITERAL1:
    case STRING_LITERAL2:
    case STRING_LITERAL_LONG1:
    case STRING_LITERAL_LONG2:
    case Q_IRIref:
    case QNAME_NS:
    case QNAME:
    case BLANK_NODE_LABEL:
    case VAR1:
    case VAR2:
    case NIL:
    case LBRACE:
    case ANON:
    case PLUS:
    case MINUS:
      s = VarOrTerm();
      PropertyListNotEmpty(s);
      break;
    case LPAREN:
    case LBRACKET:
      // Any of the triple generating syntax elements
        s = TriplesNode();
      PropertyList(s);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void PropertyList(Node s) throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case KW_A:
    case Q_IRIref:
    case QNAME_NS:
    case QNAME:
    case ARROW:
      PropertyListNotEmpty(s);
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
  }

// >>>>> SPARQL extract

// Non-recursive for Turtle long PropertyList tests
  final public void PropertyListNotEmpty(Node s) throws ParseException {
                                      Node p ;
    p = Verb();
    ObjectList(s, p);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SEMICOLON:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_2;
      }
      jj_consume_token(SEMICOLON);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case KW_A:
      case Q_IRIref:
      case QNAME_NS:
      case QNAME:
      case ARROW:
        p = Verb();
        ObjectList(s, p);
        break;
      default:
        jj_la1[5] = jj_gen;
        ;
      }
    }
  }

// Non-recursive for Turtle long PropertyList tests
  final public void ObjectList(Node s, Node p) throws ParseException {
                                   Node o ;
    Object(s, p);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_3;
      }
      jj_consume_token(COMMA);
      Object(s, p);
    }
  }

  final public void Object(Node s, Node p) throws ParseException {
                               Node o ;
    o = GraphNode();
    Triple t = new Triple(s,p,o) ;
    emitTriple(token.beginLine, token.beginColumn, t) ;
  }

// <<<<< SPARQL extract
  final public Node Verb() throws ParseException {
               Node p ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case Q_IRIref:
    case QNAME_NS:
    case QNAME:
      p = IRIref();
      break;
    case KW_A:
      jj_consume_token(KW_A);
                            p = nRDFtype ;
      break;
    case ARROW:
      jj_consume_token(ARROW);
        p = nLogImplies ;
        if ( strictTurtle )
          raiseException("=> (log:implies) not legalin Turtle",
                          token.beginLine, token.beginColumn ) ;
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return p ;}
    throw new Error("Missing return statement in function");
  }

// -------- Triple expansions

// Anything that can stand in a node slot and which is
// a number of triples
  final public Node TriplesNode() throws ParseException {
                       Node n ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LPAREN:
      n = Collection();
                     {if (true) return n ;}
      break;
    case LBRACKET:
      n = BlankNodePropertyList();
                                {if (true) return n ;}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Node BlankNodePropertyList() throws ParseException {
    jj_consume_token(LBRACKET);
      Node n = createBNode() ;
    PropertyListNotEmpty(n);
    jj_consume_token(RBRACKET);
      {if (true) return n ;}
    throw new Error("Missing return statement in function");
  }

// ------- RDF collections

// Code not as SPARQL/ARQ because of output ordering.
  final public Node Collection() throws ParseException {
      Node listHead = nRDFnil ; Node lastCell = null ; Node n ;
    jj_consume_token(LPAREN);
    label_4:
    while (true) {
      Node cell = createBNode() ;
      if ( listHead == nRDFnil )
         listHead = cell ;
      if ( lastCell != null )
        emitTriple(token.beginLine, token.beginColumn,
                   new Triple(lastCell, nRDFrest,  cell)) ;
      n = GraphNode();
      emitTriple(token.beginLine, token.beginColumn,
                 new Triple(cell, nRDFfirst,  n)) ;
      lastCell = cell ;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case DECIMAL:
      case DOUBLE:
      case STRING_LITERAL1:
      case STRING_LITERAL2:
      case STRING_LITERAL_LONG1:
      case STRING_LITERAL_LONG2:
      case Q_IRIref:
      case QNAME_NS:
      case QNAME:
      case BLANK_NODE_LABEL:
      case VAR1:
      case VAR2:
      case LPAREN:
      case NIL:
      case LBRACE:
      case LBRACKET:
      case ANON:
      case PLUS:
      case MINUS:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_4;
      }
    }
    jj_consume_token(RPAREN);
     if ( lastCell != null )
       emitTriple(token.beginLine, token.beginColumn,
                  new Triple(lastCell, nRDFrest,  nRDFnil)) ;
     {if (true) return listHead ;}
    throw new Error("Missing return statement in function");
  }

// -------- Nodes in a graph pattern or template
  final public Node GraphNode() throws ParseException {
                     Node n ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
    case DECIMAL:
    case DOUBLE:
    case STRING_LITERAL1:
    case STRING_LITERAL2:
    case STRING_LITERAL_LONG1:
    case STRING_LITERAL_LONG2:
    case Q_IRIref:
    case QNAME_NS:
    case QNAME:
    case BLANK_NODE_LABEL:
    case VAR1:
    case VAR2:
    case NIL:
    case LBRACE:
    case ANON:
    case PLUS:
    case MINUS:
      n = VarOrTerm();
                    {if (true) return n ;}
      break;
    case LPAREN:
    case LBRACKET:
      n = TriplesNode();
                      {if (true) return n ;}
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Node VarOrTerm() throws ParseException {
                    Node n = null ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case VAR1:
    case VAR2:
      n = Var();
      break;
    case INTEGER:
    case DECIMAL:
    case DOUBLE:
    case STRING_LITERAL1:
    case STRING_LITERAL2:
    case STRING_LITERAL_LONG1:
    case STRING_LITERAL_LONG2:
    case Q_IRIref:
    case QNAME_NS:
    case QNAME:
    case BLANK_NODE_LABEL:
    case NIL:
    case ANON:
    case PLUS:
    case MINUS:
      n = GraphTerm();
      break;
    case LBRACE:
      n = Formula();
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return n ;}
    throw new Error("Missing return statement in function");
  }

  final public Node Formula() throws ParseException {
                  Token t ;
    t = jj_consume_token(LBRACE);
                   startFormula(t.beginLine, t.beginColumn) ;
    TriplesSameSubject();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DOT:
        ;
        break;
      default:
        jj_la1[12] = jj_gen;
        break label_5;
      }
      jj_consume_token(DOT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case DECIMAL:
      case DOUBLE:
      case STRING_LITERAL1:
      case STRING_LITERAL2:
      case STRING_LITERAL_LONG1:
      case STRING_LITERAL_LONG2:
      case Q_IRIref:
      case QNAME_NS:
      case QNAME:
      case BLANK_NODE_LABEL:
      case VAR1:
      case VAR2:
      case LPAREN:
      case NIL:
      case LBRACE:
      case LBRACKET:
      case ANON:
      case PLUS:
      case MINUS:
        TriplesSameSubject();
        break;
      default:
        jj_la1[13] = jj_gen;
        ;
      }
    }
    t = jj_consume_token(RBRACE);
                   endFormula(t.beginLine, t.beginColumn) ;
        {if (true) return null ;}
    throw new Error("Missing return statement in function");
  }

// >>>>> SPARQL extract
  final public Node Var() throws ParseException {
               Token t ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case VAR1:
      t = jj_consume_token(VAR1);
      break;
    case VAR2:
      t = jj_consume_token(VAR2);
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return createVariable(t.image, t.beginLine, t.beginColumn) ;}
    throw new Error("Missing return statement in function");
  }

// <<<<< SPARQL extract
  final public Node GraphTerm() throws ParseException {
                     Node n ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case Q_IRIref:
    case QNAME_NS:
    case QNAME:
      n = IRIref();
                          {if (true) return n ;}
      break;
    case STRING_LITERAL1:
    case STRING_LITERAL2:
    case STRING_LITERAL_LONG1:
    case STRING_LITERAL_LONG2:
      n = RDFLiteral();
                          {if (true) return n ;}
      break;
    case INTEGER:
    case DECIMAL:
    case DOUBLE:
    case PLUS:
    case MINUS:
    boolean positive  = true ;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
      case MINUS:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case MINUS:
          jj_consume_token(MINUS);
           positive=false;
          break;
        case PLUS:
          jj_consume_token(PLUS);
          break;
        default:
          jj_la1[15] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[16] = jj_gen;
        ;
      }
      n = NumericLiteral(positive);
                                  {if (true) return n ;}
      break;
    case BLANK_NODE_LABEL:
    case ANON:
      n = BlankNode();
                          {if (true) return n ;}
      break;
    case NIL:
      jj_consume_token(NIL);
           {if (true) return nRDFnil ;}
      break;
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// >>>>> SPARQL extract
// <<<<< SPARQL extract
// ---- Basic terms
  final public Node NumericLiteral(boolean positive) throws ParseException {
                                          Token t ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
      t = jj_consume_token(INTEGER);
                  {if (true) return makeNodeInteger(positive, t.image) ;}
      break;
    case DECIMAL:
      t = jj_consume_token(DECIMAL);
                  {if (true) return makeNodeDecimal(positive, t.image) ;}
      break;
    case DOUBLE:
      t = jj_consume_token(DOUBLE);
                 {if (true) return makeNodeDouble(positive, t.image) ;}
      break;
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// >>>>> SPARQL extract
// Langtag oddity.
  final public Node RDFLiteral() throws ParseException {
                      Token t ; String lex = null ;
    lex = String();
    String lang = null ; Node uri = null ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PREFIX:
    case LANGTAG:
    case DATATYPE:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PREFIX:
      case LANGTAG:
        lang = Langtag();
        break;
      case DATATYPE:
        jj_consume_token(DATATYPE);
        uri = IRIref();
        break;
      default:
        jj_la1[19] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[20] = jj_gen;
      ;
    }
      {if (true) return makeNode(lex, lang, uri) ;}
    throw new Error("Missing return statement in function");
  }

  final public String Langtag() throws ParseException {
                     Token t ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LANGTAG:
      t = jj_consume_token(LANGTAG);
      break;
    case PREFIX:
      t = jj_consume_token(PREFIX);
      break;
    default:
      jj_la1[21] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    String lang = stripChars(t.image, 1) ; {if (true) return lang ;}
    throw new Error("Missing return statement in function");
  }

// >>>>> SPARQL extract
// Node BooleanLiteral() : {}
// {
//   <TRUE> { return XSD_TRUE ; }
//  |
//   <FALSE> { return XSD_FALSE ; }
// }

// <<<<< SPARQL extract
  final public String String() throws ParseException {
                    Token t ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRING_LITERAL1:
      t = jj_consume_token(STRING_LITERAL1);
      break;
    case STRING_LITERAL2:
      t = jj_consume_token(STRING_LITERAL2);
      break;
    case STRING_LITERAL_LONG1:
      t = jj_consume_token(STRING_LITERAL_LONG1);
      break;
    case STRING_LITERAL_LONG2:
      t = jj_consume_token(STRING_LITERAL_LONG2);
      break;
    default:
      jj_la1[22] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      String lex = stripQuotes(t.image) ;
      lex = unescapeStr(lex,  t.beginLine, t.beginColumn) ;
      {if (true) return lex ;}
    throw new Error("Missing return statement in function");
  }

  final public Node IRIref() throws ParseException {
                  Node n ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case Q_IRIref:
      n = Q_IRI_REF();
                    {if (true) return n ;}
      break;
    case QNAME_NS:
    case QNAME:
      n = QName();
                {if (true) return n ;}
      break;
    default:
      jj_la1[23] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Node QName() throws ParseException {
                 Token t ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case QNAME:
      t = jj_consume_token(QNAME);
      {if (true) return createURIfromQName(t.image, t.beginLine, t.beginColumn) ;}
      break;
    case QNAME_NS:
      t = jj_consume_token(QNAME_NS);
      {if (true) return createURIfromQName(t.image, t.beginLine, t.beginColumn) ;}
      break;
    default:
      jj_la1[24] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Node BlankNode() throws ParseException {
                      Token t = null ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BLANK_NODE_LABEL:
      t = jj_consume_token(BLANK_NODE_LABEL);
      {if (true) return createBNode(t.image, t.beginLine, t.beginColumn) ;}
      break;
    case ANON:
      jj_consume_token(ANON);
           {if (true) return createBNode() ;}
      break;
    default:
      jj_la1[25] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Node Q_IRI_REF() throws ParseException {
                     Token t ;
    t = jj_consume_token(Q_IRIref);
    {if (true) return createNodeFromURI(t.image, t.beginLine, t.beginColumn) ;}
    throw new Error("Missing return statement in function");
  }

  public TurtleParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[26];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static private int[] jj_la1_2;
  static {
      jj_la1_0();
      jj_la1_1();
      jj_la1_2();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0xe7874000,0xe7874000,0xe7870000,0xe0002000,0x0,0xe0002000,0x0,0xe0002000,0x0,0xe7870000,0xe7870000,0xe7870000,0x0,0xe7870000,0x0,0x0,0x0,0xe7870000,0x70000,0x4000,0x4000,0x4000,0x7800000,0xe0000000,0xc0000000,0x0,};
   }
   private static void jj_la1_1() {
      jj_la1_1 = new int[] {0x1802b47,0x1802b47,0x1802b47,0x40000,0x4000,0x40000,0x8000,0x40000,0x840,0x1802b47,0x1802b47,0x1802307,0x10000,0x1802b47,0x6,0x1800000,0x1800000,0x1802101,0x0,0x10000008,0x10000008,0x8,0x0,0x0,0x0,0x2001,};
   }
   private static void jj_la1_2() {
      jj_la1_2 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
   }

  public TurtleParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public TurtleParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new TurtleParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
  }

  public TurtleParser(java.io.Reader stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new TurtleParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
  }

  public TurtleParser(TurtleParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
  }

  public void ReInit(TurtleParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[68];
    for (int i = 0; i < 68; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 26; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
          if ((jj_la1_2[i] & (1<<j)) != 0) {
            la1tokens[64+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 68; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

}

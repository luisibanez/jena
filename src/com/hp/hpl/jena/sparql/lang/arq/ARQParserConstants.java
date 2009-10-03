/* Generated By:JavaCC: Do not edit this line. ARQParserConstants.java */
/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 */

package com.hp.hpl.jena.sparql.lang.arq ;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ARQParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int WS = 6;
  /** RegularExpression Id. */
  int SINGLE_LINE_COMMENT = 7;
  /** RegularExpression Id. */
  int IRIref = 8;
  /** RegularExpression Id. */
  int PNAME_NS = 9;
  /** RegularExpression Id. */
  int PNAME_LN = 10;
  /** RegularExpression Id. */
  int BLANK_NODE_LABEL = 11;
  /** RegularExpression Id. */
  int VAR1 = 12;
  /** RegularExpression Id. */
  int VAR2 = 13;
  /** RegularExpression Id. */
  int LANGTAG = 14;
  /** RegularExpression Id. */
  int A2Z = 15;
  /** RegularExpression Id. */
  int A2ZN = 16;
  /** RegularExpression Id. */
  int KW_A = 17;
  /** RegularExpression Id. */
  int BASE = 18;
  /** RegularExpression Id. */
  int PREFIX = 19;
  /** RegularExpression Id. */
  int SELECT = 20;
  /** RegularExpression Id. */
  int DISTINCT = 21;
  /** RegularExpression Id. */
  int REDUCED = 22;
  /** RegularExpression Id. */
  int DESCRIBE = 23;
  /** RegularExpression Id. */
  int CONSTRUCT = 24;
  /** RegularExpression Id. */
  int ASK = 25;
  /** RegularExpression Id. */
  int LIMIT = 26;
  /** RegularExpression Id. */
  int OFFSET = 27;
  /** RegularExpression Id. */
  int ORDER = 28;
  /** RegularExpression Id. */
  int BY = 29;
  /** RegularExpression Id. */
  int ASC = 30;
  /** RegularExpression Id. */
  int DESC = 31;
  /** RegularExpression Id. */
  int NAMED = 32;
  /** RegularExpression Id. */
  int FROM = 33;
  /** RegularExpression Id. */
  int WHERE = 34;
  /** RegularExpression Id. */
  int AND = 35;
  /** RegularExpression Id. */
  int GRAPH = 36;
  /** RegularExpression Id. */
  int OPTIONAL = 37;
  /** RegularExpression Id. */
  int UNION = 38;
  /** RegularExpression Id. */
  int SERVICE = 39;
  /** RegularExpression Id. */
  int EXISTS = 40;
  /** RegularExpression Id. */
  int NOT = 41;
  /** RegularExpression Id. */
  int NOTEXISTS = 42;
  /** RegularExpression Id. */
  int UNSAID = 43;
  /** RegularExpression Id. */
  int AS = 44;
  /** RegularExpression Id. */
  int LET = 45;
  /** RegularExpression Id. */
  int GROUP = 46;
  /** RegularExpression Id. */
  int HAVING = 47;
  /** RegularExpression Id. */
  int AGG = 48;
  /** RegularExpression Id. */
  int COUNT = 49;
  /** RegularExpression Id. */
  int MIN = 50;
  /** RegularExpression Id. */
  int MAX = 51;
  /** RegularExpression Id. */
  int SUM = 52;
  /** RegularExpression Id. */
  int AVG = 53;
  /** RegularExpression Id. */
  int STDDEV = 54;
  /** RegularExpression Id. */
  int FETCH = 55;
  /** RegularExpression Id. */
  int FILTER = 56;
  /** RegularExpression Id. */
  int BOUND = 57;
  /** RegularExpression Id. */
  int COALESCE = 58;
  /** RegularExpression Id. */
  int IF = 59;
  /** RegularExpression Id. */
  int STR = 60;
  /** RegularExpression Id. */
  int DTYPE = 61;
  /** RegularExpression Id. */
  int LANG = 62;
  /** RegularExpression Id. */
  int LANGMATCHES = 63;
  /** RegularExpression Id. */
  int IS_URI = 64;
  /** RegularExpression Id. */
  int IS_IRI = 65;
  /** RegularExpression Id. */
  int IS_BLANK = 66;
  /** RegularExpression Id. */
  int IS_LITERAL = 67;
  /** RegularExpression Id. */
  int REGEX = 68;
  /** RegularExpression Id. */
  int SAME_TERM = 69;
  /** RegularExpression Id. */
  int TRUE = 70;
  /** RegularExpression Id. */
  int FALSE = 71;
  /** RegularExpression Id. */
  int MODIFY = 72;
  /** RegularExpression Id. */
  int INSERT = 73;
  /** RegularExpression Id. */
  int DELETE = 74;
  /** RegularExpression Id. */
  int DATA = 75;
  /** RegularExpression Id. */
  int ADD = 76;
  /** RegularExpression Id. */
  int REMOVE = 77;
  /** RegularExpression Id. */
  int LOAD = 78;
  /** RegularExpression Id. */
  int CLEAR = 79;
  /** RegularExpression Id. */
  int CREATE = 80;
  /** RegularExpression Id. */
  int SILENT = 81;
  /** RegularExpression Id. */
  int DROP = 82;
  /** RegularExpression Id. */
  int INTO = 83;
  /** RegularExpression Id. */
  int DIGITS = 84;
  /** RegularExpression Id. */
  int INTEGER = 85;
  /** RegularExpression Id. */
  int DECIMAL = 86;
  /** RegularExpression Id. */
  int DOUBLE = 87;
  /** RegularExpression Id. */
  int INTEGER_POSITIVE = 88;
  /** RegularExpression Id. */
  int DECIMAL_POSITIVE = 89;
  /** RegularExpression Id. */
  int DOUBLE_POSITIVE = 90;
  /** RegularExpression Id. */
  int INTEGER_NEGATIVE = 91;
  /** RegularExpression Id. */
  int DECIMAL_NEGATIVE = 92;
  /** RegularExpression Id. */
  int DOUBLE_NEGATIVE = 93;
  /** RegularExpression Id. */
  int EXPONENT = 94;
  /** RegularExpression Id. */
  int QUOTE_3D = 95;
  /** RegularExpression Id. */
  int QUOTE_3S = 96;
  /** RegularExpression Id. */
  int ECHAR = 97;
  /** RegularExpression Id. */
  int STRING_LITERAL1 = 98;
  /** RegularExpression Id. */
  int STRING_LITERAL2 = 99;
  /** RegularExpression Id. */
  int STRING_LITERAL_LONG1 = 100;
  /** RegularExpression Id. */
  int STRING_LITERAL_LONG2 = 101;
  /** RegularExpression Id. */
  int LPAREN = 102;
  /** RegularExpression Id. */
  int RPAREN = 103;
  /** RegularExpression Id. */
  int NIL = 104;
  /** RegularExpression Id. */
  int LBRACE = 105;
  /** RegularExpression Id. */
  int RBRACE = 106;
  /** RegularExpression Id. */
  int LBRACKET = 107;
  /** RegularExpression Id. */
  int RBRACKET = 108;
  /** RegularExpression Id. */
  int ANON = 109;
  /** RegularExpression Id. */
  int SEMICOLON = 110;
  /** RegularExpression Id. */
  int COMMA = 111;
  /** RegularExpression Id. */
  int DOT = 112;
  /** RegularExpression Id. */
  int EQ = 113;
  /** RegularExpression Id. */
  int NE = 114;
  /** RegularExpression Id. */
  int GT = 115;
  /** RegularExpression Id. */
  int LT = 116;
  /** RegularExpression Id. */
  int LE = 117;
  /** RegularExpression Id. */
  int GE = 118;
  /** RegularExpression Id. */
  int BANG = 119;
  /** RegularExpression Id. */
  int TILDE = 120;
  /** RegularExpression Id. */
  int COLON = 121;
  /** RegularExpression Id. */
  int SC_OR = 122;
  /** RegularExpression Id. */
  int SC_AND = 123;
  /** RegularExpression Id. */
  int PLUS = 124;
  /** RegularExpression Id. */
  int MINUS = 125;
  /** RegularExpression Id. */
  int STAR = 126;
  /** RegularExpression Id. */
  int SLASH = 127;
  /** RegularExpression Id. */
  int DATATYPE = 128;
  /** RegularExpression Id. */
  int AT = 129;
  /** RegularExpression Id. */
  int ASSIGN = 130;
  /** RegularExpression Id. */
  int VBAR = 131;
  /** RegularExpression Id. */
  int CARROT = 132;
  /** RegularExpression Id. */
  int FPATH = 133;
  /** RegularExpression Id. */
  int RPATH = 134;
  /** RegularExpression Id. */
  int QMARK = 135;
  /** RegularExpression Id. */
  int PN_CHARS_BASE = 136;
  /** RegularExpression Id. */
  int PN_CHARS_U = 137;
  /** RegularExpression Id. */
  int PN_CHARS = 138;
  /** RegularExpression Id. */
  int PN_PREFIX = 139;
  /** RegularExpression Id. */
  int PN_LOCAL = 140;
  /** RegularExpression Id. */
  int VARNAME = 141;
  /** RegularExpression Id. */
  int UNKNOWN = 142;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<WS>",
    "<SINGLE_LINE_COMMENT>",
    "<IRIref>",
    "<PNAME_NS>",
    "<PNAME_LN>",
    "<BLANK_NODE_LABEL>",
    "<VAR1>",
    "<VAR2>",
    "<LANGTAG>",
    "<A2Z>",
    "<A2ZN>",
    "\"a\"",
    "\"base\"",
    "\"prefix\"",
    "\"select\"",
    "\"distinct\"",
    "\"reduced\"",
    "\"describe\"",
    "\"construct\"",
    "\"ask\"",
    "\"limit\"",
    "\"offset\"",
    "\"order\"",
    "\"by\"",
    "\"asc\"",
    "\"desc\"",
    "\"named\"",
    "\"from\"",
    "\"where\"",
    "\"and\"",
    "\"graph\"",
    "\"optional\"",
    "\"union\"",
    "\"service\"",
    "\"exists\"",
    "\"not\"",
    "<NOTEXISTS>",
    "\"unsaid\"",
    "\"as\"",
    "\"let\"",
    "\"group\"",
    "\"having\"",
    "\"agg\"",
    "\"count\"",
    "\"min\"",
    "\"max\"",
    "\"sum\"",
    "\"avg\"",
    "\"stdev\"",
    "\"fetch\"",
    "\"filter\"",
    "\"bound\"",
    "\"coalesce\"",
    "\"if\"",
    "\"str\"",
    "\"datatype\"",
    "\"lang\"",
    "\"langmatches\"",
    "\"isURI\"",
    "\"isIRI\"",
    "\"isBlank\"",
    "\"isLiteral\"",
    "\"regex\"",
    "\"sameTerm\"",
    "\"true\"",
    "\"false\"",
    "\"modify\"",
    "\"insert\"",
    "\"delete\"",
    "\"data\"",
    "\"add\"",
    "\"remove\"",
    "\"load\"",
    "\"clear\"",
    "\"create\"",
    "\"silent\"",
    "\"drop\"",
    "\"into\"",
    "<DIGITS>",
    "<INTEGER>",
    "<DECIMAL>",
    "<DOUBLE>",
    "<INTEGER_POSITIVE>",
    "<DECIMAL_POSITIVE>",
    "<DOUBLE_POSITIVE>",
    "<INTEGER_NEGATIVE>",
    "<DECIMAL_NEGATIVE>",
    "<DOUBLE_NEGATIVE>",
    "<EXPONENT>",
    "\"\\\"\\\"\\\"\"",
    "\"\\\'\\\'\\\'\"",
    "<ECHAR>",
    "<STRING_LITERAL1>",
    "<STRING_LITERAL2>",
    "<STRING_LITERAL_LONG1>",
    "<STRING_LITERAL_LONG2>",
    "\"(\"",
    "\")\"",
    "<NIL>",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "<ANON>",
    "\";\"",
    "\",\"",
    "\".\"",
    "\"=\"",
    "\"!=\"",
    "\">\"",
    "\"<\"",
    "\"<=\"",
    "\">=\"",
    "\"!\"",
    "\"~\"",
    "\":\"",
    "\"||\"",
    "\"&&\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"^^\"",
    "\"@\"",
    "\":=\"",
    "\"|\"",
    "\"^\"",
    "\"->\"",
    "\"<-\"",
    "\"?\"",
    "<PN_CHARS_BASE>",
    "<PN_CHARS_U>",
    "<PN_CHARS>",
    "<PN_PREFIX>",
    "<PN_LOCAL>",
    "<VARNAME>",
    "<UNKNOWN>",
  };

}
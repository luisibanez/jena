//-*- mode: antlr -*-

/* This is part of the Jena RDF Framework.
 * (c) Copyright 2002-2003, Hewlett-Packard Company, all rights reserved.
 * [See end of file for details]
 */

/* This N3 grammar is based on:
 *     http://www.w3.org/DesignIssues/Notation3.html
 *       The grammar uses the rule names in this (as of August 2002)
 *     Tests in http://www.w3.org/2000/10/swap/test/syntax
 *     http://www.w3.org/2000/10/swap/rdfn3.g
 *     http://www.w3.org/2000/10/swap/rdfn3-gram.html
 *
 * For information about N3:
 *     http://infomesh.net/2002/notation3/
 *       Has references to other grammar for N3
 *     http://notabug.com/2002/n3/
 *
 * Grammar notes: 
 *         http://infomesh.net/2002/notation3/#deprecated
 *  N3 is UTF-8
 * 
 * N-Triples is defined in 
 *   http://www.w3.org/TR/rdf-testcases/
 * which is where the lang tag / datatype syntax comes from
 */


header
{
package com.hp.hpl.jena.n3 ;
import java.io.* ;
import antlr.TokenStreamRecognitionException ;
}


class N3AntlrParser extends Parser ;
options
{
	// We stop parsing on any error
	defaultErrorHandler = false ;
    k = 1 ; 
    buildAST = true;
}

tokens
{
	ANON ; FORMULA ;
	QNAME ; KEYWORD ; NAME_OP ;
	KW_THIS ; KW_OF ; KW_HAS ; KW_A ; KW_IS ;
	AT_PREFIX ; AT_LANG ;
	STRING ; LITERAL ;
}

// Parser operations for emitting results.
//
// This is a streaming parser: the AST is only built for statements
// and directives, not for the whole file.  Triples and directives are
// output as soon as they are found.
//
// The event handler will need to do some work to maintain the bNode
// references (_:xxxx in the file and generated anon:NNNN) and to maintain
// the prefix mapping in generate quads.
//
// The event handler needs to filter for non-RDF-isms.

{
	// Extra code for the parser.

	N3AntlrLexer lexer = null ;
	void setLexer(N3AntlrLexer _lexer) { lexer = _lexer ; }

	// Internallly generated anon id.  Avoid clash with _:xxx
	private int anonId = 0 ;
	private String genAnonId() { return "=:"+(anonId++) ; }

	// Forumla zero is the outer context.  Avoid clash with other labels.
	private int formulaId = 1 ;
	private String genFormulaId() { return "{}:"+(formulaId++) ; }

	private N3ParserEventHandler handler = null ;

	public void setEventHandler(N3ParserEventHandler h) { this.handler = h ; }

	private void startDocument()
	{
		if ( handler == null )
			throw new RuntimeException("N3AntlrParser: No sink specified") ;
		handler.startDocument() ;
	}

	private void endDocument() { handler.endDocument() ; }


	private void startFormula(String context)
	{
		handler.startFormula(lexer.getLine(), context) ;
	}

	private void endFormula(String context)
	{
		handler.endFormula(lexer.getLine(), context) ;
	}

	private String currentFormula = null ;

    private void emitQuad(AST subj, AST prop, AST obj)
	{ 
		handler.quad(lexer.getLine(), subj, prop, obj, currentFormula ) ;
	}

	private void directive(AST directive, AST arg)
	{
		handler.directive(lexer.getLine(),
						  directive, new AST[]{arg},
						  currentFormula) ;
	}

	private void directive(AST directive, AST arg1, AST arg2)
	{
		handler.directive(lexer.getLine(),
						  directive, new AST[]{arg1, arg2},
						  currentFormula) ;
	}

	public void reportError(RecognitionException ex)
	{
		handler.error(ex, "N3 error: ["+ex.line+":"+ex.column+"] "+ex.getMessage());
    }

    /** Parser error-reporting function can be overridden in subclass */
    public void reportError(String s)
    {
	    //System.err.println("N3AntlrParser(s): "+s);
		handler.error(null, "N3AntlrParser(s): ["+lexer.getLine()+":"+lexer.getColumn()+"] "+s) ;
    }

	private String damlFirst  = "http://www.daml.org/2001/03/daml+oil#first" ;
	private String damlRest   = "http://www.daml.org/2001/03/daml+oil#rest" ;
	private String damlNil    = "http://www.daml.org/2001/03/daml+oil#nil" ;

}

// The top level rule
document!: 
		{ startDocument() ; }
		(n3Directive | statement)*	// Not a statementList: must end in a SEP
		{ endDocument() ; }
		EOF ;

		exception
		catch [RecognitionException ex]
		{ reportError(ex) ; throw ex ; }
		catch [TokenStreamRecognitionException ex]
		{ reportError(ex.recog) ; throw ex.recog ; }


n3Directive!: n3Directive0 SEP! ;

n3Directive0!:
		d:AT_PREFIX ns:nsprefix u:uriref
		{directive(#d, #ns, #u);}
		;

// A statement is "item verb item." with various
// syntactic sugar for multiple properties and objects.
// "verb" is a node and also the shorthand forms: 'a', => = etc
// "item" is just a node presently.

statement!
	: statement0 SEP! ;

statement0!
	: subj:subject propertyList[#subj] ;	

// List of statements without, necessarily, a final SEP.
// Possible empty
formulaList!
	: (statement0|n3Directive0) (SEP formulaList)?
	| ;

subject
	: item ;

propertyList![AST subj]
	: NAME_OP! anonnode[subj] propertyList[subj]
	| propValue[subj] (SEMI propertyList[subj])?
	| 		// void : allows for [ :a :b ].
	;

propValue [AST subj]
	:  v1:verb objectList[subj, #v1]
		// Reverse the subject and object
	|  v2:verbReverse subjectList[subj, #v2]
	;

subjectList![AST oldSub, AST prop]
	: obj:item { emitQuad(#obj, prop, oldSub) ; }
		(COMMA subjectList[oldSub, prop])? ;

objectList! [AST subj, AST prop]
	: obj:item { emitQuad(subj,prop,#obj) ; }
		(COMMA objectList[subj, prop])? ;


// Node, or path which evaluates to a node.
item
	: n:node
	(
		// Possible forward path 
		PATH! n1:node
		{ 
			AST a1 = #([ANON, genAnonId()]) ;
			emitQuad(#n, #n1, a1) ;
			#n = a1 ;
		}
	|
		// Possible backward path 
		RPATH! n2:node
		{
			AST a2 = #([ANON, genAnonId()]) ;
			emitQuad(a2, #n2, #n) ;
			#n = a2 ;
		}
	)*
	{ #item = #n ; } ;

	//:	node ;

testPoint!: v:verb { AntlrUtils.ast(System.out, #v) ; } ;


node
	:	qname
	|	uriref
	|	anonnode[null]
	|	literal
	|	kwTHIS
	|	variableDT
	;

// Keywords: do not use parser literals as things like URIREFs
// get misclassified.

kwTHIS: 	KW_THIS ;
kwOF!:		KW_OF   ;
kwHAS!:		KW_HAS  ;
kwA:    	KW_A    ;
kwIS!:    	KW_IS   ;



verb
	:  	item
	|   kwA
	|	EQUAL | ARROW_R | ARROW_L
	|   ARROW_PATH_L! node ARROW_PATH_R!			// Deprecated
	|	kwHAS! item
	;

// Verbs that reverse the sense of subject and object
verbReverse
	:	kwIS! n:node kwOF!
	;

// Label is set if we have seen a :- in a propertyList
anonnode[AST label]
    { String oldCxt = null ; String cxt = null ; }
		// BNode
	: LBRACK!
		{ if ( label == null )
	          label = #([ANON, genAnonId()]) ;
		  #anonnode = label ;
		}
		propertyList[label]
	  RBRACK!

		// Formula.
		// Push old formula context, generate new one.
	| LCURLY!
		{ oldCxt = currentFormula ;
		  if ( label == null )
	          label = #([FORMULA, genFormulaId()]) ;
	      cxt = label.getText() ;
		  currentFormula = cxt ;
		  startFormula(cxt) ;
		  #anonnode = label ;
		}
		formulaList
		{ endFormula(cxt) ; currentFormula = oldCxt ;}
	  RCURLY!

		// List syntax
	| LPAREN!
		damllist[label]
	  RPAREN!
	;

damllist[AST label]
	: i:item
	  {
	  	if ( label == null )
	          label = #([ANON, genAnonId()]) ;
		#damllist = label ;
	  }
	  n:damllist[null]	
	  {
	    emitQuad(label, #([URIREF, damlFirst]), #i);
		emitQuad(label, #([URIREF, damlRest]), #n) ;
	  }
	| { #damllist = #([URIREF, damlNil]); } // void - generate daml:nil
	;


	// Extract from the N-Tripes syntax
	//    literal ::= langString | datatypeString  
	//    langString ::= '"' string '"' ( '@' language )?  
	//    datatypeString ::= langString '^^' uriref 
	//    language ::= [a-z0-9]+ ('-' [a-z0-9]+ )? 
	// This is a permissive parse and allows the
	// lang and the datatype to be reversed.
	// Actually, the grammar allow two lang tags or twp datatype
	// specifications.

literal:
	s:STRING literalModifier { #literal.setType(LITERAL) ; } ;
	
literalModifier:
	literalModifier1 literalModifier1 ;
	
literalModifier1
	: (AT_LANG) => AT_LANG
	| (DATATYPE) => DATATYPE dt:datatype
		{ #literalModifier1 = #([DATATYPE], #dt) ; }
	|
	;

datatype:
	// Allowing a literal here is merely symetry.
	// We allow literals everywhere else.
	qname | uriref | variableNoDT | literal ;

// Restricted case for nsprefix.
nsprefix: ns:QNAME { ns.getText().endsWith(":") }? ;
	exception
	catch [SemanticException ex]
	{ 
		RecognitionException rEx = 
            new RecognitionException("Illegal prefix: '"+ns.getText()+"'") ; 
		rEx.line = lexer.getLine() ; rEx.column = lexer.getColumn() ; 
		throw rEx ;
	}

qname: QNAME ; //| QNAME_ANON ;

uriref: URIREF ;

// There are two types of variable: one where it can
// be followed by a datatype and one where it can't
// The only place where it can't is in a datatype slot itself
//  i.e. "111"^^?x

variableDT:
	v:UVAR (DATATYPE dt:datatype )? { #variableDT = #(#[UVAR, v.getText()], dt) ; } ;

variableNoDT:
	v:UVAR ;


// --------------------------------------------------------

class N3AntlrLexer extends Lexer ;
options {
	k=3;		// Beause of """ and '''

	// UTF-8 expanded to Java chars
	// NB: antlr 2.7.1 \uFFFF is the EOF char
	// Not fixed in antlr 2.7.2
	// N3 Comments cause OutOfMemoryError if FFFF used.
	// It does make for rather large tables
	charVocabulary= '\u0000'..'\uFFFE' ;
}

// Keywords are a little strange: (the letters for) a keyword
// could be part of a qname, either NS prefix or the local name.


QNAME_OR_KEYWORD_OR_NAME_OP
	// Order of syntactic predicates matters here

	// A qname (including the prefix used in @prefix)
	// and bNodes, using "_:"

	:	(NSNAME COLON LNAME)=>	NSNAME COLON LNAME	{ $setType(QNAME) ; }
	|	(COLON LNAME)=>			COLON LNAME			{ $setType(QNAME) ; }
	|	(NSNAME COLON )=>	    NSNAME COLON		{ $setType(QNAME) ; } 
	|	(COLON)=>			    COLON      			{ $setType(QNAME) ; } 

		// Named anon node
	|	(COLON '-') =>	":-"						{ $setType(NAME_OP) ; }

		// Keywords: uses fact keywords can not be last in file (must be a .)
	|   ("has"    NON_ANC)=>	"has"				{ $setType(KW_HAS) ; }
	|   ("of"     NON_ANC)=>	"of"				{ $setType(KW_OF) ; }
	|   ("this"   NON_ANC)=>	"this"				{ $setType(KW_THIS) ; }
	|   ("a"      NON_ANC)=>	"a"					{ $setType(KW_A) ; }
	|   ("is"     NON_ANC)=>	"is"				{ $setType(KW_IS) ; }
	;

// Need to check against RFC2396 (code from Xerces?)
// Need to differentiate from "<=" 

URI_OR_IMPLIES
		: (ARROW_L) => ARROW_L { $setType(ARROW_L) ; }
		| (ARROW_MEANS) =>  ARROW_MEANS { $setType(ARROW_MEANS) ; }
		| URIREF  { $setType(URIREF) ; }
		;
		
// Needs to be protected ... or the antlr compiler loops ...
protected
URIREF:
	'<'! (options{greedy=false;}: ~('\n'|'\r'))* '>'! ;

// RDFC2396 + chars for limited IRI compatibility 
// processing to check URIref syntax and chanracter sets

protected
URICHAR:
	ALPHANUMERIC |
	// RFC 2396 unreserved
	'-' | '_' | '.' | '!' | '~' | '*' | "'" | '(' | ')' |
	// RFC 2396 reserved
	';' | '/' | '?' | ':' | '@' | '&' | '=' | '+' | '$' | ',' |
	// unwise
	'{' | '}' | '|' | '\\' | '^' | '[' | ']' | '`' |
	// Delims: Escape and ref
	'%' | '#' | '"' |
	// Not RFC2396 but here to help IRI compliance
	' '
	;

UVAR: QUESTION (ALPHANUMERIC)+ ;


// To cases of @word: dire3ctives (@prefix) and language tags.
// Can't have a language of "prefix".

AT_WORD
	: (AT "prefix") => AT "prefix" { $setType(AT_PREFIX) ; }
	| (AT (ALPHA)) => AT a:(ALPHA)+ ("-" (ALPHA)*)?
		{ $setType(AT_LANG) ; }
	;


STRING: ( STRING1 | STRING2 ) ;


// Named characters
SEP			:	'.' ;
//protected
AT			:	'@'	;
LPAREN		:	'('	;
RPAREN		:	')'	;
LBRACK		:	'['	;
RBRACK		:	']'	;
LCURLY		:	'{'	;
RCURLY		:	'}'	;
SEMI		:	';'	;
COMMA		:	','	;
PATH		:	'!' ;
RPATH		:	'^' ;

DATATYPE	:	"^^"	;

protected
NAME_IT		:	":-"	;

protected
QUESTION	:	'?'	;

ARROW_R		:	"=>"	;
protected
ARROW_L		:	"<="	;
protected
ARROW_MEANS	:	"<=>"	;

ARROW_PATH_L	:	">-"	;
ARROW_PATH_R	:	"->"	;

EQUAL		:	"="	;

// Protected so it does not conflict with the
// QNAME_OR_PREFIX_OR_KEYWORD_OR_NAME_OP rule
// which tests for a leading COLON
protected
COLON		: 	':' 	;


// Single line comment.
SL_COMMENT:
	"#"
	// Uses the fact that the first clause is greedy, eating all
	// non-newlines, thus the end condition is optional newline
	// and it works at the end of the file.
	(~('\n'|'\r'))* (NL)?
	{$setType(Token.SKIP); }
    ;

// Windows: \r\n
// Unix:    \n
// Mac:     \r

protected NL1: "\r\n"  { newline(); } ;
protected NL2: "\n"  { newline(); } ;
protected NL3: "\r"  { newline(); } ;
// Hard work! This makes NL's in ''' and """ strings work
protected NL: (NL1) => NL1 | (NL2) => NL2 | (NL3) => NL3;

// Ignore whitespace.  Not protected as SKIP is passed to parser.
WS:
	( ' ' | '\t' | '\f' | NL )
	{ $setType(Token.SKIP); }
	;

protected
ALPHA: ('A'..'Z')|('a'..'z') ;

protected
ALPHANUMERIC: (ALPHA|'0'..'9') ;

protected
NON_ANC:	~('A'..'Z'|'a'..'z'|'0'..'9'|':') ;

// Namespace prefix name: include bNode ids.
protected
NSNAME: (ALPHANUMERIC|'_'|'-')+ ;

protected
// Not '-' because it confuses with the name operator :-
// Also, N3 does not allow '.' in the localname part of a qname (although
// that is XML-legal) because N3 uses . as the end of statement separator
// or as a path separator.
// See N3JenaWriter, which avoids outputing qnames with a '.' in them.
LNAME: (ALPHANUMERIC|'_') (ALPHANUMERIC|'_'|'-')* ;


protected
STRING1
    : (QUOTE3S)=>
      // Needs k=3: if k less a lexer is generated but fails : see antlr doc
	  QUOTE3S!
	  (options{greedy=false;}: (NL)=>NL | ESCAPE | ~('\\'))*
	  QUOTE3S!
	| '\''! (options{greedy=false;}: ESCAPE  | ~'\\')* '\''! ;

protected
STRING2
	: (QUOTE3D)=>
	  QUOTE3D!
	  (options{greedy=false;}: (NL)=>NL | ESCAPE | ~('\\'))*
	  QUOTE3D!
	| '"'! (options{greedy=false;}: ESCAPE  | ~'\\')* '"'!
	;

protected
QUOTE3S: "'''" ;			// 3 single quotes
protected
QUOTE3D: '"' '"' '"' ;		// 3 double quotes

// @@Needs work
protected
ESCAPE: 
		'\\'!
		( (ESC_CHAR) => ESC_CHAR
		| ch:.	{ $setText("\\"+ch) ; }
		) ;


protected
ESC_CHAR:
		( 'n'  { $setText("\n") ; }
		| 'r'  { $setText("\r") ; }
		| 'b'  { $setText("\b") ; }
		| 't'  { $setText("\t") ; }
		| 'f'  { $setText("\f") ; }
		| 'v'  { $setText("\f") ; }
		| 'a'  { $setText("\007") ; }
			// UNICODE escape
			// @@TODO
		//| 'u'  { $setText("\\u") ; }
		| '"'  { $setText("\"") ; }
		| '\\' { $setText("\\") ; }
		| '\'' { $setText("'") ; }
		)
		; 

/*
 *  (c) Copyright Hewlett-Packard Company 2002-2003
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

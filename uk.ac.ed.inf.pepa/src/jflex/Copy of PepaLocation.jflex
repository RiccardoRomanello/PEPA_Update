/* 20070222 Brand new implementation of the PEPA scanner.
 *
 * Author: Mirco Tribastone -mtribast
 */

package uk.ac.ed.inf.pepa.parsing.internal;

import java_cup.runtime.*;
import java.io.Reader;
import java.io.InputStreamReader;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;

%%

/* Activates Line, Character and Column Counting */
%line
%char
%column

/* Overrides default class name for this lexer, namely: Yylex*/
%class PepaLexer
%public

/* Informs JFlex that we will be using a CUP generated parser, with Symbol file called PepaToken */
%cupsym PepaToken
%cup
%unicode

%{
	  int getCurrentLineNumber(){
	    return yyline;
	  }
	
	  int getCurrentColumn(){
	    return yycolumn;
	  }
	  
	  int getCurrentChar() {
	  	return yychar;
	  }
	  
	  public PepaLexer(Reader reader, ComplexSymbolFactory sf) {
	  	this(reader);
	  	symbolFactory = sf;
	  	
	  }
	  public PepaLexer(ComplexSymbolFactory sf){
	        this(new InputStreamReader(System.in));
	        symbolFactory = sf;
	  }
  
	private ComplexSymbolFactory symbolFactory;
    
    public Symbol symbol(String name, int code){
        return symbolFactory.newSymbol(name, code,new Location(yyline+1,yycolumn+1-yylength()),new Location(yyline+1,yycolumn+1));
    }
    public Symbol symbol(String name, int code, Object lexem){
    
        return symbolFactory.newSymbol(name, code, new Location(yyline+1, yycolumn +1), new Location(yyline+1,yycolumn+yylength()), lexem);
    }

%} 

%eofval{
    return symbolFactory.newSymbol("EOF",PepaToken.EOF);
%eofval}


/* Line terminators and white spaces definitions */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

/* Passive multiplier is a natural number, according to Jane's thesis */
PASSIVE_MULTIPLIER = 0*[1-9][0-9]*
DOUBLE_NUMBER=[0-9]+(\.[0-9]*)?

%% 

<YYINITIAL> {
   /* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}

/* Keywords Section */
<YYINITIAL> { 
	"," 	{ return symbol("COMMA", PepaToken.COMMA); }
	
	"." 	{ return symbol("DOT", PepaToken.DOT); }
	
	"tau"   { return symbol("TAU", PepaToken.TAU, yytext()); }

	"infty" { return symbol("TOP", PepaToken.TOP); }
	
	"T" 	{ return symbol("TOP", PepaToken.TOP); } 
	
	"(" 	{ return symbol("LPAREN", PepaToken.LPAREN); }
}

<YYINITIAL> {PASSIVE_MULTIPLIER} { 
	return symbol("PASSIVE_MULTIPLIER", PepaToken.PASSIVE_MULTIPLIER, 
				Integer.valueOf(yytext()));
}

<YYINITIAL> ")" { return symbol("RPARENT", PepaToken.RPAREN); }

<YYINITIAL> ";" { 
                  return symbol("SEMI", PepaToken.SEMI); }

<YYINITIAL> "=" { return symbol("EQUALS", PepaToken.EQUALS); }

<YYINITIAL> ":=" { return symbol("RATE_EQUALS", PepaToken.RATE_EQUALS); }

<YYINITIAL> "||" { return symbol("PAR", PepaToken.PAR); }

<YYINITIAL> "<" { return symbol("LCOOP", PepaToken.LCOOP); }

<YYINITIAL> ">" { return symbol("RCOOP", PepaToken.RCOOP); }

<YYINITIAL> "{" { return symbol("LSET", PepaToken.LSET); }

<YYINITIAL> "}" { return symbol("RSET", PepaToken.RSET); }

<YYINITIAL> "*" { return symbol("MULT", PepaToken.MULT); }

<YYINITIAL> "-" { return symbol("MINUS", PepaToken.MINUS); }

<YYINITIAL> "+" { return symbol("SUM", PepaToken.SUM); }

<YYINITIAL> "/" { return symbol("DIVIDE", PepaToken.DIVIDE); }

<YYINITIAL> "#" { return symbol("HASH", PepaToken.HASH); }

<YYINITIAL> "[" { return symbol("LSQUARE", PepaToken.LSQUARE); }

<YYINITIAL> "]" { return symbol("RSQUARE", PepaToken.RSQUARE); }

<YYINITIAL> "%" { return symbol("PERC", PepaToken.PERC); }

<YYINITIAL> {DOUBLE_NUMBER} {
        
	return symbol("DOUBLE_NUMBER", PepaToken.DOUBLE_NUMBER,Double.valueOf(yytext()));
}

<YYINITIAL> [:lowercase:][:jletterdigit:]* {
        
	return symbol("ACTIVITY_STRN", PepaToken.ACTIVITY_STRN,yytext());
}	

<YYINITIAL> [:uppercase:][:jletterdigit:]* {

	return symbol("PROCESS_STRN", PepaToken.PROCESS_STRN,yytext());
}

. {
	return symbol("ERROR", PepaToken.error,"Illegal character: <" + yytext() + ">");
}
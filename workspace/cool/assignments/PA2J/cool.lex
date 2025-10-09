/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();
    boolean string_too_long = false;
    boolean null_in_string = false;
    boolean escaped_null_in_string = false;

    // For nested block comments
    int comment_depth = 0;

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yy_lexical_state) {
    case STRING: 
      yy_lexical_state = YYINITIAL;
      return new Symbol(TokenConstants.ERROR, "EOF in string constant");
    case COMMENT:
      yy_lexical_state = YYINITIAL;
      return new Symbol(TokenConstants.ERROR, "EOF in comment");
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
	/* If necessary, add code for other states here, e.g:
	   case COMMENT:
	   ...
	   break;
	*/
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%unicode
%cup

%state STRING
%state COMMENT

%%

<YYINITIAL>[ \t\f\r\u000B]   { /*Do nothing because is whitespace or special character*/ }
<YYINITIAL>[\n]   { curr_lineno++; }

<YYINITIAL>[0-9]+   {
  /* Define an integer value */
  return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext()));
}

<YYINITIAL>"=>"   {
  return new Symbol(TokenConstants.DARROW);
}



<YYINITIAL> [cC][aA][sS][eE] { return new Symbol(TokenConstants.CASE); }
<YYINITIAL> [cC][lL][aA][sS][sS] { return new Symbol(TokenConstants.CLASS); }
<YYINITIAL> [eE][lL][sS][eE] { return new Symbol(TokenConstants.ELSE); }
<YYINITIAL> [eE][sS][aA][cC] { return new Symbol(TokenConstants.ESAC); }
<YYINITIAL> [fF][iI] { return new Symbol(TokenConstants.FI); }
<YYINITIAL> [iI][fF] { return new Symbol(TokenConstants.IF); }
<YYINITIAL> [iI][nN] { return new Symbol(TokenConstants.IN); }
<YYINITIAL> [iI][nN][hH][eE][rR][iI][tT][sS] { return new Symbol(TokenConstants.INHERITS); }
<YYINITIAL> [iI][sS][vV][oO][iI][dD] { return new Symbol(TokenConstants.ISVOID); }
<YYINITIAL> [lL][eE][tT] { return new Symbol(TokenConstants.LET); }
<YYINITIAL> [lL][oO][oO][pP] { return new Symbol(TokenConstants.LOOP); }
<YYINITIAL> [nN][eE][wW] { return new Symbol(TokenConstants.NEW); }
<YYINITIAL> [nN][oO][tT] { return new Symbol(TokenConstants.NOT); }
<YYINITIAL> [oO][fF] { return new Symbol(TokenConstants.OF); }
<YYINITIAL> [pP][oO][oO][lL] { return new Symbol(TokenConstants.POOL); }
<YYINITIAL> [tT][hH][eE][nN] { return new Symbol(TokenConstants.THEN); }
<YYINITIAL> [wW][hH][iI][lL][eE] { return new Symbol(TokenConstants.WHILE); }

<YYINITIAL> [t][rR][uU][eE] { return new Symbol(TokenConstants.BOOL_CONST, Boolean.TRUE); }
<YYINITIAL> [f][aA][lL][sS][eE] { return new Symbol(TokenConstants.BOOL_CONST, Boolean.FALSE); }

<YYINITIAL>[A-Z][A-Za-z0-9_]* { return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
<YYINITIAL>[a-z][A-Za-z0-9_]* { return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }

<YYINITIAL>"<=" { return new Symbol(TokenConstants.LE); }
<YYINITIAL>"<-" { return new Symbol(TokenConstants.ASSIGN); }
<YYINITIAL>"+"  { return new Symbol(TokenConstants.PLUS); }
<YYINITIAL>"-"  { return new Symbol(TokenConstants.MINUS); }
<YYINITIAL>"*"  { return new Symbol(TokenConstants.MULT); }
<YYINITIAL>"/"  { return new Symbol(TokenConstants.DIV); }
<YYINITIAL>"<"  { return new Symbol(TokenConstants.LT); }
<YYINITIAL>"="  { return new Symbol(TokenConstants.EQ); }
<YYINITIAL>"~"  { return new Symbol(TokenConstants.NEG); }
<YYINITIAL>"("  { return new Symbol(TokenConstants.LPAREN); }
<YYINITIAL>")"  { return new Symbol(TokenConstants.RPAREN); }
<YYINITIAL>"{"  { return new Symbol(TokenConstants.LBRACE); }
<YYINITIAL>"}"  { return new Symbol(TokenConstants.RBRACE); }
<YYINITIAL>";"  { return new Symbol(TokenConstants.SEMI); }
<YYINITIAL>":"  { return new Symbol(TokenConstants.COLON); }
<YYINITIAL>"."  { return new Symbol(TokenConstants.DOT); }
<YYINITIAL>","  { return new Symbol(TokenConstants.COMMA); }
<YYINITIAL>"@"  { return new Symbol(TokenConstants.AT); }

<YYINITIAL>"--"[^\n]* { /* skip till end of line */ }

<YYINITIAL>"(*" {
  comment_depth = 1;
  yybegin(COMMENT);
}

<YYINITIAL>"*)" {
  return new Symbol(TokenConstants.ERROR, "Unmatched *)");
}

<COMMENT>"(*" { comment_depth++; }
<COMMENT>"*)" {
  comment_depth--;
  if (comment_depth == 0) { yybegin(YYINITIAL); }
}
<COMMENT>\n { curr_lineno++; }
<COMMENT>\r|. { /* consume */ }

<YYINITIAL>\" {
  string_buf.setLength(0);
  string_too_long = false;
  null_in_string = false;
  escaped_null_in_string = false;
  yybegin(STRING);
}

<STRING>\" {
  if (string_too_long) {
    yybegin(YYINITIAL);
    return new Symbol(TokenConstants.ERROR, "String constant too long");
  }

  if (null_in_string){
    yybegin(YYINITIAL);
    return new Symbol(TokenConstants.ERROR, "String contains null character");
  }

  if (escaped_null_in_string){
    yybegin(YYINITIAL);
    return new Symbol(TokenConstants.ERROR, "String contains escaped null character.");
  }

  yybegin(YYINITIAL);
  return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString()));

}

<STRING>\\[btnf\\\"] {
  char c = yytext().charAt(1);
  switch (c) {
    case 'b': string_buf.append('\b'); break;
    case 't': string_buf.append('\t'); break;
    case 'n': string_buf.append('\n'); break;
    case 'f': string_buf.append('\f'); break;
    case '\\': string_buf.append('\\'); break;
    case '"': string_buf.append('"'); break;
  }
  if (string_buf.length() >= MAX_STR_CONST) { string_too_long = true; }
}

<STRING>\\\u0000 {
  escaped_null_in_string = true;
}

<STRING>\\. {
  string_buf.append(yytext().charAt(1));
  if (string_buf.length() >= MAX_STR_CONST) { string_too_long = true; }
}

<STRING>\u0000 {
  null_in_string = true;
}

<STRING>[^\\\"\n\u0000]+ {
  string_buf.append(yytext());
  if (string_buf.length() >= MAX_STR_CONST) { string_too_long = true; }
}

<STRING>\\\n {
  curr_lineno++;
  string_buf.append('\n');
  if (string_buf.length() >= MAX_STR_CONST) { string_too_long = true; }
}

<STRING>\n {
  curr_lineno++;
  yybegin(YYINITIAL);
  return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
}



.   { /* Any other single char is an error */
        return new Symbol(TokenConstants.ERROR, yytext());
  }


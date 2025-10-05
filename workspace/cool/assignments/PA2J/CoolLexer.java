/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */
    // Max size of string constants
    static int MAX_STR_CONST = 1025;
    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();
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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NOT_ACCEPT,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NOT_ACCEPT,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NOT_ACCEPT,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NOT_ACCEPT,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NOT_ACCEPT,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NOT_ACCEPT,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NOT_ACCEPT,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NOT_ACCEPT,
		/* 88 */ YY_NOT_ACCEPT,
		/* 89 */ YY_NOT_ACCEPT,
		/* 90 */ YY_NOT_ACCEPT,
		/* 91 */ YY_NOT_ACCEPT,
		/* 92 */ YY_NOT_ACCEPT,
		/* 93 */ YY_NOT_ACCEPT,
		/* 94 */ YY_NOT_ACCEPT,
		/* 95 */ YY_NOT_ACCEPT,
		/* 96 */ YY_NOT_ACCEPT,
		/* 97 */ YY_NOT_ACCEPT,
		/* 98 */ YY_NOT_ACCEPT,
		/* 99 */ YY_NOT_ACCEPT,
		/* 100 */ YY_NOT_ACCEPT,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NOT_ACCEPT,
		/* 103 */ YY_NOT_ACCEPT,
		/* 104 */ YY_NOT_ACCEPT,
		/* 105 */ YY_NOT_ACCEPT,
		/* 106 */ YY_NOT_ACCEPT,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"42:9,1,2,42,1:2,42:18,1,42:15,3:10,42:3,4,5,42:2,26,27,28,29,30,11,27,31,32" +
",27:2,33,27,34,35,36,27,37,38,16,39,17,40,27:3,42:4,41,42,7,41,6,20,9,25,41" +
",14,12,41:2,10,41,13,19,21,41,15,8,23,24,18,22,41:3,42:5,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,130,
"0,1:3,2,3,4,1,5,1,6,1:8,5,1:7,7,1,8,1,5,9,5:8,1,5:5,10,11,12,13,14,15,16,17" +
",18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42" +
",43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67" +
",68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,5,88,89,90,91")[0];

	private int yy_nxt[][] = unpackFromString(92,43,
"1,2,3,4,5,28,48,28:2,51,54,6,57,60,28:2,29,125,2,63,28,66,69,72,28,75,125:2" +
",126,125,127,125,49,101,107,52,128,125:3,129,28:2,-1:46,4,-1:44,7,-1:40,125" +
",-1:2,125:6,8,125:19,8,125:9,-1:4,125,-1:2,125:36,-1:15,104,-1:16,104,-1:19" +
",77,-1:29,77,-1:7,125,-1:2,125:8,108,125:16,108,125:10,-1:4,125,-1:2,125:8," +
"119,125:16,119,125:10,-1:8,79,-1:18,79,-1:23,27,-1:2,47,-1:15,27,-1:6,47,-1" +
":12,125,-1:2,125:2,113,125:2,31,125,32,125:11,31,125:8,32,125:3,113,125:3,-" +
"1:17,12,-1:6,12,-1:27,102,-1,100,-1:22,100,-1:4,102,-1:7,125,-1:2,125:5,33," +
"125:13,33,125:16,-1:20,85,-1:15,85,-1:16,50,-1:9,53,-1:10,50,-1:4,53,-1:10," +
"125,-1:2,125:10,34,125:6,34,125:18,-1:18,87:2,-1:32,56,-1:2,9,-1,10,-1:11,9" +
",-1:8,10,-1:3,56,-1:7,125,-1:2,125:16,35,125:17,35,125,-1:23,13,-1:17,13,-1" +
":11,59,-1:9,62,-1:10,59,-1:4,62,-1:10,125,-1:2,125:10,36,125:6,36,125:18,-1" +
":17,14,-1:6,14,-1:30,11,-1:13,11,-1:20,125,-1:2,125:7,19,125:20,19,125:7,-1" +
":13,105,-1:19,105,-1:29,103,-1:15,103,-1:10,125,-1:2,125:3,37,125:20,37,125" +
":11,-1:10,89,-1:20,89,-1:26,65,-1:16,65,-1:14,125,-1:2,38,125:21,38,125:13," +
"-1:25,90,-1:14,90,-1:17,68,71,-1:15,68,-1:5,71,-1:8,125,-1:2,125:3,39,125:2" +
"0,39,125:11,-1:11,106,-1:22,106,-1:16,74,-1:4,30,-1:13,74,-1:5,30,-1:13,125" +
",-1:2,125:15,40,125:14,40,125:5,-1:10,15,-1:20,15,-1:15,125,-1:2,125:4,42,1" +
"25:22,42,125:8,-1:9,91,-1:29,91,-1:7,125,-1:2,125:2,43,125:29,43,125:3,-1:7" +
",16,-1:21,16,-1:17,125,-1:2,125:3,44,125:20,44,125:11,-1:10,17,-1:20,17,-1:" +
"15,125,-1:2,125:14,45,125:8,45,125:12,-1:22,18,-1:14,18,-1:9,125,-1:2,125:2" +
",46,125:29,46,125:3,-1:20,92,-1:15,92,-1:17,20,-1:22,20,-1:22,41,-1:20,41,-" +
"1:17,21,-1:20,21,-1:20,22,-1:29,22,-1:16,96,-1:19,96,-1:25,97,-1:21,97,-1:1" +
"4,23,-1:20,23,-1:21,24,-1:20,24,-1:32,25,-1:8,25,-1:25,98,-1:19,98,-1:26,99" +
",-1:6,99,-1:27,26,-1:29,26,-1:12,83,-1:29,83,-1:7,125,-1:2,125:3,55,125:9,1" +
"14,125:10,55,125:4,114,125:6,-1:8,81,-1:18,81,-1:35,88,-1:15,88,-1:16,93,-1" +
":20,93,-1:22,94,-1:22,94,-1:17,95,-1:29,95,-1:7,125,-1:2,125:3,58,125:9,61," +
"125:10,58,125:4,61,125:6,-1:4,125,-1:2,125:3,64,125:20,64,125:11,-1:4,125,-" +
"1:2,125:2,67,125:29,67,125:3,-1:4,125,-1:2,125,117,125:18,117,125:15,-1:4,1" +
"25,-1:2,125,70,125:18,70,125:15,-1:4,125,-1:2,125:2,73,125:29,73,125:3,-1:4" +
",125,-1:2,125:11,118:2,125:23,-1:4,125,-1:2,125:13,76,125:15,76,125:6,-1:4," +
"125,-1:2,125:13,78,125:15,78,125:6,-1:4,125,-1:2,125:6,120,125:19,120,125:9" +
",-1:4,125,-1:2,125:2,80,125:29,80,125:3,-1:4,125,-1:2,125:13,121,125:15,121" +
",125:6,-1:4,125,-1:2,125:3,122,125:20,122,125:11,-1:4,125,-1:2,125:4,82,125" +
":22,82,125:8,-1:4,125,-1:2,125:6,84,125:19,84,125:9,-1:4,125,-1:2,125:9,123" +
",125:21,123,125:4,-1:4,125,-1:2,125:6,124,125:19,124,125:9,-1:4,125,-1:2,12" +
"5:10,86,125:6,86,125:18,-1:4,125,-1:2,125,109,125:2,110,125:15,109,125:6,11" +
"0,125:8,-1:4,125,-1:2,125:2,111,125,112,125:22,112,125:4,111,125:3,-1:4,125" +
",-1:2,125:13,115,125:15,115,125:6,-1:4,125,-1:2,125:8,116,125:16,116,125:10" +
",-1");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */
    switch(yy_lexical_state) {
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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ /*Do nothing because is whitespace or special character*/ }
					case -3:
						break;
					case 3:
						{ curr_lineno++; }
					case -4:
						break;
					case 4:
						{
  /* Define an integer value */
  return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext()));
}
					case -5:
						break;
					case 5:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -6:
						break;
					case 6:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -7:
						break;
					case 7:
						{
  return new Symbol(TokenConstants.DARROW);
}
					case -8:
						break;
					case 8:
						{ return new Symbol(TokenConstants.FI); }
					case -9:
						break;
					case 9:
						{ return new Symbol(TokenConstants.IF); }
					case -10:
						break;
					case 10:
						{ return new Symbol(TokenConstants.IN); }
					case -11:
						break;
					case 11:
						{ return new Symbol(TokenConstants.OF); }
					case -12:
						break;
					case 12:
						{ return new Symbol(TokenConstants.LET); }
					case -13:
						break;
					case 13:
						{ return new Symbol(TokenConstants.NEW); }
					case -14:
						break;
					case 14:
						{ return new Symbol(TokenConstants.NOT); }
					case -15:
						break;
					case 15:
						{ return new Symbol(TokenConstants.CASE); }
					case -16:
						break;
					case 16:
						{ return new Symbol(TokenConstants.ESAC); }
					case -17:
						break;
					case 17:
						{ return new Symbol(TokenConstants.ELSE); }
					case -18:
						break;
					case 18:
						{ return new Symbol(TokenConstants.LOOP); }
					case -19:
						break;
					case 19:
						{ return new Symbol(TokenConstants.THEN); }
					case -20:
						break;
					case 20:
						{ return new Symbol(TokenConstants.POOL); }
					case -21:
						break;
					case 21:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.TRUE); }
					case -22:
						break;
					case 22:
						{ return new Symbol(TokenConstants.CLASS); }
					case -23:
						break;
					case 23:
						{ return new Symbol(TokenConstants.WHILE); }
					case -24:
						break;
					case 24:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.FALSE); }
					case -25:
						break;
					case 25:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -26:
						break;
					case 26:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -27:
						break;
					case 28:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -28:
						break;
					case 29:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -29:
						break;
					case 30:
						{ return new Symbol(TokenConstants.FI); }
					case -30:
						break;
					case 31:
						{ return new Symbol(TokenConstants.IF); }
					case -31:
						break;
					case 32:
						{ return new Symbol(TokenConstants.IN); }
					case -32:
						break;
					case 33:
						{ return new Symbol(TokenConstants.OF); }
					case -33:
						break;
					case 34:
						{ return new Symbol(TokenConstants.LET); }
					case -34:
						break;
					case 35:
						{ return new Symbol(TokenConstants.NEW); }
					case -35:
						break;
					case 36:
						{ return new Symbol(TokenConstants.NOT); }
					case -36:
						break;
					case 37:
						{ return new Symbol(TokenConstants.CASE); }
					case -37:
						break;
					case 38:
						{ return new Symbol(TokenConstants.ESAC); }
					case -38:
						break;
					case 39:
						{ return new Symbol(TokenConstants.ELSE); }
					case -39:
						break;
					case 40:
						{ return new Symbol(TokenConstants.LOOP); }
					case -40:
						break;
					case 41:
						{ return new Symbol(TokenConstants.THEN); }
					case -41:
						break;
					case 42:
						{ return new Symbol(TokenConstants.POOL); }
					case -42:
						break;
					case 43:
						{ return new Symbol(TokenConstants.CLASS); }
					case -43:
						break;
					case 44:
						{ return new Symbol(TokenConstants.WHILE); }
					case -44:
						break;
					case 45:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -45:
						break;
					case 46:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -46:
						break;
					case 48:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -47:
						break;
					case 49:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -48:
						break;
					case 51:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -49:
						break;
					case 52:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -50:
						break;
					case 54:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -51:
						break;
					case 55:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -52:
						break;
					case 57:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -53:
						break;
					case 58:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -54:
						break;
					case 60:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -55:
						break;
					case 61:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -56:
						break;
					case 63:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -57:
						break;
					case 64:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -58:
						break;
					case 66:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -59:
						break;
					case 67:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -60:
						break;
					case 69:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -61:
						break;
					case 70:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -62:
						break;
					case 72:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -63:
						break;
					case 73:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -64:
						break;
					case 75:
						{ /* This rule should be the very last
         in your lexical specification and
         will match match everything not
         matched by other lexical rules. */
        System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
  }
					case -65:
						break;
					case 76:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -66:
						break;
					case 78:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -67:
						break;
					case 80:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -68:
						break;
					case 82:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -69:
						break;
					case 84:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -70:
						break;
					case 86:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -71:
						break;
					case 101:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -72:
						break;
					case 107:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -73:
						break;
					case 108:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -74:
						break;
					case 109:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -75:
						break;
					case 110:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -76:
						break;
					case 111:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -77:
						break;
					case 112:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -78:
						break;
					case 113:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -79:
						break;
					case 114:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -80:
						break;
					case 115:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -81:
						break;
					case 116:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -82:
						break;
					case 117:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -83:
						break;
					case 118:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -84:
						break;
					case 119:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -85:
						break;
					case 120:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -86:
						break;
					case 121:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -87:
						break;
					case 122:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -88:
						break;
					case 123:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -89:
						break;
					case 124:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -90:
						break;
					case 125:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -91:
						break;
					case 126:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -92:
						break;
					case 127:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -93:
						break;
					case 128:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -94:
						break;
					case 129:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.stringtable.addString(yytext())); }
					case -95:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}

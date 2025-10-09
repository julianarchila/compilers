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
	private final int STRING = 1;
	private final int YYINITIAL = 0;
	private final int COMMENT = 2;
	private final int yy_state_dtrans[] = {
		0,
		62,
		84
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
		/* 27 */ YY_NO_ANCHOR,
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
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NOT_ACCEPT,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
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
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"63,58:8,59,2,59:2,1,58:18,59,58,60,58:5,49,50,46,45,56,44,55,47,3:10,54,53," +
"43,4,5,58,57,25,26,27,28,29,11,26,30,31,26:2,32,26,33,34,35,26,36,37,16,38," +
"39,40,26:3,58,61,58:2,41,58,7,62,6,19,9,24,42,14,12,42:2,10,42,13,18,20,42," +
"15,8,22,23,17,21,42:3,51,58,52,48,58,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,169,
"0,1:3,2,3,1,4,5,6,7,1,8,1:2,9,1:10,10,11,12,11,1:2,13,1:2,11:7,10,11:7,14,1" +
":11,15,16,17,18,11,10,19,10:8,11,10:5,20,21,22,23,24,25,26,27,28,29,30,31,3" +
"2,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,5" +
"7,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,8" +
"2,83,84,85,86,87,88,89,90,91,92,93,94,95,11,10,96,97,98,99,100,101,102,103")[0];

	private int yy_nxt[][] = unpackFromString(104,64,
"1,2,3,4,5,6,7,159:2,161,118,8,64,120,159:2,65,159,85,159,163,165,167,159,88" +
",160:2,162,160,164,160,86,119,121,89,166,160:4,168,6,159,9,10,11,12,13,14,1" +
"5,16,17,18,19,20,21,22,23,6,2,24,6,159,6,-1:67,4,-1:65,25,-1:61,159,-1:2,15" +
"9,122,159:2,124,159:14,122,159:6,124,159:10,-1:19,159,-1:4,160,-1:2,160:6,2" +
"6,160:18,26,160:11,-1:19,160,-1:5,30,-1:39,31,-1:63,32,-1:69,33,-1:59,34,-1" +
":20,160,-1:2,160:37,-1:19,160,-1:4,159,-1:2,159:37,-1:19,159,-1:4,159,-1:2," +
"159:8,148,159:15,148,159:12,-1:19,159,-1:2,32,-1,32:61,-1,50,-1,50:57,-1:2," +
"50,-1,1,50,51,50:57,52,63,50,53,-1:2,54,55:10,56,55:8,56,55,56,55:35,56:3,5" +
"7,-1:3,159,-1:2,159:2,132,159:2,27,159,28,159:10,27,159:8,28,159:3,132,159:" +
"5,-1:19,159,-1:4,160,-1:2,160:8,123,160:15,123,160:12,-1:19,160,-1:4,160,-1" +
":2,160:8,145,160:15,145,160:12,-1:19,160,-1:51,60,-1:13,1,-1,58,59:43,83,59" +
":2,87,59:14,-1:3,159,-1:2,159:5,29,159:12,29,159:18,-1:19,159,-1:4,160,-1:2" +
",160:2,133,160:2,67,160,68,160:10,67,160:8,68,160:3,133,160:5,-1:19,160,-1:" +
"47,61,-1:20,159,-1:2,159,142,159:4,66,159:12,142,159:5,66,159:11,-1:19,159," +
"-1:4,160,-1:2,160:5,69,160:12,69,160:18,-1:19,160,-1:4,159,-1:2,159:10,35,1" +
"59:5,35,159:20,-1:19,159,-1:4,160,-1:2,160:10,70,160:5,70,160:20,-1:19,160," +
"-1:4,159,-1:2,159:15,36,159:18,36,159:2,-1:19,159,-1:4,160,-1:2,160:15,71,1" +
"60:18,71,160:2,-1:19,160,-1:4,159,-1:2,159:10,37,159:5,37,159:20,-1:19,159," +
"-1:4,160,-1:2,160:10,72,160:5,72,160:20,-1:19,160,-1:4,159,-1:2,159:3,38,15" +
"9:19,38,159:13,-1:19,159,-1:4,160,-1:2,160:7,42,160:19,42,160:9,-1:19,160,-" +
"1:4,159,-1:2,39,159:20,39,159:15,-1:19,159,-1:4,160,-1:2,160:3,73,160:19,73" +
",160:13,-1:19,160,-1:4,159,-1:2,159:3,40,159:19,40,159:13,-1:19,159,-1:4,16" +
"0,-1:2,74,160:20,74,160:15,-1:19,160,-1:4,159,-1:2,159:14,41,159:14,41,159:" +
"7,-1:19,159,-1:4,160,-1:2,160:3,75,160:19,75,160:13,-1:19,160,-1:4,159,-1:2" +
",159:4,43,159:21,43,159:10,-1:19,159,-1:4,160,-1:2,160:14,76,160:14,76,160:" +
"7,-1:19,160,-1:4,159,-1:2,159:7,77,159:19,77,159:9,-1:19,159,-1:4,160,-1:2," +
"160:4,78,160:21,78,160:10,-1:19,160,-1:4,159,-1:2,159:3,44,159:19,44,159:13" +
",-1:19,159,-1:4,160,-1:2,160:2,79,160:28,79,160:5,-1:19,160,-1:4,159,-1:2,1" +
"59:2,45,159:28,45,159:5,-1:19,159,-1:4,160,-1:2,160:3,80,160:19,80,160:13,-" +
"1:19,160,-1:4,159,-1:2,159:3,46,159:19,46,159:13,-1:19,159,-1:4,160,-1:2,16" +
"0:13,81,160:8,81,160:14,-1:19,160,-1:4,159,-1:2,159:3,47,159:19,47,159:13,-" +
"1:19,159,-1:4,160,-1:2,160:2,82,160:28,82,160:5,-1:19,160,-1:4,159,-1:2,159" +
":13,48,159:8,48,159:14,-1:19,159,-1:4,159,-1:2,159:2,49,159:28,49,159:5,-1:" +
"19,159,-1:4,159,-1:2,159:3,90,159:8,130,159:10,90,159:4,130,159:8,-1:19,159" +
",-1:4,160,-1:2,160:3,91,160:8,135,160:10,91,160:4,135,160:8,-1:19,160,-1:4," +
"159,-1:2,159:3,92,159:8,94,159:10,92,159:4,94,159:8,-1:19,159,-1:4,160,-1:2" +
",160:3,93,160:8,95,160:10,93,160:4,95,160:8,-1:19,160,-1:4,159,-1:2,159:2,9" +
"6,159:28,96,159:5,-1:19,159,-1:4,160,-1:2,160:3,97,160:19,97,160:13,-1:19,1" +
"60,-1:4,159,-1:2,159,144,159:17,144,159:17,-1:19,159,-1:4,160,-1:2,160:2,99" +
",160:28,99,160:5,-1:19,160,-1:4,159,-1:2,159,98,159:17,98,159:17,-1:19,159," +
"-1:4,160,-1:2,160,141,160:17,141,160:17,-1:19,160,-1:4,159,-1:2,159:2,100,1" +
"59:28,100,159:5,-1:19,159,-1:4,160,-1:2,160,101,160:17,101,160:17,-1:19,160" +
",-1:4,159,-1:2,159:12,102,159:15,102,159:8,-1:19,159,-1:4,160,-1:2,160:2,10" +
"3,160:28,103,160:5,-1:19,160,-1:4,159,-1:2,159:11,146,159:21,146,159:3,-1:1" +
"9,159,-1:4,160,-1:2,160:11,143,160:21,143,160:3,-1:19,160,-1:4,159,-1:2,159" +
":12,104,159:15,104,159:8,-1:19,159,-1:4,160,-1:2,160:12,105,160:15,105,160:" +
"8,-1:19,160,-1:4,159,-1:2,159:6,150,159:18,150,159:11,-1:19,159,-1:4,160,-1" +
":2,160:12,107,160:15,107,160:8,-1:19,160,-1:4,159,-1:2,159:3,106,159:19,106" +
",159:13,-1:19,159,-1:4,160,-1:2,160:6,147,160:18,147,160:11,-1:19,160,-1:4," +
"159,-1:2,159:17,108,159:14,108,159:4,-1:19,159,-1:4,160,-1:2,160:2,109,160:" +
"28,109,160:5,-1:19,160,-1:4,159,-1:2,159:4,152,159:21,152,159:10,-1:19,159," +
"-1:4,160,-1:2,160:12,149,160:15,149,160:8,-1:19,160,-1:4,159,-1:2,159:2,110" +
",159:28,110,159:5,-1:19,159,-1:4,160,-1:2,160:3,151,160:19,151,160:13,-1:19" +
",160,-1:4,159,-1:2,159:12,154,159:15,154,159:8,-1:19,159,-1:4,160,-1:2,160:" +
"4,111,160:21,111,160:10,-1:19,160,-1:4,159,-1:2,159:3,156,159:19,156,159:13" +
",-1:19,159,-1:4,160,-1:2,160:6,113,160:18,113,160:11,-1:19,160,-1:4,159,-1:" +
"2,159:4,112,159:21,112,159:10,-1:19,159,-1:4,160,-1:2,160:9,153,160:20,153," +
"160:6,-1:19,160,-1:4,159,-1:2,159:2,114,159:28,114,159:5,-1:19,159,-1:4,160" +
",-1:2,160:6,155,160:18,155,160:11,-1:19,160,-1:4,159,-1:2,159:6,116,159:18," +
"116,159:11,-1:19,159,-1:4,160,-1:2,160:10,115,160:5,115,160:20,-1:19,160,-1" +
":4,159,-1:2,159:9,157,159:20,157,159:6,-1:19,159,-1:4,159,-1:2,159:6,158,15" +
"9:18,158,159:11,-1:19,159,-1:4,159,-1:2,159:10,117,159:5,117,159:20,-1:19,1" +
"59,-1:4,159,-1:2,159:2,126,159,128,159:21,128,159:4,126,159:5,-1:19,159,-1:" +
"4,160,-1:2,160,125,160:2,127,160:14,125,160:6,127,160:10,-1:19,160,-1:4,159" +
",-1:2,159:12,134,159:15,134,159:8,-1:19,159,-1:4,160,-1:2,160:2,129,160,131" +
",160:21,131,160:4,129,160:5,-1:19,160,-1:4,159,-1:2,159:8,136,159:15,136,15" +
"9:12,-1:19,159,-1:4,160,-1:2,160:12,137,160:15,137,160:8,-1:19,160,-1:4,159" +
",-1:2,159:8,138,140,159:14,138,159:5,140,159:6,-1:19,159,-1:4,160,-1:2,160:" +
"8,139,160:15,139,160:12,-1:19,160,-1");

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
						{ return new Symbol(TokenConstants.EQ); }
					case -6:
						break;
					case 6:
						{ /* Any other single char is an error */
        return new Symbol(TokenConstants.ERROR, yytext());
  }
					case -7:
						break;
					case 7:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -8:
						break;
					case 8:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -9:
						break;
					case 9:
						{ return new Symbol(TokenConstants.LT); }
					case -10:
						break;
					case 10:
						{ return new Symbol(TokenConstants.MINUS); }
					case -11:
						break;
					case 11:
						{ return new Symbol(TokenConstants.PLUS); }
					case -12:
						break;
					case 12:
						{ return new Symbol(TokenConstants.MULT); }
					case -13:
						break;
					case 13:
						{ return new Symbol(TokenConstants.DIV); }
					case -14:
						break;
					case 14:
						{ return new Symbol(TokenConstants.NEG); }
					case -15:
						break;
					case 15:
						{ return new Symbol(TokenConstants.LPAREN); }
					case -16:
						break;
					case 16:
						{ return new Symbol(TokenConstants.RPAREN); }
					case -17:
						break;
					case 17:
						{ return new Symbol(TokenConstants.LBRACE); }
					case -18:
						break;
					case 18:
						{ return new Symbol(TokenConstants.RBRACE); }
					case -19:
						break;
					case 19:
						{ return new Symbol(TokenConstants.SEMI); }
					case -20:
						break;
					case 20:
						{ return new Symbol(TokenConstants.COLON); }
					case -21:
						break;
					case 21:
						{ return new Symbol(TokenConstants.DOT); }
					case -22:
						break;
					case 22:
						{ return new Symbol(TokenConstants.COMMA); }
					case -23:
						break;
					case 23:
						{ return new Symbol(TokenConstants.AT); }
					case -24:
						break;
					case 24:
						{
  string_buf.setLength(0);
  string_too_long = false;
  null_in_string = false;
  escaped_null_in_string = false;
  yybegin(STRING);
}
					case -25:
						break;
					case 25:
						{
  return new Symbol(TokenConstants.DARROW);
}
					case -26:
						break;
					case 26:
						{ return new Symbol(TokenConstants.FI); }
					case -27:
						break;
					case 27:
						{ return new Symbol(TokenConstants.IF); }
					case -28:
						break;
					case 28:
						{ return new Symbol(TokenConstants.IN); }
					case -29:
						break;
					case 29:
						{ return new Symbol(TokenConstants.OF); }
					case -30:
						break;
					case 30:
						{ return new Symbol(TokenConstants.LE); }
					case -31:
						break;
					case 31:
						{ return new Symbol(TokenConstants.ASSIGN); }
					case -32:
						break;
					case 32:
						{ /* skip till end of line */ }
					case -33:
						break;
					case 33:
						{
  return new Symbol(TokenConstants.ERROR, "Unmatched *)");
}
					case -34:
						break;
					case 34:
						{
  comment_depth = 1;
  yybegin(COMMENT);
}
					case -35:
						break;
					case 35:
						{ return new Symbol(TokenConstants.LET); }
					case -36:
						break;
					case 36:
						{ return new Symbol(TokenConstants.NEW); }
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenConstants.NOT); }
					case -38:
						break;
					case 38:
						{ return new Symbol(TokenConstants.CASE); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenConstants.ESAC); }
					case -40:
						break;
					case 40:
						{ return new Symbol(TokenConstants.ELSE); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenConstants.LOOP); }
					case -42:
						break;
					case 42:
						{ return new Symbol(TokenConstants.THEN); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenConstants.POOL); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.TRUE); }
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenConstants.CLASS); }
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenConstants.WHILE); }
					case -47:
						break;
					case 47:
						{ return new Symbol(TokenConstants.BOOL_CONST, Boolean.FALSE); }
					case -48:
						break;
					case 48:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -49:
						break;
					case 49:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -50:
						break;
					case 50:
						{
  string_buf.append(yytext());
  if (string_buf.length() >= MAX_STR_CONST) { string_too_long = true; }
}
					case -51:
						break;
					case 51:
						{
  curr_lineno++;
  yybegin(YYINITIAL);
  return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
}
					case -52:
						break;
					case 52:
						{
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
					case -53:
						break;
					case 53:
						{
  null_in_string = true;
}
					case -54:
						break;
					case 54:
						{
  curr_lineno++;
  string_buf.append('\n');
  if (string_buf.length() >= MAX_STR_CONST) { string_too_long = true; }
}
					case -55:
						break;
					case 55:
						{
  string_buf.append(yytext().charAt(1));
  if (string_buf.length() >= MAX_STR_CONST) { string_too_long = true; }
}
					case -56:
						break;
					case 56:
						{
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
					case -57:
						break;
					case 57:
						{
  escaped_null_in_string = true;
}
					case -58:
						break;
					case 58:
						{ curr_lineno++; }
					case -59:
						break;
					case 59:
						{ /* consume */ }
					case -60:
						break;
					case 60:
						{
  comment_depth--;
  if (comment_depth == 0) { yybegin(YYINITIAL); }
}
					case -61:
						break;
					case 61:
						{ comment_depth++; }
					case -62:
						break;
					case 63:
						{ /* Any other single char is an error */
        return new Symbol(TokenConstants.ERROR, yytext());
  }
					case -63:
						break;
					case 64:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -64:
						break;
					case 65:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -65:
						break;
					case 66:
						{ return new Symbol(TokenConstants.FI); }
					case -66:
						break;
					case 67:
						{ return new Symbol(TokenConstants.IF); }
					case -67:
						break;
					case 68:
						{ return new Symbol(TokenConstants.IN); }
					case -68:
						break;
					case 69:
						{ return new Symbol(TokenConstants.OF); }
					case -69:
						break;
					case 70:
						{ return new Symbol(TokenConstants.LET); }
					case -70:
						break;
					case 71:
						{ return new Symbol(TokenConstants.NEW); }
					case -71:
						break;
					case 72:
						{ return new Symbol(TokenConstants.NOT); }
					case -72:
						break;
					case 73:
						{ return new Symbol(TokenConstants.CASE); }
					case -73:
						break;
					case 74:
						{ return new Symbol(TokenConstants.ESAC); }
					case -74:
						break;
					case 75:
						{ return new Symbol(TokenConstants.ELSE); }
					case -75:
						break;
					case 76:
						{ return new Symbol(TokenConstants.LOOP); }
					case -76:
						break;
					case 77:
						{ return new Symbol(TokenConstants.THEN); }
					case -77:
						break;
					case 78:
						{ return new Symbol(TokenConstants.POOL); }
					case -78:
						break;
					case 79:
						{ return new Symbol(TokenConstants.CLASS); }
					case -79:
						break;
					case 80:
						{ return new Symbol(TokenConstants.WHILE); }
					case -80:
						break;
					case 81:
						{ return new Symbol(TokenConstants.ISVOID); }
					case -81:
						break;
					case 82:
						{ return new Symbol(TokenConstants.INHERITS); }
					case -82:
						break;
					case 83:
						{ /* consume */ }
					case -83:
						break;
					case 85:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -84:
						break;
					case 86:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -85:
						break;
					case 87:
						{ /* consume */ }
					case -86:
						break;
					case 88:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -87:
						break;
					case 89:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -88:
						break;
					case 90:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -89:
						break;
					case 91:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -90:
						break;
					case 92:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -91:
						break;
					case 93:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -92:
						break;
					case 94:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -93:
						break;
					case 95:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -94:
						break;
					case 96:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -95:
						break;
					case 97:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -96:
						break;
					case 98:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -97:
						break;
					case 99:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -98:
						break;
					case 100:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -99:
						break;
					case 101:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -100:
						break;
					case 102:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -101:
						break;
					case 103:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -102:
						break;
					case 104:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -103:
						break;
					case 105:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -104:
						break;
					case 106:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -105:
						break;
					case 107:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -106:
						break;
					case 108:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -107:
						break;
					case 109:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -108:
						break;
					case 110:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -109:
						break;
					case 111:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -110:
						break;
					case 112:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -111:
						break;
					case 113:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -112:
						break;
					case 114:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -113:
						break;
					case 115:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -114:
						break;
					case 116:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -115:
						break;
					case 117:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -116:
						break;
					case 118:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -117:
						break;
					case 119:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -118:
						break;
					case 120:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -119:
						break;
					case 121:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -120:
						break;
					case 122:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -121:
						break;
					case 123:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -122:
						break;
					case 124:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -123:
						break;
					case 125:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -124:
						break;
					case 126:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -125:
						break;
					case 127:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -126:
						break;
					case 128:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -127:
						break;
					case 129:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -128:
						break;
					case 130:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -129:
						break;
					case 131:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -130:
						break;
					case 132:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -131:
						break;
					case 133:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -132:
						break;
					case 134:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -133:
						break;
					case 135:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -134:
						break;
					case 136:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -135:
						break;
					case 137:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -136:
						break;
					case 138:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -137:
						break;
					case 139:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -138:
						break;
					case 140:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -139:
						break;
					case 141:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -140:
						break;
					case 142:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -141:
						break;
					case 143:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -142:
						break;
					case 144:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -143:
						break;
					case 145:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -144:
						break;
					case 146:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -145:
						break;
					case 147:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -146:
						break;
					case 148:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -147:
						break;
					case 149:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -148:
						break;
					case 150:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -149:
						break;
					case 151:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -150:
						break;
					case 152:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -151:
						break;
					case 153:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -152:
						break;
					case 154:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -153:
						break;
					case 155:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -154:
						break;
					case 156:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -155:
						break;
					case 157:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -156:
						break;
					case 158:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -157:
						break;
					case 159:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -158:
						break;
					case 160:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -159:
						break;
					case 161:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -160:
						break;
					case 162:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -161:
						break;
					case 163:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -162:
						break;
					case 164:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -163:
						break;
					case 165:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -164:
						break;
					case 166:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -165:
						break;
					case 167:
						{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
					case -166:
						break;
					case 168:
						{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
					case -167:
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

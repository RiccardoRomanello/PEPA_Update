// $ANTLR 3.3 Nov 30, 2010 12:45:30 /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g 2011-04-01 16:44:10

package uk.ac.ed.inf.pepa.ctmc.modelchecking.parsing;

import java.util.HashMap;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class CSLLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__8=8;
    public static final int T__9=9;
    public static final int T__10=10;
    public static final int T__11=11;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int NUM=4;
    public static final int ID=5;
    public static final int INT=6;
    public static final int WS=7;

    // delegates
    // delegators

    public CSLLexer() {;} 
    public CSLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public CSLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g"; }

    // $ANTLR start "T__8"
    public final void mT__8() throws RecognitionException {
        try {
            int _type = T__8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:13:6: ( '=>' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:13:8: '=>'
            {
            match("=>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__8"

    // $ANTLR start "T__9"
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:14:6: ( '&' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:14:8: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__9"

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:15:7: ( '|' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:15:9: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:16:7: ( '!' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:16:9: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:17:7: ( 'true' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:17:9: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:18:7: ( 'false' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:18:9: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:19:7: ( 'P' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:19:9: 'P'
            {
            match('P'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:20:7: ( '[' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:20:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:21:7: ( ']' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:21:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:22:7: ( 'S' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:22:9: 'S'
            {
            match('S'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:23:7: ( 'L' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:23:9: 'L'
            {
            match('L'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:24:7: ( '(' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:24:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:25:7: ( ')' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:25:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:26:7: ( 'X' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:26:9: 'X'
            {
            match('X'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:27:7: ( 'F' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:27:9: 'F'
            {
            match('F'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:28:7: ( 'G' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:28:9: 'G'
            {
            match('G'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:29:7: ( 'U' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:29:9: 'U'
            {
            match('U'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:30:7: ( '=?' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:30:9: '=?'
            {
            match("=?"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:31:7: ( '<=' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:31:9: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:32:7: ( '>=' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:32:9: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:33:7: ( ',' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:33:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:91:5: ( ( '0' .. '9' )+ )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:91:7: ( '0' .. '9' )+
            {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:91:7: ( '0' .. '9' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:91:7: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "NUM"
    public final void mNUM() throws RecognitionException {
        try {
            int _type = NUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:92:5: ( INT ( '.' INT )? )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:92:7: INT ( '.' INT )?
            {
            mINT(); 
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:92:11: ( '.' INT )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='.') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:92:12: '.' INT
                    {
                    match('.'); 
                    mINT(); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NUM"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:93:5: ( '\"' (~ '\"' )+ '\"' )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:93:7: '\"' (~ '\"' )+ '\"'
            {
            match('\"'); 
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:93:11: (~ '\"' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='!')||(LA3_0>='#' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:93:12: ~ '\"'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:95:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:95:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:95:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='\t' && LA4_0<='\n')||LA4_0=='\r'||LA4_0==' ') ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:8: ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | INT | NUM | ID | WS )
        int alt5=25;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:10: T__8
                {
                mT__8(); 

                }
                break;
            case 2 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:15: T__9
                {
                mT__9(); 

                }
                break;
            case 3 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:20: T__10
                {
                mT__10(); 

                }
                break;
            case 4 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:26: T__11
                {
                mT__11(); 

                }
                break;
            case 5 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:32: T__12
                {
                mT__12(); 

                }
                break;
            case 6 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:38: T__13
                {
                mT__13(); 

                }
                break;
            case 7 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:44: T__14
                {
                mT__14(); 

                }
                break;
            case 8 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:50: T__15
                {
                mT__15(); 

                }
                break;
            case 9 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:56: T__16
                {
                mT__16(); 

                }
                break;
            case 10 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:62: T__17
                {
                mT__17(); 

                }
                break;
            case 11 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:68: T__18
                {
                mT__18(); 

                }
                break;
            case 12 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:74: T__19
                {
                mT__19(); 

                }
                break;
            case 13 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:80: T__20
                {
                mT__20(); 

                }
                break;
            case 14 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:86: T__21
                {
                mT__21(); 

                }
                break;
            case 15 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:92: T__22
                {
                mT__22(); 

                }
                break;
            case 16 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:98: T__23
                {
                mT__23(); 

                }
                break;
            case 17 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:104: T__24
                {
                mT__24(); 

                }
                break;
            case 18 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:110: T__25
                {
                mT__25(); 

                }
                break;
            case 19 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:116: T__26
                {
                mT__26(); 

                }
                break;
            case 20 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:122: T__27
                {
                mT__27(); 

                }
                break;
            case 21 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:128: T__28
                {
                mT__28(); 

                }
                break;
            case 22 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:134: INT
                {
                mINT(); 

                }
                break;
            case 23 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:138: NUM
                {
                mNUM(); 

                }
                break;
            case 24 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:142: ID
                {
                mID(); 

                }
                break;
            case 25 :
                // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:1:145: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\25\uffff\1\32\6\uffff";
    static final String DFA5_eofS =
        "\34\uffff";
    static final String DFA5_minS =
        "\1\11\1\76\23\uffff\1\56\6\uffff";
    static final String DFA5_maxS =
        "\1\174\1\77\23\uffff\1\71\6\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1"+
        "\16\1\17\1\20\1\21\1\23\1\24\1\25\1\uffff\1\30\1\31\1\1\1\22\1\26"+
        "\1\27";
    static final String DFA5_specialS =
        "\34\uffff}>";
    static final String[] DFA5_transitionS = {
            "\2\27\2\uffff\1\27\22\uffff\1\27\1\4\1\26\3\uffff\1\2\1\uffff"+
            "\1\14\1\15\2\uffff\1\24\3\uffff\12\25\2\uffff\1\22\1\1\1\23"+
            "\7\uffff\1\17\1\20\4\uffff\1\13\3\uffff\1\7\2\uffff\1\12\1\uffff"+
            "\1\21\2\uffff\1\16\2\uffff\1\10\1\uffff\1\11\10\uffff\1\6\15"+
            "\uffff\1\5\7\uffff\1\3",
            "\1\30\1\31",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\33\1\uffff\12\25",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | INT | NUM | ID | WS );";
        }
    }
 

}
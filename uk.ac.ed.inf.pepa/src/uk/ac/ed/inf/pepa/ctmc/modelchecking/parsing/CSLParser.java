// $ANTLR 3.3 Nov 30, 2010 12:45:30 /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g 2011-04-01 16:44:09

package uk.ac.ed.inf.pepa.ctmc.modelchecking.parsing;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class CSLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NUM", "ID", "INT", "WS", "'=>'", "'&'", "'|'", "'!'", "'true'", "'false'", "'P'", "'['", "']'", "'S'", "'L'", "'('", "')'", "'X'", "'F'", "'G'", "'U'", "'=?'", "'<='", "'>='", "','"
    };
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


        public CSLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public CSLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return CSLParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g"; }



    // $ANTLR start "cslProperty"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:19:1: cslProperty returns [CSLAbstractStateProperty value] : s= stateFormula ;
    public final CSLAbstractStateProperty cslProperty() throws RecognitionException {
        CSLAbstractStateProperty value = null;

        CSLAbstractStateProperty s = null;


        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:20:5: (s= stateFormula )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:20:7: s= stateFormula
            {
            pushFollow(FOLLOW_stateFormula_in_cslProperty49);
            s=stateFormula();

            state._fsp--;

             value = s; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "cslProperty"


    // $ANTLR start "stateFormula"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:23:1: stateFormula returns [CSLAbstractStateProperty value] : s= orStateFormula ( '=>' s= orStateFormula )* ;
    public final CSLAbstractStateProperty stateFormula() throws RecognitionException {
        CSLAbstractStateProperty value = null;

        CSLAbstractStateProperty s = null;


        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:24:5: (s= orStateFormula ( '=>' s= orStateFormula )* )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:24:7: s= orStateFormula ( '=>' s= orStateFormula )*
            {
            pushFollow(FOLLOW_orStateFormula_in_stateFormula74);
            s=orStateFormula();

            state._fsp--;

             value = s; 
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:25:7: ( '=>' s= orStateFormula )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==8) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:25:9: '=>' s= orStateFormula
            	    {
            	    match(input,8,FOLLOW_8_in_stateFormula86); 
            	    pushFollow(FOLLOW_orStateFormula_in_stateFormula90);
            	    s=orStateFormula();

            	    state._fsp--;

            	     value = new CSLImpliesNode(value,s); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "stateFormula"


    // $ANTLR start "orStateFormula"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:28:1: orStateFormula returns [CSLAbstractStateProperty value] : s= andStateFormula ( '&' s= andStateFormula )* ;
    public final CSLAbstractStateProperty orStateFormula() throws RecognitionException {
        CSLAbstractStateProperty value = null;

        CSLAbstractStateProperty s = null;


        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:29:5: (s= andStateFormula ( '&' s= andStateFormula )* )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:29:7: s= andStateFormula ( '&' s= andStateFormula )*
            {
            pushFollow(FOLLOW_andStateFormula_in_orStateFormula119);
            s=andStateFormula();

            state._fsp--;

             value = s; 
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:30:7: ( '&' s= andStateFormula )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==9) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:30:9: '&' s= andStateFormula
            	    {
            	    match(input,9,FOLLOW_9_in_orStateFormula131); 
            	    pushFollow(FOLLOW_andStateFormula_in_orStateFormula135);
            	    s=andStateFormula();

            	    state._fsp--;

            	     value = new CSLAndNode(value,s); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "orStateFormula"


    // $ANTLR start "andStateFormula"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:33:1: andStateFormula returns [CSLAbstractStateProperty value] : s= notStateFormula ( '|' s= notStateFormula )* ;
    public final CSLAbstractStateProperty andStateFormula() throws RecognitionException {
        CSLAbstractStateProperty value = null;

        CSLAbstractStateProperty s = null;


        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:34:5: (s= notStateFormula ( '|' s= notStateFormula )* )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:34:7: s= notStateFormula ( '|' s= notStateFormula )*
            {
            pushFollow(FOLLOW_notStateFormula_in_andStateFormula163);
            s=notStateFormula();

            state._fsp--;

             value = s; 
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:35:7: ( '|' s= notStateFormula )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==10) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:35:9: '|' s= notStateFormula
            	    {
            	    match(input,10,FOLLOW_10_in_andStateFormula175); 
            	    pushFollow(FOLLOW_notStateFormula_in_andStateFormula179);
            	    s=notStateFormula();

            	    state._fsp--;

            	     value = new CSLOrNode(value,s); 

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "andStateFormula"


    // $ANTLR start "notStateFormula"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:38:1: notStateFormula returns [CSLAbstractStateProperty value] : (n= '!' )? s= baseStateFormula ;
    public final CSLAbstractStateProperty notStateFormula() throws RecognitionException {
        CSLAbstractStateProperty value = null;

        Token n=null;
        CSLAbstractStateProperty s = null;


        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:39:5: ( (n= '!' )? s= baseStateFormula )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:39:7: (n= '!' )? s= baseStateFormula
            {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:39:7: (n= '!' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==11) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:39:8: n= '!'
                    {
                    n=(Token)match(input,11,FOLLOW_11_in_notStateFormula208); 

                    }
                    break;

            }

            pushFollow(FOLLOW_baseStateFormula_in_notStateFormula214);
            s=baseStateFormula();

            state._fsp--;

             if (n!=null) { value = new CSLNotNode(s); } else { value = s; } 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "notStateFormula"


    // $ANTLR start "baseStateFormula"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:43:1: baseStateFormula returns [CSLAbstractStateProperty value] : ( 'true' | 'false' | id= identifier | 'P' c= comparator '[' p= pathFormula ']' | 'S' c= comparator '[' s= stateFormula ']' | 'L' c= comparator '[' s= stateFormula ']' | '(' s= stateFormula ')' );
    public final CSLAbstractStateProperty baseStateFormula() throws RecognitionException {
        CSLAbstractStateProperty value = null;

        String id = null;

        CSLAbstractProbability c = null;

        CSLAbstractPathProperty p = null;

        CSLAbstractStateProperty s = null;


        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:44:5: ( 'true' | 'false' | id= identifier | 'P' c= comparator '[' p= pathFormula ']' | 'S' c= comparator '[' s= stateFormula ']' | 'L' c= comparator '[' s= stateFormula ']' | '(' s= stateFormula ')' )
            int alt5=7;
            switch ( input.LA(1) ) {
            case 12:
                {
                alt5=1;
                }
                break;
            case 13:
                {
                alt5=2;
                }
                break;
            case ID:
                {
                alt5=3;
                }
                break;
            case 14:
                {
                alt5=4;
                }
                break;
            case 17:
                {
                alt5=5;
                }
                break;
            case 18:
                {
                alt5=6;
                }
                break;
            case 19:
                {
                alt5=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:44:7: 'true'
                    {
                    match(input,12,FOLLOW_12_in_baseStateFormula238); 
                     value = new CSLBooleanNode(true); 

                    }
                    break;
                case 2 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:45:7: 'false'
                    {
                    match(input,13,FOLLOW_13_in_baseStateFormula281); 
                     value = new CSLBooleanNode(false); 

                    }
                    break;
                case 3 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:46:7: id= identifier
                    {
                    pushFollow(FOLLOW_identifier_in_baseStateFormula325);
                    id=identifier();

                    state._fsp--;

                     value = new CSLAtomicNode(id); 

                    }
                    break;
                case 4 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:47:7: 'P' c= comparator '[' p= pathFormula ']'
                    {
                    match(input,14,FOLLOW_14_in_baseStateFormula361); 
                    pushFollow(FOLLOW_comparator_in_baseStateFormula365);
                    c=comparator();

                    state._fsp--;

                    match(input,15,FOLLOW_15_in_baseStateFormula367); 
                    pushFollow(FOLLOW_pathFormula_in_baseStateFormula371);
                    p=pathFormula();

                    state._fsp--;

                    match(input,16,FOLLOW_16_in_baseStateFormula373); 
                     value = new CSLPathPropertyNode(p,c); 

                    }
                    break;
                case 5 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:48:7: 'S' c= comparator '[' s= stateFormula ']'
                    {
                    match(input,17,FOLLOW_17_in_baseStateFormula384); 
                    pushFollow(FOLLOW_comparator_in_baseStateFormula388);
                    c=comparator();

                    state._fsp--;

                    match(input,15,FOLLOW_15_in_baseStateFormula390); 
                    pushFollow(FOLLOW_stateFormula_in_baseStateFormula394);
                    s=stateFormula();

                    state._fsp--;

                    match(input,16,FOLLOW_16_in_baseStateFormula396); 
                     value = new CSLSteadyStateNode(s,c); 

                    }
                    break;
                case 6 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:49:7: 'L' c= comparator '[' s= stateFormula ']'
                    {
                    match(input,18,FOLLOW_18_in_baseStateFormula406); 
                    pushFollow(FOLLOW_comparator_in_baseStateFormula410);
                    c=comparator();

                    state._fsp--;

                    match(input,15,FOLLOW_15_in_baseStateFormula412); 
                    pushFollow(FOLLOW_stateFormula_in_baseStateFormula416);
                    s=stateFormula();

                    state._fsp--;

                    match(input,16,FOLLOW_16_in_baseStateFormula418); 
                     value = new CSLLongRunNode(s,c); 

                    }
                    break;
                case 7 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:50:7: '(' s= stateFormula ')'
                    {
                    match(input,19,FOLLOW_19_in_baseStateFormula428); 
                    pushFollow(FOLLOW_stateFormula_in_baseStateFormula432);
                    s=stateFormula();

                    state._fsp--;

                    match(input,20,FOLLOW_20_in_baseStateFormula434); 
                     value = s; 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "baseStateFormula"


    // $ANTLR start "pathFormula"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:53:1: pathFormula returns [CSLAbstractPathProperty value] : ( 'X' (i= interval s= stateFormula | s= stateFormula ) | 'F' (i= interval s= stateFormula | s= stateFormula ) | 'G' (i= interval s= stateFormula | s= stateFormula ) | s1= stateFormula 'U' (i= interval s2= stateFormula | s2= stateFormula ) );
    public final CSLAbstractPathProperty pathFormula() throws RecognitionException {
        CSLAbstractPathProperty value = null;

        CSLTimeInterval i = null;

        CSLAbstractStateProperty s = null;

        CSLAbstractStateProperty s1 = null;

        CSLAbstractStateProperty s2 = null;


        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:54:5: ( 'X' (i= interval s= stateFormula | s= stateFormula ) | 'F' (i= interval s= stateFormula | s= stateFormula ) | 'G' (i= interval s= stateFormula | s= stateFormula ) | s1= stateFormula 'U' (i= interval s2= stateFormula | s2= stateFormula ) )
            int alt10=4;
            switch ( input.LA(1) ) {
            case 21:
                {
                alt10=1;
                }
                break;
            case 22:
                {
                alt10=2;
                }
                break;
            case 23:
                {
                alt10=3;
                }
                break;
            case ID:
            case 11:
            case 12:
            case 13:
            case 14:
            case 17:
            case 18:
            case 19:
                {
                alt10=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:54:7: 'X' (i= interval s= stateFormula | s= stateFormula )
                    {
                    match(input,21,FOLLOW_21_in_pathFormula474); 
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:54:11: (i= interval s= stateFormula | s= stateFormula )
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==15||(LA6_0>=26 && LA6_0<=27)) ) {
                        alt6=1;
                    }
                    else if ( (LA6_0==ID||(LA6_0>=11 && LA6_0<=14)||(LA6_0>=17 && LA6_0<=19)) ) {
                        alt6=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 0, input);

                        throw nvae;
                    }
                    switch (alt6) {
                        case 1 :
                            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:54:13: i= interval s= stateFormula
                            {
                            pushFollow(FOLLOW_interval_in_pathFormula480);
                            i=interval();

                            state._fsp--;

                            pushFollow(FOLLOW_stateFormula_in_pathFormula484);
                            s=stateFormula();

                            state._fsp--;

                             value = new CSLNextNode(s,i); 

                            }
                            break;
                        case 2 :
                            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:55:13: s= stateFormula
                            {
                            pushFollow(FOLLOW_stateFormula_in_pathFormula502);
                            s=stateFormula();

                            state._fsp--;

                             value = new CSLNextNode(s,new CSLTimeInterval()); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:57:7: 'F' (i= interval s= stateFormula | s= stateFormula )
                    {
                    match(input,22,FOLLOW_22_in_pathFormula535); 
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:57:11: (i= interval s= stateFormula | s= stateFormula )
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==15||(LA7_0>=26 && LA7_0<=27)) ) {
                        alt7=1;
                    }
                    else if ( (LA7_0==ID||(LA7_0>=11 && LA7_0<=14)||(LA7_0>=17 && LA7_0<=19)) ) {
                        alt7=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 0, input);

                        throw nvae;
                    }
                    switch (alt7) {
                        case 1 :
                            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:57:13: i= interval s= stateFormula
                            {
                            pushFollow(FOLLOW_interval_in_pathFormula541);
                            i=interval();

                            state._fsp--;

                            pushFollow(FOLLOW_stateFormula_in_pathFormula545);
                            s=stateFormula();

                            state._fsp--;

                             value = new CSLEventuallyNode(s,i); 

                            }
                            break;
                        case 2 :
                            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:58:13: s= stateFormula
                            {
                            pushFollow(FOLLOW_stateFormula_in_pathFormula563);
                            s=stateFormula();

                            state._fsp--;

                             value = new CSLEventuallyNode(s,new CSLTimeInterval()); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:60:7: 'G' (i= interval s= stateFormula | s= stateFormula )
                    {
                    match(input,23,FOLLOW_23_in_pathFormula596); 
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:60:11: (i= interval s= stateFormula | s= stateFormula )
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==15||(LA8_0>=26 && LA8_0<=27)) ) {
                        alt8=1;
                    }
                    else if ( (LA8_0==ID||(LA8_0>=11 && LA8_0<=14)||(LA8_0>=17 && LA8_0<=19)) ) {
                        alt8=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 0, input);

                        throw nvae;
                    }
                    switch (alt8) {
                        case 1 :
                            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:60:13: i= interval s= stateFormula
                            {
                            pushFollow(FOLLOW_interval_in_pathFormula602);
                            i=interval();

                            state._fsp--;

                            pushFollow(FOLLOW_stateFormula_in_pathFormula606);
                            s=stateFormula();

                            state._fsp--;

                             value = new CSLGloballyNode(s,i); 

                            }
                            break;
                        case 2 :
                            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:61:13: s= stateFormula
                            {
                            pushFollow(FOLLOW_stateFormula_in_pathFormula624);
                            s=stateFormula();

                            state._fsp--;

                             value = new CSLGloballyNode(s,new CSLTimeInterval()); 

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:63:7: s1= stateFormula 'U' (i= interval s2= stateFormula | s2= stateFormula )
                    {
                    pushFollow(FOLLOW_stateFormula_in_pathFormula659);
                    s1=stateFormula();

                    state._fsp--;

                    match(input,24,FOLLOW_24_in_pathFormula661); 
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:63:27: (i= interval s2= stateFormula | s2= stateFormula )
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==15||(LA9_0>=26 && LA9_0<=27)) ) {
                        alt9=1;
                    }
                    else if ( (LA9_0==ID||(LA9_0>=11 && LA9_0<=14)||(LA9_0>=17 && LA9_0<=19)) ) {
                        alt9=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, input);

                        throw nvae;
                    }
                    switch (alt9) {
                        case 1 :
                            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:63:29: i= interval s2= stateFormula
                            {
                            pushFollow(FOLLOW_interval_in_pathFormula667);
                            i=interval();

                            state._fsp--;

                            pushFollow(FOLLOW_stateFormula_in_pathFormula671);
                            s2=stateFormula();

                            state._fsp--;

                             value = new CSLUntilNode(s1,s2,i); 

                            }
                            break;
                        case 2 :
                            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:64:29: s2= stateFormula
                            {
                            pushFollow(FOLLOW_stateFormula_in_pathFormula705);
                            s2=stateFormula();

                            state._fsp--;

                             value = new CSLUntilNode(s1,s2,new CSLTimeInterval()); 

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "pathFormula"


    // $ANTLR start "comparator"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:68:1: comparator returns [CSLAbstractProbability value] : ( '=?' | '<=' NUM | '>=' NUM );
    public final CSLAbstractProbability comparator() throws RecognitionException {
        CSLAbstractProbability value = null;

        Token NUM1=null;
        Token NUM2=null;

        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:69:5: ( '=?' | '<=' NUM | '>=' NUM )
            int alt11=3;
            switch ( input.LA(1) ) {
            case 25:
                {
                alt11=1;
                }
                break;
            case 26:
                {
                alt11=2;
                }
                break;
            case 27:
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:69:7: '=?'
                    {
                    match(input,25,FOLLOW_25_in_comparator767); 
                     value = new CSLProbabilityTest(); 

                    }
                    break;
                case 2 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:70:7: '<=' NUM
                    {
                    match(input,26,FOLLOW_26_in_comparator781); 
                    NUM1=(Token)match(input,NUM,FOLLOW_NUM_in_comparator783); 
                     double prob = Double.parseDouble(NUM1.getText());
                                     value = new CSLProbabilityComparator(true, new CSLDouble(true, prob)); 

                    }
                    break;
                case 3 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:72:7: '>=' NUM
                    {
                    match(input,27,FOLLOW_27_in_comparator793); 
                    NUM2=(Token)match(input,NUM,FOLLOW_NUM_in_comparator795); 
                     double prob = Double.parseDouble(NUM2.getText());
                                     value = new CSLProbabilityComparator(false, new CSLDouble(true, prob)); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "comparator"


    // $ANTLR start "interval"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:76:1: interval returns [CSLTimeInterval value] : ( '[' n1= NUM ',' n2= NUM ']' | '<=' NUM | '>=' NUM );
    public final CSLTimeInterval interval() throws RecognitionException {
        CSLTimeInterval value = null;

        Token n1=null;
        Token n2=null;
        Token NUM3=null;
        Token NUM4=null;

        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:77:5: ( '[' n1= NUM ',' n2= NUM ']' | '<=' NUM | '>=' NUM )
            int alt12=3;
            switch ( input.LA(1) ) {
            case 15:
                {
                alt12=1;
                }
                break;
            case 26:
                {
                alt12=2;
                }
                break;
            case 27:
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:77:7: '[' n1= NUM ',' n2= NUM ']'
                    {
                    match(input,15,FOLLOW_15_in_interval818); 
                    n1=(Token)match(input,NUM,FOLLOW_NUM_in_interval822); 
                    match(input,28,FOLLOW_28_in_interval824); 
                    n2=(Token)match(input,NUM,FOLLOW_NUM_in_interval828); 
                    match(input,16,FOLLOW_16_in_interval830); 
                     double lower = Double.parseDouble(n1.getText());
                                                      double upper = Double.parseDouble(n2.getText()); 
                                                      value = new CSLTimeInterval(new CSLDouble(false, lower), new CSLDouble(false, upper)); 

                    }
                    break;
                case 2 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:80:7: '<=' NUM
                    {
                    match(input,26,FOLLOW_26_in_interval840); 
                    NUM3=(Token)match(input,NUM,FOLLOW_NUM_in_interval842); 
                     double upper = Double.parseDouble(NUM3.getText());
                                                      value = new CSLTimeInterval(new CSLDouble(false, upper),false); 

                    }
                    break;
                case 3 :
                    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:82:7: '>=' NUM
                    {
                    match(input,27,FOLLOW_27_in_interval869); 
                    NUM4=(Token)match(input,NUM,FOLLOW_NUM_in_interval871); 
                     double lower = Double.parseDouble(NUM4.getText());
                                                      value = new CSLTimeInterval(new CSLDouble(false, lower),true); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "interval"


    // $ANTLR start "identifier"
    // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:86:1: identifier returns [String value] : ID ;
    public final String identifier() throws RecognitionException {
        String value = null;

        Token ID5=null;

        try {
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:87:5: ( ID )
            // /Users/mjas/Documents/workspace/uk.ac.ed.inf.pepa/src/uk/ac/ed/inf/pepa/ctmc/modelchecking/parsing/CSL.g:87:7: ID
            {
            ID5=(Token)match(input,ID,FOLLOW_ID_in_identifier911); 
             String id = ID5.getText();
                       value = id.substring(1,id.length()-1); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "identifier"

    // Delegated rules


 

    public static final BitSet FOLLOW_stateFormula_in_cslProperty49 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_orStateFormula_in_stateFormula74 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_8_in_stateFormula86 = new BitSet(new long[]{0x00000000000E7820L});
    public static final BitSet FOLLOW_orStateFormula_in_stateFormula90 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_andStateFormula_in_orStateFormula119 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_9_in_orStateFormula131 = new BitSet(new long[]{0x00000000000E7820L});
    public static final BitSet FOLLOW_andStateFormula_in_orStateFormula135 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_notStateFormula_in_andStateFormula163 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_10_in_andStateFormula175 = new BitSet(new long[]{0x00000000000E7820L});
    public static final BitSet FOLLOW_notStateFormula_in_andStateFormula179 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_11_in_notStateFormula208 = new BitSet(new long[]{0x00000000000E7820L});
    public static final BitSet FOLLOW_baseStateFormula_in_notStateFormula214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_baseStateFormula238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_baseStateFormula281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_baseStateFormula325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_baseStateFormula361 = new BitSet(new long[]{0x000000000E000000L});
    public static final BitSet FOLLOW_comparator_in_baseStateFormula365 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_baseStateFormula367 = new BitSet(new long[]{0x0000000000EE7820L});
    public static final BitSet FOLLOW_pathFormula_in_baseStateFormula371 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_baseStateFormula373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_baseStateFormula384 = new BitSet(new long[]{0x000000000E000000L});
    public static final BitSet FOLLOW_comparator_in_baseStateFormula388 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_baseStateFormula390 = new BitSet(new long[]{0x0000000000EE7820L});
    public static final BitSet FOLLOW_stateFormula_in_baseStateFormula394 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_baseStateFormula396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_baseStateFormula406 = new BitSet(new long[]{0x000000000E000000L});
    public static final BitSet FOLLOW_comparator_in_baseStateFormula410 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_baseStateFormula412 = new BitSet(new long[]{0x0000000000EE7820L});
    public static final BitSet FOLLOW_stateFormula_in_baseStateFormula416 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_baseStateFormula418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_baseStateFormula428 = new BitSet(new long[]{0x0000000000EE7820L});
    public static final BitSet FOLLOW_stateFormula_in_baseStateFormula432 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_baseStateFormula434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_pathFormula474 = new BitSet(new long[]{0x000000000CEEF820L});
    public static final BitSet FOLLOW_interval_in_pathFormula480 = new BitSet(new long[]{0x0000000000EE7820L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_pathFormula535 = new BitSet(new long[]{0x000000000CEEF820L});
    public static final BitSet FOLLOW_interval_in_pathFormula541 = new BitSet(new long[]{0x0000000000EE7820L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_pathFormula596 = new BitSet(new long[]{0x000000000CEEF820L});
    public static final BitSet FOLLOW_interval_in_pathFormula602 = new BitSet(new long[]{0x0000000000EE7820L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula659 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_pathFormula661 = new BitSet(new long[]{0x000000000CEEF820L});
    public static final BitSet FOLLOW_interval_in_pathFormula667 = new BitSet(new long[]{0x0000000000EE7820L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFormula_in_pathFormula705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_comparator767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_comparator781 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NUM_in_comparator783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_comparator793 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NUM_in_comparator795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_interval818 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NUM_in_interval822 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_interval824 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NUM_in_interval828 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_interval830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_interval840 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NUM_in_interval842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_interval869 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NUM_in_interval871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier911 = new BitSet(new long[]{0x0000000000000002L});

}
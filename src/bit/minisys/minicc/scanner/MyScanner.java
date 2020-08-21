package bit.minisys.minicc.scanner;

import java.util.ArrayList;
import java.util.HashSet;

import bit.minisys.minicc.MiniCCCfg;
import bit.minisys.minicc.internal.util.MiniCCUtil;


public class MyScanner implements IMiniCCScanner {
	
	public String strTokens = "";

	public int counter = 0;
	
	  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;
  private static final String ZZ_NL = System.getProperty("line.separator");

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\15\1\23\1\25\1\25\1\24\22\0\1\15\1\62\1\30"+
    "\2\0\1\62\1\64\1\21\1\56\1\56\1\62\1\66\1\56\1\5"+
    "\1\26\1\62\1\16\7\20\1\27\1\1\1\63\1\56\1\61\1\57"+
    "\1\60\1\63\1\0\4\3\1\4\1\10\5\2\1\12\3\2\1\6"+
    "\4\2\1\14\2\2\1\17\2\2\1\56\1\22\1\56\1\62\1\2"+
    "\1\0\1\31\1\34\1\40\1\45\1\36\1\7\1\50\1\42\1\44"+
    "\1\2\1\37\1\11\1\47\1\43\1\33\1\54\1\2\1\35\1\41"+
    "\1\32\1\13\1\55\1\51\1\46\1\53\1\52\1\56\1\65\1\56"+
    "\1\63\6\0\1\25\u1fa2\0\1\25\1\25\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\udfe6\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\3\4\2\1\4\1\1\1\0"+
    "\1\5\1\0\14\2\1\5\7\3\1\0\4\1\6\2"+
    "\1\0\1\1\5\0\16\2\1\6\1\2\1\6\3\2"+
    "\1\1\1\0\4\1\4\2\1\1\1\0\1\7\30\2"+
    "\1\1\25\2";

  private static int [] zzUnpackAction() {
    int [] result = new int[130];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\67\0\156\0\245\0\334\0\u0113\0\u014a\0\u0181"+
    "\0\u01b8\0\u01ef\0\u0226\0\u025d\0\u0294\0\u02cb\0\u0302\0\u0339"+
    "\0\u0370\0\u03a7\0\u03de\0\u0415\0\u044c\0\u0483\0\u04ba\0\u04f1"+
    "\0\u0528\0\u055f\0\u0596\0\u05cd\0\u0604\0\u055f\0\u063b\0\u0672"+
    "\0\u06a9\0\u06e0\0\u0717\0\u074e\0\u0785\0\u07bc\0\u07f3\0\u082a"+
    "\0\u0861\0\u0898\0\u08cf\0\u0906\0\u093d\0\u0974\0\u09ab\0\u09e2"+
    "\0\u0a19\0\u0a50\0\u0a87\0\u0abe\0\u0af5\0\u0b2c\0\u0b63\0\u0b9a"+
    "\0\u0bd1\0\u0c08\0\u0c3f\0\u0c76\0\u0cad\0\u0ce4\0\u0d1b\0\u0d52"+
    "\0\u0d89\0\156\0\u0dc0\0\u0df7\0\u0e2e\0\u0e65\0\u0e9c\0\u0ed3"+
    "\0\u0f0a\0\u0f41\0\u055f\0\u0f78\0\u0faf\0\u0fe6\0\u101d\0\u1054"+
    "\0\u108b\0\u10c2\0\u10f9\0\u0a50\0\u1130\0\u1167\0\u119e\0\u11d5"+
    "\0\u120c\0\u1243\0\u127a\0\u12b1\0\u12e8\0\u131f\0\u1356\0\u138d"+
    "\0\u13c4\0\u13fb\0\u1432\0\u1469\0\u14a0\0\u14d7\0\u150e\0\u1545"+
    "\0\u157c\0\u15b3\0\u15ea\0\u1621\0\u1658\0\u168f\0\u16c6\0\u16fd"+
    "\0\u1734\0\u176b\0\u17a2\0\u17d9\0\u1810\0\u1847\0\u187e\0\u18b5"+
    "\0\u18ec\0\u1923\0\u195a\0\u1991\0\u19c8\0\u19ff\0\u1a36\0\u1a6d"+
    "\0\u1aa4\0\u1adb";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[130];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\0\1\2\3\3\1\4\1\3\1\5\1\3\1\6"+
    "\1\7\1\10\1\7\1\11\1\12\1\3\1\2\1\13"+
    "\1\0\2\11\1\0\1\14\1\2\1\15\1\16\1\17"+
    "\1\3\1\20\1\21\1\22\1\3\1\23\1\24\2\3"+
    "\1\25\1\26\2\3\1\27\1\30\3\3\1\31\1\32"+
    "\1\33\1\34\1\35\1\33\1\36\1\37\1\40\1\41"+
    "\1\0\1\2\2\0\1\42\4\0\1\43\1\44\2\45"+
    "\1\0\1\2\1\0\1\2\5\0\1\46\1\2\6\0"+
    "\1\42\31\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\25\3\16\0\1\36"+
    "\51\0\2\36\7\0\1\47\3\3\1\0\3\3\1\50"+
    "\3\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\2\3\1\51\22\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\2\3"+
    "\1\52\22\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\1\13\5\0\1\47\1\15\25\3"+
    "\12\0\1\47\3\3\1\0\7\3\1\0\1\47\1\3"+
    "\1\47\1\13\5\0\1\53\1\15\12\3\1\54\12\3"+
    "\26\0\1\11\5\0\2\11\43\0\1\55\2\0\1\42"+
    "\4\0\1\43\1\44\2\45\1\0\1\56\1\57\1\56"+
    "\5\0\1\46\1\55\6\0\1\42\7\0\1\57\20\0"+
    "\21\60\1\0\1\61\1\0\43\60\1\0\1\46\14\0"+
    "\1\46\1\0\1\46\6\0\1\46\37\0\21\62\1\0"+
    "\1\63\1\0\43\62\1\0\1\47\3\3\1\0\5\3"+
    "\1\64\1\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\25\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\22\3\1\65"+
    "\2\3\12\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\4\3\1\66\20\3"+
    "\12\0\1\47\3\3\1\0\7\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\5\3\1\67\17\3\12\0"+
    "\1\47\3\3\1\0\3\3\1\70\3\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\12\3\1\71\2\3"+
    "\1\72\7\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\1\73\1\3"+
    "\1\74\6\3\1\75\13\3\12\0\1\47\3\3\1\0"+
    "\7\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\1\3\1\76\7\3\1\77\1\3\1\100\4\3\1\101"+
    "\4\3\12\0\1\47\3\3\1\0\1\3\1\102\5\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\12\3"+
    "\1\103\12\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\2\3\1\104"+
    "\2\3\1\105\17\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\2\3"+
    "\1\64\22\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\11\3\1\106"+
    "\13\3\12\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\2\3\1\107\22\3"+
    "\157\0\1\36\66\0\1\36\1\33\65\0\1\36\1\0"+
    "\1\33\64\0\1\36\4\0\1\36\61\0\1\36\5\0"+
    "\1\36\60\0\1\36\6\0\1\36\1\0\1\110\3\0"+
    "\1\111\10\0\1\110\1\0\1\110\6\0\1\110\36\0"+
    "\1\111\11\0\1\112\1\0\2\113\64\0\1\112\2\113"+
    "\63\0\1\114\1\115\55\0\1\46\2\0\1\42\2\0"+
    "\4\113\3\0\1\46\1\0\1\46\6\0\1\46\6\0"+
    "\1\42\31\0\1\47\1\0\2\47\2\0\2\47\5\0"+
    "\1\47\1\0\1\47\6\0\1\47\1\0\1\47\2\0"+
    "\1\47\1\0\1\47\1\0\1\47\4\0\1\47\22\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\2\3\1\116\22\3\12\0\1\47"+
    "\3\3\1\0\7\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\4\3\1\102\20\3\12\0\1\47\3\3"+
    "\1\0\7\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\12\3\1\117\12\3\12\0\1\47\1\0\2\47"+
    "\2\0\2\47\5\0\1\47\1\0\1\47\6\0\1\47"+
    "\1\15\1\47\2\0\1\47\1\0\1\47\1\0\1\47"+
    "\4\0\1\47\22\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\10\3\1\120"+
    "\2\3\1\121\11\3\12\0\1\55\2\0\1\42\11\0"+
    "\1\55\1\0\1\55\5\0\1\46\1\55\6\0\1\42"+
    "\31\0\1\55\2\0\1\42\4\0\1\43\1\44\2\45"+
    "\1\0\1\56\1\0\1\56\5\0\1\46\1\55\6\0"+
    "\1\42\31\0\1\122\1\0\2\122\2\0\2\122\5\0"+
    "\1\122\1\0\1\122\5\0\1\123\1\122\1\0\1\122"+
    "\2\0\1\122\1\0\1\122\1\0\1\122\4\0\1\122"+
    "\21\0\21\60\1\113\1\61\1\0\66\60\3\0\41\60"+
    "\21\62\1\0\1\63\1\0\4\62\1\124\61\62\3\0"+
    "\41\62\1\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\1\3\1\125\23\3"+
    "\12\0\1\47\3\3\1\0\7\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\23\3\1\126\1\3\12\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\5\3\1\127\17\3\12\0\1\47"+
    "\3\3\1\0\7\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\1\3\1\130\15\3\1\131\5\3\12\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\10\3\1\132\14\3\12\0\1\47"+
    "\3\3\1\0\5\3\1\133\1\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\25\3\12\0\1\47\3\3"+
    "\1\0\7\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\1\3\1\134\23\3\12\0\1\47\3\3\1\0"+
    "\7\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\1\3\1\135\6\3\1\132\14\3\12\0\1\47\3\3"+
    "\1\0\7\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\12\3\1\136\12\3\12\0\1\47\3\3\1\0"+
    "\7\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\1\51\24\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\1\137\3\3"+
    "\1\140\20\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\2\3\1\141"+
    "\22\3\12\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\17\3\1\142\1\3"+
    "\1\143\3\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\13\3\1\144"+
    "\11\3\12\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\1\3\1\102\5\3"+
    "\1\145\15\3\12\0\1\47\3\3\1\0\5\3\1\146"+
    "\1\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\25\3\12\0\1\47\3\3\1\0\1\3\1\147\5\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\25\3"+
    "\12\0\1\47\3\3\1\0\7\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\13\3\1\150\11\3\12\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\13\3\1\151\11\3\12\0\1\110"+
    "\5\0\4\113\3\0\1\110\1\0\1\110\6\0\1\110"+
    "\40\0\1\110\14\0\1\110\1\0\1\110\6\0\1\110"+
    "\52\0\2\113\63\0\1\113\67\0\1\113\55\0\1\47"+
    "\3\3\1\0\7\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\1\152\24\3\12\0\1\47\3\3\1\0"+
    "\7\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\17\3\1\102\5\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\13\3"+
    "\1\153\11\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\2\3\1\154"+
    "\22\3\12\0\1\122\1\0\2\122\1\0\1\42\2\122"+
    "\1\43\1\44\2\45\1\0\1\122\1\0\1\122\5\0"+
    "\1\155\1\122\1\0\1\122\2\0\1\122\1\0\1\122"+
    "\1\0\1\122\4\0\1\122\6\0\1\42\13\0\1\155"+
    "\1\0\2\155\2\0\2\155\5\0\1\155\1\0\1\155"+
    "\6\0\1\155\1\0\1\155\2\0\1\155\1\0\1\155"+
    "\1\0\1\155\4\0\1\155\22\0\1\47\3\3\1\0"+
    "\7\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\2\3\1\102\22\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\5\3"+
    "\1\156\17\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\1\157\24\3"+
    "\12\0\1\47\3\3\1\0\5\3\1\160\1\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\25\3\12\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\13\3\1\161\11\3\12\0\1\47"+
    "\3\3\1\0\7\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\5\3\1\102\17\3\12\0\1\47\3\3"+
    "\1\0\7\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\16\3\1\102\6\3\12\0\1\47\3\3\1\0"+
    "\7\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\5\3\1\160\17\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\7\3"+
    "\1\162\15\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\1\3\1\163"+
    "\6\3\1\152\14\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\1\3"+
    "\1\164\23\3\12\0\1\47\3\3\1\0\5\3\1\165"+
    "\1\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\25\3\12\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\4\3\1\152\20\3"+
    "\12\0\1\47\3\3\1\0\7\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\12\3\1\166\12\3\12\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\5\3\1\167\17\3\12\0\1\47"+
    "\3\3\1\0\7\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\1\3\1\135\23\3\12\0\1\47\3\3"+
    "\1\0\3\3\1\170\3\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\25\3\12\0\1\47\3\3\1\0"+
    "\7\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\3\3\1\150\21\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\1\171"+
    "\24\3\12\0\1\47\3\3\1\0\3\3\1\132\3\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\25\3"+
    "\12\0\1\47\3\3\1\0\7\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\14\3\1\102\10\3\12\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\1\3\1\102\23\3\12\0\1\47"+
    "\3\3\1\0\7\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\17\3\1\142\5\3\12\0\1\47\3\3"+
    "\1\0\7\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\12\3\1\102\12\3\12\0\1\155\1\0\2\155"+
    "\1\0\1\42\2\155\2\113\3\0\1\155\1\0\1\155"+
    "\6\0\1\155\1\0\1\155\2\0\1\155\1\0\1\155"+
    "\1\0\1\155\4\0\1\155\6\0\1\42\13\0\1\47"+
    "\3\3\1\0\7\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\14\3\1\172\10\3\12\0\1\47\3\3"+
    "\1\0\7\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\6\3\1\102\16\3\12\0\1\47\3\3\1\0"+
    "\7\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\4\3\1\154\20\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\10\3"+
    "\1\173\14\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\11\3\1\102"+
    "\13\3\12\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\13\3\1\174\11\3"+
    "\12\0\1\47\3\3\1\0\7\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\13\3\1\175\11\3\12\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\7\3\1\152\15\3\12\0\1\47"+
    "\3\3\1\0\7\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\5\3\1\151\17\3\12\0\1\47\3\3"+
    "\1\0\7\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\2\3\1\176\22\3\12\0\1\47\3\3\1\0"+
    "\5\3\1\177\1\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\25\3\12\0\1\47\3\3\1\0\5\3"+
    "\1\200\1\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\25\3\12\0\1\47\3\3\1\0\7\3\1\0"+
    "\1\47\1\3\1\47\6\0\1\47\1\0\5\3\1\176"+
    "\17\3\12\0\1\47\3\3\1\0\7\3\1\0\1\47"+
    "\1\3\1\47\6\0\1\47\1\0\1\3\1\201\23\3"+
    "\12\0\1\47\3\3\1\0\7\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\12\3\1\202\12\3\12\0"+
    "\1\47\3\3\1\0\7\3\1\0\1\47\1\3\1\47"+
    "\6\0\1\47\1\0\7\3\1\102\15\3\12\0\1\47"+
    "\3\3\1\0\1\3\1\102\5\3\1\0\1\47\1\3"+
    "\1\47\6\0\1\47\1\0\25\3\12\0\1\47\3\3"+
    "\1\0\7\3\1\0\1\47\1\3\1\47\6\0\1\47"+
    "\1\0\14\3\1\132\10\3\12\0\1\47\3\3\1\0"+
    "\3\3\1\152\3\3\1\0\1\47\1\3\1\47\6\0"+
    "\1\47\1\0\25\3\12\0\1\47\3\3\1\0\7\3"+
    "\1\0\1\47\1\3\1\47\6\0\1\47\1\0\5\3"+
    "\1\51\17\3\12\0\1\47\3\3\1\0\5\3\1\132"+
    "\1\3\1\0\1\47\1\3\1\47\6\0\1\47\1\0"+
    "\25\3\11\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[6930];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\11\1\1\0\1\1\1\0\14\1\1\11\3\1"+
    "\1\11\3\1\1\0\12\1\1\0\1\1\5\0\25\1"+
    "\1\0\1\1\1\11\7\1\1\0\57\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[130];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }

  /* user code: */
   private MyToken token(MyToken.T type, Object val) {
     MyToken tk = new MyToken(type, val, yyline, yycolumn, yylength(), yychar, counter);
		 strTokens += tk.toString() + "\n";
     counter++;
     return tk;
   }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 206) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  private static String zzToPrintable(String str) {
    StringBuilder builder = new StringBuilder();
    for (int n = 0 ; n < str.length() ; ) {
      int ch = str.codePointAt(n);
      int charCount = Character.charCount(ch);
      n += charCount;
      if (ch > 31 && ch < 127) {
        builder.append((char)ch);
      } else if (charCount == 1) {
        builder.append(String.format("\\u%04X", ch));
      } else {
        builder.append(String.format("\\U%06X", ch));
      }
    }
    return builder.toString();
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public MyToken yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      yychar+= zzMarkedPosL-zzStartRead;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
              {
                return token(MyToken.T.EOF, "<EOF>");
              }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { return token(MyToken.T.Constant , yytext());
            }
          case 8: break;
          case 2: 
            { return token(MyToken.T.Identifier , yytext());
            }
          case 9: break;
          case 3: 
            { return token(MyToken.T.OPERATOR , yytext());
            }
          case 10: break;
          case 4: 
            { /* do nothing */
            }
          case 11: break;
          case 5: 
            { return token(MyToken.T.SIGN , yytext());
            }
          case 12: break;
          case 6: 
            { return token(MyToken.T.KEYWORD , yytext());
            }
          case 13: break;
          case 7: 
            { return token(MyToken.T.StringLiteral , yytext());
            }
          case 14: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }
	
	@Override
	public String run(String iFile) throws Exception {
		
		System.out.println("Scanning...");
		
		int firstFilePos = 0;
	    String encodingName = "UTF-8";
	   
	    try {
		    java.io.FileInputStream stream = new java.io.FileInputStream(iFile);
	        java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName); 
	        this.zzReader = reader;
		    
	        do {
	            System.out.println(this.yylex());
	          } while (!this.zzAtEOF);
	    }
	    catch (java.io.FileNotFoundException e) {
	          System.out.println("File not found : \""+iFile+"\"");
	        }
	        catch (java.io.IOException e) {
	          System.out.println("IO error scanning file \""+iFile+"\"");
	          System.out.println(e);
	        }
	        catch (Exception e) {
	          System.out.println("Unexpected exception:");
	          e.printStackTrace();
	        }
		
	
		String oFile = MiniCCUtil.removeAllExt(iFile) + MiniCCCfg.MINICC_SCANNER_OUTPUT_EXT;
		MiniCCUtil.createAndWriteFile(oFile, strTokens);
		
		return oFile;
	}

}

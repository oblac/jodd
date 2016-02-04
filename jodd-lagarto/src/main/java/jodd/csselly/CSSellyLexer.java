// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

/* The following code was generated by JFlex 1.6.0 */

package jodd.csselly;

import java.util.ArrayList;
import java.nio.CharBuffer;

/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.6.0
 * from the specification file <tt>csselly.flex</tt>
 */
final class CSSellyLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 4096;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int SELECTOR = 2;
  public static final int ATTR = 4;
  public static final int COMBINATOR = 6;
  public static final int PSEUDO_FN = 8;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   * at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int[] ZZ_LEXSTATE = {
          0, 0, 1, 1, 2, 2, 3, 3, 4, 4
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\2\1\10\1\7\1\16\1\11\1\6\22\2\1\10\1\2\1\13"+
    "\1\21\1\26\2\2\1\14\1\23\1\32\1\15\1\31\1\2\1\1"+
    "\1\12\1\2\12\5\1\22\2\2\1\24\1\30\2\2\32\0\1\20"+
    "\1\3\1\27\1\26\1\0\1\2\6\4\24\0\1\2\1\26\1\2"+
    "\1\25\1\2\5\0\1\17\u1fa2\0\1\17\1\17\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\udfe6\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\1\1\1\0\1\2\3\3\1\4\1\5\1\6"+
    "\2\7\1\10\2\7\1\11\3\6\1\12\1\13\1\1"+
    "\1\14\1\15\1\16\6\6\1\0\1\2\1\17\2\0"+
    "\1\20\1\0\1\21\4\0\1\11\2\0\1\11\7\0"+
    "\1\22\3\0\2\2\1\17\1\20\1\23\1\21\1\11"+
    "\4\0\2\11\6\0\1\2\2\17\2\20\2\21\2\11"+
    "\2\0\1\11\4\0\1\22\3\0\1\22\1\2\1\17"+
    "\1\20\1\21\2\11\2\0\1\11\2\0\2\11\7\0"+
    "\1\2\1\17\1\20\1\21\1\11\2\0\1\11\11\0"+
    "\1\2\1\17\1\20\1\21\1\11\2\0\1\11\5\0"+
    "\1\22\2\0\1\17\1\20\1\21\1\11\11\0\1\11"+
    "\21\0";

  private static int [] zzUnpackAction() {
    int [] result = new int[184];
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
    "\0\0\0\33\0\66\0\121\0\154\0\207\0\242\0\275"+
    "\0\330\0\363\0\275\0\275\0\275\0\u010e\0\275\0\u0129"+
    "\0\u0144\0\u015f\0\u017a\0\u0195\0\u01b0\0\275\0\275\0\u01cb"+
    "\0\u01e6\0\u0201\0\u021c\0\u0237\0\u0252\0\u026d\0\u0288\0\u02a3"+
    "\0\u02be\0\330\0\u02d9\0\u02f4\0\u030f\0\u032a\0\u0129\0\u0345"+
    "\0\u0360\0\u037b\0\u0396\0\u03b1\0\u0195\0\u03cc\0\u03e7\0\u0402"+
    "\0\u041d\0\u017a\0\u01b0\0\u0237\0\u0252\0\u0288\0\u02a3\0\u02be"+
    "\0\275\0\u026d\0\u0438\0\u0453\0\u046e\0\u0489\0\u04a4\0\u04bf"+
    "\0\275\0\u04da\0\u04f5\0\u0510\0\u052b\0\u0546\0\u0561\0\u057c"+
    "\0\u0597\0\u05b2\0\u05cd\0\u05e8\0\u0603\0\u061e\0\u0639\0\u0654"+
    "\0\u066f\0\u068a\0\u06a5\0\u06c0\0\u06db\0\u06f6\0\u0711\0\u072c"+
    "\0\u0747\0\u0762\0\u077d\0\u0798\0\u07b3\0\u07ce\0\u07e9\0\u02a3"+
    "\0\u0804\0\u081f\0\u083a\0\u02be\0\u0855\0\u0870\0\u088b\0\u08a6"+
    "\0\u08c1\0\u08dc\0\u08f7\0\u0912\0\u092d\0\u0948\0\u0963\0\u097e"+
    "\0\u0999\0\u09b4\0\u09cf\0\u09ea\0\u0a05\0\u0a20\0\u0a3b\0\u0a56"+
    "\0\u0a71\0\u0a8c\0\u0aa7\0\u0ac2\0\u0add\0\u0af8\0\u0b13\0\u0b2e"+
    "\0\u0b49\0\u0b64\0\u0b7f\0\u0b9a\0\u0bb5\0\u0bd0\0\u0beb\0\u0c06"+
    "\0\u0c21\0\u0c3c\0\u0c57\0\u0c72\0\u0c8d\0\u0ca8\0\u0cc3\0\u0cde"+
    "\0\u0cf9\0\u0d14\0\u0d2f\0\u0d4a\0\u0d65\0\u0d80\0\u07e9\0\u0d9b"+
    "\0\u0db6\0\u0dd1\0\u0dec\0\u0e07\0\u0e22\0\u0e3d\0\u0e58\0\u0e73"+
    "\0\u0e8e\0\u0ea9\0\u0ec4\0\u0edf\0\u0efa\0\u0f15\0\u0f30\0\u0f4b"+
    "\0\u0f66\0\u0f81\0\u0f9c\0\u0fb7\0\u0fd2\0\u0fed\0\u1008\0\u1023"+
    "\0\u103e\0\u1059\0\u1074\0\u108f\0\u10aa\0\u10c5\0\u10e0\0\u10fb";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[184];
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
    "\1\6\1\7\1\10\1\11\1\6\1\10\4\12\3\10"+
    "\1\13\1\14\1\6\13\10\6\15\2\14\1\15\1\14"+
    "\1\16\3\15\2\14\1\17\1\20\1\21\10\15\1\22"+
    "\1\23\1\14\1\24\1\22\1\14\4\25\5\14\1\22"+
    "\7\14\1\26\3\14\6\27\4\30\4\27\2\14\5\27"+
    "\1\31\2\27\1\32\1\33\1\27\2\34\1\14\1\35"+
    "\2\34\4\36\1\37\1\40\1\41\2\14\1\34\11\14"+
    "\1\34\1\14\2\6\1\0\1\42\2\6\11\0\1\6"+
    "\13\0\1\6\2\0\1\42\1\6\12\0\1\6\46\0"+
    "\4\6\2\43\2\0\1\6\1\0\21\6\6\0\4\12"+
    "\21\0\1\44\1\45\1\0\1\46\1\44\12\0\1\44"+
    "\13\0\2\47\1\0\1\50\2\47\11\0\1\47\13\0"+
    "\1\51\1\52\1\0\1\53\1\51\12\0\1\51\2\0"+
    "\1\54\10\0\2\22\1\0\1\55\2\22\4\56\3\0"+
    "\1\57\1\0\1\22\4\0\1\60\2\57\4\0\1\22"+
    "\2\0\1\55\1\22\12\0\1\22\13\0\4\22\2\61"+
    "\2\0\1\22\1\0\22\22\1\62\1\0\1\55\1\22"+
    "\1\0\4\63\5\0\1\22\21\0\4\30\13\0\1\31"+
    "\2\0\1\32\1\33\7\0\4\31\27\0\4\32\27\0"+
    "\4\33\21\0\2\64\1\0\1\65\6\64\1\66\1\67"+
    "\1\70\2\0\1\64\11\0\1\64\1\71\6\64\2\0"+
    "\1\64\1\0\23\64\1\0\1\65\2\64\4\72\1\66"+
    "\1\67\1\70\2\0\1\64\11\0\1\64\6\0\1\64"+
    "\25\0\3\67\1\73\2\67\2\0\1\67\1\0\1\67"+
    "\1\64\17\67\3\70\1\74\2\70\2\0\1\70\1\0"+
    "\2\70\1\64\16\70\2\6\1\0\1\42\2\75\1\76"+
    "\3\6\5\0\1\6\13\0\2\44\1\0\1\46\2\44"+
    "\11\0\1\44\13\0\1\44\2\0\1\46\1\44\12\0"+
    "\1\44\13\0\4\44\2\77\2\0\1\44\1\0\21\44"+
    "\4\47\2\100\2\0\1\47\1\0\21\47\2\51\1\0"+
    "\1\53\2\51\11\0\1\51\3\0\1\101\7\0\1\51"+
    "\2\0\1\53\1\51\12\0\1\51\13\0\4\51\2\102"+
    "\2\0\1\51\1\0\22\51\1\52\1\0\1\53\1\51"+
    "\12\0\1\51\21\0\4\56\3\0\1\57\6\0\1\60"+
    "\2\57\30\0\1\60\6\0\1\103\1\104\1\0\1\105"+
    "\1\103\1\0\4\60\1\0\1\106\1\107\2\0\1\103"+
    "\13\0\2\22\1\0\1\55\2\110\1\111\3\22\3\0"+
    "\1\57\1\0\1\22\4\0\1\60\2\57\4\0\3\67"+
    "\1\73\2\112\1\113\4\67\1\114\17\67\3\70\1\74"+
    "\2\115\1\116\5\70\1\117\16\70\2\6\1\0\1\42"+
    "\2\120\1\76\3\6\5\0\1\6\13\0\2\6\1\0"+
    "\1\42\2\6\1\0\1\6\7\0\1\6\13\0\2\44"+
    "\1\0\1\46\2\121\1\122\3\44\5\0\1\44\13\0"+
    "\2\47\1\0\1\50\2\123\1\124\3\47\5\0\1\47"+
    "\13\0\2\51\1\0\1\53\2\125\1\126\3\51\5\0"+
    "\1\51\3\0\1\101\7\0\2\103\1\0\1\105\2\103"+
    "\4\127\5\0\1\103\13\0\1\103\2\0\1\105\1\103"+
    "\12\0\1\103\13\0\4\103\2\130\2\0\1\103\1\0"+
    "\21\103\3\106\1\131\2\106\2\0\1\106\1\0\1\106"+
    "\1\127\17\106\3\107\1\132\2\107\2\0\1\107\1\0"+
    "\2\107\1\127\16\107\2\22\1\0\1\55\2\133\1\111"+
    "\3\22\3\0\1\57\1\0\1\22\4\0\1\60\2\57"+
    "\4\0\2\22\1\0\1\55\2\22\1\56\1\22\2\56"+
    "\3\0\1\57\1\0\1\22\4\0\1\60\2\57\4\0"+
    "\3\67\1\73\2\134\1\113\4\67\1\64\22\67\1\73"+
    "\2\67\1\0\2\67\1\0\1\67\1\64\17\67\2\114"+
    "\1\67\1\135\2\114\2\64\1\114\1\64\1\136\1\114"+
    "\1\137\2\67\1\114\11\67\1\114\1\140\3\70\1\74"+
    "\2\141\1\116\5\70\1\64\21\70\1\74\2\70\1\0"+
    "\2\70\1\0\2\70\1\64\16\70\2\117\1\70\1\142"+
    "\2\117\2\64\1\117\1\64\1\143\1\137\1\117\2\70"+
    "\1\117\11\70\1\117\1\144\2\6\1\0\1\42\2\145"+
    "\1\76\3\6\5\0\1\6\13\0\2\44\1\0\1\46"+
    "\2\146\1\122\3\44\5\0\1\44\13\0\2\44\1\0"+
    "\1\46\2\44\1\0\1\44\7\0\1\44\13\0\2\47"+
    "\1\0\1\50\2\147\1\124\3\47\5\0\1\47\13\0"+
    "\2\47\1\0\1\50\2\47\1\0\1\47\7\0\1\47"+
    "\13\0\2\51\1\0\1\53\2\150\1\126\3\51\5\0"+
    "\1\51\3\0\1\101\7\0\2\51\1\0\1\53\2\51"+
    "\1\0\1\51\7\0\1\51\3\0\1\101\15\0\4\127"+
    "\21\0\2\103\1\0\1\105\2\151\1\152\3\103\5\0"+
    "\1\103\13\0\3\106\1\131\2\153\1\154\4\106\1\155"+
    "\17\106\3\107\1\132\2\156\1\157\5\107\1\160\16\107"+
    "\2\22\1\0\1\55\2\161\1\111\3\22\3\0\1\57"+
    "\1\0\1\22\4\0\1\60\2\57\4\0\3\67\1\73"+
    "\2\162\1\113\4\67\1\64\17\67\3\114\1\163\2\164"+
    "\1\113\1\67\1\114\1\67\21\114\3\67\1\73\1\67"+
    "\1\114\2\0\1\67\1\0\1\67\1\64\17\67\3\137"+
    "\1\165\2\137\2\0\1\137\1\0\1\137\1\117\1\114"+
    "\16\137\3\70\1\74\2\166\1\116\5\70\1\64\16\70"+
    "\3\117\1\167\2\170\1\116\1\70\1\117\1\70\21\117"+
    "\3\70\1\74\1\70\1\117\2\0\1\70\1\0\2\70"+
    "\1\64\16\70\2\6\1\0\1\42\2\171\1\76\3\6"+
    "\5\0\1\6\13\0\2\44\1\0\1\46\2\172\1\122"+
    "\3\44\5\0\1\44\13\0\2\47\1\0\1\50\2\173"+
    "\1\124\3\47\5\0\1\47\13\0\2\51\1\0\1\53"+
    "\2\174\1\126\3\51\5\0\1\51\3\0\1\101\7\0"+
    "\2\103\1\0\1\105\2\175\1\152\3\103\5\0\1\103"+
    "\13\0\2\103\1\0\1\105\2\103\1\127\1\103\2\127"+
    "\5\0\1\103\13\0\3\106\1\131\2\176\1\154\4\106"+
    "\1\127\22\106\1\131\2\106\1\0\2\106\1\0\1\106"+
    "\1\127\22\106\1\131\2\106\2\127\1\155\1\127\1\106"+
    "\1\127\17\106\3\107\1\132\2\177\1\157\5\107\1\127"+
    "\21\107\1\132\2\107\1\0\2\107\1\0\2\107\1\127"+
    "\21\107\1\132\2\107\2\127\1\160\1\127\2\107\1\127"+
    "\16\107\2\22\1\0\1\55\2\200\1\111\3\22\3\0"+
    "\1\57\1\0\1\22\4\0\1\60\2\57\4\0\3\67"+
    "\1\73\2\201\1\113\4\67\1\64\17\67\2\114\1\67"+
    "\1\135\2\164\1\202\3\114\1\136\1\114\1\137\2\67"+
    "\1\114\11\67\1\114\1\140\2\114\1\67\1\135\2\203"+
    "\1\202\3\114\1\136\1\114\1\137\2\67\1\114\11\67"+
    "\1\114\1\140\3\137\1\165\2\204\1\205\4\137\2\206"+
    "\16\137\3\70\1\74\2\207\1\116\5\70\1\64\16\70"+
    "\2\117\1\70\1\142\2\170\1\210\3\117\1\143\1\137"+
    "\1\117\2\70\1\117\11\70\1\117\1\144\2\117\1\70"+
    "\1\142\2\211\1\210\3\117\1\143\1\137\1\117\2\70"+
    "\1\117\11\70\1\117\1\144\2\6\1\0\1\42\2\212"+
    "\1\76\3\6\5\0\1\6\13\0\2\44\1\0\1\46"+
    "\2\213\1\122\3\44\5\0\1\44\13\0\2\47\1\0"+
    "\1\50\2\214\1\124\3\47\5\0\1\47\13\0\2\51"+
    "\1\0\1\53\2\215\1\126\3\51\5\0\1\51\3\0"+
    "\1\101\7\0\2\103\1\0\1\105\2\216\1\152\3\103"+
    "\5\0\1\103\13\0\3\106\1\131\2\217\1\154\4\106"+
    "\1\127\17\106\3\107\1\132\2\220\1\157\5\107\1\127"+
    "\16\107\2\22\1\0\1\55\2\221\1\111\3\22\3\0"+
    "\1\57\1\0\1\22\4\0\1\60\2\57\4\0\3\67"+
    "\1\73\2\222\1\113\4\67\1\64\17\67\2\114\1\67"+
    "\1\135\2\114\1\64\2\114\1\64\1\136\1\114\1\137"+
    "\2\67\1\114\11\67\1\114\1\140\2\114\1\67\1\135"+
    "\2\223\1\202\3\114\1\136\1\114\1\137\2\67\1\114"+
    "\11\67\1\114\1\140\3\137\1\165\2\224\1\205\4\137"+
    "\1\117\1\114\21\137\1\165\2\137\1\0\2\137\1\0"+
    "\1\137\1\117\1\114\16\137\2\206\1\137\1\225\2\206"+
    "\2\64\1\206\1\64\1\226\2\206\2\137\1\206\11\137"+
    "\1\206\1\227\3\70\1\74\2\230\1\116\5\70\1\64"+
    "\16\70\2\117\1\70\1\142\2\117\1\64\2\117\1\64"+
    "\1\143\1\137\1\117\2\70\1\117\11\70\1\117\1\144"+
    "\2\117\1\70\1\142\2\231\1\210\3\117\1\143\1\137"+
    "\1\117\2\70\1\117\11\70\1\117\1\144\2\6\1\0"+
    "\1\42\2\6\1\76\3\6\5\0\1\6\13\0\2\44"+
    "\1\0\1\46\2\232\1\122\3\44\5\0\1\44\13\0"+
    "\2\47\1\0\1\50\2\233\1\124\3\47\5\0\1\47"+
    "\13\0\2\51\1\0\1\53\2\234\1\126\3\51\5\0"+
    "\1\51\3\0\1\101\7\0\2\103\1\0\1\105\2\235"+
    "\1\152\3\103\5\0\1\103\13\0\3\106\1\131\2\236"+
    "\1\154\4\106\1\127\17\106\3\107\1\132\2\237\1\157"+
    "\5\107\1\127\16\107\2\22\1\0\1\55\2\22\1\111"+
    "\3\22\3\0\1\57\1\0\1\22\4\0\1\60\2\57"+
    "\4\0\3\67\1\73\2\240\1\113\4\67\1\64\17\67"+
    "\2\114\1\67\1\135\2\241\1\202\3\114\1\136\1\114"+
    "\1\137\2\67\1\114\11\67\1\114\1\140\3\137\1\165"+
    "\2\242\1\205\4\137\1\117\1\114\16\137\3\206\1\243"+
    "\2\244\1\205\1\137\1\206\1\137\21\206\3\137\1\165"+
    "\1\137\1\206\2\0\1\137\1\0\1\137\1\117\1\114"+
    "\16\137\3\70\1\74\2\245\1\116\5\70\1\64\16\70"+
    "\2\117\1\70\1\142\2\246\1\210\3\117\1\143\1\137"+
    "\1\117\2\70\1\117\11\70\1\117\1\144\2\44\1\0"+
    "\1\46\2\44\1\122\3\44\5\0\1\44\13\0\2\47"+
    "\1\0\1\50\2\47\1\124\3\47\5\0\1\47\13\0"+
    "\2\51\1\0\1\53\2\51\1\126\3\51\5\0\1\51"+
    "\3\0\1\101\7\0\2\103\1\0\1\105\2\247\1\152"+
    "\3\103\5\0\1\103\13\0\3\106\1\131\2\250\1\154"+
    "\4\106\1\127\17\106\3\107\1\132\2\251\1\157\5\107"+
    "\1\127\16\107\3\67\1\73\2\67\1\113\4\67\1\64"+
    "\17\67\2\114\1\67\1\135\2\252\1\202\3\114\1\136"+
    "\1\114\1\137\2\67\1\114\11\67\1\114\1\140\3\137"+
    "\1\165\2\253\1\205\4\137\1\117\1\114\16\137\2\206"+
    "\1\137\1\225\2\244\1\254\3\206\1\226\2\206\2\137"+
    "\1\206\11\137\1\206\1\227\2\206\1\137\1\225\2\255"+
    "\1\254\3\206\1\226\2\206\2\137\1\206\11\137\1\206"+
    "\1\227\3\70\1\74\2\70\1\116\5\70\1\64\16\70"+
    "\2\117\1\70\1\142\2\256\1\210\3\117\1\143\1\137"+
    "\1\117\2\70\1\117\11\70\1\117\1\144\2\103\1\0"+
    "\1\105\2\103\1\152\3\103\5\0\1\103\13\0\3\106"+
    "\1\131\2\257\1\154\4\106\1\127\17\106\3\107\1\132"+
    "\2\260\1\157\5\107\1\127\16\107\2\114\1\67\1\135"+
    "\2\261\1\202\3\114\1\136\1\114\1\137\2\67\1\114"+
    "\11\67\1\114\1\140\3\137\1\165\2\262\1\205\4\137"+
    "\1\117\1\114\16\137\2\206\1\137\1\225\2\206\1\64"+
    "\2\206\1\64\1\226\2\206\2\137\1\206\11\137\1\206"+
    "\1\227\2\206\1\137\1\225\2\263\1\254\3\206\1\226"+
    "\2\206\2\137\1\206\11\137\1\206\1\227\2\117\1\70"+
    "\1\142\2\264\1\210\3\117\1\143\1\137\1\117\2\70"+
    "\1\117\11\70\1\117\1\144\3\106\1\131\2\106\1\154"+
    "\4\106\1\127\17\106\3\107\1\132\2\107\1\157\5\107"+
    "\1\127\16\107\2\114\1\67\1\135\2\114\1\202\3\114"+
    "\1\136\1\114\1\137\2\67\1\114\11\67\1\114\1\140"+
    "\3\137\1\165\2\265\1\205\4\137\1\117\1\114\16\137"+
    "\2\206\1\137\1\225\2\266\1\254\3\206\1\226\2\206"+
    "\2\137\1\206\11\137\1\206\1\227\2\117\1\70\1\142"+
    "\2\117\1\210\3\117\1\143\1\137\1\117\2\70\1\117"+
    "\11\70\1\117\1\144\3\137\1\165\2\137\1\205\4\137"+
    "\1\117\1\114\16\137\2\206\1\137\1\225\2\267\1\254"+
    "\3\206\1\226\2\206\2\137\1\206\11\137\1\206\1\227"+
    "\2\206\1\137\1\225\2\270\1\254\3\206\1\226\2\206"+
    "\2\137\1\206\11\137\1\206\1\227\2\206\1\137\1\225"+
    "\2\206\1\254\3\206\1\226\2\206\2\137\1\206\11\137"+
    "\1\206\1\227";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4374];
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
  private static final String[] ZZ_ERROR_MSG = {
          "Unkown internal scanner error",
          "Error: could not match input",
          "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\1\1\0\2\1\1\11\2\1\3\11\1\1"+
    "\1\11\6\1\2\11\12\1\1\0\2\1\2\0\1\1"+
    "\1\0\1\1\4\0\1\1\2\0\1\1\7\0\1\11"+
    "\3\0\4\1\1\11\2\1\4\0\2\1\6\0\11\1"+
    "\2\0\1\1\4\0\1\1\3\0\7\1\2\0\1\1"+
    "\2\0\2\1\7\0\5\1\2\0\1\1\11\0\5\1"+
    "\2\0\1\1\5\0\1\1\2\0\4\1\11\0\1\1"+
    "\21\0";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[184];
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
  private char[] zzChars;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /**
   * this buffer contains the current text to be matched and is
   * the source of the yytext() string
   */
  private char[] zzBuffer;

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

  /* user code: */
	// position methods
	public int position() { return yychar; }
	public int length()   { return yylength(); }
	public int line()     { return -1; /*yyline;*/ }   	// for debugging
	public int column()   { return -1; /*yycolumn;*/ } 	// for debugging

	// state methods
	public void stateReset() 		{ yybegin(YYINITIAL); }
	public void stateSelector() 	{ yybegin(SELECTOR); }
	public void stateAttr()			{ yybegin(ATTR); }
	public void stateCombinator()	{ yybegin(COMBINATOR); }
	public void statePseudoFn()		{ yybegin(PSEUDO_FN); }

	// fast methods
	public final CharSequence xxtext() {
		return CharBuffer.wrap(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
	}

	public final String yytext(int startIndex) {
		startIndex += zzStartRead;
		return new String(zzBuffer, startIndex, zzMarkedPos - startIndex);
	}
	public final String yytext(int startIndex, int endIndexOffset) {
		startIndex += zzStartRead;
		return new String(zzBuffer, startIndex, zzMarkedPos - endIndexOffset - startIndex);
	}

	ArrayList<CssSelector> selectors = new ArrayList<>();
	CssSelector cssSelector;
	String pseudoFnName;

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
    while (i < 128) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  /**
   * Creates a new scanner.
   */
  CSSellyLexer(char[] input) {
    this.zzChars = input;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   */
  private boolean zzRefill() {
  	if (zzBuffer == null) {
        zzBuffer = zzChars;
        zzEndRead += zzChars.length;
        return false;
    }
    return true;
  }

  /**
   * Closes the input stream.
   */
  public final void yyclose() {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */
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
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
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
  private static void zzScanError(int errorCode) {
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
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int yylex() throws java.io.IOException {
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

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 1: 
          { cssSelector.setCombinator(Combinator.DESCENDANT); stateReset();
          }
        case 20: break;
        case 2: 
          { cssSelector = new CssSelector(yytext()); selectors.add(cssSelector); stateSelector();
          }
        case 21: break;
        case 3: 
          { cssSelector = new CssSelector(); selectors.add(cssSelector); yypushback(1); stateSelector();
          }
        case 22: break;
        case 4: 
          { /* ignore whitespaces */
          }
        case 23: break;
        case 5: 
          { cssSelector = new CssSelector(); selectors.add(cssSelector); stateSelector();
          }
        case 24: break;
        case 6: 
          { throw new CSSellyException("Illegal character <"+ yytext() +">.", yystate(), line(), column());
          }
        case 25: break;
        case 7: 
          { yypushback(1); stateCombinator();
          }
        case 26: break;
        case 8: 
          { stateAttr();
          }
        case 27: break;
        case 9: 
          { cssSelector.addAttributeSelector(yytext());
          }
        case 28: break;
        case 10: 
          { stateSelector();
          }
        case 29: break;
        case 11: 
          { throw new CSSellyException("Invalid combinator <"+ yytext() +">.", yystate(), line(), column());
          }
        case 30: break;
        case 12: 
          { cssSelector.setCombinator(Combinator.GENERAL_SIBLING); stateReset();
          }
        case 31: break;
        case 13: 
          { cssSelector.setCombinator(Combinator.CHILD); stateReset();
          }
        case 32: break;
        case 14: 
          { cssSelector.setCombinator(Combinator.ADJACENT_SIBLING); stateReset();
          }
        case 33: break;
        case 15: 
          { cssSelector.addClassSelector(yytext(1));
          }
        case 34: break;
        case 16: 
          { cssSelector.addIdSelector(yytext(1));
          }
        case 35: break;
        case 17: 
          { cssSelector.addPseudoClassSelector(yytext( yycharat(1) == ':' ? 2 : 1 ));
          }
        case 36: break;
        case 18: 
          { cssSelector.addPseudoFunctionSelector(pseudoFnName, yytext(0, 1)); stateSelector();
          }
        case 37: break;
        case 19: 
          { pseudoFnName = yytext(yycharat(1) == ':' ? 2 : 1,1); statePseudoFn();
          }
        case 38: break;
        default:
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { return 0; }
          }
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}

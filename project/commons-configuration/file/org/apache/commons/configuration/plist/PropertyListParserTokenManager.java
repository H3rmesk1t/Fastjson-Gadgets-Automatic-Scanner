package org.apache.commons.configuration.plist;

import java.io.IOException;
import java.io.PrintStream;

public class PropertyListParserTokenManager implements PropertyListParserConstants {
   public PrintStream debugStream;
   static final long[] jjbitVec0 = new long[]{0L, 0L, -1L, -1L};
   static final int[] jjnextStates = new int[]{10, 12, 13};
   public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, "(", ")", ",", "{", "}", ";", "=", "<", ">", "<*D", "\"", null, null, null, null, null, null, null, "\\\""};
   public static final String[] lexStateNames = new String[]{"DEFAULT", "IN_COMMENT", "IN_SINGLE_LINE_COMMENT"};
   public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, 1, -1, 0, 2, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
   static final long[] jjtoToken = new long[]{1044379649L};
   static final long[] jjtoSkip = new long[]{670L};
   static final long[] jjtoSpecial = new long[]{512L};
   static final long[] jjtoMore = new long[]{1376L};
   protected SimpleCharStream input_stream;
   private final int[] jjrounds;
   private final int[] jjstateSet;
   private final StringBuilder jjimage;
   private StringBuilder image;
   private int jjimageLen;
   private int lengthOfMatch;
   protected char curChar;
   int curLexState;
   int defaultLexState;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;

   public void setDebugStream(PrintStream ds) {
      this.debugStream = ds;
   }

   private final int jjStopStringLiteralDfa_0(int pos, long active0) {
      switch(pos) {
      case 0:
         if ((active0 & 536871200L) != 0L) {
            this.jjmatchedKind = 27;
            return 8;
         } else if ((active0 & 524288L) != 0L) {
            return 8;
         } else if ((active0 & 2097152L) != 0L) {
            return 14;
         } else {
            if ((active0 & 1310720L) != 0L) {
               return 6;
            }

            return -1;
         }
      case 1:
         if ((active0 & 1048576L) != 0L) {
            this.jjmatchedKind = 27;
            this.jjmatchedPos = 1;
            return 3;
         } else {
            if ((active0 & 288L) != 0L) {
               return 8;
            }

            return -1;
         }
      default:
         return -1;
      }
   }

   private final int jjStartNfa_0(int pos, long active0) {
      return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
   }

   private int jjStopAtPos(int pos, int kind) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;
      return pos + 1;
   }

   private int jjMoveStringLiteralDfa0_0() {
      switch(this.curChar) {
      case '"':
         return this.jjStartNfaWithStates_0(0, 21, 14);
      case '(':
         return this.jjStopAtPos(0, 11);
      case ')':
         return this.jjStopAtPos(0, 12);
      case ',':
         return this.jjStopAtPos(0, 13);
      case '/':
         return this.jjMoveStringLiteralDfa1_0(288L);
      case ';':
         return this.jjStopAtPos(0, 16);
      case '<':
         this.jjmatchedKind = 18;
         return this.jjMoveStringLiteralDfa1_0(1048576L);
      case '=':
         return this.jjStopAtPos(0, 17);
      case '>':
         return this.jjStartNfaWithStates_0(0, 19, 8);
      case '\\':
         return this.jjMoveStringLiteralDfa1_0(536870912L);
      case '{':
         return this.jjStopAtPos(0, 14);
      case '}':
         return this.jjStopAtPos(0, 15);
      default:
         return this.jjMoveNfa_0(0, 0);
      }
   }

   private int jjMoveStringLiteralDfa1_0(long active0) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var4) {
         this.jjStopStringLiteralDfa_0(0, active0);
         return 1;
      }

      switch(this.curChar) {
      case '"':
         if ((active0 & 536870912L) != 0L) {
            return this.jjStopAtPos(1, 29);
         }
         break;
      case '*':
         if ((active0 & 32L) != 0L) {
            return this.jjStartNfaWithStates_0(1, 5, 8);
         }

         return this.jjMoveStringLiteralDfa2_0(active0, 1048576L);
      case '/':
         if ((active0 & 256L) != 0L) {
            return this.jjStartNfaWithStates_0(1, 8, 8);
         }
      }

      return this.jjStartNfa_0(0, active0);
   }

   private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
      if ((active0 &= old0) == 0L) {
         return this.jjStartNfa_0(0, old0);
      } else {
         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var6) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
         }

         switch(this.curChar) {
         case 'D':
            if ((active0 & 1048576L) != 0L) {
               return this.jjStartNfaWithStates_0(2, 20, 15);
            }
         default:
            return this.jjStartNfa_0(1, active0);
         }
      }
   }

   private int jjStartNfaWithStates_0(int pos, int kind, int state) {
      this.jjmatchedKind = kind;
      this.jjmatchedPos = pos;

      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var5) {
         return pos + 1;
      }

      return this.jjMoveNfa_0(state, pos + 1);
   }

   private int jjMoveNfa_0(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 14;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < '@') {
            l = 1L << this.curChar;

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 0:
                  if ((-2882324673712891393L & l) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  } else if (this.curChar == '"') {
                     this.jjCheckNAddStates(0, 2);
                  }

                  if (this.curChar == '<') {
                     this.jjstateSet[this.jjnewStateCnt++] = 6;
                  }

                  if (this.curChar == '<') {
                     this.jjCheckNAddTwoStates(1, 2);
                  }
                  break;
               case 1:
                  if ((287948905469978112L & l) != 0L) {
                     this.jjCheckNAddTwoStates(1, 2);
                  }
                  break;
               case 2:
                  if (this.curChar == '>' && kind > 25) {
                     kind = 25;
                  }
                  break;
               case 3:
               case 8:
                  if ((-2882324673712891393L & l) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }
                  break;
               case 4:
                  if ((576223262086791168L & l) != 0L) {
                     this.jjCheckNAddTwoStates(4, 5);
                  }
                  break;
               case 5:
                  if (this.curChar == '>' && kind > 26) {
                     kind = 26;
                  }
                  break;
               case 6:
                  if ((-2882324673712891393L & l) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }

                  if ((287948905469978112L & l) != 0L) {
                     this.jjCheckNAddTwoStates(1, 2);
                  } else if (this.curChar == '*') {
                     this.jjstateSet[this.jjnewStateCnt++] = 3;
                  } else if (this.curChar == '>' && kind > 25) {
                     kind = 25;
                  }
                  break;
               case 7:
                  if (this.curChar == '<') {
                     this.jjstateSet[this.jjnewStateCnt++] = 6;
                  }
                  break;
               case 9:
               case 11:
                  if (this.curChar == '"') {
                     this.jjCheckNAddStates(0, 2);
                  }
                  break;
               case 10:
                  if ((-17179869185L & l) != 0L) {
                     this.jjCheckNAddStates(0, 2);
                  }
               case 12:
               default:
                  break;
               case 13:
                  if (this.curChar == '"' && kind > 28) {
                     kind = 28;
                  }
                  break;
               case 14:
                  if ((-17179869185L & l) != 0L) {
                     this.jjCheckNAddStates(0, 2);
                  } else if (this.curChar == '"' && kind > 28) {
                     kind = 28;
                  }
                  break;
               case 15:
                  if ((-2882324673712891393L & l) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }

                  if ((576223262086791168L & l) != 0L) {
                     this.jjCheckNAddTwoStates(4, 5);
                  } else if (this.curChar == '>' && kind > 26) {
                     kind = 26;
                  }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 0:
               case 8:
                  if ((-2882303761517117441L & l) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }
                  break;
               case 1:
                  if ((541165879422L & l) != 0L) {
                     this.jjCheckNAddTwoStates(1, 2);
                  }
               case 2:
               case 5:
               case 7:
               case 9:
               case 11:
               case 13:
               default:
                  break;
               case 3:
                  if ((-2882303761517117441L & l) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }

                  if (this.curChar == 'D') {
                     this.jjCheckNAddTwoStates(4, 5);
                  }
                  break;
               case 4:
                  if (this.curChar == 'Z') {
                     this.jjCheckNAddTwoStates(4, 5);
                  }
                  break;
               case 6:
                  if ((-2882303761517117441L & l) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }

                  if ((541165879422L & l) != 0L) {
                     this.jjCheckNAddTwoStates(1, 2);
                  }
                  break;
               case 10:
                  this.jjCheckNAddStates(0, 2);
                  break;
               case 12:
                  if (this.curChar == '\\') {
                     this.jjstateSet[this.jjnewStateCnt++] = 11;
                  }
                  break;
               case 14:
                  this.jjCheckNAddStates(0, 2);
                  if (this.curChar == '\\') {
                     this.jjstateSet[this.jjnewStateCnt++] = 11;
                  }
                  break;
               case 15:
                  if ((-2882303761517117441L & l) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }

                  if (this.curChar == 'Z') {
                     this.jjCheckNAddTwoStates(4, 5);
                  }
               }
            } while(i != startsAt);
         } else {
            int i2 = (this.curChar & 255) >> 6;
            long l2 = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 0:
                  if ((jjbitVec0[i2] & l2) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }
               case 1:
               case 2:
               case 4:
               case 5:
               case 7:
               case 9:
               case 11:
               case 12:
               case 13:
               default:
                  break;
               case 3:
                  if ((jjbitVec0[i2] & l2) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }
                  break;
               case 6:
                  if ((jjbitVec0[i2] & l2) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }
                  break;
               case 8:
               case 15:
                  if ((jjbitVec0[i2] & l2) != 0L) {
                     if (kind > 27) {
                        kind = 27;
                     }

                     this.jjCheckNAdd(8);
                  }
                  break;
               case 10:
               case 14:
                  if ((jjbitVec0[i2] & l2) != 0L) {
                     this.jjCheckNAddStates(0, 2);
                  }
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 14 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var9) {
            return curPos;
         }
      }
   }

   private int jjMoveStringLiteralDfa0_2() {
      return this.jjMoveNfa_2(0, 0);
   }

   private int jjMoveNfa_2(int startState, int curPos) {
      int startsAt = 0;
      this.jjnewStateCnt = 3;
      int i = 1;
      this.jjstateSet[0] = startState;
      int kind = Integer.MAX_VALUE;

      while(true) {
         if (++this.jjround == Integer.MAX_VALUE) {
            this.ReInitRounds();
         }

         long l;
         if (this.curChar < '@') {
            l = 1L << this.curChar;

            do {
               --i;
               switch(this.jjstateSet[i]) {
               case 0:
                  if ((9216L & l) != 0L && kind > 9) {
                     kind = 9;
                  }

                  if (this.curChar == '\r') {
                     this.jjstateSet[this.jjnewStateCnt++] = 1;
                  }
                  break;
               case 1:
                  if (this.curChar == '\n' && kind > 9) {
                     kind = 9;
                  }
                  break;
               case 2:
                  if (this.curChar == '\r') {
                     this.jjstateSet[this.jjnewStateCnt++] = 1;
                  }
               }
            } while(i != startsAt);
         } else if (this.curChar < 128) {
            l = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               }
            } while(i != startsAt);
         } else {
            int i2 = (this.curChar & 255) >> 6;
            long var7 = 1L << (this.curChar & 63);

            do {
               --i;
               switch(this.jjstateSet[i]) {
               }
            } while(i != startsAt);
         }

         if (kind != Integer.MAX_VALUE) {
            this.jjmatchedKind = kind;
            this.jjmatchedPos = curPos;
            kind = Integer.MAX_VALUE;
         }

         ++curPos;
         if ((i = this.jjnewStateCnt) == (startsAt = 3 - (this.jjnewStateCnt = startsAt))) {
            return curPos;
         }

         try {
            this.curChar = this.input_stream.readChar();
         } catch (IOException var9) {
            return curPos;
         }
      }
   }

   private int jjMoveStringLiteralDfa0_1() {
      switch(this.curChar) {
      case '*':
         return this.jjMoveStringLiteralDfa1_1(128L);
      default:
         return 1;
      }
   }

   private int jjMoveStringLiteralDfa1_1(long active0) {
      try {
         this.curChar = this.input_stream.readChar();
      } catch (IOException var4) {
         return 1;
      }

      switch(this.curChar) {
      case '/':
         if ((active0 & 128L) != 0L) {
            return this.jjStopAtPos(1, 7);
         }

         return 2;
      default:
         return 2;
      }
   }

   public PropertyListParserTokenManager(SimpleCharStream stream) {
      this.debugStream = System.out;
      this.jjrounds = new int[14];
      this.jjstateSet = new int[28];
      this.jjimage = new StringBuilder();
      this.image = this.jjimage;
      this.curLexState = 0;
      this.defaultLexState = 0;
      this.input_stream = stream;
   }

   public PropertyListParserTokenManager(SimpleCharStream stream, int lexState) {
      this(stream);
      this.SwitchTo(lexState);
   }

   public void ReInit(SimpleCharStream stream) {
      this.jjmatchedPos = this.jjnewStateCnt = 0;
      this.curLexState = this.defaultLexState;
      this.input_stream = stream;
      this.ReInitRounds();
   }

   private void ReInitRounds() {
      this.jjround = -2147483647;

      for(int i = 14; i-- > 0; this.jjrounds[i] = Integer.MIN_VALUE) {
      }

   }

   public void ReInit(SimpleCharStream stream, int lexState) {
      this.ReInit(stream);
      this.SwitchTo(lexState);
   }

   public void SwitchTo(int lexState) {
      if (lexState < 3 && lexState >= 0) {
         this.curLexState = lexState;
      } else {
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
      }
   }

   protected Token jjFillToken() {
      String im = jjstrLiteralImages[this.jjmatchedKind];
      String curTokenImage = im == null ? this.input_stream.GetImage() : im;
      int beginLine = this.input_stream.getBeginLine();
      int beginColumn = this.input_stream.getBeginColumn();
      int endLine = this.input_stream.getEndLine();
      int endColumn = this.input_stream.getEndColumn();
      Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
      t.beginLine = beginLine;
      t.endLine = endLine;
      t.beginColumn = beginColumn;
      t.endColumn = endColumn;
      return t;
   }

   public Token getNextToken() {
      Token specialToken = null;
      int curPos = 0;

      label123:
      while(true) {
         Token matchedToken;
         try {
            this.curChar = this.input_stream.BeginToken();
         } catch (IOException var9) {
            this.jjmatchedKind = 0;
            matchedToken = this.jjFillToken();
            matchedToken.specialToken = specialToken;
            return matchedToken;
         }

         this.image = this.jjimage;
         this.image.setLength(0);
         this.jjimageLen = 0;

         while(true) {
            switch(this.curLexState) {
            case 0:
               try {
                  this.input_stream.backup(0);

                  while(this.curChar <= ' ' && (4294977024L & 1L << this.curChar) != 0L) {
                     this.curChar = this.input_stream.BeginToken();
                  }
               } catch (IOException var12) {
                  continue label123;
               }

               this.jjmatchedKind = Integer.MAX_VALUE;
               this.jjmatchedPos = 0;
               curPos = this.jjMoveStringLiteralDfa0_0();
               break;
            case 1:
               this.jjmatchedKind = Integer.MAX_VALUE;
               this.jjmatchedPos = 0;
               curPos = this.jjMoveStringLiteralDfa0_1();
               if (this.jjmatchedPos == 0 && this.jjmatchedKind > 6) {
                  this.jjmatchedKind = 6;
               }
               break;
            case 2:
               this.jjmatchedKind = Integer.MAX_VALUE;
               this.jjmatchedPos = 0;
               curPos = this.jjMoveStringLiteralDfa0_2();
               if (this.jjmatchedPos == 0 && this.jjmatchedKind > 10) {
                  this.jjmatchedKind = 10;
               }
            }

            if (this.jjmatchedKind == Integer.MAX_VALUE) {
               break label123;
            }

            if (this.jjmatchedPos + 1 < curPos) {
               this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }

            if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
               matchedToken = this.jjFillToken();
               matchedToken.specialToken = specialToken;
               if (jjnewLexState[this.jjmatchedKind] != -1) {
                  this.curLexState = jjnewLexState[this.jjmatchedKind];
               }

               return matchedToken;
            }

            if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
               if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 63)) != 0L) {
                  matchedToken = this.jjFillToken();
                  if (specialToken == null) {
                     specialToken = matchedToken;
                  } else {
                     matchedToken.specialToken = specialToken;
                     specialToken = specialToken.next = matchedToken;
                  }

                  this.SkipLexicalActions(matchedToken);
               } else {
                  this.SkipLexicalActions((Token)null);
               }

               if (jjnewLexState[this.jjmatchedKind] != -1) {
                  this.curLexState = jjnewLexState[this.jjmatchedKind];
               }
               break;
            }

            this.jjimageLen += this.jjmatchedPos + 1;
            if (jjnewLexState[this.jjmatchedKind] != -1) {
               this.curLexState = jjnewLexState[this.jjmatchedKind];
            }

            curPos = 0;
            this.jjmatchedKind = Integer.MAX_VALUE;

            try {
               this.curChar = this.input_stream.readChar();
            } catch (IOException var11) {
               break label123;
            }
         }
      }

      int error_line = this.input_stream.getEndLine();
      int error_column = this.input_stream.getEndColumn();
      String error_after = null;
      boolean EOFSeen = false;

      try {
         this.input_stream.readChar();
         this.input_stream.backup(1);
      } catch (IOException var10) {
         EOFSeen = true;
         error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
         if (this.curChar != '\n' && this.curChar != '\r') {
            ++error_column;
         } else {
            ++error_line;
            error_column = 0;
         }
      }

      if (!EOFSeen) {
         this.input_stream.backup(1);
         error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
      }

      throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
   }

   void SkipLexicalActions(Token matchedToken) {
      switch(this.jjmatchedKind) {
      default:
      }
   }

   private void jjCheckNAdd(int state) {
      if (this.jjrounds[state] != this.jjround) {
         this.jjstateSet[this.jjnewStateCnt++] = state;
         this.jjrounds[state] = this.jjround;
      }

   }

   private void jjAddStates(int start, int end) {
      do {
         this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
      } while(start++ != end);

   }

   private void jjCheckNAddTwoStates(int state1, int state2) {
      this.jjCheckNAdd(state1);
      this.jjCheckNAdd(state2);
   }

   private void jjCheckNAddStates(int start, int end) {
      do {
         this.jjCheckNAdd(jjnextStates[start]);
      } while(start++ != end);

   }
}

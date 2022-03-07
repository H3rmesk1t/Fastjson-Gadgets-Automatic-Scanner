package org.apache.commons.configuration.plist;

public class TokenMgrError extends Error {
   private static final long serialVersionUID = 1L;
   static final int LEXICAL_ERROR = 0;
   static final int STATIC_LEXER_ERROR = 1;
   static final int INVALID_LEXICAL_STATE = 2;
   static final int LOOP_DETECTED = 3;
   int errorCode;

   protected static final String addEscapes(String str) {
      StringBuffer retval = new StringBuffer();

      for(int i = 0; i < str.length(); ++i) {
         switch(str.charAt(i)) {
         case '\u0000':
            break;
         case '\b':
            retval.append("\\b");
            break;
         case '\t':
            retval.append("\\t");
            break;
         case '\n':
            retval.append("\\n");
            break;
         case '\f':
            retval.append("\\f");
            break;
         case '\r':
            retval.append("\\r");
            break;
         case '"':
            retval.append("\\\"");
            break;
         case '\'':
            retval.append("\\'");
            break;
         case '\\':
            retval.append("\\\\");
            break;
         default:
            char ch;
            if ((ch = str.charAt(i)) >= ' ' && ch <= '~') {
               retval.append(ch);
            } else {
               String s = "0000" + Integer.toString(ch, 16);
               retval.append("\\u" + s.substring(s.length() - 4, s.length()));
            }
         }
      }

      return retval.toString();
   }

   protected static String LexicalError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar) {
      return "Lexical error at line " + errorLine + ", column " + errorColumn + ".  Encountered: " + (EOFSeen ? "<EOF> " : "\"" + addEscapes(String.valueOf(curChar)) + "\"" + " (" + curChar + "), ") + "after : \"" + addEscapes(errorAfter) + "\"";
   }

   public String getMessage() {
      return super.getMessage();
   }

   public TokenMgrError() {
   }

   public TokenMgrError(String message, int reason) {
      super(message);
      this.errorCode = reason;
   }

   public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason) {
      this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
   }
}

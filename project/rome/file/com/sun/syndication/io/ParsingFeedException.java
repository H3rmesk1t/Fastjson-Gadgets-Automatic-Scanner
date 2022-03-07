package com.sun.syndication.io;

import org.jdom.input.JDOMParseException;

public class ParsingFeedException extends FeedException {
   public ParsingFeedException(String msg) {
      super(msg);
   }

   public ParsingFeedException(String msg, Throwable rootCause) {
      super(msg, rootCause);
   }

   public int getLineNumber() {
      return this.getCause() instanceof JDOMParseException ? ((JDOMParseException)this.getCause()).getLineNumber() : -1;
   }

   public int getColumnNumber() {
      return this.getCause() instanceof JDOMParseException ? ((JDOMParseException)this.getCause()).getColumnNumber() : -1;
   }
}

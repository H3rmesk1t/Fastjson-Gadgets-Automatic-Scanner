package com.sun.syndication.io;

public class FeedException extends Exception {
   public FeedException(String msg) {
      super(msg);
   }

   public FeedException(String msg, Throwable rootCause) {
      super(msg, rootCause);
   }
}

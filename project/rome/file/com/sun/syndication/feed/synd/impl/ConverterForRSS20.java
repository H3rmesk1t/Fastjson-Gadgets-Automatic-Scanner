package com.sun.syndication.feed.synd.impl;

public class ConverterForRSS20 extends ConverterForRSS094 {
   public ConverterForRSS20() {
      this("rss_2.0");
   }

   protected ConverterForRSS20(String type) {
      super(type);
   }
}

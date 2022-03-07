package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndEntry;
import java.util.Date;

public class ConverterForRSS093 extends ConverterForRSS092 {
   public ConverterForRSS093() {
      this("rss_0.93");
   }

   protected ConverterForRSS093(String type) {
      super(type);
   }

   protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
      SyndEntry syndEntry = super.createSyndEntry(item, preserveWireItem);
      Date pubDate = item.getPubDate();
      if (pubDate != null) {
         syndEntry.setPublishedDate(pubDate);
      }

      return syndEntry;
   }

   protected Item createRSSItem(SyndEntry sEntry) {
      Item item = super.createRSSItem(sEntry);
      item.setPubDate(sEntry.getPublishedDate());
      return item;
   }
}

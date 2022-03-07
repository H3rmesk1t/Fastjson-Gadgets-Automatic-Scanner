package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Item;
import java.util.Date;
import java.util.List;
import org.jdom.Element;

public class RSS093Generator extends RSS092Generator {
   public RSS093Generator() {
      this("rss_0.93", "0.93");
   }

   protected RSS093Generator(String feedType, String version) {
      super(feedType, version);
   }

   protected void populateItem(Item item, Element eItem, int index) {
      super.populateItem(item, eItem, index);
      Date pubDate = item.getPubDate();
      if (pubDate != null) {
         eItem.addContent(this.generateSimpleElement("pubDate", DateParser.formatRFC822(pubDate)));
      }

      Date expirationDate = item.getExpirationDate();
      if (expirationDate != null) {
         eItem.addContent(this.generateSimpleElement("expirationDate", DateParser.formatRFC822(expirationDate)));
      }

   }

   protected int getNumberOfEnclosures(List enclosures) {
      return enclosures.size();
   }
}

package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Item;
import org.jdom.Element;

public class RSS093Parser extends RSS092Parser {
   public RSS093Parser() {
      this("rss_0.93");
   }

   protected RSS093Parser(String type) {
      super(type);
   }

   protected String getRSSVersion() {
      return "0.93";
   }

   protected Item parseItem(Element rssRoot, Element eItem) {
      Item item = super.parseItem(rssRoot, eItem);
      Element e = eItem.getChild("pubDate", this.getRSSNamespace());
      if (e != null) {
         item.setPubDate(DateParser.parseDate(e.getText()));
      }

      e = eItem.getChild("expirationDate", this.getRSSNamespace());
      if (e != null) {
         item.setExpirationDate(DateParser.parseDate(e.getText()));
      }

      e = eItem.getChild("description", this.getRSSNamespace());
      if (e != null) {
         String type = e.getAttributeValue("type");
         if (type != null) {
            item.getDescription().setType(type);
         }
      }

      return item;
   }
}

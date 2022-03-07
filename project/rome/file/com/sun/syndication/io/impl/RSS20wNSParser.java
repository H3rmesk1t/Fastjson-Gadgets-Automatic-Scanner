package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS20wNSParser extends RSS20Parser {
   private static String RSS20_URI = "http://backend.userland.com/rss2";

   public RSS20wNSParser() {
      this("rss_2.0wNS");
   }

   protected RSS20wNSParser(String type) {
      super(type);
   }

   public boolean isMyType(Document document) {
      Element rssRoot = document.getRootElement();
      Namespace defaultNS = rssRoot.getNamespace();
      boolean ok = defaultNS != null && defaultNS.equals(this.getRSSNamespace());
      if (ok) {
         ok = super.isMyType(document);
      }

      return ok;
   }

   protected Namespace getRSSNamespace() {
      return Namespace.getNamespace(RSS20_URI);
   }

   protected WireFeed parseChannel(Element rssRoot) {
      WireFeed wFeed = super.parseChannel(rssRoot);
      wFeed.setFeedType("rss_2.0");
      return wFeed;
   }
}

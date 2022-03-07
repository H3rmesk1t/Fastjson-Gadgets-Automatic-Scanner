package com.sun.syndication.io.impl;

import com.sun.syndication.io.WireFeedParser;
import java.util.List;
import org.jdom.Document;

public class FeedParsers extends PluginManager {
   public static final String FEED_PARSERS_KEY = "WireFeedParser.classes";

   public FeedParsers() {
      super("WireFeedParser.classes");
   }

   public List getSupportedFeedTypes() {
      return this.getKeys();
   }

   public WireFeedParser getParserFor(Document document) {
      List parsers = this.getPlugins();
      WireFeedParser parser = null;

      for(int i = 0; parser == null && i < parsers.size(); ++i) {
         parser = (WireFeedParser)parsers.get(i);
         if (!parser.isMyType(document)) {
            parser = null;
         }
      }

      return parser;
   }

   protected String getKey(Object obj) {
      return ((WireFeedParser)obj).getType();
   }
}

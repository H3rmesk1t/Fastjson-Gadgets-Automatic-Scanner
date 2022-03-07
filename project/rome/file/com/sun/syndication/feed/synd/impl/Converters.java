package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.synd.Converter;
import com.sun.syndication.io.impl.PluginManager;
import java.util.List;

public class Converters extends PluginManager {
   public static final String CONVERTERS_KEY = "Converter.classes";

   public Converters() {
      super("Converter.classes");
   }

   public Converter getConverter(String feedType) {
      return (Converter)this.getPlugin(feedType);
   }

   protected String getKey(Object obj) {
      return ((Converter)obj).getType();
   }

   public List getSupportedFeedTypes() {
      return this.getKeys();
   }
}

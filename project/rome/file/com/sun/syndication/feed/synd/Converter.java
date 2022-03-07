package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.WireFeed;

public interface Converter {
   String getType();

   void copyInto(WireFeed var1, SyndFeed var2);

   WireFeed createRealFeed(SyndFeed var1);
}

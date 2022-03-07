package com.sun.syndication.io;

import com.sun.syndication.feed.WireFeed;
import org.jdom.Document;

public interface WireFeedParser {
   String getType();

   boolean isMyType(Document var1);

   WireFeed parse(Document var1, boolean var2) throws IllegalArgumentException, FeedException;
}

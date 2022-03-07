package com.sun.syndication.io;

import com.sun.syndication.feed.WireFeed;
import org.jdom.Document;

public interface WireFeedGenerator {
   String getType();

   Document generate(WireFeed var1) throws IllegalArgumentException, FeedException;
}

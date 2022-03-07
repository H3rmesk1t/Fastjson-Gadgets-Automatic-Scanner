package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;

public interface SyndEnclosure extends Cloneable, CopyFrom {
   String getUrl();

   void setUrl(String var1);

   long getLength();

   void setLength(long var1);

   String getType();

   void setType(String var1);
}

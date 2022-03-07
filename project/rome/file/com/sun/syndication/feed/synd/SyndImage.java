package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;

public interface SyndImage extends Cloneable, CopyFrom {
   String getTitle();

   void setTitle(String var1);

   String getUrl();

   void setUrl(String var1);

   String getLink();

   void setLink(String var1);

   String getDescription();

   void setDescription(String var1);

   Object clone() throws CloneNotSupportedException;
}

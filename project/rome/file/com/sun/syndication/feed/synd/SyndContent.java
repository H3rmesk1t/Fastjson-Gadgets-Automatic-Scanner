package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;

public interface SyndContent extends Cloneable, CopyFrom {
   String getType();

   void setType(String var1);

   String getMode();

   void setMode(String var1);

   String getValue();

   void setValue(String var1);

   Object clone() throws CloneNotSupportedException;
}

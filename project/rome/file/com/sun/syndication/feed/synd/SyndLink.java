package com.sun.syndication.feed.synd;

public interface SyndLink {
   Object clone() throws CloneNotSupportedException;

   boolean equals(Object var1);

   int hashCode();

   String toString();

   String getRel();

   void setRel(String var1);

   String getType();

   void setType(String var1);

   String getHref();

   void setHref(String var1);

   String getTitle();

   void setTitle(String var1);

   String getHreflang();

   void setHreflang(String var1);

   long getLength();

   void setLength(long var1);
}

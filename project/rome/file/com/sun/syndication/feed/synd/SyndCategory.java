package com.sun.syndication.feed.synd;

public interface SyndCategory extends Cloneable {
   String getName();

   void setName(String var1);

   String getTaxonomyUri();

   void setTaxonomyUri(String var1);

   Object clone() throws CloneNotSupportedException;
}

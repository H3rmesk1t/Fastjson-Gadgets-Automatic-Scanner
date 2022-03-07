package com.sun.syndication.feed.module;

import com.sun.syndication.feed.CopyFrom;

public interface DCSubject extends Cloneable, CopyFrom {
   String getTaxonomyUri();

   void setTaxonomyUri(String var1);

   String getValue();

   void setValue(String var1);

   Object clone() throws CloneNotSupportedException;
}

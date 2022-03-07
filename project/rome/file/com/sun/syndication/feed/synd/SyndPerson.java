package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.module.Extendable;

public interface SyndPerson extends Cloneable, Extendable {
   String getName();

   void setName(String var1);

   String getUri();

   void setUri(String var1);

   String getEmail();

   void setEmail(String var1);

   Object clone() throws CloneNotSupportedException;
}

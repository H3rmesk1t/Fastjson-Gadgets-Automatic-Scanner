package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import java.util.Date;
import java.util.List;

public interface SyndEntry extends Cloneable, CopyFrom, Extendable {
   String getUri();

   void setUri(String var1);

   String getTitle();

   void setTitle(String var1);

   SyndContent getTitleEx();

   void setTitleEx(SyndContent var1);

   String getLink();

   void setLink(String var1);

   List getLinks();

   void setLinks(List var1);

   SyndContent getDescription();

   void setDescription(SyndContent var1);

   List getContents();

   void setContents(List var1);

   List getEnclosures();

   void setEnclosures(List var1);

   Date getPublishedDate();

   void setPublishedDate(Date var1);

   Date getUpdatedDate();

   void setUpdatedDate(Date var1);

   List getAuthors();

   void setAuthors(List var1);

   String getAuthor();

   void setAuthor(String var1);

   List getContributors();

   void setContributors(List var1);

   List getCategories();

   void setCategories(List var1);

   SyndFeed getSource();

   void setSource(SyndFeed var1);

   Object getWireEntry();

   Module getModule(String var1);

   List getModules();

   void setModules(List var1);

   Object getForeignMarkup();

   void setForeignMarkup(Object var1);

   Object clone() throws CloneNotSupportedException;
}

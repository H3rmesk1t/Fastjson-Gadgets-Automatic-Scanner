package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.CopyFrom;
import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import java.util.Date;
import java.util.List;

public interface SyndFeed extends Cloneable, CopyFrom, Extendable {
   List getSupportedFeedTypes();

   WireFeed createWireFeed();

   WireFeed createWireFeed(String var1);

   WireFeed originalWireFeed();

   boolean isPreservingWireFeed();

   String getFeedType();

   void setFeedType(String var1);

   String getEncoding();

   void setEncoding(String var1);

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

   String getDescription();

   void setDescription(String var1);

   SyndContent getDescriptionEx();

   void setDescriptionEx(SyndContent var1);

   Date getPublishedDate();

   void setPublishedDate(Date var1);

   List getAuthors();

   void setAuthors(List var1);

   String getAuthor();

   void setAuthor(String var1);

   List getContributors();

   void setContributors(List var1);

   String getCopyright();

   void setCopyright(String var1);

   SyndImage getImage();

   void setImage(SyndImage var1);

   List getCategories();

   void setCategories(List var1);

   List getEntries();

   void setEntries(List var1);

   String getLanguage();

   void setLanguage(String var1);

   Module getModule(String var1);

   List getModules();

   void setModules(List var1);

   Object getForeignMarkup();

   void setForeignMarkup(Object var1);

   Object clone() throws CloneNotSupportedException;
}

package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConverterForRSS094 extends ConverterForRSS093 {
   public ConverterForRSS094() {
      this("rss_0.94");
   }

   protected ConverterForRSS094(String type) {
      super(type);
   }

   public void copyInto(WireFeed feed, SyndFeed syndFeed) {
      Channel channel = (Channel)feed;
      super.copyInto(channel, syndFeed);
      List cats = channel.getCategories();
      if (cats.size() > 0) {
         Set s = new HashSet();
         s.addAll(this.createSyndCategories(cats));
         s.addAll(syndFeed.getCategories());
         syndFeed.setCategories(new ArrayList(s));
      }

   }

   protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
      SyndEntry syndEntry = super.createSyndEntry(item, preserveWireItem);
      String author = item.getAuthor();
      if (author != null) {
         List creators = ((DCModule)syndEntry.getModule("http://purl.org/dc/elements/1.1/")).getCreators();
         if (!creators.contains(author)) {
            Set s = new HashSet();
            s.addAll(creators);
            s.add(author);
            creators.clear();
            creators.addAll(s);
         }
      }

      Guid guid = item.getGuid();
      if (guid != null) {
         syndEntry.setUri(guid.getValue());
         if (item.getLink() == null && guid.isPermaLink()) {
            syndEntry.setLink(guid.getValue());
         }
      } else {
         syndEntry.setUri(item.getLink());
      }

      return syndEntry;
   }

   protected WireFeed createRealFeed(String type, SyndFeed syndFeed) {
      Channel channel = (Channel)super.createRealFeed(type, syndFeed);
      List cats = syndFeed.getCategories();
      if (cats.size() > 0) {
         channel.setCategories(this.createRSSCategories(cats));
      }

      return channel;
   }

   protected Item createRSSItem(SyndEntry sEntry) {
      Item item = super.createRSSItem(sEntry);
      if (sEntry.getAuthors() != null && sEntry.getAuthors().size() > 0) {
         SyndPerson author = (SyndPerson)sEntry.getAuthors().get(0);
         item.setAuthor(author.getEmail());
      }

      Guid guid = null;
      String uri = sEntry.getUri();
      if (uri != null) {
         guid = new Guid();
         guid.setPermaLink(false);
         guid.setValue(uri);
      } else {
         String link = sEntry.getLink();
         if (link != null) {
            guid = new Guid();
            guid.setPermaLink(true);
            guid.setValue(link);
         }
      }

      item.setGuid(guid);
      return item;
   }
}

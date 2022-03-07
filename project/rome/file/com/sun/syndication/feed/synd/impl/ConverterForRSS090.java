package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.Converter;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.feed.synd.SyndLink;
import java.util.ArrayList;
import java.util.List;

public class ConverterForRSS090 implements Converter {
   private String _type;

   public ConverterForRSS090() {
      this("rss_0.9");
   }

   protected ConverterForRSS090(String type) {
      this._type = type;
   }

   public String getType() {
      return this._type;
   }

   public void copyInto(WireFeed feed, SyndFeed syndFeed) {
      syndFeed.setModules(ModuleUtils.cloneModules(feed.getModules()));
      if (((List)feed.getForeignMarkup()).size() > 0) {
         syndFeed.setForeignMarkup(feed.getForeignMarkup());
      }

      syndFeed.setEncoding(feed.getEncoding());
      Channel channel = (Channel)feed;
      syndFeed.setTitle(channel.getTitle());
      syndFeed.setLink(channel.getLink());
      syndFeed.setDescription(channel.getDescription());
      Image image = channel.getImage();
      if (image != null) {
         syndFeed.setImage(this.createSyndImage(image));
      }

      List items = channel.getItems();
      if (items != null) {
         syndFeed.setEntries(this.createSyndEntries(items, syndFeed.isPreservingWireFeed()));
      }

   }

   protected SyndImage createSyndImage(Image rssImage) {
      SyndImage syndImage = new SyndImageImpl();
      syndImage.setTitle(rssImage.getTitle());
      syndImage.setUrl(rssImage.getUrl());
      syndImage.setLink(rssImage.getLink());
      return syndImage;
   }

   protected List createSyndEntries(List rssItems, boolean preserveWireItems) {
      List syndEntries = new ArrayList();

      for(int i = 0; i < rssItems.size(); ++i) {
         syndEntries.add(this.createSyndEntry((Item)rssItems.get(i), preserveWireItems));
      }

      return syndEntries;
   }

   protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
      SyndEntryImpl syndEntry = new SyndEntryImpl();
      if (preserveWireItem) {
         syndEntry.setWireEntry(item);
      }

      syndEntry.setModules(ModuleUtils.cloneModules(item.getModules()));
      if (((List)item.getForeignMarkup()).size() > 0) {
         syndEntry.setForeignMarkup(item.getForeignMarkup());
      }

      syndEntry.setUri(item.getUri());
      syndEntry.setLink(item.getLink());
      syndEntry.setTitle(item.getTitle());
      syndEntry.setLink(item.getLink());
      return syndEntry;
   }

   public WireFeed createRealFeed(SyndFeed syndFeed) {
      return this.createRealFeed(this.getType(), syndFeed);
   }

   protected WireFeed createRealFeed(String type, SyndFeed syndFeed) {
      Channel channel = new Channel(type);
      channel.setModules(ModuleUtils.cloneModules(syndFeed.getModules()));
      channel.setEncoding(syndFeed.getEncoding());
      channel.setTitle(syndFeed.getTitle());
      if (syndFeed.getLink() != null) {
         channel.setLink(syndFeed.getLink());
      } else if (syndFeed.getLinks().size() > 0) {
         channel.setLink(((SyndLink)syndFeed.getLinks().get(0)).getHref());
      }

      channel.setDescription(syndFeed.getDescription());
      SyndImage sImage = syndFeed.getImage();
      if (sImage != null) {
         channel.setImage(this.createRSSImage(sImage));
      }

      List sEntries = syndFeed.getEntries();
      if (sEntries != null) {
         channel.setItems(this.createRSSItems(sEntries));
      }

      if (((List)syndFeed.getForeignMarkup()).size() > 0) {
         channel.setForeignMarkup(syndFeed.getForeignMarkup());
      }

      return channel;
   }

   protected Image createRSSImage(SyndImage sImage) {
      Image image = new Image();
      image.setTitle(sImage.getTitle());
      image.setUrl(sImage.getUrl());
      image.setLink(sImage.getLink());
      return image;
   }

   protected List createRSSItems(List sEntries) {
      List list = new ArrayList();

      for(int i = 0; i < sEntries.size(); ++i) {
         list.add(this.createRSSItem((SyndEntry)sEntries.get(i)));
      }

      return list;
   }

   protected Item createRSSItem(SyndEntry sEntry) {
      Item item = new Item();
      item.setModules(ModuleUtils.cloneModules(sEntry.getModules()));
      item.setTitle(sEntry.getTitle());
      item.setLink(sEntry.getLink());
      if (((List)sEntry.getForeignMarkup()).size() > 0) {
         item.setForeignMarkup(sEntry.getForeignMarkup());
      }

      String uri = sEntry.getUri();
      if (uri != null) {
         item.setUri(uri);
      }

      return item;
   }
}

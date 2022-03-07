package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;
import java.util.Date;
import java.util.List;
import org.jdom.Element;

public class RSS094Parser extends RSS093Parser {
   public RSS094Parser() {
      this("rss_0.94");
   }

   protected RSS094Parser(String type) {
      super(type);
   }

   protected String getRSSVersion() {
      return "0.94";
   }

   protected WireFeed parseChannel(Element rssRoot) {
      Channel channel = (Channel)super.parseChannel(rssRoot);
      Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
      List eCats = eChannel.getChildren("category", this.getRSSNamespace());
      channel.setCategories(this.parseCategories(eCats));
      Element eTtl = eChannel.getChild("ttl", this.getRSSNamespace());
      if (eTtl != null && eTtl.getText() != null) {
         Integer ttlValue = null;

         try {
            ttlValue = new Integer(eTtl.getText());
         } catch (NumberFormatException var8) {
         }

         if (ttlValue != null) {
            channel.setTtl(ttlValue);
         }
      }

      return channel;
   }

   public Item parseItem(Element rssRoot, Element eItem) {
      Item item = super.parseItem(rssRoot, eItem);
      item.setExpirationDate((Date)null);
      Element e = eItem.getChild("author", this.getRSSNamespace());
      if (e != null) {
         item.setAuthor(e.getText());
      }

      e = eItem.getChild("guid", this.getRSSNamespace());
      if (e != null) {
         Guid guid = new Guid();
         String att = e.getAttributeValue("isPermaLink");
         if (att != null) {
            guid.setPermaLink(att.equalsIgnoreCase("true"));
         }

         guid.setValue(e.getText());
         item.setGuid(guid);
      }

      e = eItem.getChild("comments", this.getRSSNamespace());
      if (e != null) {
         item.setComments(e.getText());
      }

      return item;
   }

   protected Description parseItemDescription(Element rssRoot, Element eDesc) {
      Description desc = super.parseItemDescription(rssRoot, eDesc);
      String att = eDesc.getAttributeValue("type");
      if (att == null) {
         att = "text/html";
      }

      desc.setType(att);
      return desc;
   }
}

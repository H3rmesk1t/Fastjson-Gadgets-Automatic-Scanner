package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Category;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;
import java.util.List;
import org.jdom.Element;

public class RSS20Generator extends RSS094Generator {
   public RSS20Generator() {
      this("rss_2.0", "2.0");
   }

   protected RSS20Generator(String feedType, String version) {
      super(feedType, version);
   }

   protected void populateChannel(Channel channel, Element eChannel) {
      super.populateChannel(channel, eChannel);
      String generator = channel.getGenerator();
      if (generator != null) {
         eChannel.addContent(this.generateSimpleElement("generator", generator));
      }

      int ttl = channel.getTtl();
      if (ttl > -1) {
         eChannel.addContent(this.generateSimpleElement("ttl", String.valueOf(ttl)));
      }

      List categories = channel.getCategories();

      for(int i = 0; i < categories.size(); ++i) {
         eChannel.addContent(this.generateCategoryElement((Category)categories.get(i)));
      }

   }

   public void populateItem(Item item, Element eItem, int index) {
      super.populateItem(item, eItem, index);
      Element eDescription = eItem.getChild("description", this.getFeedNamespace());
      if (eDescription != null) {
         eDescription.removeAttribute("type");
      }

      String author = item.getAuthor();
      if (author != null) {
         eItem.addContent(this.generateSimpleElement("author", author));
      }

      String comments = item.getComments();
      if (comments != null) {
         eItem.addContent(this.generateSimpleElement("comments", comments));
      }

      Guid guid = item.getGuid();
      if (guid != null) {
         Element eGuid = this.generateSimpleElement("guid", guid.getValue());
         if (!guid.isPermaLink()) {
            eGuid.setAttribute("isPermaLink", "false");
         }

         eItem.addContent(eGuid);
      }

   }
}

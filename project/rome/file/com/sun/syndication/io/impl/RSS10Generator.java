package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.FeedException;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS10Generator extends RSS090Generator {
   private static final String RSS_URI = "http://purl.org/rss/1.0/";
   private static final Namespace RSS_NS = Namespace.getNamespace("http://purl.org/rss/1.0/");

   public RSS10Generator() {
      super("rss_1.0");
   }

   protected RSS10Generator(String feedType) {
      super(feedType);
   }

   protected Namespace getFeedNamespace() {
      return RSS_NS;
   }

   protected void populateChannel(Channel channel, Element eChannel) {
      super.populateChannel(channel, eChannel);
      if (channel.getUri() != null) {
         eChannel.setAttribute("about", channel.getUri(), this.getRDFNamespace());
      }

      List items = channel.getItems();
      if (items.size() > 0) {
         Element eItems = new Element("items", this.getFeedNamespace());
         Element eSeq = new Element("Seq", this.getRDFNamespace());

         for(int i = 0; i < items.size(); ++i) {
            Item item = (Item)items.get(i);
            Element eLi = new Element("li", this.getRDFNamespace());
            String uri = item.getUri();
            if (uri != null) {
               eLi.setAttribute("resource", uri, this.getRDFNamespace());
            }

            eSeq.addContent(eLi);
         }

         eItems.addContent(eSeq);
         eChannel.addContent(eItems);
      }

   }

   protected void populateItem(Item item, Element eItem, int index) {
      super.populateItem(item, eItem, index);
      String link = item.getLink();
      String uri = item.getUri();
      if (uri != null) {
         eItem.setAttribute("about", uri, this.getRDFNamespace());
      } else if (link != null) {
         eItem.setAttribute("about", link, this.getRDFNamespace());
      }

      Description description = item.getDescription();
      if (description != null) {
         eItem.addContent(this.generateSimpleElement("description", description.getValue()));
      }

      if (item.getModule(this.getContentNamespace().getURI()) == null && item.getContent() != null) {
         Element elem = new Element("encoded", this.getContentNamespace());
         elem.addContent(item.getContent().getValue());
         eItem.addContent(elem);
      }

   }

   protected void checkChannelConstraints(Element eChannel) throws FeedException {
      this.checkNotNullAndLength(eChannel, "title", 0, -1);
      this.checkNotNullAndLength(eChannel, "description", 0, -1);
      this.checkNotNullAndLength(eChannel, "link", 0, -1);
   }

   protected void checkImageConstraints(Element eImage) throws FeedException {
      this.checkNotNullAndLength(eImage, "title", 0, -1);
      this.checkNotNullAndLength(eImage, "url", 0, -1);
      this.checkNotNullAndLength(eImage, "link", 0, -1);
   }

   protected void checkTextInputConstraints(Element eTextInput) throws FeedException {
      this.checkNotNullAndLength(eTextInput, "title", 0, -1);
      this.checkNotNullAndLength(eTextInput, "description", 0, -1);
      this.checkNotNullAndLength(eTextInput, "name", 0, -1);
      this.checkNotNullAndLength(eTextInput, "link", 0, -1);
   }

   protected void checkItemsConstraints(Element parent) throws FeedException {
   }

   protected void checkItemConstraints(Element eItem) throws FeedException {
      this.checkNotNullAndLength(eItem, "title", 0, -1);
      this.checkNotNullAndLength(eItem, "link", 0, -1);
   }
}

package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Category;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Cloud;
import com.sun.syndication.feed.rss.Enclosure;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.rss.Source;
import com.sun.syndication.io.FeedException;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;

public class RSS092Generator extends RSS091UserlandGenerator {
   public RSS092Generator() {
      this("rss_0.92", "0.92");
   }

   protected RSS092Generator(String type, String version) {
      super(type, version);
   }

   protected void populateChannel(Channel channel, Element eChannel) {
      super.populateChannel(channel, eChannel);
      Cloud cloud = channel.getCloud();
      if (cloud != null) {
         eChannel.addContent(this.generateCloud(cloud));
      }

   }

   protected Element generateCloud(Cloud cloud) {
      Element eCloud = new Element("cloud", this.getFeedNamespace());
      if (cloud.getDomain() != null) {
         eCloud.setAttribute(new Attribute("domain", cloud.getDomain()));
      }

      if (cloud.getPort() != 0) {
         eCloud.setAttribute(new Attribute("port", String.valueOf(cloud.getPort())));
      }

      if (cloud.getPath() != null) {
         eCloud.setAttribute(new Attribute("path", cloud.getPath()));
      }

      if (cloud.getRegisterProcedure() != null) {
         eCloud.setAttribute(new Attribute("registerProcedure", cloud.getRegisterProcedure()));
      }

      if (cloud.getProtocol() != null) {
         eCloud.setAttribute(new Attribute("protocol", cloud.getProtocol()));
      }

      return eCloud;
   }

   protected int getNumberOfEnclosures(List enclosures) {
      return enclosures.size() > 0 ? 1 : 0;
   }

   protected void populateItem(Item item, Element eItem, int index) {
      super.populateItem(item, eItem, index);
      Source source = item.getSource();
      if (source != null) {
         eItem.addContent(this.generateSourceElement(source));
      }

      List enclosures = item.getEnclosures();

      for(int i = 0; i < this.getNumberOfEnclosures(enclosures); ++i) {
         eItem.addContent(this.generateEnclosure((Enclosure)enclosures.get(i)));
      }

      List categories = item.getCategories();

      for(int i = 0; i < categories.size(); ++i) {
         eItem.addContent(this.generateCategoryElement((Category)categories.get(i)));
      }

   }

   protected Element generateSourceElement(Source source) {
      Element sourceElement = new Element("source", this.getFeedNamespace());
      if (source.getUrl() != null) {
         sourceElement.setAttribute(new Attribute("url", source.getUrl()));
      }

      sourceElement.addContent(source.getValue());
      return sourceElement;
   }

   protected Element generateEnclosure(Enclosure enclosure) {
      Element enclosureElement = new Element("enclosure", this.getFeedNamespace());
      if (enclosure.getUrl() != null) {
         enclosureElement.setAttribute("url", enclosure.getUrl());
      }

      if (enclosure.getLength() != 0L) {
         enclosureElement.setAttribute("length", String.valueOf(enclosure.getLength()));
      }

      if (enclosure.getType() != null) {
         enclosureElement.setAttribute("type", enclosure.getType());
      }

      return enclosureElement;
   }

   protected Element generateCategoryElement(Category category) {
      Element categoryElement = new Element("category", this.getFeedNamespace());
      if (category.getDomain() != null) {
         categoryElement.setAttribute("domain", category.getDomain());
      }

      categoryElement.addContent(category.getValue());
      return categoryElement;
   }

   protected void checkChannelConstraints(Element eChannel) throws FeedException {
      this.checkNotNullAndLength(eChannel, "title", 0, -1);
      this.checkNotNullAndLength(eChannel, "description", 0, -1);
      this.checkNotNullAndLength(eChannel, "link", 0, -1);
   }

   protected void checkImageConstraints(Element eImage) throws FeedException {
      this.checkNotNullAndLength(eImage, "title", 0, -1);
      this.checkNotNullAndLength(eImage, "url", 0, -1);
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
   }
}

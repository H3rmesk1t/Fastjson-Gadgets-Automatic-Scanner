package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Category;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Cloud;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Enclosure;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.rss.Source;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

public class RSS092Parser extends RSS091UserlandParser {
   public RSS092Parser() {
      this("rss_0.92");
   }

   protected RSS092Parser(String type) {
      super(type);
   }

   protected String getRSSVersion() {
      return "0.92";
   }

   protected WireFeed parseChannel(Element rssRoot) {
      Channel channel = (Channel)super.parseChannel(rssRoot);
      Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
      Element eCloud = eChannel.getChild("cloud", this.getRSSNamespace());
      if (eCloud != null) {
         Cloud cloud = new Cloud();
         String att = eCloud.getAttributeValue("domain");
         if (att != null) {
            cloud.setDomain(att);
         }

         att = eCloud.getAttributeValue("port");
         if (att != null) {
            cloud.setPort(Integer.parseInt(att.trim()));
         }

         att = eCloud.getAttributeValue("path");
         if (att != null) {
            cloud.setPath(att);
         }

         att = eCloud.getAttributeValue("registerProcedure");
         if (att != null) {
            cloud.setRegisterProcedure(att);
         }

         att = eCloud.getAttributeValue("protocol");
         if (att != null) {
            cloud.setProtocol(att);
         }

         channel.setCloud(cloud);
      }

      return channel;
   }

   protected Item parseItem(Element rssRoot, Element eItem) {
      Item item = super.parseItem(rssRoot, eItem);
      Element e = eItem.getChild("source", this.getRSSNamespace());
      if (e != null) {
         Source source = new Source();
         String url = e.getAttributeValue("url");
         source.setUrl(url);
         source.setValue(e.getText());
         item.setSource(source);
      }

      List eEnclosures = eItem.getChildren("enclosure");
      if (eEnclosures.size() > 0) {
         List enclosures = new ArrayList();

         for(int i = 0; i < eEnclosures.size(); ++i) {
            e = (Element)eEnclosures.get(i);
            Enclosure enclosure = new Enclosure();
            String att = e.getAttributeValue("url");
            if (att != null) {
               enclosure.setUrl(att);
            }

            att = e.getAttributeValue("length");
            enclosure.setLength(NumberParser.parseLong(att, 0L));
            att = e.getAttributeValue("type");
            if (att != null) {
               enclosure.setType(att);
            }

            enclosures.add(enclosure);
         }

         item.setEnclosures(enclosures);
      }

      List eCats = eItem.getChildren("category");
      item.setCategories(this.parseCategories(eCats));
      return item;
   }

   protected List parseCategories(List eCats) {
      List cats = null;
      if (eCats.size() > 0) {
         cats = new ArrayList();

         for(int i = 0; i < eCats.size(); ++i) {
            Category cat = new Category();
            Element e = (Element)eCats.get(i);
            String att = e.getAttributeValue("domain");
            if (att != null) {
               cat.setDomain(att);
            }

            cat.setValue(e.getText());
            cats.add(cat);
         }
      }

      return cats;
   }

   protected Description parseItemDescription(Element rssRoot, Element eDesc) {
      Description desc = super.parseItemDescription(rssRoot, eDesc);
      desc.setType("text/html");
      return desc;
   }
}

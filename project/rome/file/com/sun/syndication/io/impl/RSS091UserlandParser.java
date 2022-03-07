package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS091UserlandParser extends RSS090Parser {
   public RSS091UserlandParser() {
      this("rss_0.91U");
   }

   protected RSS091UserlandParser(String type) {
      super(type, (Namespace)null);
   }

   public boolean isMyType(Document document) {
      Element rssRoot = document.getRootElement();
      boolean ok = rssRoot.getName().equals("rss");
      if (ok) {
         ok = false;
         Attribute version = rssRoot.getAttribute("version");
         if (version != null) {
            ok = version.getValue().equals(this.getRSSVersion());
         }
      }

      return ok;
   }

   protected String getRSSVersion() {
      return "0.91";
   }

   protected Namespace getRSSNamespace() {
      return Namespace.getNamespace("");
   }

   protected boolean isHourFormat24(Element rssRoot) {
      return true;
   }

   protected WireFeed parseChannel(Element rssRoot) {
      Channel channel = (Channel)super.parseChannel(rssRoot);
      Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
      Element e = eChannel.getChild("language", this.getRSSNamespace());
      if (e != null) {
         channel.setLanguage(e.getText());
      }

      e = eChannel.getChild("rating", this.getRSSNamespace());
      if (e != null) {
         channel.setRating(e.getText());
      }

      e = eChannel.getChild("copyright", this.getRSSNamespace());
      if (e != null) {
         channel.setCopyright(e.getText());
      }

      e = eChannel.getChild("pubDate", this.getRSSNamespace());
      if (e != null) {
         channel.setPubDate(DateParser.parseDate(e.getText()));
      }

      e = eChannel.getChild("lastBuildDate", this.getRSSNamespace());
      if (e != null) {
         channel.setLastBuildDate(DateParser.parseDate(e.getText()));
      }

      e = eChannel.getChild("docs", this.getRSSNamespace());
      if (e != null) {
         channel.setDocs(e.getText());
      }

      e = eChannel.getChild("docs", this.getRSSNamespace());
      if (e != null) {
         channel.setDocs(e.getText());
      }

      e = eChannel.getChild("managingEditor", this.getRSSNamespace());
      if (e != null) {
         channel.setManagingEditor(e.getText());
      }

      e = eChannel.getChild("webMaster", this.getRSSNamespace());
      if (e != null) {
         channel.setWebMaster(e.getText());
      }

      e = eChannel.getChild("skipHours");
      ArrayList skipDays;
      List eDays;
      int i;
      Element eDay;
      if (e != null) {
         skipDays = new ArrayList();
         eDays = e.getChildren("hour", this.getRSSNamespace());

         for(i = 0; i < eDays.size(); ++i) {
            eDay = (Element)eDays.get(i);
            skipDays.add(new Integer(eDay.getText().trim()));
         }

         channel.setSkipHours(skipDays);
      }

      e = eChannel.getChild("skipDays");
      if (e != null) {
         skipDays = new ArrayList();
         eDays = e.getChildren("day", this.getRSSNamespace());

         for(i = 0; i < eDays.size(); ++i) {
            eDay = (Element)eDays.get(i);
            skipDays.add(eDay.getText().trim());
         }

         channel.setSkipDays(skipDays);
      }

      return channel;
   }

   protected Image parseImage(Element rssRoot) {
      Image image = super.parseImage(rssRoot);
      if (image != null) {
         Element eImage = this.getImage(rssRoot);
         Element e = eImage.getChild("width", this.getRSSNamespace());
         Integer val;
         if (e != null) {
            val = NumberParser.parseInt(e.getText());
            if (val != null) {
               image.setWidth(val);
            }
         }

         e = eImage.getChild("height", this.getRSSNamespace());
         if (e != null) {
            val = NumberParser.parseInt(e.getText());
            if (val != null) {
               image.setHeight(val);
            }
         }

         e = eImage.getChild("description", this.getRSSNamespace());
         if (e != null) {
            image.setDescription(e.getText());
         }
      }

      return image;
   }

   protected List getItems(Element rssRoot) {
      Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
      return eChannel != null ? eChannel.getChildren("item", this.getRSSNamespace()) : Collections.EMPTY_LIST;
   }

   protected Element getImage(Element rssRoot) {
      Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
      return eChannel != null ? eChannel.getChild("image", this.getRSSNamespace()) : null;
   }

   protected String getTextInputLabel() {
      return "textInput";
   }

   protected Element getTextInput(Element rssRoot) {
      String elementName = this.getTextInputLabel();
      Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
      return eChannel != null ? eChannel.getChild(elementName, this.getRSSNamespace()) : null;
   }

   protected Item parseItem(Element rssRoot, Element eItem) {
      Item item = super.parseItem(rssRoot, eItem);
      Element e = eItem.getChild("description", this.getRSSNamespace());
      if (e != null) {
         item.setDescription(this.parseItemDescription(rssRoot, e));
      }

      Element ce = eItem.getChild("encoded", this.getContentNamespace());
      if (ce != null) {
         Content content = new Content();
         content.setType("html");
         content.setValue(ce.getText());
         item.setContent(content);
      }

      return item;
   }

   protected Description parseItemDescription(Element rssRoot, Element eDesc) {
      Description desc = new Description();
      desc.setType("text/plain");
      desc.setValue(eDesc.getText());
      return desc;
   }
}

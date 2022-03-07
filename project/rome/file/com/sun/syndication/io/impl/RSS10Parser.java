package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS10Parser extends RSS090Parser {
   private static final String RSS_URI = "http://purl.org/rss/1.0/";
   private static final Namespace RSS_NS = Namespace.getNamespace("http://purl.org/rss/1.0/");

   public RSS10Parser() {
      this("rss_1.0", RSS_NS);
   }

   protected RSS10Parser(String type, Namespace ns) {
      super(type, ns);
   }

   public boolean isMyType(Document document) {
      boolean ok = false;
      Element rssRoot = document.getRootElement();
      Namespace defaultNS = rssRoot.getNamespace();
      List additionalNSs = rssRoot.getAdditionalNamespaces();
      ok = defaultNS != null && defaultNS.equals(this.getRDFNamespace());
      if (ok) {
         if (additionalNSs == null) {
            ok = false;
         } else {
            ok = false;

            for(int i = 0; !ok && i < additionalNSs.size(); ++i) {
               ok = this.getRSSNamespace().equals(additionalNSs.get(i));
            }
         }
      }

      return ok;
   }

   protected Namespace getRSSNamespace() {
      return Namespace.getNamespace("http://purl.org/rss/1.0/");
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

      String uri = eItem.getAttributeValue("about", this.getRDFNamespace());
      if (uri != null) {
         item.setUri(uri);
      }

      return item;
   }

   protected WireFeed parseChannel(Element rssRoot) {
      Channel channel = (Channel)super.parseChannel(rssRoot);
      Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
      String uri = eChannel.getAttributeValue("about", this.getRDFNamespace());
      if (uri != null) {
         channel.setUri(uri);
      }

      return channel;
   }

   protected Description parseItemDescription(Element rssRoot, Element eDesc) {
      Description desc = new Description();
      desc.setType("text/plain");
      desc.setValue(eDesc.getText());
      return desc;
   }
}

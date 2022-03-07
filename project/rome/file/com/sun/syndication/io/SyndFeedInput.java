package com.sun.syndication.io;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SyndFeedInput {
   private WireFeedInput _feedInput;
   private boolean preserveWireFeed;

   public SyndFeedInput() {
      this(false);
   }

   public SyndFeedInput(boolean validate) {
      this.preserveWireFeed = false;
      this._feedInput = new WireFeedInput(validate);
   }

   public void setXmlHealerOn(boolean heals) {
      this._feedInput.setXmlHealerOn(heals);
   }

   public boolean getXmlHealerOn() {
      return this._feedInput.getXmlHealerOn();
   }

   public SyndFeed build(File file) throws FileNotFoundException, IOException, IllegalArgumentException, FeedException {
      return new SyndFeedImpl(this._feedInput.build(file), this.preserveWireFeed);
   }

   public SyndFeed build(Reader reader) throws IllegalArgumentException, FeedException {
      return new SyndFeedImpl(this._feedInput.build(reader), this.preserveWireFeed);
   }

   public SyndFeed build(InputSource is) throws IllegalArgumentException, FeedException {
      return new SyndFeedImpl(this._feedInput.build(is), this.preserveWireFeed);
   }

   public SyndFeed build(Document document) throws IllegalArgumentException, FeedException {
      return new SyndFeedImpl(this._feedInput.build(document), this.preserveWireFeed);
   }

   public SyndFeed build(org.jdom.Document document) throws IllegalArgumentException, FeedException {
      return new SyndFeedImpl(this._feedInput.build(document), this.preserveWireFeed);
   }

   public boolean isPreserveWireFeed() {
      return this.preserveWireFeed;
   }

   public void setPreserveWireFeed(boolean preserveWireFeed) {
      this.preserveWireFeed = preserveWireFeed;
   }
}

package com.sun.syndication.io;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.impl.FeedGenerators;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class WireFeedOutput {
   private static Map clMap = new WeakHashMap();

   private static FeedGenerators getFeedGenerators() {
      synchronized(WireFeedOutput.class) {
         FeedGenerators generators = (FeedGenerators)clMap.get(Thread.currentThread().getContextClassLoader());
         if (generators == null) {
            generators = new FeedGenerators();
            clMap.put(Thread.currentThread().getContextClassLoader(), generators);
         }

         return generators;
      }
   }

   public static List getSupportedFeedTypes() {
      return getFeedGenerators().getSupportedFeedTypes();
   }

   public String outputString(WireFeed feed) throws IllegalArgumentException, FeedException {
      return this.outputString(feed, true);
   }

   public String outputString(WireFeed feed, boolean prettyPrint) throws IllegalArgumentException, FeedException {
      Document doc = this.outputJDom(feed);
      String encoding = feed.getEncoding();
      Format format = prettyPrint ? Format.getPrettyFormat() : Format.getCompactFormat();
      if (encoding != null) {
         format.setEncoding(encoding);
      }

      XMLOutputter outputter = new XMLOutputter(format);
      return outputter.outputString(doc);
   }

   public void output(WireFeed feed, File file) throws IllegalArgumentException, IOException, FeedException {
      this.output(feed, file, true);
   }

   public void output(WireFeed feed, File file, boolean prettyPrint) throws IllegalArgumentException, IOException, FeedException {
      Writer writer = new FileWriter(file);
      this.output(feed, (Writer)writer, prettyPrint);
      writer.close();
   }

   public void output(WireFeed feed, Writer writer) throws IllegalArgumentException, IOException, FeedException {
      this.output(feed, writer, true);
   }

   public void output(WireFeed feed, Writer writer, boolean prettyPrint) throws IllegalArgumentException, IOException, FeedException {
      Document doc = this.outputJDom(feed);
      String encoding = feed.getEncoding();
      Format format = prettyPrint ? Format.getPrettyFormat() : Format.getCompactFormat();
      if (encoding != null) {
         format.setEncoding(encoding);
      }

      XMLOutputter outputter = new XMLOutputter(format);
      outputter.output(doc, writer);
   }

   public org.w3c.dom.Document outputW3CDom(WireFeed feed) throws IllegalArgumentException, FeedException {
      Document doc = this.outputJDom(feed);
      DOMOutputter outputter = new DOMOutputter();

      try {
         return outputter.output(doc);
      } catch (JDOMException var5) {
         throw new FeedException("Could not create DOM", var5);
      }
   }

   public Document outputJDom(WireFeed feed) throws IllegalArgumentException, FeedException {
      String type = feed.getFeedType();
      WireFeedGenerator generator = getFeedGenerators().getGenerator(type);
      if (generator == null) {
         throw new IllegalArgumentException("Invalid feed type [" + type + "]");
      } else if (!generator.getType().equals(type)) {
         throw new IllegalArgumentException("WireFeedOutput type[" + type + "] and WireFeed type [" + type + "] don't match");
      } else {
         return generator.generate(feed);
      }
   }
}

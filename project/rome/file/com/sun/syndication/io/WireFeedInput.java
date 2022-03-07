package com.sun.syndication.io;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.impl.FeedParsers;
import com.sun.syndication.io.impl.XmlFixerReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.input.JDOMParseException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class WireFeedInput {
   private static Map clMap = new WeakHashMap();
   private static final InputSource EMPTY_INPUTSOURCE = new InputSource(new ByteArrayInputStream(new byte[0]));
   private static final EntityResolver RESOLVER = new WireFeedInput.EmptyEntityResolver();
   private boolean _validate;
   private boolean _xmlHealerOn;

   private static FeedParsers getFeedParsers() {
      synchronized(WireFeedInput.class) {
         FeedParsers parsers = (FeedParsers)clMap.get(Thread.currentThread().getContextClassLoader());
         if (parsers == null) {
            parsers = new FeedParsers();
            clMap.put(Thread.currentThread().getContextClassLoader(), parsers);
         }

         return parsers;
      }
   }

   public static List getSupportedFeedTypes() {
      return getFeedParsers().getSupportedFeedTypes();
   }

   public WireFeedInput() {
      this(false);
   }

   public WireFeedInput(boolean validate) {
      this._validate = false;
      this._xmlHealerOn = true;
   }

   public void setXmlHealerOn(boolean heals) {
      this._xmlHealerOn = heals;
   }

   public boolean getXmlHealerOn() {
      return this._xmlHealerOn;
   }

   public WireFeed build(File file) throws FileNotFoundException, IOException, IllegalArgumentException, FeedException {
      Reader reader = new FileReader(file);
      if (this._xmlHealerOn) {
         reader = new XmlFixerReader((Reader)reader);
      }

      WireFeed feed = this.build((Reader)reader);
      ((Reader)reader).close();
      return feed;
   }

   public WireFeed build(Reader reader) throws IllegalArgumentException, FeedException {
      SAXBuilder saxBuilder = this.createSAXBuilder();

      try {
         if (this._xmlHealerOn) {
            reader = new XmlFixerReader((Reader)reader);
         }

         Document document = saxBuilder.build((Reader)reader);
         return this.build(document);
      } catch (JDOMParseException var4) {
         throw new ParsingFeedException("Invalid XML: " + var4.getMessage(), var4);
      } catch (IllegalArgumentException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new ParsingFeedException("Invalid XML", var6);
      }
   }

   public WireFeed build(InputSource is) throws IllegalArgumentException, FeedException {
      SAXBuilder saxBuilder = this.createSAXBuilder();

      try {
         Document document = saxBuilder.build(is);
         return this.build(document);
      } catch (JDOMParseException var4) {
         throw new ParsingFeedException("Invalid XML: " + var4.getMessage(), var4);
      } catch (IllegalArgumentException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new ParsingFeedException("Invalid XML", var6);
      }
   }

   public WireFeed build(org.w3c.dom.Document document) throws IllegalArgumentException, FeedException {
      DOMBuilder domBuilder = new DOMBuilder();

      try {
         Document jdomDoc = domBuilder.build(document);
         return this.build(jdomDoc);
      } catch (IllegalArgumentException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new ParsingFeedException("Invalid XML", var5);
      }
   }

   public WireFeed build(Document document) throws IllegalArgumentException, FeedException {
      WireFeedParser parser = getFeedParsers().getParserFor(document);
      if (parser == null) {
         throw new IllegalArgumentException("Invalid document");
      } else {
         return parser.parse(document, this._validate);
      }
   }

   protected SAXBuilder createSAXBuilder() {
      SAXBuilder saxBuilder = new SAXBuilder(this._validate);
      saxBuilder.setEntityResolver(RESOLVER);

      try {
         XMLReader parser = saxBuilder.createParser();

         try {
            parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxBuilder.setFeature("http://xml.org/sax/features/external-general-entities", false);
         } catch (SAXNotRecognizedException var8) {
         } catch (SAXNotSupportedException var9) {
         }

         try {
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            saxBuilder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
         } catch (SAXNotRecognizedException var6) {
         } catch (SAXNotSupportedException var7) {
         }

         try {
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
         } catch (SAXNotRecognizedException var4) {
         } catch (SAXNotSupportedException var5) {
         }
      } catch (JDOMException var10) {
         throw new IllegalStateException("JDOM could not create a SAX parser");
      }

      saxBuilder.setExpandEntities(false);
      return saxBuilder;
   }

   private static class EmptyEntityResolver implements EntityResolver {
      private EmptyEntityResolver() {
      }

      public InputSource resolveEntity(String publicId, String systemId) {
         return systemId != null && systemId.endsWith(".dtd") ? WireFeedInput.EMPTY_INPUTSOURCE : null;
      }

      // $FF: synthetic method
      EmptyEntityResolver(Object x0) {
         this();
      }
   }
}

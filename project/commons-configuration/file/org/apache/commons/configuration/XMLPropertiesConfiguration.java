package org.apache.commons.configuration;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLPropertiesConfiguration extends PropertiesConfiguration {
   private static final String DEFAULT_ENCODING = "UTF-8";
   private static final String MALFORMED_XML_EXCEPTION = "Malformed XML";

   public XMLPropertiesConfiguration() {
      this.setEncoding("UTF-8");
   }

   public XMLPropertiesConfiguration(String fileName) throws ConfigurationException {
      super(fileName);
      this.setEncoding("UTF-8");
   }

   public XMLPropertiesConfiguration(File file) throws ConfigurationException {
      super(file);
      this.setEncoding("UTF-8");
   }

   public XMLPropertiesConfiguration(URL url) throws ConfigurationException {
      super(url);
      this.setEncoding("UTF-8");
   }

   public XMLPropertiesConfiguration(Element element) throws ConfigurationException {
      this.setEncoding("UTF-8");
      this.load(element);
   }

   public void load(Reader in) throws ConfigurationException {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(true);

      try {
         SAXParser parser = factory.newSAXParser();
         XMLReader xmlReader = parser.getXMLReader();
         xmlReader.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
               return new InputSource(this.getClass().getClassLoader().getResourceAsStream("properties.dtd"));
            }
         });
         xmlReader.setContentHandler(new XMLPropertiesConfiguration.XMLPropertiesHandler());
         xmlReader.parse(new InputSource(in));
      } catch (Exception var5) {
         throw new ConfigurationException("Unable to parse the configuration file", var5);
      }
   }

   public void load(Element element) throws ConfigurationException {
      if (!element.getNodeName().equals("properties")) {
         throw new ConfigurationException("Malformed XML");
      } else {
         NodeList childNodes = element.getChildNodes();

         for(int i = 0; i < childNodes.getLength(); ++i) {
            Node item = childNodes.item(i);
            if (item instanceof Element) {
               if (item.getNodeName().equals("comment")) {
                  this.setHeader(item.getTextContent());
               } else {
                  if (!item.getNodeName().equals("entry")) {
                     throw new ConfigurationException("Malformed XML");
                  }

                  String key = ((Element)item).getAttribute("key");
                  this.addProperty(key, item.getTextContent());
               }
            }
         }

      }
   }

   public void save(Writer out) throws ConfigurationException {
      PrintWriter writer = new PrintWriter(out);
      String encoding = this.getEncoding() != null ? this.getEncoding() : "UTF-8";
      writer.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
      writer.println("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
      writer.println("<properties>");
      if (this.getHeader() != null) {
         writer.println("  <comment>" + StringEscapeUtils.escapeXml(this.getHeader()) + "</comment>");
      }

      Iterator keys = this.getKeys();

      while(keys.hasNext()) {
         String key = (String)keys.next();
         Object value = this.getProperty(key);
         if (value instanceof List) {
            this.writeProperty(writer, key, (List)value);
         } else {
            this.writeProperty(writer, key, value);
         }
      }

      writer.println("</properties>");
      writer.flush();
   }

   private void writeProperty(PrintWriter out, String key, Object value) {
      String k = StringEscapeUtils.escapeXml(key);
      if (value != null) {
         String v = StringEscapeUtils.escapeXml(String.valueOf(value));
         v = StringUtils.replace(v, String.valueOf(this.getListDelimiter()), "\\" + this.getListDelimiter());
         out.println("  <entry key=\"" + k + "\">" + v + "</entry>");
      } else {
         out.println("  <entry key=\"" + k + "\"/>");
      }

   }

   private void writeProperty(PrintWriter out, String key, List values) {
      Iterator i$ = values.iterator();

      while(i$.hasNext()) {
         Object value = i$.next();
         this.writeProperty(out, key, value);
      }

   }

   public void save(Document document, Node parent) {
      Element properties = document.createElement("properties");
      parent.appendChild(properties);
      if (this.getHeader() != null) {
         Element comment = document.createElement("comment");
         properties.appendChild(comment);
         comment.setTextContent(StringEscapeUtils.escapeXml(this.getHeader()));
      }

      Iterator keys = this.getKeys();

      while(keys.hasNext()) {
         String key = (String)keys.next();
         Object value = this.getProperty(key);
         if (value instanceof List) {
            this.writeProperty(document, properties, key, (List)((List)value));
         } else {
            this.writeProperty(document, properties, key, (Object)value);
         }
      }

   }

   private void writeProperty(Document document, Node properties, String key, Object value) {
      Element entry = document.createElement("entry");
      properties.appendChild(entry);
      String k = StringEscapeUtils.escapeXml(key);
      entry.setAttribute("key", k);
      if (value != null) {
         String v = StringEscapeUtils.escapeXml(String.valueOf(value));
         v = StringUtils.replace(v, String.valueOf(this.getListDelimiter()), "\\" + this.getListDelimiter());
         entry.setTextContent(v);
      }

   }

   private void writeProperty(Document document, Node properties, String key, List values) {
      Iterator i$ = values.iterator();

      while(i$.hasNext()) {
         Object value = i$.next();
         this.writeProperty(document, properties, key, value);
      }

   }

   private class XMLPropertiesHandler extends DefaultHandler {
      private String key;
      private StringBuilder value;
      private boolean inCommentElement;
      private boolean inEntryElement;

      private XMLPropertiesHandler() {
         this.value = new StringBuilder();
      }

      public void startElement(String uri, String localName, String qName, Attributes attrs) {
         if ("comment".equals(qName)) {
            this.inCommentElement = true;
         }

         if ("entry".equals(qName)) {
            this.key = attrs.getValue("key");
            this.inEntryElement = true;
         }

      }

      public void endElement(String uri, String localName, String qName) {
         if (this.inCommentElement) {
            XMLPropertiesConfiguration.this.setHeader(this.value.toString());
            this.inCommentElement = false;
         }

         if (this.inEntryElement) {
            XMLPropertiesConfiguration.this.addProperty(this.key, this.value.toString());
            this.inEntryElement = false;
         }

         this.value = new StringBuilder();
      }

      public void characters(char[] chars, int start, int length) {
         this.value.append(chars, start, length);
      }

      // $FF: synthetic method
      XMLPropertiesHandler(Object x1) {
         this();
      }
   }
}

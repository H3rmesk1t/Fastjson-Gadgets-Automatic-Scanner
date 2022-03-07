package org.apache.commons.configuration.plist;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.AbstractHierarchicalFileConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLPropertyListConfiguration extends AbstractHierarchicalFileConfiguration {
   private static final long serialVersionUID = -3162063751042475985L;
   private static final int INDENT_SIZE = 4;

   public XMLPropertyListConfiguration() {
      this.initRoot();
   }

   public XMLPropertyListConfiguration(HierarchicalConfiguration configuration) {
      super(configuration);
   }

   public XMLPropertyListConfiguration(String fileName) throws ConfigurationException {
      super(fileName);
   }

   public XMLPropertyListConfiguration(File file) throws ConfigurationException {
      super(file);
   }

   public XMLPropertyListConfiguration(URL url) throws ConfigurationException {
      super(url);
   }

   public void setProperty(String key, Object value) {
      if (value instanceof byte[]) {
         this.fireEvent(3, key, value, true);
         this.setDetailEvents(false);

         try {
            this.clearProperty(key);
            this.addPropertyDirect(key, value);
         } finally {
            this.setDetailEvents(true);
         }

         this.fireEvent(3, key, value, false);
      } else {
         super.setProperty(key, value);
      }

   }

   public void addProperty(String key, Object value) {
      if (value instanceof byte[]) {
         this.fireEvent(1, key, value, true);
         this.addPropertyDirect(key, value);
         this.fireEvent(1, key, value, false);
      } else {
         super.addProperty(key, value);
      }

   }

   public void load(Reader in) throws ConfigurationException {
      if (!(this.getRootNode() instanceof XMLPropertyListConfiguration.PListNode)) {
         this.initRoot();
      }

      EntityResolver resolver = new EntityResolver() {
         public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(this.getClass().getClassLoader().getResourceAsStream("PropertyList-1.0.dtd"));
         }
      };
      XMLPropertyListConfiguration.XMLPropertyListHandler handler = new XMLPropertyListConfiguration.XMLPropertyListHandler(this.getRoot());

      try {
         SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setValidating(true);
         SAXParser parser = factory.newSAXParser();
         parser.getXMLReader().setEntityResolver(resolver);
         parser.getXMLReader().setContentHandler(handler);
         parser.getXMLReader().parse(new InputSource(in));
      } catch (Exception var6) {
         throw new ConfigurationException("Unable to parse the configuration file", var6);
      }
   }

   public void save(Writer out) throws ConfigurationException {
      PrintWriter writer = new PrintWriter(out);
      if (this.getEncoding() != null) {
         writer.println("<?xml version=\"1.0\" encoding=\"" + this.getEncoding() + "\"?>");
      } else {
         writer.println("<?xml version=\"1.0\"?>");
      }

      writer.println("<!DOCTYPE plist SYSTEM \"file://localhost/System/Library/DTDs/PropertyList.dtd\">");
      writer.println("<plist version=\"1.0\">");
      this.printNode(writer, 1, this.getRoot());
      writer.println("</plist>");
      writer.flush();
   }

   private void printNode(PrintWriter out, int indentLevel, ConfigurationNode node) {
      String padding = StringUtils.repeat(" ", indentLevel * 4);
      if (node.getName() != null) {
         out.println(padding + "<key>" + StringEscapeUtils.escapeXml(node.getName()) + "</key>");
      }

      List children = node.getChildren();
      if (!children.isEmpty()) {
         out.println(padding + "<dict>");
         Iterator it = children.iterator();

         while(it.hasNext()) {
            ConfigurationNode child = (ConfigurationNode)it.next();
            this.printNode(out, indentLevel + 1, child);
            if (it.hasNext()) {
               out.println();
            }
         }

         out.println(padding + "</dict>");
      } else if (node.getValue() == null) {
         out.println(padding + "<dict/>");
      } else {
         Object value = node.getValue();
         this.printValue(out, indentLevel, value);
      }

   }

   private void printValue(PrintWriter out, int indentLevel, Object value) {
      String padding = StringUtils.repeat(" ", indentLevel * 4);
      if (value instanceof Date) {
         synchronized(XMLPropertyListConfiguration.PListNode.FORMAT) {
            out.println(padding + "<date>" + XMLPropertyListConfiguration.PListNode.FORMAT.format((Date)value) + "</date>");
         }
      } else if (value instanceof Calendar) {
         this.printValue(out, indentLevel, ((Calendar)value).getTime());
      } else if (value instanceof Number) {
         if (!(value instanceof Double) && !(value instanceof Float) && !(value instanceof BigDecimal)) {
            out.println(padding + "<integer>" + value.toString() + "</integer>");
         } else {
            out.println(padding + "<real>" + value.toString() + "</real>");
         }
      } else if (value instanceof Boolean) {
         if ((Boolean)value) {
            out.println(padding + "<true/>");
         } else {
            out.println(padding + "<false/>");
         }
      } else if (value instanceof List) {
         out.println(padding + "<array>");
         Iterator it = ((List)value).iterator();

         while(it.hasNext()) {
            this.printValue(out, indentLevel + 1, it.next());
         }

         out.println(padding + "</array>");
      } else if (value instanceof HierarchicalConfiguration) {
         this.printNode(out, indentLevel, ((HierarchicalConfiguration)value).getRoot());
      } else if (value instanceof Configuration) {
         out.println(padding + "<dict>");
         Configuration config = (Configuration)value;
         Iterator it = config.getKeys();

         while(it.hasNext()) {
            String key = (String)it.next();
            HierarchicalConfiguration.Node node = new HierarchicalConfiguration.Node(key);
            node.setValue(config.getProperty(key));
            this.printNode(out, indentLevel + 1, node);
            if (it.hasNext()) {
               out.println();
            }
         }

         out.println(padding + "</dict>");
      } else if (value instanceof Map) {
         Map map = transformMap((Map)value);
         this.printValue(out, indentLevel, new MapConfiguration(map));
      } else if (value instanceof byte[]) {
         String base64 = new String(Base64.encodeBase64((byte[])((byte[])value)));
         out.println(padding + "<data>" + StringEscapeUtils.escapeXml(base64) + "</data>");
      } else if (value != null) {
         out.println(padding + "<string>" + StringEscapeUtils.escapeXml(String.valueOf(value)) + "</string>");
      } else {
         out.println(padding + "<string/>");
      }

   }

   private void initRoot() {
      this.setRootNode(new XMLPropertyListConfiguration.PListNode());
   }

   private static Map transformMap(Map src) {
      Map dest = new HashMap();
      Iterator i$ = src.entrySet().iterator();

      while(i$.hasNext()) {
         Entry e = (Entry)i$.next();
         if (e.getKey() instanceof String) {
            dest.put((String)e.getKey(), e.getValue());
         }
      }

      return dest;
   }

   public static class ArrayNode extends XMLPropertyListConfiguration.PListNode {
      private static final long serialVersionUID = 5586544306664205835L;
      private List list = new ArrayList();

      public void addValue(Object value) {
         this.list.add(value);
      }

      public Object getValue() {
         return this.list;
      }
   }

   public static class PListNode extends HierarchicalConfiguration.Node {
      private static final long serialVersionUID = -7614060264754798317L;
      private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      private static final DateFormat GNUSTEP_FORMAT;

      public void addValue(Object value) {
         if (this.getValue() == null) {
            this.setValue(value);
         } else if (this.getValue() instanceof Collection) {
            Collection collection = (Collection)this.getValue();
            collection.add(value);
         } else {
            List list = new ArrayList();
            list.add(this.getValue());
            list.add(value);
            this.setValue(list);
         }

      }

      public void addDateValue(String value) {
         try {
            if (value.indexOf(32) != -1) {
               synchronized(GNUSTEP_FORMAT) {
                  this.addValue(GNUSTEP_FORMAT.parse(value));
               }
            } else {
               synchronized(FORMAT) {
                  this.addValue(FORMAT.parse(value));
               }
            }

         } catch (java.text.ParseException var7) {
            throw new IllegalArgumentException(String.format("'%s' cannot be parsed to a date!", value), var7);
         }
      }

      public void addDataValue(String value) {
         this.addValue(Base64.decodeBase64(value.getBytes()));
      }

      public void addIntegerValue(String value) {
         this.addValue(new BigInteger(value));
      }

      public void addRealValue(String value) {
         this.addValue(new BigDecimal(value));
      }

      public void addTrueValue() {
         this.addValue(Boolean.TRUE);
      }

      public void addFalseValue() {
         this.addValue(Boolean.FALSE);
      }

      public void addList(XMLPropertyListConfiguration.ArrayNode node) {
         this.addValue(node.getValue());
      }

      static {
         FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
         GNUSTEP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
      }
   }

   private class XMLPropertyListHandler extends DefaultHandler {
      private StringBuilder buffer = new StringBuilder();
      private List stack = new ArrayList();

      public XMLPropertyListHandler(HierarchicalConfiguration.Node root) {
         this.push(root);
      }

      private HierarchicalConfiguration.Node peek() {
         return !this.stack.isEmpty() ? (HierarchicalConfiguration.Node)this.stack.get(this.stack.size() - 1) : null;
      }

      private HierarchicalConfiguration.Node pop() {
         return !this.stack.isEmpty() ? (HierarchicalConfiguration.Node)this.stack.remove(this.stack.size() - 1) : null;
      }

      private void push(HierarchicalConfiguration.Node node) {
         this.stack.add(node);
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if ("array".equals(qName)) {
            this.push(new XMLPropertyListConfiguration.ArrayNode());
         } else if ("dict".equals(qName) && this.peek() instanceof XMLPropertyListConfiguration.ArrayNode) {
            XMLPropertyListConfiguration config = new XMLPropertyListConfiguration();
            XMLPropertyListConfiguration.ArrayNode node = (XMLPropertyListConfiguration.ArrayNode)this.peek();
            node.addValue(config);
            this.push(config.getRoot());
         }

      }

      public void endElement(String uri, String localName, String qName) throws SAXException {
         if ("key".equals(qName)) {
            XMLPropertyListConfiguration.PListNode node = new XMLPropertyListConfiguration.PListNode();
            node.setName(this.buffer.toString());
            this.peek().addChild(node);
            this.push(node);
         } else if ("dict".equals(qName)) {
            this.pop();
         } else {
            if ("string".equals(qName)) {
               ((XMLPropertyListConfiguration.PListNode)this.peek()).addValue(this.buffer.toString());
            } else if ("integer".equals(qName)) {
               ((XMLPropertyListConfiguration.PListNode)this.peek()).addIntegerValue(this.buffer.toString());
            } else if ("real".equals(qName)) {
               ((XMLPropertyListConfiguration.PListNode)this.peek()).addRealValue(this.buffer.toString());
            } else if ("true".equals(qName)) {
               ((XMLPropertyListConfiguration.PListNode)this.peek()).addTrueValue();
            } else if ("false".equals(qName)) {
               ((XMLPropertyListConfiguration.PListNode)this.peek()).addFalseValue();
            } else if ("data".equals(qName)) {
               ((XMLPropertyListConfiguration.PListNode)this.peek()).addDataValue(this.buffer.toString());
            } else if ("date".equals(qName)) {
               try {
                  ((XMLPropertyListConfiguration.PListNode)this.peek()).addDateValue(this.buffer.toString());
               } catch (IllegalArgumentException var5) {
                  XMLPropertyListConfiguration.this.getLogger().warn("Ignoring invalid date property " + this.buffer);
               }
            } else if ("array".equals(qName)) {
               XMLPropertyListConfiguration.ArrayNode array = (XMLPropertyListConfiguration.ArrayNode)this.pop();
               ((XMLPropertyListConfiguration.PListNode)this.peek()).addList(array);
            }

            if (!(this.peek() instanceof XMLPropertyListConfiguration.ArrayNode)) {
               this.pop();
            }
         }

         this.buffer.setLength(0);
      }

      public void characters(char[] ch, int start, int length) throws SAXException {
         this.buffer.append(ch, start, length);
      }
   }
}

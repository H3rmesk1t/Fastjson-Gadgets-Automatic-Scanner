package org.apache.commons.configuration.plist;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.configuration.AbstractHierarchicalFileConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringUtils;

public class PropertyListConfiguration extends AbstractHierarchicalFileConfiguration {
   private static final PropertyListConfiguration.DateComponentParser DATE_SEPARATOR_PARSER = new PropertyListConfiguration.DateSeparatorParser("-");
   private static final PropertyListConfiguration.DateComponentParser TIME_SEPARATOR_PARSER = new PropertyListConfiguration.DateSeparatorParser(":");
   private static final PropertyListConfiguration.DateComponentParser BLANK_SEPARATOR_PARSER = new PropertyListConfiguration.DateSeparatorParser(" ");
   private static final PropertyListConfiguration.DateComponentParser[] DATE_PARSERS;
   private static final String TIME_ZONE_PREFIX = "GMT";
   private static final long serialVersionUID = 3227248503779092127L;
   private static final int MILLIS_PER_MINUTE = 60000;
   private static final int MINUTES_PER_HOUR = 60;
   private static final int INDENT_SIZE = 4;
   private static final int TIME_ZONE_LENGTH = 5;
   private static final char PAD_CHAR = '0';

   public PropertyListConfiguration() {
   }

   public PropertyListConfiguration(HierarchicalConfiguration c) {
      super(c);
   }

   public PropertyListConfiguration(String fileName) throws ConfigurationException {
      super(fileName);
   }

   public PropertyListConfiguration(File file) throws ConfigurationException {
      super(file);
   }

   public PropertyListConfiguration(URL url) throws ConfigurationException {
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
      PropertyListParser parser = new PropertyListParser(in);

      try {
         HierarchicalConfiguration config = parser.parse();
         this.setRoot(config.getRoot());
      } catch (ParseException var4) {
         throw new ConfigurationException(var4);
      }
   }

   public void save(Writer out) throws ConfigurationException {
      PrintWriter writer = new PrintWriter(out);
      this.printNode(writer, 0, this.getRoot());
      writer.flush();
   }

   private void printNode(PrintWriter out, int indentLevel, ConfigurationNode node) {
      String padding = StringUtils.repeat(" ", indentLevel * 4);
      if (node.getName() != null) {
         out.print(padding + this.quoteString(node.getName()) + " = ");
      }

      List children = new ArrayList(node.getChildren());
      if (!children.isEmpty()) {
         if (indentLevel > 0) {
            out.println();
         }

         out.println(padding + "{");
         Iterator it = children.iterator();

         while(true) {
            Object value;
            do {
               do {
                  if (!it.hasNext()) {
                     out.print(padding + "}");
                     if (node.getParentNode() != null) {
                        out.println();
                     }

                     return;
                  }

                  ConfigurationNode child = (ConfigurationNode)it.next();
                  this.printNode(out, indentLevel + 1, child);
                  value = child.getValue();
                  if (value != null && !(value instanceof Map) && !(value instanceof Configuration)) {
                     out.println(";");
                  }
               } while(!it.hasNext());
            } while(value != null && !(value instanceof List));

            out.println();
         }
      } else if (node.getValue() == null) {
         out.println();
         out.print(padding + "{ };");
         if (node.getParentNode() != null) {
            out.println();
         }
      } else {
         Object value = node.getValue();
         this.printValue(out, indentLevel, value);
      }

   }

   private void printValue(PrintWriter out, int indentLevel, Object value) {
      String padding = StringUtils.repeat(" ", indentLevel * 4);
      if (value instanceof List) {
         out.print("( ");
         Iterator it = ((List)value).iterator();

         while(it.hasNext()) {
            this.printValue(out, indentLevel + 1, it.next());
            if (it.hasNext()) {
               out.print(", ");
            }
         }

         out.print(" )");
      } else if (value instanceof HierarchicalConfiguration) {
         this.printNode(out, indentLevel, ((HierarchicalConfiguration)value).getRoot());
      } else if (value instanceof Configuration) {
         out.println();
         out.println(padding + "{");
         Configuration config = (Configuration)value;
         Iterator it = config.getKeys();

         while(it.hasNext()) {
            String key = (String)it.next();
            HierarchicalConfiguration.Node node = new HierarchicalConfiguration.Node(key);
            node.setValue(config.getProperty(key));
            this.printNode(out, indentLevel + 1, node);
            out.println(";");
         }

         out.println(padding + "}");
      } else if (value instanceof Map) {
         Map map = transformMap((Map)value);
         this.printValue(out, indentLevel, new MapConfiguration(map));
      } else if (value instanceof byte[]) {
         out.print("<" + new String(Hex.encodeHex((byte[])((byte[])value))) + ">");
      } else if (value instanceof Date) {
         out.print(formatDate((Date)value));
      } else if (value != null) {
         out.print(this.quoteString(String.valueOf(value)));
      }

   }

   String quoteString(String s) {
      if (s == null) {
         return null;
      } else {
         if (s.indexOf(32) != -1 || s.indexOf(9) != -1 || s.indexOf(13) != -1 || s.indexOf(10) != -1 || s.indexOf(34) != -1 || s.indexOf(40) != -1 || s.indexOf(41) != -1 || s.indexOf(123) != -1 || s.indexOf(125) != -1 || s.indexOf(61) != -1 || s.indexOf(44) != -1 || s.indexOf(59) != -1) {
            s = s.replaceAll("\"", "\\\\\\\"");
            s = "\"" + s + "\"";
         }

         return s;
      }
   }

   static Date parseDate(String s) throws ParseException {
      Calendar cal = Calendar.getInstance();
      cal.clear();
      int index = 0;
      PropertyListConfiguration.DateComponentParser[] arr$ = DATE_PARSERS;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         PropertyListConfiguration.DateComponentParser parser = arr$[i$];
         index += parser.parseComponent(s, index, cal);
      }

      return cal.getTime();
   }

   static String formatDate(Calendar cal) {
      StringBuilder buf = new StringBuilder();

      for(int i = 0; i < DATE_PARSERS.length; ++i) {
         DATE_PARSERS[i].formatComponent(buf, cal);
      }

      return buf.toString();
   }

   static String formatDate(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      return formatDate(cal);
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

   static {
      DATE_PARSERS = new PropertyListConfiguration.DateComponentParser[]{new PropertyListConfiguration.DateSeparatorParser("<*D"), new PropertyListConfiguration.DateFieldParser(1, 4), DATE_SEPARATOR_PARSER, new PropertyListConfiguration.DateFieldParser(2, 2, 1), DATE_SEPARATOR_PARSER, new PropertyListConfiguration.DateFieldParser(5, 2), BLANK_SEPARATOR_PARSER, new PropertyListConfiguration.DateFieldParser(11, 2), TIME_SEPARATOR_PARSER, new PropertyListConfiguration.DateFieldParser(12, 2), TIME_SEPARATOR_PARSER, new PropertyListConfiguration.DateFieldParser(13, 2), BLANK_SEPARATOR_PARSER, new PropertyListConfiguration.DateTimeZoneParser(), new PropertyListConfiguration.DateSeparatorParser(">")};
   }

   private static class DateTimeZoneParser extends PropertyListConfiguration.DateComponentParser {
      private DateTimeZoneParser() {
         super(null);
      }

      public void formatComponent(StringBuilder buf, Calendar cal) {
         TimeZone tz = cal.getTimeZone();
         int ofs = tz.getRawOffset() / '\uea60';
         if (ofs < 0) {
            buf.append('-');
            ofs = -ofs;
         } else {
            buf.append('+');
         }

         int hour = ofs / 60;
         int min = ofs % 60;
         this.padNum(buf, hour, 2);
         this.padNum(buf, min, 2);
      }

      public int parseComponent(String s, int index, Calendar cal) throws ParseException {
         this.checkLength(s, index, 5);
         TimeZone tz = TimeZone.getTimeZone("GMT" + s.substring(index, index + 5));
         cal.setTimeZone(tz);
         return 5;
      }

      // $FF: synthetic method
      DateTimeZoneParser(Object x0) {
         this();
      }
   }

   private static class DateSeparatorParser extends PropertyListConfiguration.DateComponentParser {
      private String separator;

      public DateSeparatorParser(String sep) {
         super(null);
         this.separator = sep;
      }

      public void formatComponent(StringBuilder buf, Calendar cal) {
         buf.append(this.separator);
      }

      public int parseComponent(String s, int index, Calendar cal) throws ParseException {
         this.checkLength(s, index, this.separator.length());
         if (!s.startsWith(this.separator, index)) {
            throw new ParseException("Invalid input: " + s + ", index " + index + ", expected " + this.separator);
         } else {
            return this.separator.length();
         }
      }
   }

   private static class DateFieldParser extends PropertyListConfiguration.DateComponentParser {
      private int calendarField;
      private int length;
      private int offset;

      public DateFieldParser(int calFld, int len) {
         this(calFld, len, 0);
      }

      public DateFieldParser(int calFld, int len, int ofs) {
         super(null);
         this.calendarField = calFld;
         this.length = len;
         this.offset = ofs;
      }

      public void formatComponent(StringBuilder buf, Calendar cal) {
         this.padNum(buf, cal.get(this.calendarField) + this.offset, this.length);
      }

      public int parseComponent(String s, int index, Calendar cal) throws ParseException {
         this.checkLength(s, index, this.length);

         try {
            cal.set(this.calendarField, Integer.parseInt(s.substring(index, index + this.length)) - this.offset);
            return this.length;
         } catch (NumberFormatException var5) {
            throw new ParseException("Invalid number: " + s + ", index " + index);
         }
      }
   }

   private abstract static class DateComponentParser {
      private DateComponentParser() {
      }

      public abstract int parseComponent(String var1, int var2, Calendar var3) throws ParseException;

      public abstract void formatComponent(StringBuilder var1, Calendar var2);

      protected void checkLength(String s, int index, int length) throws ParseException {
         int len = s == null ? 0 : s.length();
         if (index + length > len) {
            throw new ParseException("Input string too short: " + s + ", index: " + index);
         }
      }

      protected void padNum(StringBuilder buf, int num, int length) {
         buf.append(StringUtils.leftPad(String.valueOf(num), length, '0'));
      }

      // $FF: synthetic method
      DateComponentParser(Object x0) {
         this();
      }
   }
}

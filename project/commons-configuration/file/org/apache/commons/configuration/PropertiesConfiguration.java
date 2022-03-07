package org.apache.commons.configuration;

import java.io.File;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class PropertiesConfiguration extends AbstractFileConfiguration {
   static final String COMMENT_CHARS = "#!";
   static final String DEFAULT_SEPARATOR = " = ";
   private static final PropertiesConfiguration.IOFactory DEFAULT_IO_FACTORY = new PropertiesConfiguration.DefaultIOFactory();
   private static String include = "include";
   private static final char[] SEPARATORS = new char[]{'=', ':'};
   private static final char[] WHITE_SPACE = new char[]{' ', '\t', '\f'};
   private static final String DEFAULT_ENCODING = "ISO-8859-1";
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static final String ESCAPE = "\\";
   private static final String DOUBLE_ESC = "\\\\";
   private static final int HEX_RADIX = 16;
   private static final int UNICODE_LEN = 4;
   private PropertiesConfigurationLayout layout;
   private volatile PropertiesConfiguration.IOFactory ioFactory;
   private boolean includesAllowed = true;

   public PropertiesConfiguration() {
      this.layout = this.createLayout();
   }

   public PropertiesConfiguration(String fileName) throws ConfigurationException {
      super(fileName);
   }

   public PropertiesConfiguration(File file) throws ConfigurationException {
      super(file);
      this.getLayout();
   }

   public PropertiesConfiguration(URL url) throws ConfigurationException {
      super(url);
   }

   public static String getInclude() {
      return include;
   }

   public static void setInclude(String inc) {
      include = inc;
   }

   public void setIncludesAllowed(boolean includesAllowed) {
      this.includesAllowed = includesAllowed;
   }

   /** @deprecated */
   @Deprecated
   public boolean getIncludesAllowed() {
      return this.isIncludesAllowed();
   }

   public boolean isIncludesAllowed() {
      return this.includesAllowed;
   }

   public String getHeader() {
      return this.getLayout().getHeaderComment();
   }

   public void setHeader(String header) {
      this.getLayout().setHeaderComment(header);
   }

   public String getFooter() {
      return this.getLayout().getFooterComment();
   }

   public void setFooter(String footer) {
      this.getLayout().setFooterComment(footer);
   }

   public String getEncoding() {
      String enc = super.getEncoding();
      return enc != null ? enc : "ISO-8859-1";
   }

   public synchronized PropertiesConfigurationLayout getLayout() {
      if (this.layout == null) {
         this.layout = this.createLayout();
      }

      return this.layout;
   }

   public synchronized void setLayout(PropertiesConfigurationLayout layout) {
      if (this.layout != null) {
         this.removeConfigurationListener(this.layout);
      }

      if (layout == null) {
         this.layout = this.createLayout();
      } else {
         this.layout = layout;
      }

   }

   protected PropertiesConfigurationLayout createLayout() {
      return new PropertiesConfigurationLayout(this);
   }

   public PropertiesConfiguration.IOFactory getIOFactory() {
      return this.ioFactory != null ? this.ioFactory : DEFAULT_IO_FACTORY;
   }

   public void setIOFactory(PropertiesConfiguration.IOFactory ioFactory) {
      if (ioFactory == null) {
         throw new IllegalArgumentException("IOFactory must not be null!");
      } else {
         this.ioFactory = ioFactory;
      }
   }

   public synchronized void load(Reader in) throws ConfigurationException {
      boolean oldAutoSave = this.isAutoSave();
      this.setAutoSave(false);

      try {
         this.getLayout().load(in);
      } finally {
         this.setAutoSave(oldAutoSave);
      }

   }

   public void save(Writer writer) throws ConfigurationException {
      this.enterNoReload();

      try {
         this.getLayout().save(writer);
      } finally {
         this.exitNoReload();
      }

   }

   public void setBasePath(String basePath) {
      super.setBasePath(basePath);
      this.setIncludesAllowed(StringUtils.isNotEmpty(basePath));
   }

   public Object clone() {
      PropertiesConfiguration copy = (PropertiesConfiguration)super.clone();
      if (this.layout != null) {
         copy.setLayout(new PropertiesConfigurationLayout(copy, this.layout));
      }

      return copy;
   }

   boolean propertyLoaded(String key, String value) throws ConfigurationException {
      boolean result;
      if (StringUtils.isNotEmpty(getInclude()) && key.equalsIgnoreCase(getInclude())) {
         if (this.isIncludesAllowed()) {
            String[] files;
            if (!this.isDelimiterParsingDisabled()) {
               files = StringUtils.split(value, this.getListDelimiter());
            } else {
               files = new String[]{value};
            }

            String[] arr$ = files;
            int len$ = files.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               String f = arr$[i$];
               this.loadIncludeFile(this.interpolate(f.trim()));
            }
         }

         result = false;
      } else {
         this.addProperty(key, value);
         result = true;
      }

      return result;
   }

   static boolean isCommentLine(String line) {
      String s = line.trim();
      return s.length() < 1 || "#!".indexOf(s.charAt(0)) >= 0;
   }

   private static int countTrailingBS(String line) {
      int bsCount = 0;

      for(int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; --idx) {
         ++bsCount;
      }

      return bsCount;
   }

   protected static String unescapeJava(String str, char delimiter) {
      if (str == null) {
         return null;
      } else {
         int sz = str.length();
         StringBuilder out = new StringBuilder(sz);
         StringBuilder unicode = new StringBuilder(4);
         boolean hadSlash = false;
         boolean inUnicode = false;

         for(int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (inUnicode) {
               unicode.append(ch);
               if (unicode.length() == 4) {
                  try {
                     int value = Integer.parseInt(unicode.toString(), 16);
                     out.append((char)value);
                     unicode.setLength(0);
                     inUnicode = false;
                     hadSlash = false;
                  } catch (NumberFormatException var10) {
                     throw new ConfigurationRuntimeException("Unable to parse unicode value: " + unicode, var10);
                  }
               }
            } else if (hadSlash) {
               hadSlash = false;
               if (ch == '\\') {
                  out.append('\\');
               } else if (ch == '\'') {
                  out.append('\'');
               } else if (ch == '"') {
                  out.append('"');
               } else if (ch == 'r') {
                  out.append('\r');
               } else if (ch == 'f') {
                  out.append('\f');
               } else if (ch == 't') {
                  out.append('\t');
               } else if (ch == 'n') {
                  out.append('\n');
               } else if (ch == 'b') {
                  out.append('\b');
               } else if (ch == delimiter) {
                  out.append('\\');
                  out.append(delimiter);
               } else if (ch == 'u') {
                  inUnicode = true;
               } else {
                  out.append(ch);
               }
            } else if (ch == '\\') {
               hadSlash = true;
            } else {
               out.append(ch);
            }
         }

         if (hadSlash) {
            out.append('\\');
         }

         return out.toString();
      }
   }

   private void loadIncludeFile(String fileName) throws ConfigurationException {
      URL url = ConfigurationUtils.locate(this.getFileSystem(), this.getBasePath(), fileName);
      if (url == null) {
         URL baseURL = this.getURL();
         if (baseURL != null) {
            url = ConfigurationUtils.locate(this.getFileSystem(), baseURL.toString(), fileName);
         }
      }

      if (url == null) {
         throw new ConfigurationException("Cannot resolve include file " + fileName);
      } else {
         this.load(url);
      }
   }

   public static class DefaultIOFactory implements PropertiesConfiguration.IOFactory {
      public PropertiesConfiguration.PropertiesReader createPropertiesReader(Reader in, char delimiter) {
         return new PropertiesConfiguration.PropertiesReader(in, delimiter);
      }

      public PropertiesConfiguration.PropertiesWriter createPropertiesWriter(Writer out, char delimiter) {
         return new PropertiesConfiguration.PropertiesWriter(out, delimiter);
      }
   }

   public interface IOFactory {
      PropertiesConfiguration.PropertiesReader createPropertiesReader(Reader var1, char var2);

      PropertiesConfiguration.PropertiesWriter createPropertiesWriter(Writer var1, char var2);
   }

   public static class PropertiesWriter extends FilterWriter {
      private static final int BUF_SIZE = 8;
      private char delimiter;
      private String currentSeparator;
      private String globalSeparator;
      private String lineSeparator;

      public PropertiesWriter(Writer writer, char delimiter) {
         super(writer);
         this.delimiter = delimiter;
      }

      public String getCurrentSeparator() {
         return this.currentSeparator;
      }

      public void setCurrentSeparator(String currentSeparator) {
         this.currentSeparator = currentSeparator;
      }

      public String getGlobalSeparator() {
         return this.globalSeparator;
      }

      public void setGlobalSeparator(String globalSeparator) {
         this.globalSeparator = globalSeparator;
      }

      public String getLineSeparator() {
         return this.lineSeparator != null ? this.lineSeparator : PropertiesConfiguration.LINE_SEPARATOR;
      }

      public void setLineSeparator(String lineSeparator) {
         this.lineSeparator = lineSeparator;
      }

      public void writeProperty(String key, Object value) throws IOException {
         this.writeProperty(key, value, false);
      }

      public void writeProperty(String key, List values) throws IOException {
         for(int i = 0; i < values.size(); ++i) {
            this.writeProperty(key, values.get(i));
         }

      }

      public void writeProperty(String key, Object value, boolean forceSingleLine) throws IOException {
         String v;
         if (value instanceof List) {
            List values = (List)value;
            if (!forceSingleLine) {
               this.writeProperty(key, values);
               return;
            }

            v = this.makeSingleLineValue(values);
         } else {
            v = this.escapeValue(value, false);
         }

         this.write(this.escapeKey(key));
         this.write(this.fetchSeparator(key, value));
         this.write(v);
         this.writeln((String)null);
      }

      public void writeComment(String comment) throws IOException {
         this.writeln("# " + comment);
      }

      private String escapeKey(String key) {
         StringBuilder newkey = new StringBuilder();

         for(int i = 0; i < key.length(); ++i) {
            char c = key.charAt(i);
            if (!ArrayUtils.contains(PropertiesConfiguration.SEPARATORS, c) && !ArrayUtils.contains(PropertiesConfiguration.WHITE_SPACE, c)) {
               newkey.append(c);
            } else {
               newkey.append('\\');
               newkey.append(c);
            }
         }

         return newkey.toString();
      }

      private String escapeValue(Object value, boolean inList) {
         String escapedValue = this.handleBackslashs(value, inList);
         if (this.delimiter != 0) {
            escapedValue = StringUtils.replace(escapedValue, String.valueOf(this.delimiter), "\\" + this.delimiter);
         }

         return escapedValue;
      }

      private String handleBackslashs(Object value, boolean inList) {
         String strValue = String.valueOf(value);
         if (inList && strValue.indexOf("\\\\") >= 0) {
            char esc = "\\".charAt(0);
            StringBuilder buf = new StringBuilder(strValue.length() + 8);

            for(int i = 0; i < strValue.length(); ++i) {
               if (strValue.charAt(i) == esc && i < strValue.length() - 1 && strValue.charAt(i + 1) == esc) {
                  buf.append("\\\\").append("\\\\");
                  ++i;
               } else {
                  buf.append(strValue.charAt(i));
               }
            }

            strValue = buf.toString();
         }

         return StringEscapeUtils.escapeJava(strValue);
      }

      private String makeSingleLineValue(List values) {
         if (!values.isEmpty()) {
            Iterator it = values.iterator();
            String lastValue = this.escapeValue(it.next(), true);
            StringBuilder buf = new StringBuilder(lastValue);

            while(it.hasNext()) {
               if (lastValue.endsWith("\\") && PropertiesConfiguration.countTrailingBS(lastValue) / 2 % 2 != 0) {
                  buf.append("\\").append("\\");
               }

               buf.append(this.delimiter);
               lastValue = this.escapeValue(it.next(), true);
               buf.append(lastValue);
            }

            return buf.toString();
         } else {
            return null;
         }
      }

      public void writeln(String s) throws IOException {
         if (s != null) {
            this.write(s);
         }

         this.write(this.getLineSeparator());
      }

      protected String fetchSeparator(String key, Object value) {
         return this.getGlobalSeparator() != null ? this.getGlobalSeparator() : this.getCurrentSeparator();
      }
   }

   public static class PropertiesReader extends LineNumberReader {
      private static final Pattern PROPERTY_PATTERN;
      private static final int IDX_KEY = 1;
      private static final int IDX_VALUE = 5;
      private static final int IDX_SEPARATOR = 3;
      private List commentLines;
      private String propertyName;
      private String propertyValue;
      private String propertySeparator;
      private char delimiter;

      public PropertiesReader(Reader reader) {
         this(reader, AbstractConfiguration.getDefaultListDelimiter());
      }

      public PropertiesReader(Reader reader, char listDelimiter) {
         super(reader);
         this.propertySeparator = " = ";
         this.commentLines = new ArrayList();
         this.delimiter = listDelimiter;
      }

      public String readProperty() throws IOException {
         this.commentLines.clear();
         StringBuilder buffer = new StringBuilder();

         while(true) {
            String line = this.readLine();
            if (line == null) {
               return null;
            }

            if (PropertiesConfiguration.isCommentLine(line)) {
               this.commentLines.add(line);
            } else {
               line = line.trim();
               if (!checkCombineLines(line)) {
                  buffer.append(line);
                  return buffer.toString();
               }

               line = line.substring(0, line.length() - 1);
               buffer.append(line);
            }
         }
      }

      public boolean nextProperty() throws IOException {
         String line = this.readProperty();
         if (line == null) {
            return false;
         } else {
            this.parseProperty(line);
            return true;
         }
      }

      public List getCommentLines() {
         return this.commentLines;
      }

      public String getPropertyName() {
         return this.propertyName;
      }

      public String getPropertyValue() {
         return this.propertyValue;
      }

      public String getPropertySeparator() {
         return this.propertySeparator;
      }

      protected void parseProperty(String line) {
         String[] property = doParseProperty(line);
         this.initPropertyName(property[0]);
         this.initPropertyValue(property[1]);
         this.initPropertySeparator(property[2]);
      }

      protected void initPropertyName(String name) {
         this.propertyName = StringEscapeUtils.unescapeJava(name);
      }

      protected void initPropertyValue(String value) {
         this.propertyValue = PropertiesConfiguration.unescapeJava(value, this.delimiter);
      }

      protected void initPropertySeparator(String value) {
         this.propertySeparator = value;
      }

      private static boolean checkCombineLines(String line) {
         return PropertiesConfiguration.countTrailingBS(line) % 2 != 0;
      }

      private static String[] doParseProperty(String line) {
         Matcher matcher = PROPERTY_PATTERN.matcher(line);
         String[] result = new String[]{"", "", ""};
         if (matcher.matches()) {
            result[0] = matcher.group(1).trim();
            result[1] = matcher.group(5).trim();
            result[2] = matcher.group(3);
         }

         return result;
      }

      static {
         PROPERTY_PATTERN = Pattern.compile("(([\\S&&[^\\\\" + new String(PropertiesConfiguration.SEPARATORS) + "]]|\\\\.)*)(\\s*(\\s+|[" + new String(PropertiesConfiguration.SEPARATORS) + "])\\s*)(.*)");
      }
   }
}

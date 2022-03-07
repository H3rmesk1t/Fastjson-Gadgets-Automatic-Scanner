package org.apache.commons.configuration;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.lang.StringUtils;

public class PropertiesConfigurationLayout implements ConfigurationListener {
   private static final String CR = "\n";
   private static final String COMMENT_PREFIX = "# ";
   private PropertiesConfiguration configuration;
   private Map layoutData;
   private String headerComment;
   private String footerComment;
   private String globalSeparator;
   private String lineSeparator;
   private int loadCounter;
   private boolean forceSingleLine;

   public PropertiesConfigurationLayout(PropertiesConfiguration config) {
      this(config, (PropertiesConfigurationLayout)null);
   }

   public PropertiesConfigurationLayout(PropertiesConfiguration config, PropertiesConfigurationLayout c) {
      if (config == null) {
         throw new IllegalArgumentException("Configuration must not be null!");
      } else {
         this.configuration = config;
         this.layoutData = new LinkedHashMap();
         config.addConfigurationListener(this);
         if (c != null) {
            this.copyFrom(c);
         }

      }
   }

   public PropertiesConfiguration getConfiguration() {
      return this.configuration;
   }

   public String getCanonicalComment(String key, boolean commentChar) {
      return constructCanonicalComment(this.getComment(key), commentChar);
   }

   public String getComment(String key) {
      return this.fetchLayoutData(key).getComment();
   }

   public void setComment(String key, String comment) {
      this.fetchLayoutData(key).setComment(comment);
   }

   public int getBlancLinesBefore(String key) {
      return this.fetchLayoutData(key).getBlancLines();
   }

   public void setBlancLinesBefore(String key, int number) {
      this.fetchLayoutData(key).setBlancLines(number);
   }

   public String getCanonicalHeaderComment(boolean commentChar) {
      return constructCanonicalComment(this.getHeaderComment(), commentChar);
   }

   public String getHeaderComment() {
      return this.headerComment;
   }

   public void setHeaderComment(String comment) {
      this.headerComment = comment;
   }

   public String getCanonicalFooterCooment(boolean commentChar) {
      return constructCanonicalComment(this.getFooterComment(), commentChar);
   }

   public String getFooterComment() {
      return this.footerComment;
   }

   public void setFooterComment(String footerComment) {
      this.footerComment = footerComment;
   }

   public boolean isSingleLine(String key) {
      return this.fetchLayoutData(key).isSingleLine();
   }

   public void setSingleLine(String key, boolean f) {
      this.fetchLayoutData(key).setSingleLine(f);
   }

   public boolean isForceSingleLine() {
      return this.forceSingleLine;
   }

   public void setForceSingleLine(boolean f) {
      this.forceSingleLine = f;
   }

   public String getSeparator(String key) {
      return this.fetchLayoutData(key).getSeparator();
   }

   public void setSeparator(String key, String sep) {
      this.fetchLayoutData(key).setSeparator(sep);
   }

   public String getGlobalSeparator() {
      return this.globalSeparator;
   }

   public void setGlobalSeparator(String globalSeparator) {
      this.globalSeparator = globalSeparator;
   }

   public String getLineSeparator() {
      return this.lineSeparator;
   }

   public void setLineSeparator(String lineSeparator) {
      this.lineSeparator = lineSeparator;
   }

   public Set getKeys() {
      return this.layoutData.keySet();
   }

   public void load(Reader in) throws ConfigurationException {
      if (++this.loadCounter == 1) {
         this.getConfiguration().removeConfigurationListener(this);
      }

      PropertiesConfiguration.PropertiesReader reader = this.getConfiguration().getIOFactory().createPropertiesReader(in, this.getConfiguration().getListDelimiter());

      try {
         while(reader.nextProperty()) {
            if (this.getConfiguration().propertyLoaded(reader.getPropertyName(), reader.getPropertyValue())) {
               boolean contained = this.layoutData.containsKey(reader.getPropertyName());
               int blancLines = 0;

               int idx;
               for(idx = this.checkHeaderComment(reader.getCommentLines()); idx < reader.getCommentLines().size() && ((String)reader.getCommentLines().get(idx)).length() < 1; ++blancLines) {
                  ++idx;
               }

               String comment = this.extractComment(reader.getCommentLines(), idx, reader.getCommentLines().size() - 1);
               PropertiesConfigurationLayout.PropertyLayoutData data = this.fetchLayoutData(reader.getPropertyName());
               if (contained) {
                  data.addComment(comment);
                  data.setSingleLine(false);
               } else {
                  data.setComment(comment);
                  data.setBlancLines(blancLines);
                  data.setSeparator(reader.getPropertySeparator());
               }
            }
         }

         this.setFooterComment(this.extractComment(reader.getCommentLines(), 0, reader.getCommentLines().size() - 1));
      } catch (IOException var11) {
         throw new ConfigurationException(var11);
      } finally {
         if (--this.loadCounter == 0) {
            this.getConfiguration().addConfigurationListener(this);
         }

      }

   }

   public void save(Writer out) throws ConfigurationException {
      try {
         char delimiter = this.getConfiguration().isDelimiterParsingDisabled() ? 0 : this.getConfiguration().getListDelimiter();
         PropertiesConfiguration.PropertiesWriter writer = this.getConfiguration().getIOFactory().createPropertiesWriter(out, delimiter);
         writer.setGlobalSeparator(this.getGlobalSeparator());
         if (this.getLineSeparator() != null) {
            writer.setLineSeparator(this.getLineSeparator());
         }

         if (this.headerComment != null) {
            writeComment(writer, this.getCanonicalHeaderComment(true));
            writer.writeln((String)null);
         }

         Iterator i$ = this.layoutData.keySet().iterator();

         while(true) {
            String key;
            do {
               if (!i$.hasNext()) {
                  writeComment(writer, this.getCanonicalFooterCooment(true));
                  writer.flush();
                  return;
               }

               key = (String)i$.next();
            } while(!this.getConfiguration().containsKey(key));

            for(int i = 0; i < this.getBlancLinesBefore(key); ++i) {
               writer.writeln((String)null);
            }

            writeComment(writer, this.getCanonicalComment(key, true));
            boolean singleLine = (this.isForceSingleLine() || this.isSingleLine(key)) && !this.getConfiguration().isDelimiterParsingDisabled();
            writer.setCurrentSeparator(this.getSeparator(key));
            writer.writeProperty(key, this.getConfiguration().getProperty(key), singleLine);
         }
      } catch (IOException var7) {
         throw new ConfigurationException(var7);
      }
   }

   public void configurationChanged(ConfigurationEvent event) {
      if (event.isBeforeUpdate()) {
         if (20 == event.getType()) {
            this.clear();
         }
      } else {
         switch(event.getType()) {
         case 1:
            boolean contained = this.layoutData.containsKey(event.getPropertyName());
            PropertiesConfigurationLayout.PropertyLayoutData data = this.fetchLayoutData(event.getPropertyName());
            data.setSingleLine(!contained);
            break;
         case 2:
            this.layoutData.remove(event.getPropertyName());
            break;
         case 3:
            this.fetchLayoutData(event.getPropertyName());
            break;
         case 4:
            this.clear();
         }
      }

   }

   private PropertiesConfigurationLayout.PropertyLayoutData fetchLayoutData(String key) {
      if (key == null) {
         throw new IllegalArgumentException("Property key must not be null!");
      } else {
         PropertiesConfigurationLayout.PropertyLayoutData data = (PropertiesConfigurationLayout.PropertyLayoutData)this.layoutData.get(key);
         if (data == null) {
            data = new PropertiesConfigurationLayout.PropertyLayoutData();
            data.setSingleLine(true);
            this.layoutData.put(key, data);
         }

         return data;
      }
   }

   private void clear() {
      this.layoutData.clear();
      this.setHeaderComment((String)null);
   }

   static boolean isCommentLine(String line) {
      return PropertiesConfiguration.isCommentLine(line);
   }

   static String trimComment(String s, boolean comment) {
      StringBuilder buf = new StringBuilder(s.length());
      int lastPos = 0;

      int pos;
      do {
         pos = s.indexOf("\n", lastPos);
         if (pos >= 0) {
            String line = s.substring(lastPos, pos);
            buf.append(stripCommentChar(line, comment)).append("\n");
            lastPos = pos + "\n".length();
         }
      } while(pos >= 0);

      if (lastPos < s.length()) {
         buf.append(stripCommentChar(s.substring(lastPos), comment));
      }

      return buf.toString();
   }

   static String stripCommentChar(String s, boolean comment) {
      if (s.length() >= 1 && isCommentLine(s) != comment) {
         if (comment) {
            return "# " + s;
         } else {
            int pos;
            for(pos = 0; "#!".indexOf(s.charAt(pos)) < 0; ++pos) {
            }

            ++pos;

            while(pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
               ++pos;
            }

            return pos < s.length() ? s.substring(pos) : "";
         }
      } else {
         return s;
      }
   }

   private String extractComment(List commentLines, int from, int to) {
      if (to < from) {
         return null;
      } else {
         StringBuilder buf = new StringBuilder((String)commentLines.get(from));

         for(int i = from + 1; i <= to; ++i) {
            buf.append("\n");
            buf.append((String)commentLines.get(i));
         }

         return buf.toString();
      }
   }

   private int checkHeaderComment(List commentLines) {
      if (this.loadCounter == 1 && this.getHeaderComment() == null && this.layoutData.isEmpty()) {
         int index;
         for(index = commentLines.size() - 1; index >= 0 && ((String)commentLines.get(index)).length() > 0; --index) {
         }

         this.setHeaderComment(this.extractComment(commentLines, 0, index - 1));
         return index + 1;
      } else {
         return 0;
      }
   }

   private void copyFrom(PropertiesConfigurationLayout c) {
      Iterator i$ = c.getKeys().iterator();

      while(i$.hasNext()) {
         String key = (String)i$.next();
         PropertiesConfigurationLayout.PropertyLayoutData data = (PropertiesConfigurationLayout.PropertyLayoutData)c.layoutData.get(key);
         this.layoutData.put(key, data.clone());
      }

      this.setHeaderComment(c.getHeaderComment());
      this.setFooterComment(c.getFooterComment());
   }

   private static void writeComment(PropertiesConfiguration.PropertiesWriter writer, String comment) throws IOException {
      if (comment != null) {
         writer.writeln(StringUtils.replace(comment, "\n", writer.getLineSeparator()));
      }

   }

   private static String constructCanonicalComment(String comment, boolean commentChar) {
      return comment == null ? null : trimComment(comment, commentChar);
   }

   static class PropertyLayoutData implements Cloneable {
      private StringBuffer comment;
      private String separator = " = ";
      private int blancLines;
      private boolean singleLine = true;

      public PropertyLayoutData() {
      }

      public int getBlancLines() {
         return this.blancLines;
      }

      public void setBlancLines(int blancLines) {
         this.blancLines = blancLines;
      }

      public boolean isSingleLine() {
         return this.singleLine;
      }

      public void setSingleLine(boolean singleLine) {
         this.singleLine = singleLine;
      }

      public void addComment(String s) {
         if (s != null) {
            if (this.comment == null) {
               this.comment = new StringBuffer(s);
            } else {
               this.comment.append("\n").append(s);
            }
         }

      }

      public void setComment(String s) {
         if (s == null) {
            this.comment = null;
         } else {
            this.comment = new StringBuffer(s);
         }

      }

      public String getComment() {
         return this.comment == null ? null : this.comment.toString();
      }

      public String getSeparator() {
         return this.separator;
      }

      public void setSeparator(String separator) {
         this.separator = separator;
      }

      public PropertiesConfigurationLayout.PropertyLayoutData clone() {
         try {
            PropertiesConfigurationLayout.PropertyLayoutData copy = (PropertiesConfigurationLayout.PropertyLayoutData)super.clone();
            if (this.comment != null) {
               copy.comment = new StringBuffer(this.getComment());
            }

            return copy;
         } catch (CloneNotSupportedException var2) {
            throw new ConfigurationRuntimeException(var2);
         }
      }
   }
}

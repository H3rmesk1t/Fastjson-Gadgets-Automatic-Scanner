package org.apache.commons.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/** @deprecated */
@Deprecated
public class INIConfiguration extends AbstractFileConfiguration {
   protected static final String COMMENT_CHARS = "#;";
   protected static final String SEPARATOR_CHARS = "=:";

   public INIConfiguration() {
   }

   public INIConfiguration(String filename) throws ConfigurationException {
      super(filename);
   }

   public INIConfiguration(File file) throws ConfigurationException {
      super(file);
   }

   public INIConfiguration(URL url) throws ConfigurationException {
      super(url);
   }

   public void save(Writer writer) throws ConfigurationException {
      PrintWriter out = new PrintWriter(writer);

      label35:
      for(Iterator it = this.getSections().iterator(); it.hasNext(); out.println()) {
         String section = (String)it.next();
         out.print("[");
         out.print(section);
         out.print("]");
         out.println();
         Configuration subset = this.subset(section);
         Iterator keys = subset.getKeys();

         while(true) {
            while(true) {
               if (!keys.hasNext()) {
                  continue label35;
               }

               String key = (String)keys.next();
               Object value = subset.getProperty(key);
               if (value instanceof Collection) {
                  Iterator values = ((Collection)value).iterator();

                  while(values.hasNext()) {
                     value = values.next();
                     out.print(key);
                     out.print(" = ");
                     out.print(this.formatValue(value.toString()));
                     out.println();
                  }
               } else {
                  out.print(key);
                  out.print(" = ");
                  out.print(this.formatValue(value.toString()));
                  out.println();
               }
            }
         }
      }

      out.flush();
   }

   public void load(Reader reader) throws ConfigurationException {
      try {
         BufferedReader bufferedReader = new BufferedReader(reader);
         String line = bufferedReader.readLine();

         for(String section = ""; line != null; line = bufferedReader.readLine()) {
            line = line.trim();
            if (!this.isCommentLine(line)) {
               if (this.isSectionLine(line)) {
                  section = line.substring(1, line.length() - 1) + ".";
               } else {
                  String key = "";
                  String value = "";
                  int index = line.indexOf("=");
                  if (index >= 0) {
                     key = section + line.substring(0, index);
                     value = this.parseValue(line.substring(index + 1));
                  } else {
                     index = line.indexOf(":");
                     if (index >= 0) {
                        key = section + line.substring(0, index);
                        value = this.parseValue(line.substring(index + 1));
                     } else {
                        key = section + line;
                     }
                  }

                  this.addProperty(key.trim(), value);
               }
            }
         }

      } catch (IOException var8) {
         throw new ConfigurationException("Unable to load the configuration", var8);
      }
   }

   private String parseValue(String value) {
      value = value.trim();
      boolean quoted = value.startsWith("\"") || value.startsWith("'");
      boolean stop = false;
      boolean escape = false;
      char quote = quoted ? value.charAt(0) : 0;
      int i = quoted ? 1 : 0;

      StringBuilder result;
      for(result = new StringBuilder(); i < value.length() && !stop; ++i) {
         char c = value.charAt(i);
         if (quoted) {
            if ('\\' == c && !escape) {
               escape = true;
            } else if (!escape && quote == c) {
               stop = true;
            } else if (escape && quote == c) {
               escape = false;
               result.append(c);
            } else {
               if (escape) {
                  escape = false;
                  result.append('\\');
               }

               result.append(c);
            }
         } else if ("#;".indexOf(c) == -1) {
            result.append(c);
         } else {
            stop = true;
         }
      }

      String v = result.toString();
      if (!quoted) {
         v = v.trim();
      }

      return v;
   }

   private String formatValue(String value) {
      boolean quoted = false;

      for(int i = 0; i < "#;".length() && !quoted; ++i) {
         char c = "#;".charAt(i);
         if (value.indexOf(c) != -1) {
            quoted = true;
         }
      }

      return quoted ? '"' + value.replaceAll("\"", "\\\\\\\"") + '"' : value;
   }

   protected boolean isCommentLine(String line) {
      if (line == null) {
         return false;
      } else {
         return line.length() < 1 || "#;".indexOf(line.charAt(0)) >= 0;
      }
   }

   protected boolean isSectionLine(String line) {
      if (line == null) {
         return false;
      } else {
         return line.startsWith("[") && line.endsWith("]");
      }
   }

   public Set getSections() {
      Set sections = new TreeSet();
      Iterator keys = this.getKeys();

      while(keys.hasNext()) {
         String key = (String)keys.next();
         int index = key.indexOf(".");
         if (index >= 0) {
            sections.add(key.substring(0, index));
         }
      }

      return sections;
   }
}

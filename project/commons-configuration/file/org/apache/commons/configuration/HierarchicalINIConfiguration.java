package org.apache.commons.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ViewNode;

public class HierarchicalINIConfiguration extends AbstractHierarchicalFileConfiguration {
   protected static final String COMMENT_CHARS = "#;";
   protected static final String SEPARATOR_CHARS = "=:";
   private static final long serialVersionUID = 2548006161386850670L;
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static final String QUOTE_CHARACTERS = "\"'";
   private static final String LINE_CONT = "\\";

   public HierarchicalINIConfiguration() {
   }

   public HierarchicalINIConfiguration(String filename) throws ConfigurationException {
      super(filename);
   }

   public HierarchicalINIConfiguration(File file) throws ConfigurationException {
      super(file);
   }

   public HierarchicalINIConfiguration(URL url) throws ConfigurationException {
      super(url);
   }

   public void save(Writer writer) throws ConfigurationException {
      PrintWriter out = new PrintWriter(writer);

      label40:
      for(Iterator it = this.getSections().iterator(); it.hasNext(); out.println()) {
         String section = (String)it.next();
         SubnodeConfiguration subset;
         if (section != null) {
            out.print("[");
            out.print(section);
            out.print("]");
            out.println();
            subset = this.createSubnodeConfiguration(this.getSectionNode(section));
         } else {
            subset = this.getSection((String)null);
         }

         Iterator keys = subset.getKeys();

         while(true) {
            while(true) {
               if (!keys.hasNext()) {
                  continue label40;
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
         ConfigurationNode sectionNode = this.getRootNode();

         for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
            line = line.trim();
            if (!this.isCommentLine(line)) {
               String key;
               if (this.isSectionLine(line)) {
                  key = line.substring(1, line.length() - 1);
                  sectionNode = this.getSectionNode(key);
               } else {
                  key = "";
                  String value = "";
                  int index = findSeparator(line);
                  if (index >= 0) {
                     key = line.substring(0, index);
                     value = parseValue(line.substring(index + 1), bufferedReader);
                  } else {
                     key = line;
                  }

                  key = key.trim();
                  if (key.length() < 1) {
                     key = " ";
                  }

                  this.createValueNodes(sectionNode, key, value);
               }
            }
         }

      } catch (IOException var8) {
         throw new ConfigurationException("Unable to load the configuration", var8);
      }
   }

   private void createValueNodes(ConfigurationNode sectionNode, String key, String value) {
      Object values;
      if (this.isDelimiterParsingDisabled()) {
         values = Collections.singleton(value);
      } else {
         values = PropertyConverter.split(value, this.getListDelimiter(), false);
      }

      Iterator i$ = ((Collection)values).iterator();

      while(i$.hasNext()) {
         String v = (String)i$.next();
         ConfigurationNode node = this.createNode(key);
         node.setValue(v);
         sectionNode.addChild(node);
      }

   }

   private static String parseValue(String val, BufferedReader reader) throws IOException {
      StringBuilder propertyValue = new StringBuilder();
      String value = val.trim();

      boolean lineContinues;
      do {
         boolean quoted = value.startsWith("\"") || value.startsWith("'");
         boolean stop = false;
         boolean escape = false;
         char quote = quoted ? value.charAt(0) : 0;
         int i = quoted ? 1 : 0;
         StringBuilder result = new StringBuilder();

         char c;
         for(char lastChar = 0; i < value.length() && !stop; lastChar = c) {
            c = value.charAt(i);
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
            } else if (isCommentChar(c) && Character.isWhitespace(lastChar)) {
               stop = true;
            } else {
               result.append(c);
            }

            ++i;
         }

         String v = result.toString();
         if (!quoted) {
            v = v.trim();
            lineContinues = lineContinues(v);
            if (lineContinues) {
               v = v.substring(0, v.length() - 1).trim();
            }
         } else {
            lineContinues = lineContinues(value, i);
         }

         propertyValue.append(v);
         if (lineContinues) {
            propertyValue.append(LINE_SEPARATOR);
            value = reader.readLine();
         }
      } while(lineContinues && value != null);

      return propertyValue.toString();
   }

   private static boolean lineContinues(String line) {
      String s = line.trim();
      return s.equals("\\") || s.length() > 2 && s.endsWith("\\") && Character.isWhitespace(s.charAt(s.length() - 2));
   }

   private static boolean lineContinues(String line, int pos) {
      String s;
      if (pos >= line.length()) {
         s = line;
      } else {
         int end;
         for(end = pos; end < line.length() && !isCommentChar(line.charAt(end)); ++end) {
         }

         s = line.substring(pos, end);
      }

      return lineContinues(s);
   }

   private static boolean isCommentChar(char c) {
      return "#;".indexOf(c) >= 0;
   }

   private static int findSeparator(String line) {
      int index = findSeparatorBeforeQuote(line, findFirstOccurrence(line, "\"'"));
      if (index < 0) {
         index = findFirstOccurrence(line, "=:");
      }

      return index;
   }

   private static int findFirstOccurrence(String line, String separators) {
      int index = -1;

      for(int i = 0; i < separators.length(); ++i) {
         char sep = separators.charAt(i);
         int pos = line.indexOf(sep);
         if (pos >= 0 && (index < 0 || pos < index)) {
            index = pos;
         }
      }

      return index;
   }

   private static int findSeparatorBeforeQuote(String line, int quoteIndex) {
      int index;
      for(index = quoteIndex - 1; index >= 0 && Character.isWhitespace(line.charAt(index)); --index) {
      }

      if (index >= 0 && "=:".indexOf(line.charAt(index)) < 0) {
         index = -1;
      }

      return index;
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
      Set sections = new LinkedHashSet();
      boolean globalSection = false;
      boolean inSection = false;
      Iterator i$ = this.getRootNode().getChildren().iterator();

      while(i$.hasNext()) {
         ConfigurationNode node = (ConfigurationNode)i$.next();
         if (isSectionNode(node)) {
            inSection = true;
            sections.add(node.getName());
         } else if (!inSection && !globalSection) {
            globalSection = true;
            sections.add((Object)null);
         }
      }

      return sections;
   }

   public SubnodeConfiguration getSection(String name) {
      if (name == null) {
         return this.getGlobalSection();
      } else {
         try {
            return this.configurationAt(name);
         } catch (IllegalArgumentException var3) {
            return new SubnodeConfiguration(this, this.getSectionNode(name));
         }
      }
   }

   private ConfigurationNode getSectionNode(String sectionName) {
      List nodes = this.getRootNode().getChildren(sectionName);
      if (!nodes.isEmpty()) {
         return (ConfigurationNode)nodes.get(0);
      } else {
         ConfigurationNode node = this.createNode(sectionName);
         markSectionNode(node);
         this.getRootNode().addChild(node);
         return node;
      }
   }

   private SubnodeConfiguration getGlobalSection() {
      ViewNode parent = new ViewNode();
      Iterator i$ = this.getRootNode().getChildren().iterator();

      while(i$.hasNext()) {
         ConfigurationNode node = (ConfigurationNode)i$.next();
         if (!isSectionNode(node)) {
            synchronized(node) {
               parent.addChild(node);
            }
         }
      }

      return this.createSubnodeConfiguration(parent);
   }

   private static void markSectionNode(ConfigurationNode node) {
      node.setReference(Boolean.TRUE);
   }

   private static boolean isSectionNode(ConfigurationNode node) {
      return node.getReference() != null || node.getChildrenCount() > 0;
   }
}

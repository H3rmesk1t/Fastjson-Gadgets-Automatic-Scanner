package org.apache.commons.configuration.tree;

import java.io.PrintStream;
import java.util.Iterator;

public final class TreeUtils {
   private TreeUtils() {
   }

   public static void printTree(PrintStream stream, ConfigurationNode result) {
      if (stream != null) {
         printTree(stream, "", result);
      }

   }

   private static void printTree(PrintStream stream, String indent, ConfigurationNode result) {
      StringBuffer buffer = (new StringBuffer(indent)).append("<").append(result.getName());
      Iterator iter = result.getAttributes().iterator();

      while(iter.hasNext()) {
         ConfigurationNode node = (ConfigurationNode)iter.next();
         buffer.append(" ").append(node.getName()).append("='").append(node.getValue()).append("'");
      }

      buffer.append(">");
      stream.print(buffer.toString());
      if (result.getValue() != null) {
         stream.print(result.getValue());
      }

      boolean newline = false;
      if (result.getChildrenCount() > 0) {
         stream.print("\n");
         iter = result.getChildren().iterator();

         while(iter.hasNext()) {
            printTree(stream, indent + "  ", (ConfigurationNode)iter.next());
         }

         newline = true;
      }

      if (newline) {
         stream.print(indent);
      }

      stream.println("</" + result.getName() + ">");
   }
}

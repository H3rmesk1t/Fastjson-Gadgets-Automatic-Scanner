package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.WireFeedParser;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;

public class ModuleParsers extends PluginManager {
   public ModuleParsers(String propertyKey, WireFeedParser parentParser) {
      super(propertyKey, parentParser, (WireFeedGenerator)null);
   }

   public String getKey(Object obj) {
      return ((ModuleParser)obj).getNamespaceUri();
   }

   public List getModuleNamespaces() {
      return this.getKeys();
   }

   public List parseModules(Element root) {
      List parsers = this.getPlugins();
      List modules = null;

      for(int i = 0; i < parsers.size(); ++i) {
         ModuleParser parser = (ModuleParser)parsers.get(i);
         String namespaceUri = parser.getNamespaceUri();
         Namespace namespace = Namespace.getNamespace(namespaceUri);
         if (this.hasElementsFrom(root, namespace)) {
            Module module = parser.parse(root);
            if (module != null) {
               if (modules == null) {
                  modules = new ArrayList();
               }

               modules.add(module);
            }
         }
      }

      return modules;
   }

   private boolean hasElementsFrom(Element root, Namespace namespace) {
      boolean hasElements = false;
      if (!hasElements) {
         List children = root.getChildren();

         for(int i = 0; !hasElements && i < children.size(); ++i) {
            Element child = (Element)children.get(i);
            hasElements = namespace.equals(child.getNamespace());
         }
      }

      return hasElements;
   }
}

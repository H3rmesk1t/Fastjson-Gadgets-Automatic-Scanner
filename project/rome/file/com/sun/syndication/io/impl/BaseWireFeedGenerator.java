package com.sun.syndication.io.impl;

import com.sun.syndication.io.WireFeedGenerator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;

public abstract class BaseWireFeedGenerator implements WireFeedGenerator {
   private static final String FEED_MODULE_GENERATORS_POSFIX_KEY = ".feed.ModuleGenerator.classes";
   private static final String ITEM_MODULE_GENERATORS_POSFIX_KEY = ".item.ModuleGenerator.classes";
   private static final String PERSON_MODULE_GENERATORS_POSFIX_KEY = ".person.ModuleGenerator.classes";
   private String _type;
   private ModuleGenerators _feedModuleGenerators;
   private ModuleGenerators _itemModuleGenerators;
   private ModuleGenerators _personModuleGenerators;
   private Namespace[] _allModuleNamespaces;

   protected BaseWireFeedGenerator(String type) {
      this._type = type;
      this._feedModuleGenerators = new ModuleGenerators(type + ".feed.ModuleGenerator.classes", this);
      this._itemModuleGenerators = new ModuleGenerators(type + ".item.ModuleGenerator.classes", this);
      this._personModuleGenerators = new ModuleGenerators(type + ".person.ModuleGenerator.classes", this);
      Set allModuleNamespaces = new HashSet();
      Iterator i = this._feedModuleGenerators.getAllNamespaces().iterator();

      while(i.hasNext()) {
         allModuleNamespaces.add(i.next());
      }

      i = this._itemModuleGenerators.getAllNamespaces().iterator();

      while(i.hasNext()) {
         allModuleNamespaces.add(i.next());
      }

      i = this._personModuleGenerators.getAllNamespaces().iterator();

      while(i.hasNext()) {
         allModuleNamespaces.add(i.next());
      }

      this._allModuleNamespaces = new Namespace[allModuleNamespaces.size()];
      allModuleNamespaces.toArray(this._allModuleNamespaces);
   }

   public String getType() {
      return this._type;
   }

   protected void generateModuleNamespaceDefs(Element root) {
      for(int i = 0; i < this._allModuleNamespaces.length; ++i) {
         root.addNamespaceDeclaration(this._allModuleNamespaces[i]);
      }

   }

   protected void generateFeedModules(List modules, Element feed) {
      this._feedModuleGenerators.generateModules(modules, feed);
   }

   public void generateItemModules(List modules, Element item) {
      this._itemModuleGenerators.generateModules(modules, item);
   }

   public void generatePersonModules(List modules, Element person) {
      this._personModuleGenerators.generateModules(modules, person);
   }

   protected void generateForeignMarkup(Element e, List foreignMarkup) {
      Element elem;
      if (foreignMarkup != null) {
         for(Iterator elems = foreignMarkup.iterator(); elems.hasNext(); e.addContent(elem)) {
            elem = (Element)elems.next();
            Parent parent = elem.getParent();
            if (parent != null) {
               parent.removeContent(elem);
            }
         }
      }

   }

   protected static void purgeUnusedNamespaceDeclarations(Element root) {
      Set usedPrefixes = new HashSet();
      collectUsedPrefixes(root, usedPrefixes);
      List list = root.getAdditionalNamespaces();
      List additionalNamespaces = new ArrayList();
      additionalNamespaces.addAll(list);

      for(int i = 0; i < additionalNamespaces.size(); ++i) {
         Namespace ns = (Namespace)additionalNamespaces.get(i);
         String prefix = ns.getPrefix();
         if (prefix != null && prefix.length() > 0 && !usedPrefixes.contains(prefix)) {
            root.removeNamespaceDeclaration(ns);
         }
      }

   }

   private static void collectUsedPrefixes(Element el, Set collector) {
      String prefix = el.getNamespacePrefix();
      if (prefix != null && prefix.length() > 0 && !collector.contains(prefix)) {
         collector.add(prefix);
      }

      List kids = el.getChildren();

      for(int i = 0; i < kids.size(); ++i) {
         collectUsedPrefixes((Element)kids.get(i), collector);
      }

   }
}

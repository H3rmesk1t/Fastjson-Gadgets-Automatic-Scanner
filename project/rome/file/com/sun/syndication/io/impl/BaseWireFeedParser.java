package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.io.WireFeedParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

public abstract class BaseWireFeedParser implements WireFeedParser {
   private static final String FEED_MODULE_PARSERS_POSFIX_KEY = ".feed.ModuleParser.classes";
   private static final String ITEM_MODULE_PARSERS_POSFIX_KEY = ".item.ModuleParser.classes";
   private static final String PERSON_MODULE_PARSERS_POSFIX_KEY = ".person.ModuleParser.classes";
   private String _type;
   private ModuleParsers _feedModuleParsers;
   private ModuleParsers _itemModuleParsers;
   private ModuleParsers _personModuleParsers;
   private Namespace _namespace;

   protected BaseWireFeedParser(String type, Namespace namespace) {
      this._type = type;
      this._namespace = namespace;
      this._feedModuleParsers = new ModuleParsers(type + ".feed.ModuleParser.classes", this);
      this._itemModuleParsers = new ModuleParsers(type + ".item.ModuleParser.classes", this);
      this._personModuleParsers = new ModuleParsers(type + ".person.ModuleParser.classes", this);
   }

   public String getType() {
      return this._type;
   }

   protected List parseFeedModules(Element feedElement) {
      return this._feedModuleParsers.parseModules(feedElement);
   }

   protected List parseItemModules(Element itemElement) {
      return this._itemModuleParsers.parseModules(itemElement);
   }

   protected List parsePersonModules(Element itemElement) {
      return this._personModuleParsers.parseModules(itemElement);
   }

   protected List extractForeignMarkup(Element e, Extendable ext, Namespace basens) {
      ArrayList foreignMarkup = new ArrayList();
      Iterator children = e.getChildren().iterator();

      while(children.hasNext()) {
         Element elem = (Element)children.next();
         if (!basens.equals(elem.getNamespace()) && null == ext.getModule(elem.getNamespaceURI())) {
            foreignMarkup.add(elem.clone());
         }
      }

      Iterator fm = foreignMarkup.iterator();

      while(fm.hasNext()) {
         Element elem = (Element)fm.next();
         elem.detach();
      }

      return foreignMarkup;
   }

   protected Attribute getAttribute(Element e, String attributeName) {
      Attribute attribute = e.getAttribute(attributeName);
      if (attribute == null) {
         attribute = e.getAttribute(attributeName, this._namespace);
      }

      return attribute;
   }

   protected String getAttributeValue(Element e, String attributeName) {
      Attribute attr = this.getAttribute(e, attributeName);
      return attr != null ? attr.getValue() : null;
   }
}

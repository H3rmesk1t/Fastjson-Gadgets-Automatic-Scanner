package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.rss.Category;
import com.sun.syndication.feed.rss.Enclosure;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConverterForRSS092 extends ConverterForRSS091Userland {
   public ConverterForRSS092() {
      this("rss_0.92");
   }

   protected ConverterForRSS092(String type) {
      super(type);
   }

   protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
      SyndEntry syndEntry = super.createSyndEntry(item, preserveWireItem);
      List cats = item.getCategories();
      if (cats.size() > 0) {
         Set s = new LinkedHashSet();
         s.addAll(this.createSyndCategories(cats));
         s.addAll(syndEntry.getCategories());
         syndEntry.setCategories(new ArrayList(s));
      }

      List enclosures = item.getEnclosures();
      if (enclosures.size() > 0) {
         syndEntry.setEnclosures(this.createSyndEnclosures(enclosures));
      }

      return syndEntry;
   }

   protected List createSyndCategories(List rssCats) {
      List syndCats = new ArrayList();

      for(int i = 0; i < rssCats.size(); ++i) {
         Category rssCat = (Category)rssCats.get(i);
         SyndCategory sCat = new SyndCategoryImpl();
         sCat.setTaxonomyUri(rssCat.getDomain());
         sCat.setName(rssCat.getValue());
         syndCats.add(sCat);
      }

      return syndCats;
   }

   protected List createSyndEnclosures(List enclosures) {
      List sEnclosures = new ArrayList();

      for(int i = 0; i < enclosures.size(); ++i) {
         Enclosure enc = (Enclosure)enclosures.get(i);
         SyndEnclosure sEnc = new SyndEnclosureImpl();
         sEnc.setUrl(enc.getUrl());
         sEnc.setType(enc.getType());
         sEnc.setLength(enc.getLength());
         sEnclosures.add(sEnc);
      }

      return sEnclosures;
   }

   protected Item createRSSItem(SyndEntry sEntry) {
      Item item = super.createRSSItem(sEntry);
      List sCats = sEntry.getCategories();
      if (sCats.size() > 0) {
         item.setCategories(this.createRSSCategories(sCats));
      }

      List sEnclosures = sEntry.getEnclosures();
      if (sEnclosures.size() > 0) {
         item.setEnclosures(this.createEnclosures(sEnclosures));
      }

      return item;
   }

   protected List createRSSCategories(List sCats) {
      List cats = new ArrayList();

      for(int i = 0; i < sCats.size(); ++i) {
         SyndCategory sCat = (SyndCategory)sCats.get(i);
         Category cat = new Category();
         cat.setDomain(sCat.getTaxonomyUri());
         cat.setValue(sCat.getName());
         cats.add(cat);
      }

      return cats;
   }

   protected List createEnclosures(List sEnclosures) {
      List enclosures = new ArrayList();

      for(int i = 0; i < sEnclosures.size(); ++i) {
         SyndEnclosure sEnc = (SyndEnclosure)sEnclosures.get(i);
         Enclosure enc = new Enclosure();
         enc.setUrl(sEnc.getUrl());
         enc.setType(sEnc.getType());
         enc.setLength(sEnc.getLength());
         enclosures.add(enc);
      }

      return enclosures;
   }
}

package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.impl.CopyFromHelper;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.module.DCModuleImpl;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.SyModule;
import com.sun.syndication.feed.module.SyModuleImpl;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import com.sun.syndication.feed.synd.impl.URINormalizer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SyndEntryImpl implements Serializable, SyndEntry {
   private ObjectBean _objBean;
   private String _uri;
   private String _link;
   private Date _updatedDate;
   private SyndContent _title;
   private SyndContent _description;
   private List _links;
   private List _contents;
   private List _modules;
   private List _enclosures;
   private List _authors;
   private List _contributors;
   private SyndFeed _source;
   private List _foreignMarkup;
   private Object wireEntry;
   private List _categories;
   private static final Set IGNORE_PROPERTIES = new HashSet();
   public static final Set CONVENIENCE_PROPERTIES;
   private static final CopyFromHelper COPY_FROM_HELPER;

   protected SyndEntryImpl(Class beanClass, Set convenienceProperties) {
      this._categories = new ArrayList();
      this._objBean = new ObjectBean(beanClass, this, convenienceProperties);
   }

   public SyndEntryImpl() {
      this(SyndEntry.class, IGNORE_PROPERTIES);
   }

   public Object clone() throws CloneNotSupportedException {
      return this._objBean.clone();
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (!(other instanceof SyndEntryImpl)) {
         return false;
      } else {
         Object fm = this.getForeignMarkup();
         this.setForeignMarkup(((SyndEntryImpl)other).getForeignMarkup());
         boolean ret = this._objBean.equals(other);
         this.setForeignMarkup(fm);
         return ret;
      }
   }

   public int hashCode() {
      return this._objBean.hashCode();
   }

   public String toString() {
      return this._objBean.toString();
   }

   public String getUri() {
      return this._uri;
   }

   public void setUri(String uri) {
      this._uri = URINormalizer.normalize(uri);
   }

   public String getTitle() {
      return this._title != null ? this._title.getValue() : null;
   }

   public void setTitle(String title) {
      if (this._title == null) {
         this._title = new SyndContentImpl();
      }

      this._title.setValue(title);
   }

   public SyndContent getTitleEx() {
      return this._title;
   }

   public void setTitleEx(SyndContent title) {
      this._title = title;
   }

   public String getLink() {
      return this._link;
   }

   public void setLink(String link) {
      this._link = link;
   }

   public SyndContent getDescription() {
      return this._description;
   }

   public void setDescription(SyndContent description) {
      this._description = description;
   }

   public List getContents() {
      return this._contents == null ? (this._contents = new ArrayList()) : this._contents;
   }

   public void setContents(List contents) {
      this._contents = contents;
   }

   public List getEnclosures() {
      return this._enclosures == null ? (this._enclosures = new ArrayList()) : this._enclosures;
   }

   public void setEnclosures(List enclosures) {
      this._enclosures = enclosures;
   }

   public Date getPublishedDate() {
      return this.getDCModule().getDate();
   }

   public void setPublishedDate(Date publishedDate) {
      this.getDCModule().setDate(publishedDate);
   }

   public List getCategories() {
      return this._categories;
   }

   public void setCategories(List categories) {
      this._categories = categories;
   }

   public List getModules() {
      if (this._modules == null) {
         this._modules = new ArrayList();
      }

      if (ModuleUtils.getModule(this._modules, "http://purl.org/dc/elements/1.1/") == null) {
         this._modules.add(new DCModuleImpl());
      }

      return this._modules;
   }

   public void setModules(List modules) {
      this._modules = modules;
   }

   public Module getModule(String uri) {
      return ModuleUtils.getModule(this.getModules(), uri);
   }

   private DCModule getDCModule() {
      return (DCModule)this.getModule("http://purl.org/dc/elements/1.1/");
   }

   public Class getInterface() {
      return SyndEntry.class;
   }

   public void copyFrom(Object obj) {
      COPY_FROM_HELPER.copy(this, obj);
   }

   public List getLinks() {
      return this._links == null ? (this._links = new ArrayList()) : this._links;
   }

   public void setLinks(List links) {
      this._links = links;
   }

   public Date getUpdatedDate() {
      return this._updatedDate;
   }

   public void setUpdatedDate(Date updatedDate) {
      this._updatedDate = updatedDate;
   }

   public List getAuthors() {
      return this._authors == null ? (this._authors = new ArrayList()) : this._authors;
   }

   public void setAuthors(List authors) {
      this._authors = authors;
   }

   public String getAuthor() {
      String author;
      if (this._authors != null && this._authors.size() > 0) {
         author = ((SyndPerson)this._authors.get(0)).getName();
      } else {
         author = this.getDCModule().getCreator();
      }

      if (author == null) {
         author = "";
      }

      return author;
   }

   public void setAuthor(String author) {
      DCModule dcModule = this.getDCModule();
      String currentValue = dcModule.getCreator();
      if (currentValue == null || currentValue.length() == 0) {
         this.getDCModule().setCreator(author);
      }

   }

   public List getContributors() {
      return this._contributors == null ? (this._contributors = new ArrayList()) : this._contributors;
   }

   public void setContributors(List contributors) {
      this._contributors = contributors;
   }

   public SyndFeed getSource() {
      return this._source;
   }

   public void setSource(SyndFeed source) {
      this._source = source;
   }

   public Object getForeignMarkup() {
      return this._foreignMarkup == null ? (this._foreignMarkup = new ArrayList()) : this._foreignMarkup;
   }

   public void setForeignMarkup(Object foreignMarkup) {
      this._foreignMarkup = (List)foreignMarkup;
   }

   public Object getWireEntry() {
      return this.wireEntry;
   }

   public void setWireEntry(Object wireEntry) {
      this.wireEntry = wireEntry;
   }

   static {
      CONVENIENCE_PROPERTIES = Collections.unmodifiableSet(IGNORE_PROPERTIES);
      IGNORE_PROPERTIES.add("publishedDate");
      IGNORE_PROPERTIES.add("author");
      Map basePropInterfaceMap = new HashMap();
      basePropInterfaceMap.put("uri", String.class);
      basePropInterfaceMap.put("title", String.class);
      basePropInterfaceMap.put("link", String.class);
      basePropInterfaceMap.put("uri", String.class);
      basePropInterfaceMap.put("description", SyndContent.class);
      basePropInterfaceMap.put("contents", SyndContent.class);
      basePropInterfaceMap.put("enclosures", SyndEnclosure.class);
      basePropInterfaceMap.put("modules", Module.class);
      Map basePropClassImplMap = new HashMap();
      basePropClassImplMap.put(SyndContent.class, SyndContentImpl.class);
      basePropClassImplMap.put(SyndEnclosure.class, SyndEnclosureImpl.class);
      basePropClassImplMap.put(DCModule.class, DCModuleImpl.class);
      basePropClassImplMap.put(SyModule.class, SyModuleImpl.class);
      COPY_FROM_HELPER = new CopyFromHelper(SyndEntry.class, basePropInterfaceMap, basePropClassImplMap);
   }
}

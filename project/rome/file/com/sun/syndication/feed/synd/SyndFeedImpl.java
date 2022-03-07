package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.impl.CopyFromHelper;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.module.DCModuleImpl;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.SyModule;
import com.sun.syndication.feed.module.SyModuleImpl;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import com.sun.syndication.feed.synd.impl.Converters;
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

public class SyndFeedImpl implements Serializable, SyndFeed {
   private ObjectBean _objBean;
   private String _encoding;
   private String _uri;
   private SyndContent _title;
   private SyndContent _description;
   private String _feedType;
   private String _link;
   private List _links;
   private SyndImage _image;
   private List _entries;
   private List _modules;
   private List _authors;
   private List _contributors;
   private List _foreignMarkup;
   private WireFeed wireFeed;
   private boolean preserveWireFeed;
   private static final Converters CONVERTERS = new Converters();
   private static final Set IGNORE_PROPERTIES = new HashSet();
   public static final Set CONVENIENCE_PROPERTIES;
   private static final CopyFromHelper COPY_FROM_HELPER;

   public List getSupportedFeedTypes() {
      return CONVERTERS.getSupportedFeedTypes();
   }

   protected SyndFeedImpl(Class beanClass, Set convenienceProperties) {
      this.wireFeed = null;
      this.preserveWireFeed = false;
      this._objBean = new ObjectBean(beanClass, this, convenienceProperties);
   }

   public SyndFeedImpl() {
      this((WireFeed)null);
   }

   public SyndFeedImpl(WireFeed feed) {
      this(feed, false);
   }

   public SyndFeedImpl(WireFeed feed, boolean preserveWireFeed) {
      this(SyndFeed.class, IGNORE_PROPERTIES);
      if (preserveWireFeed) {
         this.wireFeed = feed;
         this.preserveWireFeed = preserveWireFeed;
      }

      if (feed != null) {
         this._feedType = feed.getFeedType();
         Converter converter = CONVERTERS.getConverter(this._feedType);
         if (converter == null) {
            throw new IllegalArgumentException("Invalid feed type [" + this._feedType + "]");
         }

         converter.copyInto(feed, this);
      }

   }

   public Object clone() throws CloneNotSupportedException {
      return this._objBean.clone();
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         Object fm = this.getForeignMarkup();
         this.setForeignMarkup(((SyndFeedImpl)other).getForeignMarkup());
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

   public WireFeed createWireFeed() {
      return this.createWireFeed(this._feedType);
   }

   public WireFeed createWireFeed(String feedType) {
      if (feedType == null) {
         throw new IllegalArgumentException("Feed type cannot be null");
      } else {
         Converter converter = CONVERTERS.getConverter(feedType);
         if (converter == null) {
            throw new IllegalArgumentException("Invalid feed type [" + feedType + "]");
         } else {
            return converter.createRealFeed(this);
         }
      }
   }

   public WireFeed originalWireFeed() {
      return this.wireFeed;
   }

   public String getFeedType() {
      return this._feedType;
   }

   public void setFeedType(String feedType) {
      this._feedType = feedType;
   }

   public String getEncoding() {
      return this._encoding;
   }

   public void setEncoding(String encoding) {
      this._encoding = encoding;
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

   public String getDescription() {
      return this._description != null ? this._description.getValue() : null;
   }

   public void setDescription(String description) {
      if (this._description == null) {
         this._description = new SyndContentImpl();
      }

      this._description.setValue(description);
   }

   public SyndContent getDescriptionEx() {
      return this._description;
   }

   public void setDescriptionEx(SyndContent description) {
      this._description = description;
   }

   public Date getPublishedDate() {
      return this.getDCModule().getDate();
   }

   public void setPublishedDate(Date publishedDate) {
      this.getDCModule().setDate(publishedDate);
   }

   public String getCopyright() {
      return this.getDCModule().getRights();
   }

   public void setCopyright(String copyright) {
      this.getDCModule().setRights(copyright);
   }

   public SyndImage getImage() {
      return this._image;
   }

   public void setImage(SyndImage image) {
      this._image = image;
   }

   public List getCategories() {
      return new SyndCategoryListFacade(this.getDCModule().getSubjects());
   }

   public void setCategories(List categories) {
      this.getDCModule().setSubjects(SyndCategoryListFacade.convertElementsSyndCategoryToSubject(categories));
   }

   public List getEntries() {
      return this._entries == null ? (this._entries = new ArrayList()) : this._entries;
   }

   public void setEntries(List entries) {
      this._entries = entries;
   }

   public String getLanguage() {
      return this.getDCModule().getLanguage();
   }

   public void setLanguage(String language) {
      this.getDCModule().setLanguage(language);
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
      return SyndFeed.class;
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

   public List getAuthors() {
      return this._authors == null ? (this._authors = new ArrayList()) : this._authors;
   }

   public void setAuthors(List authors) {
      this._authors = authors;
   }

   public String getAuthor() {
      return this.getDCModule().getCreator();
   }

   public void setAuthor(String author) {
      this.getDCModule().setCreator(author);
   }

   public List getContributors() {
      return this._contributors == null ? (this._contributors = new ArrayList()) : this._contributors;
   }

   public void setContributors(List contributors) {
      this._contributors = contributors;
   }

   public Object getForeignMarkup() {
      return this._foreignMarkup == null ? (this._foreignMarkup = new ArrayList()) : this._foreignMarkup;
   }

   public void setForeignMarkup(Object foreignMarkup) {
      this._foreignMarkup = (List)foreignMarkup;
   }

   public boolean isPreservingWireFeed() {
      return this.preserveWireFeed;
   }

   static {
      CONVENIENCE_PROPERTIES = Collections.unmodifiableSet(IGNORE_PROPERTIES);
      IGNORE_PROPERTIES.add("publishedDate");
      IGNORE_PROPERTIES.add("author");
      IGNORE_PROPERTIES.add("copyright");
      IGNORE_PROPERTIES.add("categories");
      IGNORE_PROPERTIES.add("language");
      Map basePropInterfaceMap = new HashMap();
      basePropInterfaceMap.put("feedType", String.class);
      basePropInterfaceMap.put("encoding", String.class);
      basePropInterfaceMap.put("uri", String.class);
      basePropInterfaceMap.put("title", String.class);
      basePropInterfaceMap.put("link", String.class);
      basePropInterfaceMap.put("description", String.class);
      basePropInterfaceMap.put("image", SyndImage.class);
      basePropInterfaceMap.put("entries", SyndEntry.class);
      basePropInterfaceMap.put("modules", Module.class);
      Map basePropClassImplMap = new HashMap();
      basePropClassImplMap.put(SyndEntry.class, SyndEntryImpl.class);
      basePropClassImplMap.put(SyndImage.class, SyndImageImpl.class);
      basePropClassImplMap.put(DCModule.class, DCModuleImpl.class);
      basePropClassImplMap.put(SyModule.class, SyModuleImpl.class);
      COPY_FROM_HELPER = new CopyFromHelper(SyndFeed.class, basePropInterfaceMap, basePropClassImplMap);
   }
}

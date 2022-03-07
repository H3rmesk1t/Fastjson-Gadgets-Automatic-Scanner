package com.sun.syndication.feed.rss;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Channel extends WireFeed {
   public static final String SUNDAY = "sunday";
   public static final String MONDAY = "monday";
   public static final String TUESDAY = "tuesday";
   public static final String WEDNESDAY = "wednesday";
   public static final String THURSDAY = "thursday";
   public static final String FRIDAY = "friday";
   public static final String SATURDAY = "saturday";
   private static final Set DAYS = new HashSet();
   private String _title;
   private String _description;
   private String _link;
   private String _uri;
   private Image _image;
   private List _items;
   private TextInput _textInput;
   private String _language;
   private String _rating;
   private String _copyright;
   private Date _pubDate;
   private Date _lastBuildDate;
   private String _docs;
   private String _managingEditor;
   private String _webMaster;
   private List _skipHours;
   private List _skipDays;
   private Cloud _cloud;
   private List _categories;
   private String _generator;
   private int _ttl = -1;
   private List _modules;

   public Channel() {
   }

   public Channel(String type) {
      super(type);
   }

   public String getTitle() {
      return this._title;
   }

   public void setTitle(String title) {
      this._title = title;
   }

   public String getDescription() {
      return this._description;
   }

   public void setDescription(String description) {
      this._description = description;
   }

   public String getLink() {
      return this._link;
   }

   public void setLink(String link) {
      this._link = link;
   }

   public String getUri() {
      return this._uri;
   }

   public void setUri(String uri) {
      this._uri = uri;
   }

   public Image getImage() {
      return this._image;
   }

   public void setImage(Image image) {
      this._image = image;
   }

   public List getItems() {
      return this._items == null ? (this._items = new ArrayList()) : this._items;
   }

   public void setItems(List items) {
      this._items = items;
   }

   public TextInput getTextInput() {
      return this._textInput;
   }

   public void setTextInput(TextInput textInput) {
      this._textInput = textInput;
   }

   public String getLanguage() {
      return this._language;
   }

   public void setLanguage(String language) {
      this._language = language;
   }

   public String getRating() {
      return this._rating;
   }

   public void setRating(String rating) {
      this._rating = rating;
   }

   public String getCopyright() {
      return this._copyright;
   }

   public void setCopyright(String copyright) {
      this._copyright = copyright;
   }

   public Date getPubDate() {
      return this._pubDate;
   }

   public void setPubDate(Date pubDate) {
      this._pubDate = pubDate;
   }

   public Date getLastBuildDate() {
      return this._lastBuildDate;
   }

   public void setLastBuildDate(Date lastBuildDate) {
      this._lastBuildDate = lastBuildDate;
   }

   public String getDocs() {
      return this._docs;
   }

   public void setDocs(String docs) {
      this._docs = docs;
   }

   public String getManagingEditor() {
      return this._managingEditor;
   }

   public void setManagingEditor(String managingEditor) {
      this._managingEditor = managingEditor;
   }

   public String getWebMaster() {
      return this._webMaster;
   }

   public void setWebMaster(String webMaster) {
      this._webMaster = webMaster;
   }

   public List getSkipHours() {
      return (List)(this._skipHours != null ? this._skipHours : new ArrayList());
   }

   public void setSkipHours(List skipHours) {
      if (skipHours != null) {
         for(int i = 0; i < skipHours.size(); ++i) {
            Integer iHour = (Integer)skipHours.get(i);
            if (iHour == null) {
               throw new IllegalArgumentException("Invalid hour [null]");
            }

            int hour = iHour;
            if (hour < 0 || hour > 24) {
               throw new IllegalArgumentException("Invalid hour [" + hour + "]");
            }
         }
      }

      this._skipHours = skipHours;
   }

   public List getSkipDays() {
      return (List)(this._skipDays != null ? this._skipDays : new ArrayList());
   }

   public void setSkipDays(List skipDays) {
      if (skipDays != null) {
         for(int i = 0; i < skipDays.size(); ++i) {
            String day = (String)skipDays.get(i);
            if (day == null) {
               throw new IllegalArgumentException("Invalid day [null]");
            }

            day = day.toLowerCase();
            if (!DAYS.contains(day)) {
               throw new IllegalArgumentException("Invalid day [" + day + "]");
            }

            skipDays.set(i, day);
         }
      }

      this._skipDays = skipDays;
   }

   public Cloud getCloud() {
      return this._cloud;
   }

   public void setCloud(Cloud cloud) {
      this._cloud = cloud;
   }

   public List getCategories() {
      return this._categories == null ? (this._categories = new ArrayList()) : this._categories;
   }

   public void setCategories(List categories) {
      this._categories = categories;
   }

   public String getGenerator() {
      return this._generator;
   }

   public void setGenerator(String generator) {
      this._generator = generator;
   }

   public int getTtl() {
      return this._ttl;
   }

   public void setTtl(int ttl) {
      this._ttl = ttl;
   }

   public List getModules() {
      return this._modules == null ? (this._modules = new ArrayList()) : this._modules;
   }

   public void setModules(List modules) {
      this._modules = modules;
   }

   public Module getModule(String uri) {
      return ModuleUtils.getModule(this._modules, uri);
   }

   static {
      DAYS.add("sunday");
      DAYS.add("monday");
      DAYS.add("tuesday");
      DAYS.add("wednesday");
      DAYS.add("thursday");
      DAYS.add("friday");
      DAYS.add("saturday");
   }
}

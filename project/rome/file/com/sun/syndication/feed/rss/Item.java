package com.sun.syndication.feed.rss;

import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Item implements Cloneable, Serializable, Extendable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _title;
   private String _link;
   private String _uri;
   private Description _description;
   private Content _content;
   private Source _source;
   private List _enclosures;
   private List _categories;
   private Guid _guid;
   private String _comments;
   private String _author;
   private Date _pubDate;
   private Date _expirationDate;
   private List _modules;
   private List _foreignMarkup;

   public Object clone() throws CloneNotSupportedException {
      return this._objBean.clone();
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         Object fm = this.getForeignMarkup();
         this.setForeignMarkup(((Item)other).getForeignMarkup());
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

   public String getTitle() {
      return this._title;
   }

   public void setTitle(String title) {
      this._title = title;
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

   public Description getDescription() {
      return this._description;
   }

   public void setDescription(Description description) {
      this._description = description;
   }

   public Content getContent() {
      return this._content;
   }

   public void setContent(Content content) {
      this._content = content;
   }

   public Source getSource() {
      return this._source;
   }

   public void setSource(Source source) {
      this._source = source;
   }

   public List getEnclosures() {
      return this._enclosures == null ? (this._enclosures = new ArrayList()) : this._enclosures;
   }

   public void setEnclosures(List enclosures) {
      this._enclosures = enclosures;
   }

   public List getCategories() {
      return this._categories == null ? (this._categories = new ArrayList()) : this._categories;
   }

   public void setCategories(List categories) {
      this._categories = categories;
   }

   public Guid getGuid() {
      return this._guid;
   }

   public void setGuid(Guid guid) {
      this._guid = guid;
   }

   public String getComments() {
      return this._comments;
   }

   public void setComments(String comments) {
      this._comments = comments;
   }

   public String getAuthor() {
      return this._author;
   }

   public void setAuthor(String author) {
      this._author = author;
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

   public Date getPubDate() {
      return this._pubDate;
   }

   public void setPubDate(Date pubDate) {
      this._pubDate = pubDate;
   }

   public Date getExpirationDate() {
      return this._expirationDate;
   }

   public void setExpirationDate(Date expirationDate) {
      this._expirationDate = expirationDate;
   }

   public Object getForeignMarkup() {
      return this._foreignMarkup == null ? (this._foreignMarkup = new ArrayList()) : this._foreignMarkup;
   }

   public void setForeignMarkup(Object foreignMarkup) {
      this._foreignMarkup = (List)foreignMarkup;
   }
}

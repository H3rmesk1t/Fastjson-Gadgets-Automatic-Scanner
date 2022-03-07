package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Entry implements Cloneable, Serializable, Extendable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _xmlBase;
   private List _authors;
   private List _contributors;
   private List _categories;
   private List _contents;
   private String _id;
   private Date _published;
   private String _rights;
   private Feed _source;
   private Content _summary;
   private Content _title;
   private Date _updated;
   private List _alternateLinks;
   private List _otherLinks;
   private List _foreignMarkup;
   private List _modules;
   private Date _created;

   public Object clone() throws CloneNotSupportedException {
      return this._objBean.clone();
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         Object fm = this.getForeignMarkup();
         this.setForeignMarkup(((Entry)other).getForeignMarkup());
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
      return this._title != null ? this._title.getValue() : null;
   }

   public void setTitle(String title) {
      if (this._title == null) {
         this._title = new Content();
      }

      this._title.setValue(title);
   }

   public Content getTitleEx() {
      return this._title;
   }

   public void setTitleEx(Content title) {
      this._title = title;
   }

   public List getAlternateLinks() {
      return this._alternateLinks == null ? (this._alternateLinks = new ArrayList()) : this._alternateLinks;
   }

   public void setAlternateLinks(List alternateLinks) {
      this._alternateLinks = alternateLinks;
   }

   public List getOtherLinks() {
      return this._otherLinks == null ? (this._otherLinks = new ArrayList()) : this._otherLinks;
   }

   public void setOtherLinks(List otherLinks) {
      this._otherLinks = otherLinks;
   }

   public List getAuthors() {
      return this._authors == null ? (this._authors = new ArrayList()) : this._authors;
   }

   public void setAuthors(List authors) {
      this._authors = authors;
   }

   public List getContributors() {
      return this._contributors == null ? (this._contributors = new ArrayList()) : this._contributors;
   }

   public void setContributors(List contributors) {
      this._contributors = contributors;
   }

   public String getId() {
      return this._id;
   }

   public void setId(String id) {
      this._id = id;
   }

   public Date getModified() {
      return this._updated;
   }

   public void setModified(Date modified) {
      this._updated = modified;
   }

   public Date getIssued() {
      return this._published;
   }

   public void setIssued(Date issued) {
      this._published = issued;
   }

   public Date getCreated() {
      return this._created;
   }

   public void setCreated(Date created) {
      this._created = created;
   }

   public Content getSummary() {
      return this._summary;
   }

   public void setSummary(Content summary) {
      this._summary = summary;
   }

   public List getContents() {
      return this._contents == null ? (this._contents = new ArrayList()) : this._contents;
   }

   public void setContents(List contents) {
      this._contents = contents;
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

   public Date getPublished() {
      return this._published;
   }

   public void setPublished(Date published) {
      this._published = published;
   }

   public String getRights() {
      return this._rights;
   }

   public void setRights(String rights) {
      this._rights = rights;
   }

   public Feed getSource() {
      return this._source;
   }

   public void setSource(Feed source) {
      this._source = source;
   }

   public Date getUpdated() {
      return this._updated;
   }

   public void setUpdated(Date updated) {
      this._updated = updated;
   }

   public List getCategories() {
      return this._categories == null ? (this._categories = new ArrayList()) : this._categories;
   }

   public void setCategories(List categories) {
      this._categories = categories;
   }

   public String getXmlBase() {
      return this._xmlBase;
   }

   public void setXmlBase(String xmlBase) {
      this._xmlBase = xmlBase;
   }

   public Object getForeignMarkup() {
      return this._foreignMarkup == null ? (this._foreignMarkup = new ArrayList()) : this._foreignMarkup;
   }

   public void setForeignMarkup(Object foreignMarkup) {
      this._foreignMarkup = (List)foreignMarkup;
   }

   public boolean isMediaEntry() {
      boolean mediaEntry = false;
      List links = this.getOtherLinks();
      Iterator it = links.iterator();

      while(it.hasNext()) {
         Link link = (Link)it.next();
         if ("edit-media".equals(link.getRel())) {
            mediaEntry = true;
            break;
         }
      }

      return mediaEntry;
   }
}

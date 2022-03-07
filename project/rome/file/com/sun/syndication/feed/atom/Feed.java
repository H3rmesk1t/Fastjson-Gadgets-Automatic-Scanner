package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Feed extends WireFeed {
   private String _xmlBase;
   private List _categories;
   private List _authors;
   private List _contributors;
   private Generator _generator;
   private String _icon;
   private String _id;
   private String _logo;
   private String _rights;
   private Content _subtitle;
   private Content _title;
   private Date _updated;
   private List _alternateLinks;
   private List _otherLinks;
   private List _entries;
   private List _modules;
   private Content _info;
   private String _language;

   public Feed() {
   }

   public Feed(String type) {
      super(type);
   }

   public String getLanguage() {
      return this._language;
   }

   public void setLanguage(String language) {
      this._language = language;
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

   public Content getTagline() {
      return this._subtitle;
   }

   public void setTagline(Content tagline) {
      this._subtitle = tagline;
   }

   public String getId() {
      return this._id;
   }

   public void setId(String id) {
      this._id = id;
   }

   public Generator getGenerator() {
      return this._generator;
   }

   public void setGenerator(Generator generator) {
      this._generator = generator;
   }

   public String getCopyright() {
      return this._rights;
   }

   public void setCopyright(String copyright) {
      this._rights = copyright;
   }

   public Content getInfo() {
      return this._info;
   }

   public void setInfo(Content info) {
      this._info = info;
   }

   public Date getModified() {
      return this._updated;
   }

   public void setModified(Date modified) {
      this._updated = modified;
   }

   public List getEntries() {
      return this._entries == null ? (this._entries = new ArrayList()) : this._entries;
   }

   public void setEntries(List entries) {
      this._entries = entries;
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

   public List getCategories() {
      return this._categories == null ? (this._categories = new ArrayList()) : this._categories;
   }

   public void setCategories(List categories) {
      this._categories = categories;
   }

   public String getIcon() {
      return this._icon;
   }

   public void setIcon(String icon) {
      this._icon = icon;
   }

   public String getLogo() {
      return this._logo;
   }

   public void setLogo(String logo) {
      this._logo = logo;
   }

   public String getRights() {
      return this._rights;
   }

   public void setRights(String rights) {
      this._rights = rights;
   }

   public Content getSubtitle() {
      return this._subtitle;
   }

   public void setSubtitle(Content subtitle) {
      this._subtitle = subtitle;
   }

   public Date getUpdated() {
      return this._updated;
   }

   public void setUpdated(Date updated) {
      this._updated = updated;
   }

   public String getXmlBase() {
      return this._xmlBase;
   }

   public void setXmlBase(String xmlBase) {
      this._xmlBase = xmlBase;
   }
}

package com.sun.syndication.feed;

import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class WireFeed implements Cloneable, Serializable, Extendable {
   private ObjectBean _objBean;
   private String _feedType;
   private String _encoding;
   private List _modules;
   private List _foreignMarkup;

   protected WireFeed() {
      this._objBean = new ObjectBean(this.getClass(), this);
   }

   protected WireFeed(String type) {
      this();
      this._feedType = type;
   }

   public Object clone() throws CloneNotSupportedException {
      return this._objBean.clone();
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         Object fm = this.getForeignMarkup();
         this.setForeignMarkup(((WireFeed)other).getForeignMarkup());
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

   public void setFeedType(String feedType) {
      this._feedType = feedType;
   }

   public String getFeedType() {
      return this._feedType;
   }

   public String getEncoding() {
      return this._encoding;
   }

   public void setEncoding(String encoding) {
      this._encoding = encoding;
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

   public Object getForeignMarkup() {
      return this._foreignMarkup == null ? (this._foreignMarkup = new ArrayList()) : this._foreignMarkup;
   }

   public void setForeignMarkup(Object foreignMarkup) {
      this._foreignMarkup = (List)foreignMarkup;
   }
}

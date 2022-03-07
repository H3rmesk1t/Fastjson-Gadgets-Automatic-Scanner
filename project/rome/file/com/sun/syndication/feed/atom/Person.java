package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Cloneable, Serializable, Extendable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _name;
   private String _uri;
   private String _uriResolved;
   private String _email;
   private List _modules;

   public Object clone() throws CloneNotSupportedException {
      return this._objBean.clone();
   }

   public boolean equals(Object other) {
      return this._objBean.equals(other);
   }

   public int hashCode() {
      return this._objBean.hashCode();
   }

   public String toString() {
      return this._objBean.toString();
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public String getUrl() {
      return this._uri;
   }

   public void setUrl(String url) {
      this._uri = url;
   }

   public void setUriResolved(String uriResolved) {
      this._uriResolved = uriResolved;
   }

   public String getUriResolved(String resolveURI) {
      return this._uriResolved != null ? this._uriResolved : this._uri;
   }

   public String getEmail() {
      return this._email;
   }

   public void setEmail(String email) {
      this._email = email;
   }

   public String getUri() {
      return this._uri;
   }

   public void setUri(String uri) {
      this._uri = uri;
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
}

package com.sun.syndication.feed.module;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public abstract class ModuleImpl implements Cloneable, Serializable, Module {
   private ObjectBean _objBean;
   private String _uri;

   protected ModuleImpl(Class beanClass, String uri) {
      this._objBean = new ObjectBean(beanClass, this);
      this._uri = uri;
   }

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

   public String getUri() {
      return this._uri;
   }
}

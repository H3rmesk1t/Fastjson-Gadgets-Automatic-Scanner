package com.sun.syndication.feed.rss;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class Category implements Cloneable, Serializable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _domain;
   private String _value;

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

   public String getDomain() {
      return this._domain;
   }

   public void setDomain(String domain) {
      this._domain = domain;
   }

   public String getValue() {
      return this._value;
   }

   public void setValue(String value) {
      this._value = value;
   }
}

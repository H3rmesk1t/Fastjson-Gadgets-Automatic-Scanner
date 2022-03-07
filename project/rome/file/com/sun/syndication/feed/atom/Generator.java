package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class Generator implements Cloneable, Serializable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _url;
   private String _version;
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

   public String getUrl() {
      return this._url;
   }

   public void setUrl(String url) {
      this._url = url;
   }

   public String getVersion() {
      return this._version;
   }

   public void setVersion(String version) {
      this._version = version;
   }

   public String getValue() {
      return this._value;
   }

   public void setValue(String value) {
      this._value = value;
   }
}

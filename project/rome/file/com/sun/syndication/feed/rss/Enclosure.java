package com.sun.syndication.feed.rss;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class Enclosure implements Cloneable, Serializable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _url;
   private long _length;
   private String _type;

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

   public long getLength() {
      return this._length;
   }

   public void setLength(long length) {
      this._length = length;
   }

   public String getType() {
      return this._type;
   }

   public void setType(String type) {
      this._type = type;
   }
}

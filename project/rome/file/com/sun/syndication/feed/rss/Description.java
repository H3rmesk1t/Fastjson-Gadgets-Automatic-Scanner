package com.sun.syndication.feed.rss;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class Description implements Cloneable, Serializable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _type;
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

   public String getType() {
      return this._type;
   }

   public void setType(String type) {
      this._type = type;
   }

   public String getValue() {
      return this._value;
   }

   public void setValue(String value) {
      this._value = value;
   }
}

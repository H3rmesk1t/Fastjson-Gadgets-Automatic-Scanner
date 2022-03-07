package com.sun.syndication.feed.rss;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class Image implements Cloneable, Serializable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _title;
   private String _url;
   private String _link;
   private int _width = -1;
   private int _height = -1;
   private String _description;

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

   public String getTitle() {
      return this._title;
   }

   public void setTitle(String title) {
      this._title = title;
   }

   public String getUrl() {
      return this._url;
   }

   public void setUrl(String url) {
      this._url = url;
   }

   public String getLink() {
      return this._link;
   }

   public void setLink(String link) {
      this._link = link;
   }

   public int getWidth() {
      return this._width;
   }

   public void setWidth(int width) {
      this._width = width;
   }

   public int getHeight() {
      return this._height;
   }

   public void setHeight(int height) {
      this._height = height;
   }

   public String getDescription() {
      return this._description;
   }

   public void setDescription(String description) {
      this._description = description;
   }
}

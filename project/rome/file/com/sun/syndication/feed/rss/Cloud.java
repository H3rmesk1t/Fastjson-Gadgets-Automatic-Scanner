package com.sun.syndication.feed.rss;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class Cloud implements Cloneable, Serializable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _domain;
   private int _port;
   private String _path;
   private String _registerProcedure;
   private String _protocol;

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

   public int getPort() {
      return this._port;
   }

   public void setPort(int port) {
      this._port = port;
   }

   public String getPath() {
      return this._path;
   }

   public void setPath(String path) {
      this._path = path;
   }

   public String getRegisterProcedure() {
      return this._registerProcedure;
   }

   public void setRegisterProcedure(String registerProcedure) {
      this._registerProcedure = registerProcedure;
   }

   public String getProtocol() {
      return this._protocol;
   }

   public void setProtocol(String protocol) {
      this._protocol = protocol;
   }
}

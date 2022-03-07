package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class Category implements Cloneable, Serializable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _term;
   private String _scheme;
   private String _schemeResolved;
   private String _label;

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

   public String getLabel() {
      return this._label;
   }

   public void setLabel(String label) {
      this._label = label;
   }

   public String getScheme() {
      return this._scheme;
   }

   public void setScheme(String scheme) {
      this._scheme = scheme;
   }

   public void setSchemeResolved(String schemeResolved) {
      this._schemeResolved = schemeResolved;
   }

   public String getSchemeResolved() {
      return this._schemeResolved != null ? this._schemeResolved : this._scheme;
   }

   public String getTerm() {
      return this._term;
   }

   public void setTerm(String term) {
      this._term = term;
   }
}

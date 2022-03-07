package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.DCSubject;
import com.sun.syndication.feed.module.DCSubjectImpl;
import java.io.Serializable;

public class SyndCategoryImpl implements Serializable, SyndCategory {
   private ObjectBean _objBean;
   private DCSubject _subject;

   SyndCategoryImpl(DCSubject subject) {
      this._objBean = new ObjectBean(SyndCategory.class, this);
      this._subject = subject;
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

   DCSubject getSubject() {
      return this._subject;
   }

   public SyndCategoryImpl() {
      this(new DCSubjectImpl());
   }

   public String getName() {
      return this._subject.getValue();
   }

   public void setName(String name) {
      this._subject.setValue(name);
   }

   public String getTaxonomyUri() {
      return this._subject.getTaxonomyUri();
   }

   public void setTaxonomyUri(String taxonomyUri) {
      this._subject.setTaxonomyUri(taxonomyUri);
   }
}

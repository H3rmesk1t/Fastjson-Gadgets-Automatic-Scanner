package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.module.DCModuleImpl;
import com.sun.syndication.feed.module.DCSubject;
import com.sun.syndication.feed.module.DCSubjectImpl;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

public class DCModuleParser implements ModuleParser {
   private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
   private static final String TAXO_URI = "http://purl.org/rss/1.0/modules/taxonomy/";
   private static final Namespace DC_NS = Namespace.getNamespace("http://purl.org/dc/elements/1.1/");
   private static final Namespace RDF_NS = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
   private static final Namespace TAXO_NS = Namespace.getNamespace("http://purl.org/rss/1.0/modules/taxonomy/");

   public final String getNamespaceUri() {
      return "http://purl.org/dc/elements/1.1/";
   }

   private final Namespace getDCNamespace() {
      return DC_NS;
   }

   private final Namespace getRDFNamespace() {
      return RDF_NS;
   }

   private final Namespace getTaxonomyNamespace() {
      return TAXO_NS;
   }

   public Module parse(Element dcRoot) {
      boolean foundSomething = false;
      DCModule dcm = new DCModuleImpl();
      List eList = dcRoot.getChildren("title", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setTitles(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("creator", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setCreators(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("subject", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setSubjects(this.parseSubjects(eList));
      }

      eList = dcRoot.getChildren("description", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setDescriptions(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("publisher", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setPublishers(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("contributor", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setContributors(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("date", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setDates(this.parseElementListDate(eList));
      }

      eList = dcRoot.getChildren("type", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setTypes(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("format", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setFormats(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("identifier", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setIdentifiers(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("source", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setSources(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("language", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setLanguages(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("relation", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setRelations(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("coverage", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setCoverages(this.parseElementList(eList));
      }

      eList = dcRoot.getChildren("rights", this.getDCNamespace());
      if (eList.size() > 0) {
         foundSomething = true;
         dcm.setRightsList(this.parseElementList(eList));
      }

      return foundSomething ? dcm : null;
   }

   protected final String getTaxonomy(Element desc) {
      String d = null;
      Element taxo = desc.getChild("topic", this.getTaxonomyNamespace());
      if (taxo != null) {
         Attribute a = taxo.getAttribute("resource", this.getRDFNamespace());
         if (a != null) {
            d = a.getValue();
         }
      }

      return d;
   }

   protected final List parseSubjects(List eList) {
      List subjects = new ArrayList();
      Iterator i = eList.iterator();

      while(true) {
         while(i.hasNext()) {
            Element eSubject = (Element)i.next();
            Element eDesc = eSubject.getChild("Description", this.getRDFNamespace());
            if (eDesc != null) {
               String taxonomy = this.getTaxonomy(eDesc);
               List eValues = eDesc.getChildren("value", this.getRDFNamespace());
               Iterator v = eValues.iterator();

               while(v.hasNext()) {
                  Element eValue = (Element)v.next();
                  DCSubject subject = new DCSubjectImpl();
                  subject.setTaxonomyUri(taxonomy);
                  subject.setValue(eValue.getText());
                  subjects.add(subject);
               }
            } else {
               DCSubject subject = new DCSubjectImpl();
               subject.setValue(eSubject.getText());
               subjects.add(subject);
            }
         }

         return subjects;
      }
   }

   protected final List parseElementList(List eList) {
      List values = new ArrayList();
      Iterator i = eList.iterator();

      while(i.hasNext()) {
         Element e = (Element)i.next();
         values.add(e.getText());
      }

      return values;
   }

   protected final List parseElementListDate(List eList) {
      List values = new ArrayList();
      Iterator i = eList.iterator();

      while(i.hasNext()) {
         Element e = (Element)i.next();
         values.add(DateParser.parseDate(e.getText()));
      }

      return values;
   }
}

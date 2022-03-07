package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.module.DCSubject;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

public class DCModuleGenerator implements ModuleGenerator {
   private static final String DC_URI = "http://purl.org/dc/elements/1.1/";
   private static final String TAXO_URI = "http://purl.org/rss/1.0/modules/taxonomy/";
   private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
   private static final Namespace DC_NS = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
   private static final Namespace TAXO_NS = Namespace.getNamespace("taxo", "http://purl.org/rss/1.0/modules/taxonomy/");
   private static final Namespace RDF_NS = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
   private static final Set NAMESPACES;

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

   public final Set getNamespaces() {
      return NAMESPACES;
   }

   public final void generate(Module module, Element element) {
      DCModule dcModule = (DCModule)module;
      if (dcModule.getTitle() != null) {
         element.addContent(this.generateSimpleElementList("title", dcModule.getTitles()));
      }

      if (dcModule.getCreator() != null) {
         element.addContent(this.generateSimpleElementList("creator", dcModule.getCreators()));
      }

      List subjects = dcModule.getSubjects();

      for(int i = 0; i < subjects.size(); ++i) {
         element.addContent(this.generateSubjectElement((DCSubject)subjects.get(i)));
      }

      if (dcModule.getDescription() != null) {
         element.addContent(this.generateSimpleElementList("description", dcModule.getDescriptions()));
      }

      if (dcModule.getPublisher() != null) {
         element.addContent(this.generateSimpleElementList("publisher", dcModule.getPublishers()));
      }

      if (dcModule.getContributors() != null) {
         element.addContent(this.generateSimpleElementList("contributor", dcModule.getContributors()));
      }

      if (dcModule.getDate() != null) {
         Iterator i = dcModule.getDates().iterator();

         while(i.hasNext()) {
            element.addContent(this.generateSimpleElement("date", DateParser.formatW3CDateTime((Date)i.next())));
         }
      }

      if (dcModule.getType() != null) {
         element.addContent(this.generateSimpleElementList("type", dcModule.getTypes()));
      }

      if (dcModule.getFormat() != null) {
         element.addContent(this.generateSimpleElementList("format", dcModule.getFormats()));
      }

      if (dcModule.getIdentifier() != null) {
         element.addContent(this.generateSimpleElementList("identifier", dcModule.getIdentifiers()));
      }

      if (dcModule.getSource() != null) {
         element.addContent(this.generateSimpleElementList("source", dcModule.getSources()));
      }

      if (dcModule.getLanguage() != null) {
         element.addContent(this.generateSimpleElementList("language", dcModule.getLanguages()));
      }

      if (dcModule.getRelation() != null) {
         element.addContent(this.generateSimpleElementList("relation", dcModule.getRelations()));
      }

      if (dcModule.getCoverage() != null) {
         element.addContent(this.generateSimpleElementList("coverage", dcModule.getCoverages()));
      }

      if (dcModule.getRights() != null) {
         element.addContent(this.generateSimpleElementList("rights", dcModule.getRightsList()));
      }

   }

   protected final Element generateSubjectElement(DCSubject subject) {
      Element subjectElement = new Element("subject", this.getDCNamespace());
      if (subject.getTaxonomyUri() != null) {
         Element descriptionElement = new Element("Description", this.getRDFNamespace());
         Element topicElement = new Element("topic", this.getTaxonomyNamespace());
         Attribute resourceAttribute = new Attribute("resource", subject.getTaxonomyUri(), this.getRDFNamespace());
         topicElement.setAttribute(resourceAttribute);
         descriptionElement.addContent(topicElement);
         if (subject.getValue() != null) {
            Element valueElement = new Element("value", this.getRDFNamespace());
            valueElement.addContent(subject.getValue());
            descriptionElement.addContent(valueElement);
         }

         subjectElement.addContent(descriptionElement);
      } else {
         subjectElement.addContent(subject.getValue());
      }

      return subjectElement;
   }

   protected final Element generateSimpleElement(String name, String value) {
      Element element = new Element(name, this.getDCNamespace());
      element.addContent(value);
      return element;
   }

   protected final List generateSimpleElementList(String name, List value) {
      List elements = new ArrayList();
      Iterator i = value.iterator();

      while(i.hasNext()) {
         elements.add(this.generateSimpleElement(name, (String)i.next()));
      }

      return elements;
   }

   static {
      Set nss = new HashSet();
      nss.add(DC_NS);
      nss.add(TAXO_NS);
      nss.add(RDF_NS);
      NAMESPACES = Collections.unmodifiableSet(nss);
   }
}

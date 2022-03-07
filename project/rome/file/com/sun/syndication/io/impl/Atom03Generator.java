package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Generator;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.io.FeedException;
import java.io.StringReader;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class Atom03Generator extends BaseWireFeedGenerator {
   private static final String ATOM_03_URI = "http://purl.org/atom/ns#";
   private static final Namespace ATOM_NS = Namespace.getNamespace("http://purl.org/atom/ns#");
   private String _version;

   public Atom03Generator() {
      this("atom_0.3", "0.3");
   }

   protected Atom03Generator(String type, String version) {
      super(type);
      this._version = version;
   }

   protected String getVersion() {
      return this._version;
   }

   protected Namespace getFeedNamespace() {
      return ATOM_NS;
   }

   public Document generate(WireFeed wFeed) throws FeedException {
      Feed feed = (Feed)wFeed;
      Element root = this.createRootElement(feed);
      this.populateFeed(feed, root);
      purgeUnusedNamespaceDeclarations(root);
      return this.createDocument(root);
   }

   protected Document createDocument(Element root) {
      return new Document(root);
   }

   protected Element createRootElement(Feed feed) {
      Element root = new Element("feed", this.getFeedNamespace());
      root.addNamespaceDeclaration(this.getFeedNamespace());
      Attribute version = new Attribute("version", this.getVersion());
      root.setAttribute(version);
      this.generateModuleNamespaceDefs(root);
      return root;
   }

   protected void populateFeed(Feed feed, Element parent) throws FeedException {
      this.addFeed(feed, parent);
      this.addEntries(feed, parent);
   }

   protected void addFeed(Feed feed, Element parent) throws FeedException {
      this.populateFeedHeader(feed, parent);
      this.checkFeedHeaderConstraints(parent);
      this.generateFeedModules(feed.getModules(), parent);
      this.generateForeignMarkup(parent, (List)feed.getForeignMarkup());
   }

   protected void addEntries(Feed feed, Element parent) throws FeedException {
      List items = feed.getEntries();

      for(int i = 0; i < items.size(); ++i) {
         this.addEntry((Entry)items.get(i), parent);
      }

      this.checkEntriesConstraints(parent);
   }

   protected void addEntry(Entry entry, Element parent) throws FeedException {
      Element eEntry = new Element("entry", this.getFeedNamespace());
      this.populateEntry(entry, eEntry);
      this.checkEntryConstraints(eEntry);
      this.generateItemModules(entry.getModules(), eEntry);
      parent.addContent(eEntry);
   }

   protected void populateFeedHeader(Feed feed, Element eFeed) throws FeedException {
      if (feed.getTitleEx() != null) {
         Element titleElement = new Element("title", this.getFeedNamespace());
         this.fillContentElement(titleElement, feed.getTitleEx());
         eFeed.addContent(titleElement);
      }

      List links = feed.getAlternateLinks();

      int i;
      for(i = 0; i < links.size(); ++i) {
         eFeed.addContent(this.generateLinkElement((Link)links.get(i)));
      }

      links = feed.getOtherLinks();

      for(i = 0; i < links.size(); ++i) {
         eFeed.addContent(this.generateLinkElement((Link)links.get(i)));
      }

      if (feed.getAuthors() != null && feed.getAuthors().size() > 0) {
         Element authorElement = new Element("author", this.getFeedNamespace());
         this.fillPersonElement(authorElement, (Person)feed.getAuthors().get(0));
         eFeed.addContent(authorElement);
      }

      List contributors = feed.getContributors();

      for(int i = 0; i < contributors.size(); ++i) {
         Element contributorElement = new Element("contributor", this.getFeedNamespace());
         this.fillPersonElement(contributorElement, (Person)contributors.get(i));
         eFeed.addContent(contributorElement);
      }

      Element modifiedElement;
      if (feed.getTagline() != null) {
         modifiedElement = new Element("tagline", this.getFeedNamespace());
         this.fillContentElement(modifiedElement, feed.getTagline());
         eFeed.addContent(modifiedElement);
      }

      if (feed.getId() != null) {
         eFeed.addContent(this.generateSimpleElement("id", feed.getId()));
      }

      if (feed.getGenerator() != null) {
         eFeed.addContent(this.generateGeneratorElement(feed.getGenerator()));
      }

      if (feed.getCopyright() != null) {
         eFeed.addContent(this.generateSimpleElement("copyright", feed.getCopyright()));
      }

      if (feed.getInfo() != null) {
         modifiedElement = new Element("info", this.getFeedNamespace());
         this.fillContentElement(modifiedElement, feed.getInfo());
         eFeed.addContent(modifiedElement);
      }

      if (feed.getModified() != null) {
         modifiedElement = new Element("modified", this.getFeedNamespace());
         modifiedElement.addContent(DateParser.formatW3CDateTime(feed.getModified()));
         eFeed.addContent(modifiedElement);
      }

   }

   protected void populateEntry(Entry entry, Element eEntry) throws FeedException {
      if (entry.getTitleEx() != null) {
         Element titleElement = new Element("title", this.getFeedNamespace());
         this.fillContentElement(titleElement, entry.getTitleEx());
         eEntry.addContent(titleElement);
      }

      List links = entry.getAlternateLinks();

      int i;
      for(i = 0; i < links.size(); ++i) {
         eEntry.addContent(this.generateLinkElement((Link)links.get(i)));
      }

      links = entry.getOtherLinks();

      for(i = 0; i < links.size(); ++i) {
         eEntry.addContent(this.generateLinkElement((Link)links.get(i)));
      }

      if (entry.getAuthors() != null && entry.getAuthors().size() > 0) {
         Element authorElement = new Element("author", this.getFeedNamespace());
         this.fillPersonElement(authorElement, (Person)entry.getAuthors().get(0));
         eEntry.addContent(authorElement);
      }

      List contributors = entry.getContributors();

      for(int i = 0; i < contributors.size(); ++i) {
         Element contributorElement = new Element("contributor", this.getFeedNamespace());
         this.fillPersonElement(contributorElement, (Person)contributors.get(i));
         eEntry.addContent(contributorElement);
      }

      if (entry.getId() != null) {
         eEntry.addContent(this.generateSimpleElement("id", entry.getId()));
      }

      Element summaryElement;
      if (entry.getModified() != null) {
         summaryElement = new Element("modified", this.getFeedNamespace());
         summaryElement.addContent(DateParser.formatW3CDateTime(entry.getModified()));
         eEntry.addContent(summaryElement);
      }

      if (entry.getIssued() != null) {
         summaryElement = new Element("issued", this.getFeedNamespace());
         summaryElement.addContent(DateParser.formatW3CDateTime(entry.getIssued()));
         eEntry.addContent(summaryElement);
      }

      if (entry.getCreated() != null) {
         summaryElement = new Element("created", this.getFeedNamespace());
         summaryElement.addContent(DateParser.formatW3CDateTime(entry.getCreated()));
         eEntry.addContent(summaryElement);
      }

      if (entry.getSummary() != null) {
         summaryElement = new Element("summary", this.getFeedNamespace());
         this.fillContentElement(summaryElement, entry.getSummary());
         eEntry.addContent(summaryElement);
      }

      List contents = entry.getContents();

      for(int i = 0; i < contents.size(); ++i) {
         Element contentElement = new Element("content", this.getFeedNamespace());
         this.fillContentElement(contentElement, (Content)contents.get(i));
         eEntry.addContent(contentElement);
      }

      this.generateForeignMarkup(eEntry, (List)entry.getForeignMarkup());
   }

   protected void checkFeedHeaderConstraints(Element eFeed) throws FeedException {
   }

   protected void checkEntriesConstraints(Element parent) throws FeedException {
   }

   protected void checkEntryConstraints(Element eEntry) throws FeedException {
   }

   protected Element generateLinkElement(Link link) {
      Element linkElement = new Element("link", this.getFeedNamespace());
      Attribute hrefAttribute;
      if (link.getRel() != null) {
         hrefAttribute = new Attribute("rel", link.getRel().toString());
         linkElement.setAttribute(hrefAttribute);
      }

      if (link.getType() != null) {
         hrefAttribute = new Attribute("type", link.getType());
         linkElement.setAttribute(hrefAttribute);
      }

      if (link.getHref() != null) {
         hrefAttribute = new Attribute("href", link.getHref());
         linkElement.setAttribute(hrefAttribute);
      }

      return linkElement;
   }

   protected void fillPersonElement(Element element, Person person) {
      if (person.getName() != null) {
         element.addContent(this.generateSimpleElement("name", person.getName()));
      }

      if (person.getUrl() != null) {
         element.addContent(this.generateSimpleElement("url", person.getUrl()));
      }

      if (person.getEmail() != null) {
         element.addContent(this.generateSimpleElement("email", person.getEmail()));
      }

   }

   protected Element generateTagLineElement(Content tagline) {
      Element taglineElement = new Element("tagline", this.getFeedNamespace());
      if (tagline.getType() != null) {
         Attribute typeAttribute = new Attribute("type", tagline.getType());
         taglineElement.setAttribute(typeAttribute);
      }

      if (tagline.getValue() != null) {
         taglineElement.addContent(tagline.getValue());
      }

      return taglineElement;
   }

   protected void fillContentElement(Element contentElement, Content content) throws FeedException {
      if (content.getType() != null) {
         Attribute typeAttribute = new Attribute("type", content.getType());
         contentElement.setAttribute(typeAttribute);
      }

      String mode = content.getMode();
      if (mode != null) {
         Attribute modeAttribute = new Attribute("mode", content.getMode().toString());
         contentElement.setAttribute(modeAttribute);
      }

      if (content.getValue() != null) {
         if (mode != null && !mode.equals("escaped")) {
            if (mode.equals("base64")) {
               contentElement.addContent(Base64.encode(content.getValue()));
            } else if (mode.equals("xml")) {
               StringBuffer tmpDocString = new StringBuffer("<tmpdoc>");
               tmpDocString.append(content.getValue());
               tmpDocString.append("</tmpdoc>");
               StringReader tmpDocReader = new StringReader(tmpDocString.toString());

               Document tmpDoc;
               try {
                  SAXBuilder saxBuilder = new SAXBuilder();
                  tmpDoc = saxBuilder.build(tmpDocReader);
               } catch (Exception var8) {
                  throw new FeedException("Invalid XML", var8);
               }

               List children = tmpDoc.getRootElement().removeContent();
               contentElement.addContent(children);
            }
         } else {
            contentElement.addContent(content.getValue());
         }
      }

   }

   protected Element generateGeneratorElement(Generator generator) {
      Element generatorElement = new Element("generator", this.getFeedNamespace());
      Attribute versionAttribute;
      if (generator.getUrl() != null) {
         versionAttribute = new Attribute("url", generator.getUrl());
         generatorElement.setAttribute(versionAttribute);
      }

      if (generator.getVersion() != null) {
         versionAttribute = new Attribute("version", generator.getVersion());
         generatorElement.setAttribute(versionAttribute);
      }

      if (generator.getValue() != null) {
         generatorElement.addContent(generator.getValue());
      }

      return generatorElement;
   }

   protected Element generateSimpleElement(String name, String value) {
      Element element = new Element(name, this.getFeedNamespace());
      element.addContent(value);
      return element;
   }
}

package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Category;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Generator;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class Atom10Generator extends BaseWireFeedGenerator {
   private static final String ATOM_10_URI = "http://www.w3.org/2005/Atom";
   private static final Namespace ATOM_NS = Namespace.getNamespace("http://www.w3.org/2005/Atom");
   private String _version;

   public Atom10Generator() {
      this("atom_1.0", "1.0");
   }

   protected Atom10Generator(String type, String version) {
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
      if (feed.getXmlBase() != null) {
         root.setAttribute("base", feed.getXmlBase(), Namespace.XML_NAMESPACE);
      }

      this.generateModuleNamespaceDefs(root);
      return root;
   }

   protected void populateFeed(Feed feed, Element parent) throws FeedException {
      this.addFeed(feed, parent);
      this.addEntries(feed, parent);
   }

   protected void addFeed(Feed feed, Element parent) throws FeedException {
      this.populateFeedHeader(feed, parent);
      this.generateForeignMarkup(parent, (List)feed.getForeignMarkup());
      this.checkFeedHeaderConstraints(parent);
      this.generateFeedModules(feed.getModules(), parent);
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
      if (entry.getXmlBase() != null) {
         eEntry.setAttribute("base", entry.getXmlBase(), Namespace.XML_NAMESPACE);
      }

      this.populateEntry(entry, eEntry);
      this.generateForeignMarkup(eEntry, (List)entry.getForeignMarkup());
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
      int j;
      if (links != null) {
         for(j = 0; j < links.size(); ++j) {
            eFeed.addContent(this.generateLinkElement((Link)links.get(j)));
         }
      }

      links = feed.getOtherLinks();
      if (links != null) {
         for(j = 0; j < links.size(); ++j) {
            eFeed.addContent(this.generateLinkElement((Link)links.get(j)));
         }
      }

      List cats = feed.getCategories();
      if (cats != null) {
         Iterator iter = cats.iterator();

         while(iter.hasNext()) {
            eFeed.addContent(this.generateCategoryElement((Category)iter.next()));
         }
      }

      List authors = feed.getAuthors();
      Element updatedElement;
      if (authors != null && authors.size() > 0) {
         for(int i = 0; i < authors.size(); ++i) {
            updatedElement = new Element("author", this.getFeedNamespace());
            this.fillPersonElement(updatedElement, (Person)feed.getAuthors().get(i));
            eFeed.addContent(updatedElement);
         }
      }

      List contributors = feed.getContributors();
      if (contributors != null && contributors.size() > 0) {
         for(int i = 0; i < contributors.size(); ++i) {
            Element contributorElement = new Element("contributor", this.getFeedNamespace());
            this.fillPersonElement(contributorElement, (Person)contributors.get(i));
            eFeed.addContent(contributorElement);
         }
      }

      if (feed.getSubtitle() != null) {
         updatedElement = new Element("subtitle", this.getFeedNamespace());
         this.fillContentElement(updatedElement, feed.getSubtitle());
         eFeed.addContent(updatedElement);
      }

      if (feed.getId() != null) {
         eFeed.addContent(this.generateSimpleElement("id", feed.getId()));
      }

      if (feed.getGenerator() != null) {
         eFeed.addContent(this.generateGeneratorElement(feed.getGenerator()));
      }

      if (feed.getRights() != null) {
         eFeed.addContent(this.generateSimpleElement("rights", feed.getRights()));
      }

      if (feed.getIcon() != null) {
         eFeed.addContent(this.generateSimpleElement("icon", feed.getIcon()));
      }

      if (feed.getLogo() != null) {
         eFeed.addContent(this.generateSimpleElement("logo", feed.getLogo()));
      }

      if (feed.getUpdated() != null) {
         updatedElement = new Element("updated", this.getFeedNamespace());
         updatedElement.addContent(DateParser.formatW3CDateTime(feed.getUpdated()));
         eFeed.addContent(updatedElement);
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
      if (links != null) {
         for(i = 0; i < links.size(); ++i) {
            eEntry.addContent(this.generateLinkElement((Link)links.get(i)));
         }
      }

      links = entry.getOtherLinks();
      if (links != null) {
         for(i = 0; i < links.size(); ++i) {
            eEntry.addContent(this.generateLinkElement((Link)links.get(i)));
         }
      }

      List cats = entry.getCategories();
      if (cats != null) {
         for(int i = 0; i < cats.size(); ++i) {
            eEntry.addContent(this.generateCategoryElement((Category)cats.get(i)));
         }
      }

      List authors = entry.getAuthors();
      Element sourceElement;
      if (authors != null && authors.size() > 0) {
         for(int i = 0; i < authors.size(); ++i) {
            sourceElement = new Element("author", this.getFeedNamespace());
            this.fillPersonElement(sourceElement, (Person)entry.getAuthors().get(i));
            eEntry.addContent(sourceElement);
         }
      }

      List contributors = entry.getContributors();
      if (contributors != null && contributors.size() > 0) {
         for(int i = 0; i < contributors.size(); ++i) {
            Element contributorElement = new Element("contributor", this.getFeedNamespace());
            this.fillPersonElement(contributorElement, (Person)contributors.get(i));
            eEntry.addContent(contributorElement);
         }
      }

      if (entry.getId() != null) {
         eEntry.addContent(this.generateSimpleElement("id", entry.getId()));
      }

      if (entry.getUpdated() != null) {
         sourceElement = new Element("updated", this.getFeedNamespace());
         sourceElement.addContent(DateParser.formatW3CDateTime(entry.getUpdated()));
         eEntry.addContent(sourceElement);
      }

      if (entry.getPublished() != null) {
         sourceElement = new Element("published", this.getFeedNamespace());
         sourceElement.addContent(DateParser.formatW3CDateTime(entry.getPublished()));
         eEntry.addContent(sourceElement);
      }

      if (entry.getContents() != null && entry.getContents().size() > 0) {
         sourceElement = new Element("content", this.getFeedNamespace());
         Content content = (Content)entry.getContents().get(0);
         this.fillContentElement(sourceElement, content);
         eEntry.addContent(sourceElement);
      }

      if (entry.getSummary() != null) {
         sourceElement = new Element("summary", this.getFeedNamespace());
         this.fillContentElement(sourceElement, entry.getSummary());
         eEntry.addContent(sourceElement);
      }

      if (entry.getSource() != null) {
         sourceElement = new Element("source", this.getFeedNamespace());
         this.populateFeedHeader(entry.getSource(), sourceElement);
         eEntry.addContent(sourceElement);
      }

   }

   protected void checkFeedHeaderConstraints(Element eFeed) throws FeedException {
   }

   protected void checkEntriesConstraints(Element parent) throws FeedException {
   }

   protected void checkEntryConstraints(Element eEntry) throws FeedException {
   }

   protected Element generateCategoryElement(Category cat) {
      Element catElement = new Element("category", this.getFeedNamespace());
      Attribute schemeAttribute;
      if (cat.getTerm() != null) {
         schemeAttribute = new Attribute("term", cat.getTerm());
         catElement.setAttribute(schemeAttribute);
      }

      if (cat.getLabel() != null) {
         schemeAttribute = new Attribute("label", cat.getLabel());
         catElement.setAttribute(schemeAttribute);
      }

      if (cat.getScheme() != null) {
         schemeAttribute = new Attribute("scheme", cat.getScheme());
         catElement.setAttribute(schemeAttribute);
      }

      return catElement;
   }

   protected Element generateLinkElement(Link link) {
      Element linkElement = new Element("link", this.getFeedNamespace());
      Attribute lenght;
      if (link.getRel() != null) {
         lenght = new Attribute("rel", link.getRel());
         linkElement.setAttribute(lenght);
      }

      if (link.getType() != null) {
         lenght = new Attribute("type", link.getType());
         linkElement.setAttribute(lenght);
      }

      if (link.getHref() != null) {
         lenght = new Attribute("href", link.getHref());
         linkElement.setAttribute(lenght);
      }

      if (link.getHreflang() != null) {
         lenght = new Attribute("hreflang", link.getHreflang());
         linkElement.setAttribute(lenght);
      }

      if (link.getTitle() != null) {
         lenght = new Attribute("title", link.getTitle());
         linkElement.setAttribute(lenght);
      }

      if (link.getLength() != 0L) {
         lenght = new Attribute("length", Long.toString(link.getLength()));
         linkElement.setAttribute(lenght);
      }

      return linkElement;
   }

   protected void fillPersonElement(Element element, Person person) {
      if (person.getName() != null) {
         element.addContent(this.generateSimpleElement("name", person.getName()));
      }

      if (person.getUri() != null) {
         element.addContent(this.generateSimpleElement("uri", person.getUri()));
      }

      if (person.getEmail() != null) {
         element.addContent(this.generateSimpleElement("email", person.getEmail()));
      }

      this.generatePersonModules(person.getModules(), element);
   }

   protected Element generateTagLineElement(Content tagline) {
      Element taglineElement = new Element("subtitle", this.getFeedNamespace());
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
      String type = content.getType();
      String atomType = type;
      if (type != null) {
         if ("text/plain".equals(type)) {
            atomType = "text";
         } else if ("text/html".equals(type)) {
            atomType = "html";
         } else if ("application/xhtml+xml".equals(type)) {
            atomType = "xhtml";
         }

         Attribute typeAttribute = new Attribute("type", atomType);
         contentElement.setAttribute(typeAttribute);
      }

      String href = content.getSrc();
      if (href != null) {
         Attribute srcAttribute = new Attribute("src", href);
         contentElement.setAttribute(srcAttribute);
      }

      if (content.getValue() != null) {
         if (atomType != null && (atomType.equals("xhtml") || atomType.indexOf("/xml") != -1 || atomType.indexOf("+xml") != -1)) {
            StringBuffer tmpDocString = new StringBuffer("<tmpdoc>");
            tmpDocString.append(content.getValue());
            tmpDocString.append("</tmpdoc>");
            StringReader tmpDocReader = new StringReader(tmpDocString.toString());

            Document tmpDoc;
            try {
               SAXBuilder saxBuilder = new SAXBuilder();
               tmpDoc = saxBuilder.build(tmpDocReader);
            } catch (Exception var10) {
               throw new FeedException("Invalid XML", var10);
            }

            List children = tmpDoc.getRootElement().removeContent();
            contentElement.addContent(children);
         } else {
            contentElement.addContent(content.getValue());
         }
      }

   }

   protected Element generateGeneratorElement(Generator generator) {
      Element generatorElement = new Element("generator", this.getFeedNamespace());
      Attribute versionAttribute;
      if (generator.getUrl() != null) {
         versionAttribute = new Attribute("uri", generator.getUrl());
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

   public static void serializeEntry(Entry entry, Writer writer) throws IllegalArgumentException, FeedException, IOException {
      List entries = new ArrayList();
      entries.add(entry);
      Feed feed1 = new Feed();
      feed1.setFeedType("atom_1.0");
      feed1.setEntries(entries);
      WireFeedOutput wireFeedOutput = new WireFeedOutput();
      Document feedDoc = wireFeedOutput.outputJDom(feed1);
      Element entryElement = (Element)feedDoc.getRootElement().getChildren().get(0);
      XMLOutputter outputter = new XMLOutputter();
      outputter.output(entryElement, writer);
   }
}

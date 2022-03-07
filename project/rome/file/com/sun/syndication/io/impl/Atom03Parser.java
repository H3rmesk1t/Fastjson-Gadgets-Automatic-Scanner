package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Generator;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.io.FeedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

public class Atom03Parser extends BaseWireFeedParser {
   private static final String ATOM_03_URI = "http://purl.org/atom/ns#";
   private static final Namespace ATOM_03_NS = Namespace.getNamespace("http://purl.org/atom/ns#");

   public Atom03Parser() {
      this("atom_0.3", ATOM_03_NS);
   }

   protected Atom03Parser(String type, Namespace ns) {
      super(type, ns);
   }

   protected Namespace getAtomNamespace() {
      return ATOM_03_NS;
   }

   public boolean isMyType(Document document) {
      Element rssRoot = document.getRootElement();
      Namespace defaultNS = rssRoot.getNamespace();
      return defaultNS != null && defaultNS.equals(this.getAtomNamespace());
   }

   public WireFeed parse(Document document, boolean validate) throws IllegalArgumentException, FeedException {
      if (validate) {
         this.validateFeed(document);
      }

      Element rssRoot = document.getRootElement();
      return this.parseFeed(rssRoot);
   }

   protected void validateFeed(Document document) throws FeedException {
   }

   protected WireFeed parseFeed(Element eFeed) {
      Feed feed = new Feed(this.getType());
      Element e = eFeed.getChild("title", this.getAtomNamespace());
      if (e != null) {
         feed.setTitleEx(this.parseContent(e));
      }

      List eList = eFeed.getChildren("link", this.getAtomNamespace());
      feed.setAlternateLinks(this.parseAlternateLinks(eList));
      feed.setOtherLinks(this.parseOtherLinks(eList));
      e = eFeed.getChild("author", this.getAtomNamespace());
      if (e != null) {
         List authors = new ArrayList();
         authors.add(this.parsePerson(e));
         feed.setAuthors(authors);
      }

      eList = eFeed.getChildren("contributor", this.getAtomNamespace());
      if (eList.size() > 0) {
         feed.setContributors(this.parsePersons(eList));
      }

      e = eFeed.getChild("tagline", this.getAtomNamespace());
      if (e != null) {
         feed.setTagline(this.parseContent(e));
      }

      e = eFeed.getChild("id", this.getAtomNamespace());
      if (e != null) {
         feed.setId(e.getText());
      }

      e = eFeed.getChild("generator", this.getAtomNamespace());
      if (e != null) {
         Generator gen = new Generator();
         gen.setValue(e.getText());
         String att = this.getAttributeValue(e, "url");
         if (att != null) {
            gen.setUrl(att);
         }

         att = this.getAttributeValue(e, "version");
         if (att != null) {
            gen.setVersion(att);
         }

         feed.setGenerator(gen);
      }

      e = eFeed.getChild("copyright", this.getAtomNamespace());
      if (e != null) {
         feed.setCopyright(e.getText());
      }

      e = eFeed.getChild("info", this.getAtomNamespace());
      if (e != null) {
         feed.setInfo(this.parseContent(e));
      }

      e = eFeed.getChild("modified", this.getAtomNamespace());
      if (e != null) {
         feed.setModified(DateParser.parseDate(e.getText()));
      }

      feed.setModules(this.parseFeedModules(eFeed));
      eList = eFeed.getChildren("entry", this.getAtomNamespace());
      if (eList.size() > 0) {
         feed.setEntries(this.parseEntries(eList));
      }

      List foreignMarkup = this.extractForeignMarkup(eFeed, feed, this.getAtomNamespace());
      if (foreignMarkup.size() > 0) {
         feed.setForeignMarkup(foreignMarkup);
      }

      return feed;
   }

   private Link parseLink(Element eLink) {
      Link link = new Link();
      String att = this.getAttributeValue(eLink, "rel");
      if (att != null) {
         link.setRel(att);
      }

      att = this.getAttributeValue(eLink, "type");
      if (att != null) {
         link.setType(att);
      }

      att = this.getAttributeValue(eLink, "href");
      if (att != null) {
         link.setHref(att);
      }

      return link;
   }

   private List parseLinks(List eLinks, boolean alternate) {
      List links = new ArrayList();

      for(int i = 0; i < eLinks.size(); ++i) {
         Element eLink = (Element)eLinks.get(i);
         String rel = this.getAttributeValue(eLink, "rel");
         if (alternate) {
            if ("alternate".equals(rel)) {
               links.add(this.parseLink(eLink));
            }
         } else if (!"alternate".equals(rel)) {
            links.add(this.parseLink(eLink));
         }
      }

      return links.size() > 0 ? links : null;
   }

   private List parseAlternateLinks(List eLinks) {
      return this.parseLinks(eLinks, true);
   }

   private List parseOtherLinks(List eLinks) {
      return this.parseLinks(eLinks, false);
   }

   private Person parsePerson(Element ePerson) {
      Person person = new Person();
      Element e = ePerson.getChild("name", this.getAtomNamespace());
      if (e != null) {
         person.setName(e.getText());
      }

      e = ePerson.getChild("url", this.getAtomNamespace());
      if (e != null) {
         person.setUrl(e.getText());
      }

      e = ePerson.getChild("email", this.getAtomNamespace());
      if (e != null) {
         person.setEmail(e.getText());
      }

      return person;
   }

   private List parsePersons(List ePersons) {
      List persons = new ArrayList();

      for(int i = 0; i < ePersons.size(); ++i) {
         persons.add(this.parsePerson((Element)ePersons.get(i)));
      }

      return persons.size() > 0 ? persons : null;
   }

   private Content parseContent(Element e) {
      String value = null;
      String type = this.getAttributeValue(e, "type");
      type = type != null ? type : "text/plain";
      String mode = this.getAttributeValue(e, "mode");
      if (mode == null) {
         mode = "xml";
      }

      if (mode.equals("escaped")) {
         value = e.getText();
      } else if (mode.equals("base64")) {
         value = Base64.decode(e.getText());
      } else if (mode.equals("xml")) {
         XMLOutputter outputter = new XMLOutputter();
         List eContent = e.getContent();
         Iterator i = eContent.iterator();

         while(i.hasNext()) {
            org.jdom.Content c = (org.jdom.Content)i.next();
            if (c instanceof Element) {
               Element eC = (Element)c;
               if (eC.getNamespace().equals(this.getAtomNamespace())) {
                  ((Element)c).setNamespace(Namespace.NO_NAMESPACE);
               }
            }
         }

         value = outputter.outputString(eContent);
      }

      Content content = new Content();
      content.setType(type);
      content.setMode(mode);
      content.setValue(value);
      return content;
   }

   private List parseEntries(List eEntries) {
      List entries = new ArrayList();

      for(int i = 0; i < eEntries.size(); ++i) {
         entries.add(this.parseEntry((Element)eEntries.get(i)));
      }

      return entries.size() > 0 ? entries : null;
   }

   private Entry parseEntry(Element eEntry) {
      Entry entry = new Entry();
      Element e = eEntry.getChild("title", this.getAtomNamespace());
      if (e != null) {
         entry.setTitleEx(this.parseContent(e));
      }

      List eList = eEntry.getChildren("link", this.getAtomNamespace());
      entry.setAlternateLinks(this.parseAlternateLinks(eList));
      entry.setOtherLinks(this.parseOtherLinks(eList));
      e = eEntry.getChild("author", this.getAtomNamespace());
      ArrayList content;
      if (e != null) {
         content = new ArrayList();
         content.add(this.parsePerson(e));
         entry.setAuthors(content);
      }

      eList = eEntry.getChildren("contributor", this.getAtomNamespace());
      if (eList.size() > 0) {
         entry.setContributors(this.parsePersons(eList));
      }

      e = eEntry.getChild("id", this.getAtomNamespace());
      if (e != null) {
         entry.setId(e.getText());
      }

      e = eEntry.getChild("modified", this.getAtomNamespace());
      if (e != null) {
         entry.setModified(DateParser.parseDate(e.getText()));
      }

      e = eEntry.getChild("issued", this.getAtomNamespace());
      if (e != null) {
         entry.setIssued(DateParser.parseDate(e.getText()));
      }

      e = eEntry.getChild("created", this.getAtomNamespace());
      if (e != null) {
         entry.setCreated(DateParser.parseDate(e.getText()));
      }

      e = eEntry.getChild("summary", this.getAtomNamespace());
      if (e != null) {
         entry.setSummary(this.parseContent(e));
      }

      eList = eEntry.getChildren("content", this.getAtomNamespace());
      if (eList.size() > 0) {
         content = new ArrayList();

         for(int i = 0; i < eList.size(); ++i) {
            content.add(this.parseContent((Element)eList.get(i)));
         }

         entry.setContents(content);
      }

      entry.setModules(this.parseItemModules(eEntry));
      List foreignMarkup = this.extractForeignMarkup(eEntry, entry, this.getAtomNamespace());
      if (foreignMarkup.size() > 0) {
         entry.setForeignMarkup(foreignMarkup);
      }

      return entry;
   }
}

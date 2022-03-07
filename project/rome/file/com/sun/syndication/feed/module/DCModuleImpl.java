package com.sun.syndication.feed.module;

import com.sun.syndication.feed.impl.CopyFromHelper;
import com.sun.syndication.feed.impl.ObjectBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DCModuleImpl extends ModuleImpl implements DCModule {
   private ObjectBean _objBean;
   private List _title;
   private List _creator;
   private List _subject;
   private List _description;
   private List _publisher;
   private List _contributors;
   private List _date;
   private List _type;
   private List _format;
   private List _identifier;
   private List _source;
   private List _language;
   private List _relation;
   private List _coverage;
   private List _rights;
   private static final Set IGNORE_PROPERTIES = new HashSet();
   public static final Set CONVENIENCE_PROPERTIES;
   private static final CopyFromHelper COPY_FROM_HELPER;

   public DCModuleImpl() {
      super(DCModule.class, "http://purl.org/dc/elements/1.1/");
      this._objBean = new ObjectBean(DCModule.class, this, CONVENIENCE_PROPERTIES);
   }

   public List getTitles() {
      return this._title == null ? (this._title = new ArrayList()) : this._title;
   }

   public void setTitles(List titles) {
      this._title = titles;
   }

   public String getTitle() {
      return this._title != null && this._title.size() > 0 ? (String)this._title.get(0) : null;
   }

   public void setTitle(String title) {
      this._title = new ArrayList();
      this._title.add(title);
   }

   public List getCreators() {
      return this._creator == null ? (this._creator = new ArrayList()) : this._creator;
   }

   public void setCreators(List creators) {
      this._creator = creators;
   }

   public String getCreator() {
      return this._creator != null && this._creator.size() > 0 ? (String)this._creator.get(0) : null;
   }

   public void setCreator(String creator) {
      this._creator = new ArrayList();
      this._creator.add(creator);
   }

   public List getSubjects() {
      return this._subject == null ? (this._subject = new ArrayList()) : this._subject;
   }

   public void setSubjects(List subjects) {
      this._subject = subjects;
   }

   public DCSubject getSubject() {
      return this._subject != null && this._subject.size() > 0 ? (DCSubject)this._subject.get(0) : null;
   }

   public void setSubject(DCSubject subject) {
      this._subject = new ArrayList();
      this._subject.add(subject);
   }

   public List getDescriptions() {
      return this._description == null ? (this._description = new ArrayList()) : this._description;
   }

   public void setDescriptions(List descriptions) {
      this._description = descriptions;
   }

   public String getDescription() {
      return this._description != null && this._description.size() > 0 ? (String)this._description.get(0) : null;
   }

   public void setDescription(String description) {
      this._description = new ArrayList();
      this._description.add(description);
   }

   public List getPublishers() {
      return this._publisher == null ? (this._publisher = new ArrayList()) : this._publisher;
   }

   public void setPublishers(List publishers) {
      this._publisher = publishers;
   }

   public String getPublisher() {
      return this._publisher != null && this._publisher.size() > 0 ? (String)this._publisher.get(0) : null;
   }

   public void setPublisher(String publisher) {
      this._publisher = new ArrayList();
      this._publisher.add(publisher);
   }

   public List getContributors() {
      return this._contributors == null ? (this._contributors = new ArrayList()) : this._contributors;
   }

   public void setContributors(List contributors) {
      this._contributors = contributors;
   }

   public String getContributor() {
      return this._contributors != null && this._contributors.size() > 0 ? (String)this._contributors.get(0) : null;
   }

   public void setContributor(String contributor) {
      this._contributors = new ArrayList();
      this._contributors.add(contributor);
   }

   public List getDates() {
      return this._date == null ? (this._date = new ArrayList()) : this._date;
   }

   public void setDates(List dates) {
      this._date = dates;
   }

   public Date getDate() {
      return this._date != null && this._date.size() > 0 ? (Date)this._date.get(0) : null;
   }

   public void setDate(Date date) {
      this._date = new ArrayList();
      this._date.add(date);
   }

   public List getTypes() {
      return this._type == null ? (this._type = new ArrayList()) : this._type;
   }

   public void setTypes(List types) {
      this._type = types;
   }

   public String getType() {
      return this._type != null && this._type.size() > 0 ? (String)this._type.get(0) : null;
   }

   public void setType(String type) {
      this._type = new ArrayList();
      this._type.add(type);
   }

   public List getFormats() {
      return this._format == null ? (this._format = new ArrayList()) : this._format;
   }

   public void setFormats(List formats) {
      this._format = formats;
   }

   public String getFormat() {
      return this._format != null && this._format.size() > 0 ? (String)this._format.get(0) : null;
   }

   public void setFormat(String format) {
      this._format = new ArrayList();
      this._format.add(format);
   }

   public List getIdentifiers() {
      return this._identifier == null ? (this._identifier = new ArrayList()) : this._identifier;
   }

   public void setIdentifiers(List identifiers) {
      this._identifier = identifiers;
   }

   public String getIdentifier() {
      return this._identifier != null && this._identifier.size() > 0 ? (String)this._identifier.get(0) : null;
   }

   public void setIdentifier(String identifier) {
      this._identifier = new ArrayList();
      this._identifier.add(identifier);
   }

   public List getSources() {
      return this._source == null ? (this._source = new ArrayList()) : this._source;
   }

   public void setSources(List sources) {
      this._source = sources;
   }

   public String getSource() {
      return this._source != null && this._source.size() > 0 ? (String)this._source.get(0) : null;
   }

   public void setSource(String source) {
      this._source = new ArrayList();
      this._source.add(source);
   }

   public List getLanguages() {
      return this._language == null ? (this._language = new ArrayList()) : this._language;
   }

   public void setLanguages(List languages) {
      this._language = languages;
   }

   public String getLanguage() {
      return this._language != null && this._language.size() > 0 ? (String)this._language.get(0) : null;
   }

   public void setLanguage(String language) {
      this._language = new ArrayList();
      this._language.add(language);
   }

   public List getRelations() {
      return this._relation == null ? (this._relation = new ArrayList()) : this._relation;
   }

   public void setRelations(List relations) {
      this._relation = relations;
   }

   public String getRelation() {
      return this._relation != null && this._relation.size() > 0 ? (String)this._relation.get(0) : null;
   }

   public void setRelation(String relation) {
      this._relation = new ArrayList();
      this._relation.add(relation);
   }

   public List getCoverages() {
      return this._coverage == null ? (this._coverage = new ArrayList()) : this._coverage;
   }

   public void setCoverages(List coverages) {
      this._coverage = coverages;
   }

   public String getCoverage() {
      return this._coverage != null && this._coverage.size() > 0 ? (String)this._coverage.get(0) : null;
   }

   public void setCoverage(String coverage) {
      this._coverage = new ArrayList();
      this._coverage.add(coverage);
   }

   public List getRightsList() {
      return this._rights == null ? (this._rights = new ArrayList()) : this._rights;
   }

   public void setRightsList(List rights) {
      this._rights = rights;
   }

   public String getRights() {
      return this._rights != null && this._rights.size() > 0 ? (String)this._rights.get(0) : null;
   }

   public void setRights(String rights) {
      this._rights = new ArrayList();
      this._rights.add(rights);
   }

   public final Object clone() throws CloneNotSupportedException {
      return this._objBean.clone();
   }

   public final boolean equals(Object other) {
      return this._objBean.equals(other);
   }

   public final int hashCode() {
      return this._objBean.hashCode();
   }

   public final String toString() {
      return this._objBean.toString();
   }

   public final Class getInterface() {
      return DCModule.class;
   }

   public final void copyFrom(Object obj) {
      COPY_FROM_HELPER.copy(this, obj);
   }

   static {
      CONVENIENCE_PROPERTIES = Collections.unmodifiableSet(IGNORE_PROPERTIES);
      IGNORE_PROPERTIES.add("title");
      IGNORE_PROPERTIES.add("creator");
      IGNORE_PROPERTIES.add("subject");
      IGNORE_PROPERTIES.add("description");
      IGNORE_PROPERTIES.add("publisher");
      IGNORE_PROPERTIES.add("contributor");
      IGNORE_PROPERTIES.add("date");
      IGNORE_PROPERTIES.add("type");
      IGNORE_PROPERTIES.add("format");
      IGNORE_PROPERTIES.add("identifier");
      IGNORE_PROPERTIES.add("source");
      IGNORE_PROPERTIES.add("language");
      IGNORE_PROPERTIES.add("relation");
      IGNORE_PROPERTIES.add("coverage");
      IGNORE_PROPERTIES.add("rights");
      Map basePropInterfaceMap = new HashMap();
      basePropInterfaceMap.put("titles", String.class);
      basePropInterfaceMap.put("creators", String.class);
      basePropInterfaceMap.put("subjects", DCSubject.class);
      basePropInterfaceMap.put("descriptions", String.class);
      basePropInterfaceMap.put("publishers", String.class);
      basePropInterfaceMap.put("contributors", String.class);
      basePropInterfaceMap.put("dates", Date.class);
      basePropInterfaceMap.put("types", String.class);
      basePropInterfaceMap.put("formats", String.class);
      basePropInterfaceMap.put("identifiers", String.class);
      basePropInterfaceMap.put("sources", String.class);
      basePropInterfaceMap.put("languages", String.class);
      basePropInterfaceMap.put("relations", String.class);
      basePropInterfaceMap.put("coverages", String.class);
      basePropInterfaceMap.put("rightsList", String.class);
      Map basePropClassImplMap = new HashMap();
      basePropClassImplMap.put(DCSubject.class, DCSubjectImpl.class);
      COPY_FROM_HELPER = new CopyFromHelper(DCModule.class, basePropInterfaceMap, basePropClassImplMap);
   }
}

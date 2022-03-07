package com.sun.syndication.feed.module;

import com.sun.syndication.feed.CopyFrom;
import java.util.Date;
import java.util.List;

public interface DCModule extends Module, CopyFrom {
   String URI = "http://purl.org/dc/elements/1.1/";

   List getTitles();

   void setTitles(List var1);

   String getTitle();

   void setTitle(String var1);

   List getCreators();

   void setCreators(List var1);

   String getCreator();

   void setCreator(String var1);

   List getSubjects();

   void setSubjects(List var1);

   DCSubject getSubject();

   void setSubject(DCSubject var1);

   List getDescriptions();

   void setDescriptions(List var1);

   String getDescription();

   void setDescription(String var1);

   List getPublishers();

   void setPublishers(List var1);

   String getPublisher();

   void setPublisher(String var1);

   List getContributors();

   void setContributors(List var1);

   String getContributor();

   void setContributor(String var1);

   List getDates();

   void setDates(List var1);

   Date getDate();

   void setDate(Date var1);

   List getTypes();

   void setTypes(List var1);

   String getType();

   void setType(String var1);

   List getFormats();

   void setFormats(List var1);

   String getFormat();

   void setFormat(String var1);

   List getIdentifiers();

   void setIdentifiers(List var1);

   String getIdentifier();

   void setIdentifier(String var1);

   List getSources();

   void setSources(List var1);

   String getSource();

   void setSource(String var1);

   List getLanguages();

   void setLanguages(List var1);

   String getLanguage();

   void setLanguage(String var1);

   List getRelations();

   void setRelations(List var1);

   String getRelation();

   void setRelation(String var1);

   List getCoverages();

   void setCoverages(List var1);

   String getCoverage();

   void setCoverage(String var1);

   List getRightsList();

   void setRightsList(List var1);

   String getRights();

   void setRights(String var1);
}

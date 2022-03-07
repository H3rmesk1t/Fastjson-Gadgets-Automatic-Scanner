package org.apache.commons.configuration;

import org.xml.sax.Attributes;

public class BaseConfigurationXMLReader extends ConfigurationXMLReader {
   private Configuration config;

   public BaseConfigurationXMLReader() {
   }

   public BaseConfigurationXMLReader(Configuration conf) {
      this();
      this.setConfiguration(conf);
   }

   public Configuration getConfiguration() {
      return this.config;
   }

   public void setConfiguration(Configuration conf) {
      this.config = conf;
   }

   public Configuration getParsedConfiguration() {
      return this.getConfiguration();
   }

   protected void processKeys() {
      this.fireElementStart(this.getRootName(), (Attributes)null);
      (new BaseConfigurationXMLReader.SAXConverter()).process(this.getConfiguration());
      this.fireElementEnd(this.getRootName());
   }

   class SAXConverter extends HierarchicalConfigurationConverter {
      protected void elementStart(String name, Object value) {
         BaseConfigurationXMLReader.this.fireElementStart(name, (Attributes)null);
         if (value != null) {
            BaseConfigurationXMLReader.this.fireCharacters(value.toString());
         }

      }

      protected void elementEnd(String name) {
         BaseConfigurationXMLReader.this.fireElementEnd(name);
      }
   }
}

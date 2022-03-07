package org.apache.commons.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.configuration.plist.PropertyListConfiguration;
import org.apache.commons.configuration.plist.XMLPropertyListConfiguration;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.apache.commons.digester.Substitutor;
import org.apache.commons.digester.substitution.MultiVariableExpander;
import org.apache.commons.digester.substitution.VariableSubstitutor;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** @deprecated */
@Deprecated
public class ConfigurationFactory {
   private static final String SEC_ROOT = "configuration/";
   private static final String SEC_OVERRIDE = "configuration/override/";
   private static final String SEC_ADDITIONAL = "configuration/additional/";
   private static final String ATTR_OPTIONAL = "optional";
   private static final String ATTR_FILENAME = "fileName";
   private static final String METH_LOAD = "load";
   private static final String DEF_BASE_PATH = ".";
   private static Log log = LogFactory.getLog(ConfigurationFactory.class);
   private String configurationFileName;
   private URL configurationURL;
   private String implicitBasePath;
   private String basePath;
   private URL digesterRules;
   private String digesterRuleNamespaceURI;

   public ConfigurationFactory() {
      this.setBasePath(".");
   }

   public ConfigurationFactory(String configurationFileName) {
      this.setConfigurationFileName(configurationFileName);
   }

   public Configuration getConfiguration() throws ConfigurationException {
      InputStream input = null;
      ConfigurationFactory.ConfigurationBuilder builder = new ConfigurationFactory.ConfigurationBuilder();
      URL url = this.getConfigurationURL();

      try {
         if (url == null) {
            url = ConfigurationUtils.locate(this.implicitBasePath, this.getConfigurationFileName());
         }

         input = url.openStream();
      } catch (Exception var8) {
         log.error("Exception caught opening stream to URL", var8);
         throw new ConfigurationException("Exception caught opening stream to URL", var8);
      }

      Digester digester;
      if (this.getDigesterRules() == null) {
         digester = new Digester();
         this.configureNamespace(digester);
         this.initDefaultDigesterRules(digester);
      } else {
         digester = DigesterLoader.createDigester(this.getDigesterRules());
         this.configureNamespace(digester);
      }

      digester.setUseContextClassLoader(true);
      this.enableDigesterSubstitutor(digester);
      digester.push(builder);

      try {
         digester.parse(input);
         input.close();
      } catch (SAXException var6) {
         log.error("SAX Exception caught", var6);
         throw new ConfigurationException("SAX Exception caught", var6);
      } catch (IOException var7) {
         log.error("IO Exception caught", var7);
         throw new ConfigurationException("IO Exception caught", var7);
      }

      return builder.getConfiguration();
   }

   public String getConfigurationFileName() {
      return this.configurationFileName;
   }

   public void setConfigurationFileName(String configurationFileName) {
      File file = (new File(configurationFileName)).getAbsoluteFile();
      this.configurationFileName = file.getName();
      this.implicitBasePath = file.getParent();
   }

   public URL getConfigurationURL() {
      return this.configurationURL;
   }

   public void setConfigurationURL(URL url) {
      this.configurationURL = url;
      this.implicitBasePath = url.toString();
   }

   public URL getDigesterRules() {
      return this.digesterRules;
   }

   public void setDigesterRules(URL digesterRules) {
      this.digesterRules = digesterRules;
   }

   protected void enableDigesterSubstitutor(Digester digester) {
      Map systemProperties = (Map)System.getProperties();
      MultiVariableExpander expander = new MultiVariableExpander();
      expander.addSource("$", systemProperties);
      Substitutor substitutor = new VariableSubstitutor(expander);
      digester.setSubstitutor(substitutor);
   }

   protected void initDefaultDigesterRules(Digester digester) {
      this.initDigesterSectionRules(digester, "configuration/", false);
      this.initDigesterSectionRules(digester, "configuration/override/", false);
      this.initDigesterSectionRules(digester, "configuration/additional/", true);
   }

   protected void initDigesterSectionRules(Digester digester, String matchString, boolean additional) {
      this.setupDigesterInstance(digester, matchString + "properties", new ConfigurationFactory.PropertiesConfigurationFactory(), "load", additional);
      this.setupDigesterInstance(digester, matchString + "plist", new ConfigurationFactory.PropertyListConfigurationFactory(), "load", additional);
      this.setupDigesterInstance(digester, matchString + "xml", new ConfigurationFactory.FileConfigurationFactory(XMLConfiguration.class), "load", additional);
      this.setupDigesterInstance(digester, matchString + "hierarchicalXml", new ConfigurationFactory.FileConfigurationFactory(XMLConfiguration.class), "load", additional);
      this.setupDigesterInstance(digester, matchString + "jndi", new ConfigurationFactory.JNDIConfigurationFactory(), (String)null, additional);
      this.setupDigesterInstance(digester, matchString + "system", new ConfigurationFactory.SystemConfigurationFactory(), (String)null, additional);
   }

   protected void setupDigesterInstance(Digester digester, String matchString, ObjectCreationFactory factory, String method, boolean additional) {
      if (additional) {
         this.setupUnionRules(digester, matchString);
      }

      digester.addFactoryCreate(matchString, factory);
      digester.addSetProperties(matchString);
      if (method != null) {
         digester.addRule(matchString, new ConfigurationFactory.CallOptionalMethodRule(method));
      }

      digester.addSetNext(matchString, "addConfiguration", Configuration.class.getName());
   }

   protected void setupUnionRules(Digester digester, String matchString) {
      digester.addObjectCreate(matchString, ConfigurationFactory.AdditionalConfigurationData.class);
      digester.addSetProperties(matchString);
      digester.addSetNext(matchString, "addAdditionalConfig", ConfigurationFactory.AdditionalConfigurationData.class.getName());
   }

   public String getDigesterRuleNamespaceURI() {
      return this.digesterRuleNamespaceURI;
   }

   public void setDigesterRuleNamespaceURI(String digesterRuleNamespaceURI) {
      this.digesterRuleNamespaceURI = digesterRuleNamespaceURI;
   }

   private void configureNamespace(Digester digester) {
      if (this.getDigesterRuleNamespaceURI() != null) {
         digester.setNamespaceAware(true);
         digester.setRuleNamespaceURI(this.getDigesterRuleNamespaceURI());
      } else {
         digester.setNamespaceAware(false);
      }

      digester.setValidating(false);
   }

   public String getBasePath() {
      String path = !StringUtils.isEmpty(this.basePath) && !".".equals(this.basePath) ? this.basePath : this.implicitBasePath;
      return StringUtils.isEmpty(path) ? "." : path;
   }

   public void setBasePath(String basePath) {
      this.basePath = basePath;
   }

   private static class CallOptionalMethodRule extends CallMethodRule {
      private boolean optional;

      public CallOptionalMethodRule(String methodName) {
         super(methodName);
      }

      public void begin(Attributes attrs) throws Exception {
         this.optional = attrs.getValue("optional") != null && PropertyConverter.toBoolean(attrs.getValue("optional"));
         super.begin(attrs);
      }

      public void end() throws Exception {
         try {
            super.end();
         } catch (Exception var2) {
            if (!this.optional) {
               throw var2;
            }

            ConfigurationFactory.log.warn("Could not create optional configuration!", var2);
         }

      }
   }

   public static class ConfigurationBuilder {
      private CompositeConfiguration config = new CompositeConfiguration();
      private Collection additionalConfigs = new LinkedList();

      public void addConfiguration(Configuration conf) {
         this.config.addConfiguration(conf);
      }

      public void addAdditionalConfig(ConfigurationFactory.AdditionalConfigurationData data) {
         this.additionalConfigs.add(data);
      }

      public CompositeConfiguration getConfiguration() {
         if (!this.additionalConfigs.isEmpty()) {
            Configuration unionConfig = this.createAdditionalConfiguration(this.additionalConfigs);
            if (unionConfig != null) {
               this.addConfiguration(unionConfig);
            }

            this.additionalConfigs.clear();
         }

         return this.config;
      }

      protected Configuration createAdditionalConfiguration(Collection configs) {
         HierarchicalConfiguration result = new HierarchicalConfiguration();
         Iterator i$ = configs.iterator();

         while(i$.hasNext()) {
            ConfigurationFactory.AdditionalConfigurationData cdata = (ConfigurationFactory.AdditionalConfigurationData)i$.next();
            result.addNodes(cdata.getAt(), this.createRootNode(cdata).getChildren());
         }

         return result.isEmpty() ? null : result;
      }

      private HierarchicalConfiguration.Node createRootNode(ConfigurationFactory.AdditionalConfigurationData cdata) {
         if (cdata.getConfiguration() instanceof HierarchicalConfiguration) {
            return ((HierarchicalConfiguration)cdata.getConfiguration()).getRoot();
         } else {
            HierarchicalConfiguration hc = new HierarchicalConfiguration();
            ConfigurationUtils.copy(cdata.getConfiguration(), hc);
            return hc.getRoot();
         }
      }
   }

   public static class AdditionalConfigurationData {
      private Configuration configuration;
      private String at;

      public String getAt() {
         return this.at;
      }

      public void setAt(String string) {
         this.at = string;
      }

      public Configuration getConfiguration() {
         return this.configuration;
      }

      public void addConfiguration(Configuration config) {
         this.configuration = config;
      }
   }

   private class SystemConfigurationFactory extends ConfigurationFactory.DigesterConfigurationFactory {
      public SystemConfigurationFactory() {
         super(SystemConfiguration.class);
      }
   }

   private class JNDIConfigurationFactory extends ConfigurationFactory.DigesterConfigurationFactory {
      public JNDIConfigurationFactory() {
         super(JNDIConfiguration.class);
      }
   }

   public class PropertyListConfigurationFactory extends ConfigurationFactory.FileConfigurationFactory {
      public PropertyListConfigurationFactory() {
         super((Class)null);
      }

      protected FileConfiguration createConfiguration(Attributes attributes) throws Exception {
         String filename = attributes.getValue("fileName");
         return (FileConfiguration)(filename != null && filename.toLowerCase().trim().endsWith(".xml") ? new XMLPropertyListConfiguration() : new PropertyListConfiguration());
      }
   }

   public class PropertiesConfigurationFactory extends ConfigurationFactory.FileConfigurationFactory {
      public PropertiesConfigurationFactory() {
         super((Class)null);
      }

      protected FileConfiguration createConfiguration(Attributes attributes) throws Exception {
         String filename = attributes.getValue("fileName");
         return (FileConfiguration)(filename != null && filename.toLowerCase().trim().endsWith(".xml") ? new XMLPropertiesConfiguration() : new PropertiesConfiguration());
      }
   }

   public class FileConfigurationFactory extends ConfigurationFactory.DigesterConfigurationFactory {
      public FileConfigurationFactory(Class clazz) {
         super(clazz);
      }

      public Object createObject(Attributes attributes) throws Exception {
         FileConfiguration conf = this.createConfiguration(attributes);
         conf.setBasePath(ConfigurationFactory.this.getBasePath());
         return conf;
      }

      protected FileConfiguration createConfiguration(Attributes attributes) throws Exception {
         return (FileConfiguration)super.createObject(attributes);
      }
   }

   public class DigesterConfigurationFactory extends AbstractObjectCreationFactory {
      private Class clazz;

      public DigesterConfigurationFactory(Class clazz) {
         this.clazz = clazz;
      }

      public Object createObject(Attributes attribs) throws Exception {
         return this.clazz.newInstance();
      }
   }
}

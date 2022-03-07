package org.apache.commons.configuration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanFactory;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.beanutils.DefaultBeanFactory;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration.resolver.CatalogResolver;
import org.apache.commons.configuration.resolver.EntityRegistry;
import org.apache.commons.configuration.resolver.EntityResolverSupport;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.apache.commons.configuration.tree.UnionCombiner;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;

public class DefaultConfigurationBuilder extends XMLConfiguration implements ConfigurationBuilder {
   public static final String ADDITIONAL_NAME = DefaultConfigurationBuilder.class.getName() + "/ADDITIONAL_CONFIG";
   public static final int EVENT_ERR_LOAD_OPTIONAL = 51;
   static final String CONFIG_BEAN_FACTORY_NAME = DefaultConfigurationBuilder.class.getName() + ".CONFIG_BEAN_FACTORY_NAME";
   static final String ATTR_NAME = "[@config-name]";
   static final String ATTR_ATNAME = "at";
   static final String ATTR_AT_RES = "[@config-at]";
   static final String ATTR_AT = "[@at]";
   static final String ATTR_OPTIONALNAME = "optional";
   static final String ATTR_OPTIONAL_RES = "[@config-optional]";
   static final String ATTR_OPTIONAL = "[@optional]";
   static final String ATTR_FILENAME = "[@fileName]";
   static final String ATTR_FORCECREATE = "[@config-forceCreate]";
   static final String KEY_SYSTEM_PROPS = "[@systemProperties]";
   static final String SEC_HEADER = "header";
   static final String KEY_UNION = "additional";
   static final String[] CONFIG_SECTIONS = new String[]{"additional", "override", "header"};
   static final String KEY_OVERRIDE = "override";
   static final String KEY_OVERRIDE_LIST = "header.combiner.override.list-nodes.node";
   static final String KEY_ADDITIONAL_LIST = "header.combiner.additional.list-nodes.node";
   static final String KEY_CONFIGURATION_PROVIDERS = "header.providers.provider";
   static final String KEY_PROVIDER_KEY = "[@config-tag]";
   static final String KEY_CONFIGURATION_LOOKUPS = "header.lookups.lookup";
   static final String KEY_ENTITY_RESOLVER = "header.entity-resolver";
   static final String KEY_LOOKUP_KEY = "[@config-prefix]";
   static final String FILE_SYSTEM = "header.fileSystem";
   static final String KEY_RESULT = "header.result";
   static final String KEY_COMBINER = "header.result.nodeCombiner";
   static final String EXT_XML = ".xml";
   private static final DefaultConfigurationBuilder.ConfigurationProvider PROPERTIES_PROVIDER = new DefaultConfigurationBuilder.FileExtensionConfigurationProvider(XMLPropertiesConfiguration.class, PropertiesConfiguration.class, ".xml");
   private static final DefaultConfigurationBuilder.ConfigurationProvider XML_PROVIDER = new DefaultConfigurationBuilder.XMLConfigurationProvider();
   private static final DefaultConfigurationBuilder.ConfigurationProvider JNDI_PROVIDER = new DefaultConfigurationBuilder.ConfigurationProvider(JNDIConfiguration.class);
   private static final DefaultConfigurationBuilder.ConfigurationProvider SYSTEM_PROVIDER = new DefaultConfigurationBuilder.ConfigurationProvider(SystemConfiguration.class);
   private static final DefaultConfigurationBuilder.ConfigurationProvider INI_PROVIDER = new DefaultConfigurationBuilder.FileConfigurationProvider(HierarchicalINIConfiguration.class);
   private static final DefaultConfigurationBuilder.ConfigurationProvider ENV_PROVIDER = new DefaultConfigurationBuilder.ConfigurationProvider(EnvironmentConfiguration.class);
   private static final DefaultConfigurationBuilder.ConfigurationProvider PLIST_PROVIDER = new DefaultConfigurationBuilder.FileExtensionConfigurationProvider("org.apache.commons.configuration.plist.XMLPropertyListConfiguration", "org.apache.commons.configuration.plist.PropertyListConfiguration", ".xml");
   private static final DefaultConfigurationBuilder.ConfigurationProvider BUILDER_PROVIDER = new DefaultConfigurationBuilder.ConfigurationBuilderProvider();
   private static final String[] DEFAULT_TAGS = new String[]{"properties", "xml", "hierarchicalXml", "jndi", "system", "plist", "configuration", "ini", "env"};
   private static final DefaultConfigurationBuilder.ConfigurationProvider[] DEFAULT_PROVIDERS;
   private static final long serialVersionUID = -3113777854714492123L;
   private final StrLookup combinedConfigLookup;
   private CombinedConfiguration constructedConfiguration;
   private final Map providers;
   private String configurationBasePath;

   public DefaultConfigurationBuilder() {
      this.combinedConfigLookup = new StrLookup() {
         public String lookup(String key) {
            if (DefaultConfigurationBuilder.this.constructedConfiguration != null) {
               Object value = DefaultConfigurationBuilder.this.constructedConfiguration.resolveContainerStore(key);
               return value != null ? value.toString() : null;
            } else {
               return null;
            }
         }
      };
      this.providers = new HashMap();
      this.registerDefaultProviders();
      this.registerBeanFactory();
      this.setLogger(LogFactory.getLog(this.getClass()));
      this.addErrorLogListener();
   }

   public DefaultConfigurationBuilder(File file) {
      this();
      this.setFile(file);
   }

   public DefaultConfigurationBuilder(String fileName) throws ConfigurationException {
      this();
      this.setFileName(fileName);
   }

   public DefaultConfigurationBuilder(URL url) throws ConfigurationException {
      this();
      this.setURL(url);
   }

   public String getConfigurationBasePath() {
      return this.configurationBasePath != null ? this.configurationBasePath : this.getBasePath();
   }

   public void setConfigurationBasePath(String configurationBasePath) {
      this.configurationBasePath = configurationBasePath;
   }

   public void addConfigurationProvider(String tagName, DefaultConfigurationBuilder.ConfigurationProvider provider) {
      if (tagName == null) {
         throw new IllegalArgumentException("Tag name must not be null!");
      } else if (provider == null) {
         throw new IllegalArgumentException("Provider must not be null!");
      } else {
         this.providers.put(tagName, provider);
      }
   }

   public DefaultConfigurationBuilder.ConfigurationProvider removeConfigurationProvider(String tagName) {
      return (DefaultConfigurationBuilder.ConfigurationProvider)this.providers.remove(tagName);
   }

   public DefaultConfigurationBuilder.ConfigurationProvider providerForTag(String tagName) {
      return (DefaultConfigurationBuilder.ConfigurationProvider)this.providers.get(tagName);
   }

   public Configuration getConfiguration() throws ConfigurationException {
      return this.getConfiguration(true);
   }

   public CombinedConfiguration getConfiguration(boolean load) throws ConfigurationException {
      if (load) {
         this.load();
      }

      this.initFileSystem();
      this.initSystemProperties();
      this.configureEntityResolver();
      this.registerConfiguredProviders();
      this.registerConfiguredLookups();
      CombinedConfiguration result = this.createResultConfiguration();
      this.constructedConfiguration = result;
      List overrides = this.fetchTopLevelOverrideConfigs();
      overrides.addAll(this.fetchChildConfigs("override"));
      this.initCombinedConfiguration(result, overrides, "header.combiner.override.list-nodes.node");
      List additionals = this.fetchChildConfigs("additional");
      if (!additionals.isEmpty()) {
         CombinedConfiguration addConfig = this.createAdditionalsConfiguration(result);
         result.addConfiguration(addConfig, ADDITIONAL_NAME);
         this.initCombinedConfiguration(addConfig, additionals, "header.combiner.additional.list-nodes.node");
      }

      return result;
   }

   protected CombinedConfiguration createResultConfiguration() throws ConfigurationException {
      XMLBeanDeclaration decl = new XMLBeanDeclaration(this, "header.result", true);
      CombinedConfiguration result = (CombinedConfiguration)BeanHelper.createBean(decl, CombinedConfiguration.class);
      if (this.getMaxIndex("header.result.nodeCombiner") < 0) {
         result.setNodeCombiner(new OverrideCombiner());
      }

      return result;
   }

   protected CombinedConfiguration createAdditionalsConfiguration(CombinedConfiguration resultConfig) {
      CombinedConfiguration addConfig = new CombinedConfiguration(new UnionCombiner());
      addConfig.setDelimiterParsingDisabled(resultConfig.isDelimiterParsingDisabled());
      addConfig.setForceReloadCheck(resultConfig.isForceReloadCheck());
      addConfig.setIgnoreReloadExceptions(resultConfig.isIgnoreReloadExceptions());
      return addConfig;
   }

   protected void initCombinedConfiguration(CombinedConfiguration config, List containedConfigs, String keyListNodes) throws ConfigurationException {
      List listNodes = this.getList(keyListNodes);
      Iterator i$ = listNodes.iterator();

      while(i$.hasNext()) {
         Object listNode = i$.next();
         config.getNodeCombiner().addListNode((String)listNode);
      }

      i$ = containedConfigs.iterator();

      while(i$.hasNext()) {
         HierarchicalConfiguration conf = (HierarchicalConfiguration)i$.next();
         DefaultConfigurationBuilder.ConfigurationDeclaration decl = new DefaultConfigurationBuilder.ConfigurationDeclaration(this, conf);
         if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("Creating configuration " + decl.getBeanClassName() + " with name " + decl.getConfiguration().getString("[@config-name]"));
         }

         AbstractConfiguration newConf = this.createConfigurationAt(decl);
         if (newConf != null) {
            config.addConfiguration(newConf, decl.getConfiguration().getString("[@config-name]"), decl.getAt());
         }
      }

   }

   protected void registerDefaultProviders() {
      for(int i = 0; i < DEFAULT_TAGS.length; ++i) {
         this.addConfigurationProvider(DEFAULT_TAGS[i], DEFAULT_PROVIDERS[i]);
      }

   }

   protected void registerConfiguredProviders() throws ConfigurationException {
      List nodes = this.configurationsAt("header.providers.provider");
      Iterator i$ = nodes.iterator();

      while(i$.hasNext()) {
         HierarchicalConfiguration config = (HierarchicalConfiguration)i$.next();
         XMLBeanDeclaration decl = new XMLBeanDeclaration(config);
         String key = config.getString("[@config-tag]");
         this.addConfigurationProvider(key, (DefaultConfigurationBuilder.ConfigurationProvider)BeanHelper.createBean(decl));
      }

   }

   protected void registerConfiguredLookups() throws ConfigurationException {
      List nodes = this.configurationsAt("header.lookups.lookup");
      Iterator i$ = nodes.iterator();

      while(i$.hasNext()) {
         HierarchicalConfiguration config = (HierarchicalConfiguration)i$.next();
         XMLBeanDeclaration decl = new XMLBeanDeclaration(config);
         String key = config.getString("[@config-prefix]");
         StrLookup lookup = (StrLookup)BeanHelper.createBean(decl);
         BeanHelper.setProperty(lookup, "configuration", this);
         ConfigurationInterpolator.registerGlobalLookup(key, lookup);
         this.getInterpolator().registerLookup(key, lookup);
      }

   }

   protected void initFileSystem() throws ConfigurationException {
      if (this.getMaxIndex("header.fileSystem") == 0) {
         HierarchicalConfiguration config = this.configurationAt("header.fileSystem");
         XMLBeanDeclaration decl = new XMLBeanDeclaration(config);
         this.setFileSystem((FileSystem)BeanHelper.createBean(decl));
      }

   }

   protected void initSystemProperties() throws ConfigurationException {
      String fileName = this.getString("[@systemProperties]");
      if (fileName != null) {
         try {
            SystemConfiguration.setSystemProperties(this.getConfigurationBasePath(), fileName);
         } catch (Exception var3) {
            throw new ConfigurationException("Error setting system properties from " + fileName, var3);
         }
      }

   }

   protected void configureEntityResolver() throws ConfigurationException {
      if (this.getMaxIndex("header.entity-resolver") == 0) {
         XMLBeanDeclaration decl = new XMLBeanDeclaration(this, "header.entity-resolver", true);
         EntityResolver resolver = (EntityResolver)BeanHelper.createBean(decl, CatalogResolver.class);
         BeanHelper.setProperty(resolver, "fileSystem", this.getFileSystem());
         BeanHelper.setProperty(resolver, "baseDir", this.getBasePath());
         BeanHelper.setProperty(resolver, "substitutor", this.getSubstitutor());
         this.setEntityResolver(resolver);
      }

   }

   protected Object interpolate(Object value) {
      Object result = super.interpolate(value);
      if (this.constructedConfiguration != null) {
         result = this.constructedConfiguration.interpolate(result);
      }

      return result;
   }

   private AbstractConfiguration createConfigurationAt(DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws ConfigurationException {
      try {
         return (AbstractConfiguration)BeanHelper.createBean(decl);
      } catch (Exception var3) {
         throw new ConfigurationException(var3);
      }
   }

   private List fetchChildConfigs(ConfigurationNode node) {
      List children = node.getChildren();
      List result = new ArrayList(children.size());
      Iterator i$ = children.iterator();

      while(i$.hasNext()) {
         ConfigurationNode child = (ConfigurationNode)i$.next();
         result.add(this.createSubnodeConfiguration(child));
      }

      return result;
   }

   private List fetchChildConfigs(String key) {
      List nodes = this.fetchNodeList(key);
      return nodes.size() > 0 ? this.fetchChildConfigs((ConfigurationNode)nodes.get(0)) : Collections.emptyList();
   }

   private List fetchTopLevelOverrideConfigs() {
      List configs = this.fetchChildConfigs(this.getRootNode());
      Iterator it = configs.iterator();

      while(true) {
         while(it.hasNext()) {
            String nodeName = ((SubnodeConfiguration)it.next()).getRootNode().getName();

            for(int i = 0; i < CONFIG_SECTIONS.length; ++i) {
               if (CONFIG_SECTIONS[i].equals(nodeName)) {
                  it.remove();
                  break;
               }
            }
         }

         return configs;
      }
   }

   private void registerBeanFactory() {
      Class var1 = DefaultConfigurationBuilder.class;
      synchronized(DefaultConfigurationBuilder.class) {
         if (!BeanHelper.registeredFactoryNames().contains(CONFIG_BEAN_FACTORY_NAME)) {
            BeanHelper.registerBeanFactory(CONFIG_BEAN_FACTORY_NAME, new DefaultConfigurationBuilder.ConfigurationBeanFactory());
         }

      }
   }

   static {
      DEFAULT_PROVIDERS = new DefaultConfigurationBuilder.ConfigurationProvider[]{PROPERTIES_PROVIDER, XML_PROVIDER, XML_PROVIDER, JNDI_PROVIDER, SYSTEM_PROVIDER, PLIST_PROVIDER, BUILDER_PROVIDER, INI_PROVIDER, ENV_PROVIDER};
   }

   static class ConfigurationBuilderProvider extends DefaultConfigurationBuilder.ConfigurationProvider {
      public ConfigurationBuilderProvider() {
         super(DefaultConfigurationBuilder.class);
      }

      public AbstractConfiguration getConfiguration(DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws Exception {
         DefaultConfigurationBuilder builder = (DefaultConfigurationBuilder)super.getConfiguration(decl);
         return builder.getConfiguration(true);
      }

      public AbstractConfiguration getEmptyConfiguration(DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws Exception {
         return new CombinedConfiguration();
      }

      protected void initBeanInstance(Object bean, BeanDeclaration data) throws Exception {
         DefaultConfigurationBuilder.ConfigurationDeclaration decl = (DefaultConfigurationBuilder.ConfigurationDeclaration)data;
         initChildBuilder(decl.getConfigurationBuilder(), (DefaultConfigurationBuilder)bean);
         super.initBeanInstance(bean, data);
      }

      private static void initChildBuilder(DefaultConfigurationBuilder parent, DefaultConfigurationBuilder child) {
         child.setAttributeSplittingDisabled(parent.isAttributeSplittingDisabled());
         child.setBasePath(parent.getBasePath());
         child.setDelimiterParsingDisabled(parent.isDelimiterParsingDisabled());
         child.setListDelimiter(parent.getListDelimiter());
         child.setThrowExceptionOnMissing(parent.isThrowExceptionOnMissing());
         child.setLogger(parent.getLogger());
         child.clearConfigurationListeners();
         Iterator i$ = parent.getConfigurationListeners().iterator();

         while(i$.hasNext()) {
            ConfigurationListener l = (ConfigurationListener)i$.next();
            child.addConfigurationListener(l);
         }

         child.clearErrorListeners();
         i$ = parent.getErrorListeners().iterator();

         while(i$.hasNext()) {
            ConfigurationErrorListener l = (ConfigurationErrorListener)i$.next();
            child.addErrorListener(l);
         }

      }
   }

   static class FileExtensionConfigurationProvider extends DefaultConfigurationBuilder.FileConfigurationProvider {
      private Class matchingClass;
      private String matchingClassName;
      private Class defaultClass;
      private String defaultClassName;
      private String fileExtension;

      public FileExtensionConfigurationProvider(Class matchingClass, Class defaultClass, String extension) {
         this.matchingClass = matchingClass;
         this.defaultClass = defaultClass;
         this.fileExtension = extension;
      }

      public FileExtensionConfigurationProvider(String matchingClassName, String defaultClassName, String extension) {
         this.matchingClassName = matchingClassName;
         this.defaultClassName = defaultClassName;
         this.fileExtension = extension;
      }

      protected synchronized Class fetchMatchingClass() throws Exception {
         if (this.matchingClass == null) {
            this.matchingClass = this.loadClass(this.matchingClassName);
         }

         return this.matchingClass;
      }

      protected synchronized Class fetchDefaultClass() throws Exception {
         if (this.defaultClass == null) {
            this.defaultClass = this.loadClass(this.defaultClassName);
         }

         return this.defaultClass;
      }

      protected Object createBeanInstance(Class beanClass, BeanDeclaration data) throws Exception {
         String fileName = ((DefaultConfigurationBuilder.ConfigurationDeclaration)data).getConfiguration().getString("[@fileName]");
         return fileName != null && fileName.toLowerCase().trim().endsWith(this.fileExtension) ? super.createBeanInstance(this.fetchMatchingClass(), data) : super.createBeanInstance(this.fetchDefaultClass(), data);
      }
   }

   public static class XMLConfigurationProvider extends DefaultConfigurationBuilder.FileConfigurationProvider {
      public XMLConfigurationProvider() {
         super(XMLConfiguration.class);
      }

      public AbstractConfiguration getEmptyConfiguration(DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws Exception {
         XMLConfiguration config = (XMLConfiguration)super.getEmptyConfiguration(decl);
         DefaultConfigurationBuilder builder = decl.getConfigurationBuilder();
         EntityResolver resolver = builder.getEntityResolver();
         if (resolver instanceof EntityRegistry) {
            config.getRegisteredEntities().putAll(builder.getRegisteredEntities());
         } else {
            config.setEntityResolver(resolver);
         }

         return config;
      }
   }

   public static class FileConfigurationProvider extends DefaultConfigurationBuilder.ConfigurationProvider {
      public FileConfigurationProvider() {
      }

      public FileConfigurationProvider(Class configClass) {
         super(configClass);
      }

      public FileConfigurationProvider(String configClassName) {
         super(configClassName);
      }

      public AbstractConfiguration getConfiguration(DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws Exception {
         AbstractConfiguration result = this.getEmptyConfiguration(decl);
         if (result instanceof FileSystemBased) {
            DefaultConfigurationBuilder builder = decl.getConfigurationBuilder();
            if (builder.getFileSystem() != null) {
               ((FileSystemBased)result).setFileSystem(builder.getFileSystem());
            }
         }

         ((FileConfiguration)result).load();
         return result;
      }

      public AbstractConfiguration getEmptyConfiguration(DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws Exception {
         AbstractConfiguration config = super.getConfiguration(decl);
         if (config instanceof EntityResolverSupport) {
            DefaultConfigurationBuilder builder = decl.getConfigurationBuilder();
            EntityResolver resolver = builder.getEntityResolver();
            ((EntityResolverSupport)config).setEntityResolver(resolver);
         }

         return config;
      }

      protected void initBeanInstance(Object bean, BeanDeclaration data) throws Exception {
         FileConfiguration config = (FileConfiguration)bean;
         config.setBasePath(((DefaultConfigurationBuilder.ConfigurationDeclaration)data).getConfigurationBuilder().getConfigurationBasePath());
         super.initBeanInstance(bean, data);
      }
   }

   static class ConfigurationBeanFactory implements BeanFactory {
      private Log logger = LogFactory.getLog(DefaultConfigurationBuilder.class);

      public Object createBean(Class beanClass, BeanDeclaration data, Object param) throws Exception {
         DefaultConfigurationBuilder.ConfigurationDeclaration decl = (DefaultConfigurationBuilder.ConfigurationDeclaration)data;
         String tagName = decl.getNode().getName();
         DefaultConfigurationBuilder.ConfigurationProvider provider = decl.getConfigurationBuilder().providerForTag(tagName);
         if (provider == null) {
            throw new ConfigurationRuntimeException("No ConfigurationProvider registered for tag " + tagName);
         } else {
            try {
               AbstractConfiguration config = provider.getConfiguration(decl);
               this.installInterpolator(decl, config);
               return config;
            } catch (Exception var10) {
               if (!decl.isOptional()) {
                  throw var10;
               } else {
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug("Load failed for optional configuration " + tagName + ": " + var10.getMessage());
                  }

                  decl.getConfigurationBuilder().fireError(51, decl.getConfiguration().getString("[@config-name]"), (Object)null, var10);
                  if (decl.isForceCreate()) {
                     try {
                        return provider.getEmptyConfiguration(decl);
                     } catch (Exception var9) {
                        this.logger.warn("Could not create instance of optional configuration " + tagName, var9);
                     }
                  }

                  return null;
               }
            }
         }
      }

      public Class getDefaultBeanClass() {
         return Configuration.class;
      }

      private void installInterpolator(DefaultConfigurationBuilder.ConfigurationDeclaration decl, AbstractConfiguration config) {
         ConfigurationInterpolator parent = new ConfigurationInterpolator();
         parent.setDefaultLookup(decl.getConfigurationBuilder().combinedConfigLookup);
         config.getInterpolator().setParentInterpolator(parent);
      }
   }

   public static class ConfigurationDeclaration extends XMLBeanDeclaration {
      private DefaultConfigurationBuilder configurationBuilder;

      public ConfigurationDeclaration(DefaultConfigurationBuilder builder, HierarchicalConfiguration config) {
         super(config);
         this.configurationBuilder = builder;
      }

      public DefaultConfigurationBuilder getConfigurationBuilder() {
         return this.configurationBuilder;
      }

      public String getAt() {
         String result = this.getConfiguration().getString("[@config-at]");
         return result == null ? this.getConfiguration().getString("[@at]") : result;
      }

      public boolean isOptional() {
         Boolean value = this.getConfiguration().getBoolean("[@config-optional]", (Boolean)null);
         if (value == null) {
            value = this.getConfiguration().getBoolean("[@optional]", Boolean.FALSE);
         }

         return value;
      }

      public boolean isForceCreate() {
         return this.getConfiguration().getBoolean("[@config-forceCreate]", false);
      }

      public String getBeanFactoryName() {
         return DefaultConfigurationBuilder.CONFIG_BEAN_FACTORY_NAME;
      }

      public String getBeanClassName() {
         return null;
      }

      protected boolean isReservedNode(ConfigurationNode nd) {
         if (super.isReservedNode(nd)) {
            return true;
         } else {
            return nd.isAttribute() && ("at".equals(nd.getName()) && nd.getParentNode().getAttributeCount("config-at") == 0 || "optional".equals(nd.getName()) && nd.getParentNode().getAttributeCount("config-optional") == 0);
         }
      }

      protected Object interpolate(Object value) {
         return this.getConfigurationBuilder().interpolate(value);
      }
   }

   public static class ConfigurationProvider extends DefaultBeanFactory {
      private Class configurationClass;
      private String configurationClassName;

      public ConfigurationProvider() {
         this((Class)null);
      }

      public ConfigurationProvider(Class configClass) {
         this.setConfigurationClass(configClass);
      }

      public ConfigurationProvider(String configClassName) {
         this.setConfigurationClassName(configClassName);
      }

      public Class getConfigurationClass() {
         return this.configurationClass;
      }

      public void setConfigurationClass(Class configurationClass) {
         this.configurationClass = configurationClass;
      }

      public String getConfigurationClassName() {
         return this.configurationClassName;
      }

      public void setConfigurationClassName(String configurationClassName) {
         this.configurationClassName = configurationClassName;
      }

      public AbstractConfiguration getConfiguration(DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws Exception {
         return (AbstractConfiguration)this.createBean(this.fetchConfigurationClass(), decl, (Object)null);
      }

      public AbstractConfiguration getEmptyConfiguration(DefaultConfigurationBuilder.ConfigurationDeclaration decl) throws Exception {
         return null;
      }

      protected synchronized Class fetchConfigurationClass() throws Exception {
         if (this.getConfigurationClass() == null) {
            this.setConfigurationClass(this.loadClass(this.getConfigurationClassName()));
         }

         return this.getConfigurationClass();
      }

      protected Class loadClass(String className) throws ClassNotFoundException {
         return className != null ? Class.forName(className, true, this.getClass().getClassLoader()) : null;
      }
   }
}

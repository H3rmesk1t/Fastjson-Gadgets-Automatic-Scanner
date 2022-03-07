package org.apache.commons.configuration;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import org.apache.commons.configuration.reloading.ReloadingStrategy;

public interface FileConfiguration extends Configuration {
   void load() throws ConfigurationException;

   void load(String var1) throws ConfigurationException;

   void load(File var1) throws ConfigurationException;

   void load(URL var1) throws ConfigurationException;

   void load(InputStream var1) throws ConfigurationException;

   void load(InputStream var1, String var2) throws ConfigurationException;

   void load(Reader var1) throws ConfigurationException;

   void save() throws ConfigurationException;

   void save(String var1) throws ConfigurationException;

   void save(File var1) throws ConfigurationException;

   void save(URL var1) throws ConfigurationException;

   void save(OutputStream var1) throws ConfigurationException;

   void save(OutputStream var1, String var2) throws ConfigurationException;

   void save(Writer var1) throws ConfigurationException;

   String getFileName();

   void setFileName(String var1);

   String getBasePath();

   void setBasePath(String var1);

   File getFile();

   void setFile(File var1);

   URL getURL();

   void setURL(URL var1);

   void setAutoSave(boolean var1);

   boolean isAutoSave();

   ReloadingStrategy getReloadingStrategy();

   void setReloadingStrategy(ReloadingStrategy var1);

   void reload();

   String getEncoding();

   void setEncoding(String var1);
}

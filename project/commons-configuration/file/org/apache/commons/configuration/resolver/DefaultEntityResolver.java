package org.apache.commons.configuration.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DefaultEntityResolver implements EntityResolver, EntityRegistry {
   private Map registeredEntities = new HashMap();

   public void registerEntityId(String publicId, URL entityURL) {
      if (publicId == null) {
         throw new IllegalArgumentException("Public ID must not be null!");
      } else {
         this.getRegisteredEntities().put(publicId, entityURL);
      }
   }

   public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
      URL entityURL = null;
      if (publicId != null) {
         entityURL = (URL)this.getRegisteredEntities().get(publicId);
      }

      if (entityURL != null) {
         try {
            URLConnection connection = entityURL.openConnection();
            connection.setUseCaches(false);
            InputStream stream = connection.getInputStream();
            InputSource source = new InputSource(stream);
            source.setSystemId(entityURL.toExternalForm());
            return source;
         } catch (IOException var7) {
            throw new SAXException(var7);
         }
      } else {
         return null;
      }
   }

   public Map getRegisteredEntities() {
      return this.registeredEntities;
   }
}

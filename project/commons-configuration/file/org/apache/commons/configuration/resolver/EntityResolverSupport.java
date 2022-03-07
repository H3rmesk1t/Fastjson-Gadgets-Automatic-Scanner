package org.apache.commons.configuration.resolver;

import org.xml.sax.EntityResolver;

public interface EntityResolverSupport {
   EntityResolver getEntityResolver();

   void setEntityResolver(EntityResolver var1);
}

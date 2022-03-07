package org.apache.commons.configuration.tree;

import java.util.List;

public interface ExpressionEngine {
   List query(ConfigurationNode var1, String var2);

   String nodeKey(ConfigurationNode var1, String var2);

   NodeAddData prepareAdd(ConfigurationNode var1, String var2);
}

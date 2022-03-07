package org.apache.commons.configuration.interpol;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;

public class ExprLookup extends StrLookup {
   private static final String CLASS = "Class:";
   private static final String DEFAULT_PREFIX = "$[";
   private static final String DEFAULT_SUFFIX = "]";
   private AbstractConfiguration configuration;
   private final JexlEngine engine;
   private ExprLookup.Variables variables;
   private String prefixMatcher;
   private String suffixMatcher;

   public ExprLookup() {
      this.engine = new JexlEngine();
      this.prefixMatcher = "$[";
      this.suffixMatcher = "]";
   }

   public ExprLookup(ExprLookup.Variables list) {
      this.engine = new JexlEngine();
      this.prefixMatcher = "$[";
      this.suffixMatcher = "]";
      this.setVariables(list);
   }

   public ExprLookup(ExprLookup.Variables list, String prefix, String suffix) {
      this(list);
      this.setVariablePrefixMatcher(prefix);
      this.setVariableSuffixMatcher(suffix);
   }

   public void setVariablePrefixMatcher(String prefix) {
      this.prefixMatcher = prefix;
   }

   public void setVariableSuffixMatcher(String suffix) {
      this.suffixMatcher = suffix;
   }

   public void setVariables(ExprLookup.Variables list) {
      this.variables = new ExprLookup.Variables(list);
   }

   public ExprLookup.Variables getVariables() {
      return null;
   }

   public void setConfiguration(AbstractConfiguration config) {
      this.configuration = config;
   }

   public String lookup(String var) {
      ConfigurationInterpolator interp = this.configuration.getInterpolator();
      StrSubstitutor subst = new StrSubstitutor(interp, this.prefixMatcher, this.suffixMatcher, '$');
      String result = subst.replace(var);

      try {
         Expression exp = this.engine.createExpression(result);
         result = (String)exp.evaluate(this.createContext());
      } catch (Exception var6) {
         this.configuration.getLogger().debug("Error encountered evaluating " + result, var6);
      }

      return result;
   }

   private JexlContext createContext() {
      JexlContext ctx = new MapContext();
      this.initializeContext(ctx);
      return ctx;
   }

   private void initializeContext(JexlContext ctx) {
      Iterator i$ = this.variables.iterator();

      while(i$.hasNext()) {
         ExprLookup.Variable var = (ExprLookup.Variable)i$.next();
         ctx.set(var.getName(), var.getValue());
      }

   }

   public static class Variable {
      private String key;
      private Object value;

      public Variable() {
      }

      public Variable(String name, Object value) {
         this.setName(name);
         this.setValue(value);
      }

      public String getName() {
         return this.key;
      }

      public void setName(String name) {
         this.key = name;
      }

      public Object getValue() {
         return this.value;
      }

      public void setValue(Object value) throws ConfigurationRuntimeException {
         try {
            if (!(value instanceof String)) {
               this.value = value;
            } else {
               String val = (String)value;
               String name = StringUtils.removeStartIgnoreCase(val, "Class:");
               Class clazz = ClassUtils.getClass(name);
               if (name.length() == val.length()) {
                  this.value = clazz.newInstance();
               } else {
                  this.value = clazz;
               }

            }
         } catch (Exception var5) {
            throw new ConfigurationRuntimeException("Unable to create " + value, var5);
         }
      }
   }

   public static class Variables extends ArrayList {
      private static final long serialVersionUID = 20111205L;

      public Variables() {
      }

      public Variables(ExprLookup.Variables vars) {
         super(vars);
      }

      public ExprLookup.Variable getVariable() {
         return this.size() > 0 ? (ExprLookup.Variable)this.get(this.size() - 1) : null;
      }
   }
}

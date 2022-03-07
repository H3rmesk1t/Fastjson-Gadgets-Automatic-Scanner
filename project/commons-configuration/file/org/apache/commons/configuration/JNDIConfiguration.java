package org.apache.commons.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

public class JNDIConfiguration extends AbstractConfiguration {
   private String prefix;
   private Context context;
   private Context baseContext;
   private Set clearedProperties;

   public JNDIConfiguration() throws NamingException {
      this((String)null);
   }

   public JNDIConfiguration(String prefix) throws NamingException {
      this(new InitialContext(), prefix);
   }

   public JNDIConfiguration(Context context) {
      this(context, (String)null);
   }

   public JNDIConfiguration(Context context, String prefix) {
      this.clearedProperties = new HashSet();
      this.context = context;
      this.prefix = prefix;
      this.setLogger(LogFactory.getLog(this.getClass()));
      this.addErrorLogListener();
   }

   private void recursiveGetKeys(Set keys, Context context, String prefix, Set processedCtx) throws NamingException {
      processedCtx.add(context);
      NamingEnumeration elements = null;

      try {
         elements = context.list("");

         while(elements.hasMore()) {
            NameClassPair nameClassPair = (NameClassPair)elements.next();
            String name = nameClassPair.getName();
            Object object = context.lookup(name);
            StringBuilder key = new StringBuilder();
            key.append(prefix);
            if (key.length() > 0) {
               key.append(".");
            }

            key.append(name);
            if (object instanceof Context) {
               Context subcontext = (Context)object;
               if (!processedCtx.contains(subcontext)) {
                  this.recursiveGetKeys(keys, subcontext, key.toString(), processedCtx);
               }
            } else {
               keys.add(key.toString());
            }
         }
      } finally {
         if (elements != null) {
            elements.close();
         }

      }

   }

   public Iterator getKeys() {
      return this.getKeys("");
   }

   public Iterator getKeys(String prefix) {
      String[] splitPath = StringUtils.split(prefix, ".");
      List path = Arrays.asList(splitPath);

      try {
         Context context = this.getContext(path, this.getBaseContext());
         Set keys = new HashSet();
         if (context != null) {
            this.recursiveGetKeys(keys, context, prefix, new HashSet());
         } else if (this.containsKey(prefix)) {
            keys.add(prefix);
         }

         return keys.iterator();
      } catch (NameNotFoundException var6) {
         return (new ArrayList()).iterator();
      } catch (NamingException var7) {
         this.fireError(5, (String)null, (Object)null, var7);
         return (new ArrayList()).iterator();
      }
   }

   private Context getContext(List path, Context context) throws NamingException {
      if (path != null && !path.isEmpty()) {
         String key = (String)path.get(0);
         NamingEnumeration elements = null;

         Context var9;
         try {
            elements = context.list("");

            String name;
            Object object;
            do {
               if (!elements.hasMore()) {
                  return null;
               }

               NameClassPair nameClassPair = (NameClassPair)elements.next();
               name = nameClassPair.getName();
               object = context.lookup(name);
            } while(!(object instanceof Context) || !name.equals(key));

            Context subcontext = (Context)object;
            var9 = this.getContext(path.subList(1, path.size()), subcontext);
         } finally {
            if (elements != null) {
               elements.close();
            }

         }

         return var9;
      } else {
         return context;
      }
   }

   public boolean isEmpty() {
      try {
         NamingEnumeration enumeration = null;

         boolean var2;
         try {
            enumeration = this.getBaseContext().list("");
            var2 = !enumeration.hasMore();
         } finally {
            if (enumeration != null) {
               enumeration.close();
            }

         }

         return var2;
      } catch (NamingException var7) {
         this.fireError(5, (String)null, (Object)null, var7);
         return true;
      }
   }

   public void setProperty(String key, Object value) {
      throw new UnsupportedOperationException("This operation is not supported");
   }

   public void clearProperty(String key) {
      this.clearedProperties.add(key);
   }

   public boolean containsKey(String key) {
      if (this.clearedProperties.contains(key)) {
         return false;
      } else {
         key = key.replaceAll("\\.", "/");

         try {
            this.getBaseContext().lookup(key);
            return true;
         } catch (NameNotFoundException var3) {
            return false;
         } catch (NamingException var4) {
            this.fireError(5, key, (Object)null, var4);
            return false;
         }
      }
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
      this.baseContext = null;
   }

   public Object getProperty(String key) {
      if (this.clearedProperties.contains(key)) {
         return null;
      } else {
         try {
            key = key.replaceAll("\\.", "/");
            return this.getBaseContext().lookup(key);
         } catch (NameNotFoundException var3) {
            return null;
         } catch (NotContextException var4) {
            return null;
         } catch (NamingException var5) {
            this.fireError(5, key, (Object)null, var5);
            return null;
         }
      }
   }

   protected void addPropertyDirect(String key, Object obj) {
      throw new UnsupportedOperationException("This operation is not supported");
   }

   public Context getBaseContext() throws NamingException {
      if (this.baseContext == null) {
         this.baseContext = (Context)this.getContext().lookup(this.prefix == null ? "" : this.prefix);
      }

      return this.baseContext;
   }

   public Context getContext() {
      return this.context;
   }

   public void setContext(Context context) {
      this.clearedProperties.clear();
      this.context = context;
   }
}

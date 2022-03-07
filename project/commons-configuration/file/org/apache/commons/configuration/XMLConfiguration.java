package org.apache.commons.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.resolver.DefaultEntityResolver;
import org.apache.commons.configuration.resolver.EntityRegistry;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLConfiguration extends AbstractHierarchicalFileConfiguration implements EntityResolver, EntityRegistry {
   private static final long serialVersionUID = 2453781111653383552L;
   private static final String DEFAULT_ROOT_NAME = "configuration";
   private static final String ATTR_SPACE = "xml:space";
   private static final String VALUE_PRESERVE = "preserve";
   private static final char ATTR_VALUE_DELIMITER = '|';
   private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   private Document document;
   private String rootElementName;
   private String publicID;
   private String systemID;
   private DocumentBuilder documentBuilder;
   private boolean validating;
   private boolean schemaValidation;
   private boolean attributeSplittingDisabled;
   private EntityResolver entityResolver = new DefaultEntityResolver();

   public XMLConfiguration() {
      this.setLogger(LogFactory.getLog(XMLConfiguration.class));
   }

   public XMLConfiguration(HierarchicalConfiguration c) {
      super(c);
      clearReferences(this.getRootNode());
      this.setRootElementName(this.getRootNode().getName());
      this.setLogger(LogFactory.getLog(XMLConfiguration.class));
   }

   public XMLConfiguration(String fileName) throws ConfigurationException {
      super(fileName);
      this.setLogger(LogFactory.getLog(XMLConfiguration.class));
   }

   public XMLConfiguration(File file) throws ConfigurationException {
      super(file);
      this.setLogger(LogFactory.getLog(XMLConfiguration.class));
   }

   public XMLConfiguration(URL url) throws ConfigurationException {
      super(url);
      this.setLogger(LogFactory.getLog(XMLConfiguration.class));
   }

   public String getRootElementName() {
      if (this.getDocument() == null) {
         return this.rootElementName == null ? "configuration" : this.rootElementName;
      } else {
         return this.getDocument().getDocumentElement().getNodeName();
      }
   }

   public void setRootElementName(String name) {
      if (this.getDocument() != null) {
         throw new UnsupportedOperationException("The name of the root element cannot be changed when loaded from an XML document!");
      } else {
         this.rootElementName = name;
         this.getRootNode().setName(name);
      }
   }

   public DocumentBuilder getDocumentBuilder() {
      return this.documentBuilder;
   }

   public void setDocumentBuilder(DocumentBuilder documentBuilder) {
      this.documentBuilder = documentBuilder;
   }

   public String getPublicID() {
      return this.publicID;
   }

   public void setPublicID(String publicID) {
      this.publicID = publicID;
   }

   public String getSystemID() {
      return this.systemID;
   }

   public void setSystemID(String systemID) {
      this.systemID = systemID;
   }

   public boolean isValidating() {
      return this.validating;
   }

   public void setValidating(boolean validating) {
      if (!this.schemaValidation) {
         this.validating = validating;
      }

   }

   public boolean isSchemaValidation() {
      return this.schemaValidation;
   }

   public void setSchemaValidation(boolean schemaValidation) {
      this.schemaValidation = schemaValidation;
      if (schemaValidation) {
         this.validating = true;
      }

   }

   public void setEntityResolver(EntityResolver resolver) {
      this.entityResolver = resolver;
   }

   public EntityResolver getEntityResolver() {
      return this.entityResolver;
   }

   public boolean isAttributeSplittingDisabled() {
      return this.attributeSplittingDisabled;
   }

   public void setAttributeSplittingDisabled(boolean attributeSplittingDisabled) {
      this.attributeSplittingDisabled = attributeSplittingDisabled;
   }

   public Document getDocument() {
      return this.document;
   }

   public void clear() {
      super.clear();
      this.setRoot(new HierarchicalConfiguration.Node());
      this.document = null;
   }

   public void initProperties(Document document, boolean elemRefs) {
      if (document.getDoctype() != null) {
         this.setPublicID(document.getDoctype().getPublicId());
         this.setSystemID(document.getDoctype().getSystemId());
      }

      this.constructHierarchy(this.getRoot(), document.getDocumentElement(), elemRefs, true);
      this.getRootNode().setName(document.getDocumentElement().getNodeName());
      if (elemRefs) {
         this.getRoot().setReference(document.getDocumentElement());
      }

   }

   private Map constructHierarchy(HierarchicalConfiguration.Node node, Element element, boolean elemRefs, boolean trim) {
      boolean trimFlag = this.shouldTrim(element, trim);
      Map attributes = this.processAttributes(node, element, elemRefs);
      attributes.put("xml:space", Collections.singleton(String.valueOf(trimFlag)));
      StringBuilder buffer = new StringBuilder();
      NodeList list = element.getChildNodes();

      for(int i = 0; i < list.getLength(); ++i) {
         org.w3c.dom.Node w3cNode = list.item(i);
         if (w3cNode instanceof Element) {
            Element child = (Element)w3cNode;
            HierarchicalConfiguration.Node childNode = new XMLConfiguration.XMLNode(child.getTagName(), elemRefs ? child : null);
            Map attrmap = this.constructHierarchy(childNode, child, elemRefs, trimFlag);
            node.addChild(childNode);
            Collection attrSpace = (Collection)attrmap.remove("xml:space");
            Boolean childTrim = CollectionUtils.isEmpty(attrSpace) ? Boolean.FALSE : Boolean.valueOf((String)attrSpace.iterator().next());
            this.handleDelimiters(node, childNode, childTrim, attrmap);
         } else if (w3cNode instanceof Text) {
            Text data = (Text)w3cNode;
            buffer.append(data.getData());
         }
      }

      String text = determineValue(node, buffer.toString(), trimFlag);
      if (text.length() > 0 || !node.hasChildren() && node != this.getRoot()) {
         node.setValue(text);
      }

      return attributes;
   }

   private static String determineValue(ConfigurationNode node, String content, boolean trimFlag) {
      boolean shouldTrim = trimFlag || StringUtils.isBlank(content) && node.getChildrenCount() > 0;
      return shouldTrim ? content.trim() : content;
   }

   private Map processAttributes(HierarchicalConfiguration.Node node, Element element, boolean elemRefs) {
      NamedNodeMap attributes = element.getAttributes();
      Map attrmap = new HashMap();

      for(int i = 0; i < attributes.getLength(); ++i) {
         org.w3c.dom.Node w3cNode = attributes.item(i);
         if (w3cNode instanceof Attr) {
            Attr attr = (Attr)w3cNode;
            List values;
            if (this.isAttributeSplittingDisabled()) {
               values = Collections.singletonList(attr.getValue());
            } else {
               values = PropertyConverter.split(attr.getValue(), this.isDelimiterParsingDisabled() ? '|' : this.getListDelimiter());
            }

            this.appendAttributes(node, element, elemRefs, attr.getName(), values);
            attrmap.put(attr.getName(), values);
         }
      }

      return attrmap;
   }

   private void appendAttributes(HierarchicalConfiguration.Node node, Element element, boolean elemRefs, String attr, Collection values) {
      Iterator i$ = values.iterator();

      while(i$.hasNext()) {
         String value = (String)i$.next();
         HierarchicalConfiguration.Node child = new XMLConfiguration.XMLNode(attr, elemRefs ? element : null);
         child.setValue(value);
         node.addAttribute(child);
      }

   }

   private void handleDelimiters(HierarchicalConfiguration.Node parent, HierarchicalConfiguration.Node child, boolean trim, Map attrmap) {
      if (child.getValue() != null) {
         Object values;
         if (this.isDelimiterParsingDisabled()) {
            values = new ArrayList();
            ((List)values).add(child.getValue().toString());
         } else {
            values = PropertyConverter.split(child.getValue().toString(), this.getListDelimiter(), trim);
         }

         if (((List)values).size() > 1) {
            Iterator it = ((List)values).iterator();
            HierarchicalConfiguration.Node c = this.createNode(child.getName());
            c.setValue(it.next());
            Iterator i$ = child.getAttributes().iterator();

            while(i$.hasNext()) {
               ConfigurationNode ndAttr = (ConfigurationNode)i$.next();
               ndAttr.setReference((Object)null);
               c.addAttribute(ndAttr);
            }

            parent.remove(child);
            parent.addChild(c);

            while(it.hasNext()) {
               HierarchicalConfiguration.Node c = new XMLConfiguration.XMLNode(child.getName(), (Element)null);
               c.setValue(it.next());
               i$ = attrmap.entrySet().iterator();

               while(i$.hasNext()) {
                  Entry e = (Entry)i$.next();
                  this.appendAttributes(c, (Element)null, false, (String)e.getKey(), (Collection)e.getValue());
               }

               parent.addChild(c);
            }
         } else if (((List)values).size() == 1) {
            child.setValue(((List)values).get(0));
         }
      }

   }

   private boolean shouldTrim(Element element, boolean currentTrim) {
      Attr attr = element.getAttributeNode("xml:space");
      if (attr == null) {
         return currentTrim;
      } else {
         return !"preserve".equals(attr.getValue());
      }
   }

   protected DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
      if (this.getDocumentBuilder() != null) {
         return this.getDocumentBuilder();
      } else {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         if (this.isValidating()) {
            factory.setValidating(true);
            if (this.isSchemaValidation()) {
               factory.setNamespaceAware(true);
               factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            }
         }

         DocumentBuilder result = factory.newDocumentBuilder();
         result.setEntityResolver(this.entityResolver);
         if (this.isValidating()) {
            result.setErrorHandler(new DefaultHandler() {
               public void error(SAXParseException ex) throws SAXException {
                  throw ex;
               }
            });
         }

         return result;
      }
   }

   protected Document createDocument() throws ConfigurationException {
      try {
         if (this.document == null) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document newDocument = builder.newDocument();
            Element rootElem = newDocument.createElement(this.getRootElementName());
            newDocument.appendChild(rootElem);
            this.document = newDocument;
         }

         XMLConfiguration.XMLBuilderVisitor builder = new XMLConfiguration.XMLBuilderVisitor(this.document, this.isDelimiterParsingDisabled() ? '\u0000' : this.getListDelimiter(), this.isAttributeSplittingDisabled());
         builder.processDocument(this.getRoot());
         this.initRootElementText(this.document, this.getRootNode().getValue());
         return this.document;
      } catch (DOMException var4) {
         throw new ConfigurationException(var4);
      } catch (ParserConfigurationException var5) {
         throw new ConfigurationException(var5);
      }
   }

   private void initRootElementText(Document doc, Object value) {
      Element elem = doc.getDocumentElement();
      NodeList children = elem.getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         org.w3c.dom.Node nd = children.item(i);
         if (nd.getNodeType() == 3) {
            elem.removeChild(nd);
         }
      }

      if (value != null) {
         elem.appendChild(doc.createTextNode(String.valueOf(value)));
      }

   }

   protected HierarchicalConfiguration.Node createNode(String name) {
      return new XMLConfiguration.XMLNode(name, (Element)null);
   }

   public void load(InputStream in) throws ConfigurationException {
      this.load(new InputSource(in));
   }

   public void load(Reader in) throws ConfigurationException {
      this.load(new InputSource(in));
   }

   private void load(InputSource source) throws ConfigurationException {
      try {
         URL sourceURL = this.getDelegate().getURL();
         if (sourceURL != null) {
            source.setSystemId(sourceURL.toString());
         }

         DocumentBuilder builder = this.createDocumentBuilder();
         Document newDocument = builder.parse(source);
         Document oldDocument = this.document;
         this.document = null;
         this.initProperties(newDocument, oldDocument == null);
         this.document = oldDocument == null ? newDocument : oldDocument;
      } catch (SAXParseException var6) {
         throw new ConfigurationException("Error parsing " + source.getSystemId(), var6);
      } catch (Exception var7) {
         this.getLogger().debug("Unable to load the configuraton", var7);
         throw new ConfigurationException("Unable to load the configuration", var7);
      }
   }

   public void save(Writer writer) throws ConfigurationException {
      try {
         Transformer transformer = this.createTransformer();
         Source source = new DOMSource(this.createDocument());
         Result result = new StreamResult(writer);
         transformer.transform(source, result);
      } catch (TransformerException var5) {
         throw new ConfigurationException("Unable to save the configuration", var5);
      } catch (TransformerFactoryConfigurationError var6) {
         throw new ConfigurationException("Unable to save the configuration", var6);
      }
   }

   public void validate() throws ConfigurationException {
      try {
         Transformer transformer = this.createTransformer();
         Source source = new DOMSource(this.createDocument());
         StringWriter writer = new StringWriter();
         Result result = new StreamResult(writer);
         transformer.transform(source, result);
         Reader reader = new StringReader(writer.getBuffer().toString());
         DocumentBuilder builder = this.createDocumentBuilder();
         builder.parse(new InputSource(reader));
      } catch (SAXException var7) {
         throw new ConfigurationException("Validation failed", var7);
      } catch (IOException var8) {
         throw new ConfigurationException("Validation failed", var8);
      } catch (TransformerException var9) {
         throw new ConfigurationException("Validation failed", var9);
      } catch (ParserConfigurationException var10) {
         throw new ConfigurationException("Validation failed", var10);
      }
   }

   protected Transformer createTransformer() throws TransformerException {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty("indent", "yes");
      if (this.getEncoding() != null) {
         transformer.setOutputProperty("encoding", this.getEncoding());
      }

      if (this.getPublicID() != null) {
         transformer.setOutputProperty("doctype-public", this.getPublicID());
      }

      if (this.getSystemID() != null) {
         transformer.setOutputProperty("doctype-system", this.getSystemID());
      }

      return transformer;
   }

   public Object clone() {
      XMLConfiguration copy = (XMLConfiguration)super.clone();
      copy.document = null;
      copy.setDelegate(copy.createDelegate());
      clearReferences(copy.getRootNode());
      return copy;
   }

   protected AbstractHierarchicalFileConfiguration.FileConfigurationDelegate createDelegate() {
      return new XMLConfiguration.XMLFileConfigurationDelegate();
   }

   public void addNodes(String key, Collection nodes) {
      if (nodes != null && !nodes.isEmpty()) {
         Collection xmlNodes = new ArrayList(nodes.size());
         Iterator i$ = nodes.iterator();

         while(i$.hasNext()) {
            ConfigurationNode node = (ConfigurationNode)i$.next();
            xmlNodes.add(this.convertToXMLNode(node));
         }

         super.addNodes(key, xmlNodes);
      } else {
         super.addNodes(key, nodes);
      }

   }

   private XMLConfiguration.XMLNode convertToXMLNode(ConfigurationNode node) {
      if (node instanceof XMLConfiguration.XMLNode) {
         return (XMLConfiguration.XMLNode)node;
      } else {
         XMLConfiguration.XMLNode nd = (XMLConfiguration.XMLNode)this.createNode(node.getName());
         nd.setValue(node.getValue());
         nd.setAttribute(node.isAttribute());
         Iterator i$ = node.getChildren().iterator();

         ConfigurationNode attr;
         while(i$.hasNext()) {
            attr = (ConfigurationNode)i$.next();
            nd.addChild(this.convertToXMLNode(attr));
         }

         i$ = node.getAttributes().iterator();

         while(i$.hasNext()) {
            attr = (ConfigurationNode)i$.next();
            nd.addAttribute(this.convertToXMLNode(attr));
         }

         return nd;
      }
   }

   public void registerEntityId(String publicId, URL entityURL) {
      if (this.entityResolver instanceof EntityRegistry) {
         ((EntityRegistry)this.entityResolver).registerEntityId(publicId, entityURL);
      }

   }

   /** @deprecated */
   @Deprecated
   public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
      try {
         return this.entityResolver.resolveEntity(publicId, systemId);
      } catch (IOException var4) {
         throw new SAXException(var4);
      }
   }

   public Map getRegisteredEntities() {
      return (Map)(this.entityResolver instanceof EntityRegistry ? ((EntityRegistry)this.entityResolver).getRegisteredEntities() : new HashMap());
   }

   private class XMLFileConfigurationDelegate extends AbstractHierarchicalFileConfiguration.FileConfigurationDelegate {
      private XMLFileConfigurationDelegate() {
         super();
      }

      public void load(InputStream in) throws ConfigurationException {
         XMLConfiguration.this.load(in);
      }

      // $FF: synthetic method
      XMLFileConfigurationDelegate(Object x1) {
         this();
      }
   }

   static class XMLBuilderVisitor extends HierarchicalConfiguration.BuilderVisitor {
      private Document document;
      private final char listDelimiter;
      private boolean isAttributeSplittingDisabled;

      public XMLBuilderVisitor(Document doc, char listDelimiter, boolean isAttributeSplittingDisabled) {
         this.document = doc;
         this.listDelimiter = listDelimiter;
         this.isAttributeSplittingDisabled = isAttributeSplittingDisabled;
      }

      public void processDocument(HierarchicalConfiguration.Node rootNode) {
         rootNode.visit(this, (ConfigurationKey)null);
      }

      protected Object insert(HierarchicalConfiguration.Node newNode, HierarchicalConfiguration.Node parent, HierarchicalConfiguration.Node sibling1, HierarchicalConfiguration.Node sibling2) {
         if (newNode.isAttribute()) {
            updateAttribute(parent, this.getElement(parent), newNode.getName(), this.listDelimiter, this.isAttributeSplittingDisabled);
            return null;
         } else {
            Element elem = this.document.createElement(newNode.getName());
            if (newNode.getValue() != null) {
               String txt = newNode.getValue().toString();
               if (this.listDelimiter != 0) {
                  txt = PropertyConverter.escapeListDelimiter(txt, this.listDelimiter);
               }

               elem.appendChild(this.document.createTextNode(txt));
            }

            if (sibling2 == null) {
               this.getElement(parent).appendChild(elem);
            } else if (sibling1 != null) {
               this.getElement(parent).insertBefore(elem, this.getElement(sibling1).getNextSibling());
            } else {
               this.getElement(parent).insertBefore(elem, this.getElement(parent).getFirstChild());
            }

            return elem;
         }
      }

      private static void updateAttribute(HierarchicalConfiguration.Node node, Element elem, String name, char listDelimiter, boolean isAttributeSplittingDisabled) {
         if (node != null && elem != null) {
            boolean hasAttribute = false;
            List attrs = node.getAttributes(name);
            StringBuilder buf = new StringBuilder();
            char delimiter = listDelimiter != 0 ? listDelimiter : 124;

            ConfigurationNode attr;
            for(Iterator i$ = attrs.iterator(); i$.hasNext(); attr.setReference(elem)) {
               attr = (ConfigurationNode)i$.next();
               if (attr.getValue() != null) {
                  hasAttribute = true;
                  if (buf.length() > 0) {
                     buf.append(delimiter);
                  }

                  String value = isAttributeSplittingDisabled ? attr.getValue().toString() : PropertyConverter.escapeDelimiters(attr.getValue().toString(), delimiter);
                  buf.append(value);
               }
            }

            if (!hasAttribute) {
               elem.removeAttribute(name);
            } else {
               elem.setAttribute(name, buf.toString());
            }
         }

      }

      static void updateAttribute(HierarchicalConfiguration.Node node, String name, char listDelimiter, boolean isAttributeSplittingDisabled) {
         if (node != null) {
            updateAttribute(node, (Element)node.getReference(), name, listDelimiter, isAttributeSplittingDisabled);
         }

      }

      private Element getElement(HierarchicalConfiguration.Node node) {
         return node.getName() != null && node.getReference() != null ? (Element)node.getReference() : this.document.getDocumentElement();
      }
   }

   class XMLNode extends HierarchicalConfiguration.Node {
      private static final long serialVersionUID = -4133988932174596562L;

      public XMLNode(String name, Element elem) {
         super(name);
         this.setReference(elem);
      }

      public void setValue(Object value) {
         super.setValue(value);
         if (this.getReference() != null && XMLConfiguration.this.document != null) {
            if (this.isAttribute()) {
               this.updateAttribute();
            } else {
               this.updateElement(value);
            }
         }

      }

      protected void removeReference() {
         if (this.getReference() != null) {
            Element element = (Element)this.getReference();
            if (this.isAttribute()) {
               this.updateAttribute();
            } else {
               org.w3c.dom.Node parentElem = element.getParentNode();
               if (parentElem != null) {
                  parentElem.removeChild(element);
               }
            }
         }

      }

      private void updateElement(Object value) {
         Text txtNode = this.findTextNodeForUpdate();
         if (value == null) {
            if (txtNode != null) {
               ((Element)this.getReference()).removeChild(txtNode);
            }
         } else {
            String newValue;
            if (txtNode == null) {
               newValue = XMLConfiguration.this.isDelimiterParsingDisabled() ? value.toString() : PropertyConverter.escapeDelimiters(value.toString(), XMLConfiguration.this.getListDelimiter());
               txtNode = XMLConfiguration.this.document.createTextNode(newValue);
               if (((Element)this.getReference()).getFirstChild() != null) {
                  ((Element)this.getReference()).insertBefore(txtNode, ((Element)this.getReference()).getFirstChild());
               } else {
                  ((Element)this.getReference()).appendChild(txtNode);
               }
            } else {
               newValue = XMLConfiguration.this.isDelimiterParsingDisabled() ? value.toString() : PropertyConverter.escapeDelimiters(value.toString(), XMLConfiguration.this.getListDelimiter());
               txtNode.setNodeValue(newValue);
            }
         }

      }

      private void updateAttribute() {
         XMLConfiguration.XMLBuilderVisitor.updateAttribute(this.getParent(), this.getName(), XMLConfiguration.this.getListDelimiter(), XMLConfiguration.this.isAttributeSplittingDisabled());
      }

      private Text findTextNodeForUpdate() {
         Text result = null;
         Element elem = (Element)this.getReference();
         NodeList children = elem.getChildNodes();
         Collection textNodes = new ArrayList();

         org.w3c.dom.Node tn;
         for(int i = 0; i < children.getLength(); ++i) {
            tn = children.item(i);
            if (tn instanceof Text) {
               if (result == null) {
                  result = (Text)tn;
               } else {
                  textNodes.add(tn);
               }
            }
         }

         if (result instanceof CDATASection) {
            textNodes.add(result);
            result = null;
         }

         Iterator i$ = textNodes.iterator();

         while(i$.hasNext()) {
            tn = (org.w3c.dom.Node)i$.next();
            elem.removeChild(tn);
         }

         return result;
      }
   }
}

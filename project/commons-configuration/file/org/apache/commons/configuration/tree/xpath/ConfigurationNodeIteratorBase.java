package org.apache.commons.configuration.tree.xpath;

import java.util.List;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

abstract class ConfigurationNodeIteratorBase implements NodeIterator {
   private NodePointer parent;
   private List subNodes;
   private int position;
   private int startOffset;
   private boolean reverse;

   protected ConfigurationNodeIteratorBase(NodePointer parent, boolean reverse) {
      this.parent = parent;
      this.reverse = reverse;
   }

   public int getPosition() {
      return this.position;
   }

   public boolean setPosition(int pos) {
      this.position = pos;
      return pos >= 1 && pos <= this.getMaxPosition();
   }

   public NodePointer getNodePointer() {
      return this.getPosition() < 1 && !this.setPosition(1) ? null : this.createNodePointer((ConfigurationNode)this.subNodes.get(this.positionToIndex(this.getPosition())));
   }

   protected NodePointer getParent() {
      return this.parent;
   }

   protected int getStartOffset() {
      return this.startOffset;
   }

   protected void setStartOffset(int startOffset) {
      this.startOffset = startOffset;
      if (this.reverse) {
         --this.startOffset;
      } else {
         ++this.startOffset;
      }

   }

   protected void initSubNodeList(List nodes) {
      this.subNodes = nodes;
      if (this.reverse) {
         this.setStartOffset(this.subNodes.size());
      }

   }

   protected int getMaxPosition() {
      return this.reverse ? this.getStartOffset() + 1 : this.subNodes.size() - this.getStartOffset();
   }

   protected NodePointer createNodePointer(ConfigurationNode node) {
      return new ConfigurationNodePointer(this.getParent(), node);
   }

   protected int positionToIndex(int pos) {
      return (this.reverse ? 1 - pos : pos - 1) + this.getStartOffset();
   }
}

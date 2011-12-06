// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DOM node.
 */
public abstract class Node {

	/**
	 * Node types.
	 */
	public enum NodeType {
		DOCUMENT, ELEMENT, TEXT, COMMENT, CDATA, DOCUMENT_TYPE, XML_DECLARATION
	}

	protected final String nodeName;
	protected final NodeType nodeType;
	protected final boolean caseSensitive;
	protected String nodeValue;

	protected Node parentNode;
	protected List<Attribute> attributes;

	protected List<Node> childNodes;
	protected int childElementNodesCount;
	protected Node[] childElementNodes;

	protected int siblingIndex;
	protected int siblingElementIndex = -1;
	protected int siblingNameIndex = -1;

	protected int deepLevel;

	protected Node(NodeType nodeType, String nodeName, boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		this.nodeName = caseSensitive ? nodeName : nodeName.toLowerCase();
		this.nodeType = nodeType;
	}

	// ---------------------------------------------------------------- basic

	/**
	 * Returns {@link NodeType node type}.
	 */
	public NodeType getNodeType() {
		return nodeType;
	}

	/**
	 * Returns node name or <code>null</code> if name is not available.
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * Returns node value or <code>null</code> if value is not available.
	 */
	public String getNodeValue() {
		return nodeValue;
	}

	/**
	 * Sets node value.
	 */
	public void setNodeValue(String value) {
		this.nodeValue = value;
	}

	// ---------------------------------------------------------------- tree

	/**
	 * Removes this node from DOM tree.
	 */
	public void detachFromParent() {
		if (parentNode == null) {
			return;
		}
		int index = siblingIndex;
		if (parentNode.childNodes != null) {
			parentNode.childNodes.remove(index);
			parentNode.reindexChildren();
		}
		parentNode = null;
		deepLevel = 0;
	}

	/**
	 * Appends child node.
	 */
	public void appendChild(Node node) {
		node.detachFromParent();
		node.parentNode = this;
		node.deepLevel = deepLevel + 1;
		initChildNodes();
		childNodes.add(node);
		reindexChildren();
	}

	/**
	 * Inserts node at given index.
	 */
	public void insertChild(Node node, int index) {
		node.detachFromParent();
		node.parentNode = this;
		node.deepLevel = deepLevel + 1;
		try {
			initChildNodes();
			childNodes.add(index, node);
		} catch (IndexOutOfBoundsException ioobex) {
			throw new LagartoDOMException("Invalid node index: " + index);
		}
		reindexChildren();
	}

	/**
	 * Inserts node before provided node.
	 */
	public void insertBefore(Node newChild, Node refChild) {
		int siblingIndex = refChild.getSiblingIndex();
		refChild.parentNode.insertChild(newChild, siblingIndex);
	}

	/**
	 * Inserts node after provided node.
	 */
	public void insertAfter(Node newChild, Node refChild) {
		int siblingIndex = refChild.getSiblingIndex() + 1;
		if (siblingIndex == refChild.parentNode.getChildNodesCount()) {
			refChild.parentNode.appendChild(newChild);
		} else {
			refChild.parentNode.insertChild(newChild, siblingIndex);
		}
	}

	/**
	 * Removes child node at given index.
	 * Returns removed node or <code>null</code> if index is invalid.
	 */
	public Node removeChild(int index) {
		if (childNodes == null) {
			return null;
		}
		Node node;
		try {
			node = childNodes.get(index);
		} catch (IndexOutOfBoundsException ioobex) {
			return null;
		}
		node.detachFromParent();
		return node;
	}

	/**
	 * Removes child node. It works only with direct children, i.e.
	 * if provided child node is not a child nothing happens.
	 */
	public void removeChild(Node childNode) {
		if (childNode.getParentNode() != this) {
			return;
		}
		childNode.detachFromParent();
	}

	/**
	 * Removes all child nodes.
	 */
	public void removeChilds() {
		childNodes = null;
		childElementNodes = null;
		childElementNodesCount = 0;
	}

	/**
	 * Returns parent node or <code>null</code> if no parent exist.
	 */
	public Node getParentNode() {
		return parentNode;
	}

	// ---------------------------------------------------------------- attributes

	/**
	 * Returns <code>true</code> if node has attributes.
	 */
	public boolean hasAttributes() {
		if (attributes == null) {
			return false;
		}
		return !attributes.isEmpty();
	}

	/**
	 * Returns total number of attributes.
	 */
	public int getAttributesCount() {
		if (attributes == null) {
			return 0;
		}
		return attributes.size();
	}

	/**
	 * Returns attribute at given index or <code>null</code> if index not found.
	 */
	public Attribute getAttribute(int index) {
		if (attributes == null) {
			return null;
		}
		if ((index < 0) || (index >= attributes.size())) {
			return null;
		}
		return attributes.get(index);
	}

	/**
	 * Returns <code>true</code> if node contains an attribute.
	 */
	public boolean hasAttribute(String name) {
		if (attributes == null) {
			return false;
		}
		int nameHash = name.hashCode();
		for (Attribute attr : attributes) {
			if (attr.equalsName(name, nameHash)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns attribute value. Returns <code>null</code> when
	 * attribute doesn't exist or when attribute exist but doesn't
	 * specify a value.
	 */
	public String getAttribute(String name) {
		if (attributes == null) {
			return null;
		}
		int nameHash = name.hashCode();
		for (Attribute attr : attributes) {
			if (attr.equalsName(name, nameHash)) {
				return attr.getValue();
			}
		}
		return null;
	}

	/**
	 * Sets attribute value. Value may be <code>null</code>.
	 */
	public void setAttribute(String name, String value) {
		initAttributes();

		if (!caseSensitive) {
			name = name.toLowerCase();
		}

		// search if attribute with the same name exist
		int nameHash = name.hashCode();
		for (Attribute attr : attributes) {
			if (attr.equalsName(name, nameHash)) {
				attr.setValue(value);
				return;
			}
		}
		attributes.add(new Attribute(name, value, true));
	}

	/**
	 * Sets attribute that doesn't need a value.
	 */
	public void setAttribute(String name) {
		setAttribute(name, null);
	}

	// ---------------------------------------------------------------- children count

	/**
	 * Returns <code>true</code> if node has child nodes.
	 */
	public boolean hasChildNodes() {
		if (childNodes == null) {
			return false;
		}
		return !childNodes.isEmpty();
	}

	/**
	 * Returns number of all child nodes.
	 */
	public int getChildNodesCount() {
		if (childNodes == null) {
			return 0;
		}
		return childNodes.size();
	}

	/**
	 * Returns number of child <b>elements</b>.
	 */
	public int getChildElementsCount() {
		return childElementNodesCount;
	}

	/**
	 * Returns number of child <b>elements</b> with given name.
	 */
	public int getChildElementsCount(String elementName) {
		Node lastChild = getLastChildElement(elementName);
		return lastChild.siblingNameIndex + 1;
	}

	// ---------------------------------------------------------------- children

	/**
	 * Returns an array of all children nodes. Returns an empty array
	 * if there are no children.
	 */
	public Node[] getChildNodes() {
		if (childNodes == null) {
			return new Node[0];
		}
		return childNodes.toArray(new Node[childNodes.size()]);
	}

	/**
	 * Returns an array of all children elements.
	 */
	public Node[] getChildElements() {
		initChildElementNodes();
		return childElementNodes.clone();
	}

	/**
	 * Returns a child node at given index or <code>null</code>
	 * if child doesn't exist for that index.
	 */
	public Node getChild(int index) {
		if (childNodes == null) {
			return null;
		}
		if ((index < 0) || (index >= childNodes.size())) {
			return null;
		}
		return childNodes.get(index);
	}

	/**
	 * Returns a child element node at given index.
	 */
	public Node getChildElement(int index) {
		initChildElementNodes();
		if ((index < 0) || (index >= childElementNodes.length)) {
			return null;
		}
		return childElementNodes[index];
	}

	// ---------------------------------------------------------------- first child

	/**
	 * Returns first child or <code>null</code> if no children exist.
	 */
	public Node getFirstChild() {
		if (childNodes == null) {
			return null;
		}
		if (childNodes.isEmpty()) {
			return null;
		}
		return childNodes.get(0);
	}

	/**
	 * Returns first child <b>element</b> node or
	 * <code>null</code> if no element children exist.
	 */
	public Node getFirstChildElement() {
		initChildElementNodes();
		if (childElementNodes.length == 0) {
			return null;
		}
		return childElementNodes[0];
	}

	/**
	 * Returns first child <b>element</b> with given name or
	 * <code>null</code> if no such children exist.
	 */
	public Node getFirstChildElement(String elementName) {
		if (childNodes == null) {
			return null;
		}
		for (Node child : childNodes) {
			if (elementName.equals(child.getNodeName())) {
				child.initSiblingNames();
				return child;
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- last child

	/**
	 * Returns last child or <code>null</code> if no children exist.
	 */
	public Node getLastChild() {
		if (childNodes == null) {
			return null;
		}
		if (childNodes.isEmpty()) {
			return null;
		}
		return childNodes.get(getChildNodesCount() - 1);
	}

	/**
	 * Returns last child <b>element</b> node or
	 * <code>null</code> if no such child node exist.
	 */
	public Node getLastChildElement() {
		initChildElementNodes();
		if (childElementNodes.length == 0) {
			return null;
		}
		return childElementNodes[childElementNodes.length - 1];
	}

	/**
	 * Returns last child <b>element</b> with given name or
	 * <code>null</code> if no such child node exist.
	 */
	public Node getLastChildElement(String elementName) {
		if (childNodes == null) {
			return null;
		}
		int from = childNodes.size() - 1;
		for (int i = from; i >= 0; i--) {
			Node child = childNodes.get(i);
			if (elementName.equals(child.getNodeName())) {
				child.initSiblingNames();
				return child;
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- internal

	/**
	 * Reindex children nodes. Must be called on every children addition/removal.
	 */
	protected void reindexChildren() {
		int siblingElementIndex = 0;
		for (int i = 0; i < childNodes.size(); i++) {
			Node childNode = childNodes.get(i);
			childNode.siblingIndex = i;
			childNode.siblingNameIndex = -1;	// reset sibling name info
			if (childNode.getNodeType() == NodeType.ELEMENT) {
				childNode.siblingElementIndex = siblingElementIndex;
				siblingElementIndex++;
			}
		}
		childElementNodesCount = siblingElementIndex;
		childElementNodes = null;	// reset child element nodes
	}

	/**
	 * Initializes list of child elements.
	 */
	protected void initChildElementNodes() {
		if (childElementNodes == null) {
			childElementNodes = new Node[childElementNodesCount];
			int childCount = getChildNodesCount();
			for (int i = 0; i < childCount; i++) {
				Node child = getChild(i);
				if (child.siblingElementIndex >= 0) {
					childElementNodes[child.siblingElementIndex] = child;
				}
			}
		}
	}

	/**
	 * Initializes siblings elements of the same name.
	 */
	protected void initSiblingNames() {
		if (siblingNameIndex == -1) {
			List<Node> siblings = parentNode.childNodes;
			int index = 0;
			for (Node sibling : siblings) {
				if (sibling.siblingNameIndex == -1
						&& nodeType == NodeType.ELEMENT
						&& nodeName.equals(sibling.getNodeName())) {
					sibling.siblingNameIndex = index++;
				}
			}
		}
	}

	/**
	 * Initializes attributes when needed.
	 */
	protected void initAttributes() {
		if (attributes == null) {
			attributes = new ArrayList<Attribute>();
		}
	}

	/**
	 * Initializes child nodes list when needed.
	 */
	protected void initChildNodes() {
		if (childNodes == null) {
			childNodes = new ArrayList<Node>();
		}
	}

	// ---------------------------------------------------------------- siblings index

	/**
	 * Get the list index of this node in its node sibling list.
	 * For example, if this is the first node sibling, returns 0.
	 * Index address all nodes, i.e. of all node types.
	 */
	public int getSiblingIndex() {
		return siblingIndex;
	}

	public int getSiblingElementIndex() {
		return siblingElementIndex;
	}

	public int getSiblingNameIndex() {
		initSiblingNames();
		return siblingNameIndex;
	}

	// ---------------------------------------------------------------- next

	/**
	 * Returns this node's next sibling of <b>any</b> type or
	 * <code>null</code> if this is the last sibling.
	 */
	public Node getNextSibling() {
		List<Node> siblings = parentNode.childNodes;
		int index = siblingIndex + 1;
		if (index >= siblings.size()) {
			return null;
		}
		return siblings.get(index);
	}

	/**
	 * Returns this node's next <b>element</b>.
	 */
	public Node getNextSiblingElement() {
		parentNode.initChildElementNodes();
		if (siblingElementIndex == -1) {
			int max = parentNode.getChildNodesCount();
			for (int i = siblingIndex; i < max; i++) {
				Node sibling = parentNode.childNodes.get(i);
				if (sibling.getNodeType() == NodeType.ELEMENT) {
					return sibling;
				}
			}
			return null;
		}
		int index = siblingElementIndex + 1;
		if (index >= parentNode.childElementNodesCount) {
			return null;
		}
		return parentNode.childElementNodes[index];
	}

	/**
	 * Returns this node's next <b>element</b> with the same name.
	 */
	public Node getNextSiblingName() {
		if (nodeName == null) {
			return null;
		}
		initSiblingNames();
		int index = siblingNameIndex + 1;
		int max = parentNode.getChildNodesCount();
		for (int i = siblingIndex + 1; i < max; i++) {
			Node sibling = parentNode.childNodes.get(i);
			if ((index == sibling.siblingNameIndex) && nodeName.equals(sibling.getNodeName())) {
				return sibling;
			}
		}
		return null;
	}


	// ---------------------------------------------------------------- prev

	/**
	 * Returns this node's previous sibling of <b>any</b> type
	 * or <code>null</code> if this is the first sibling.
	 */
	public Node getPreviousSibling() {
		List<Node> siblings = parentNode.childNodes;
		int index = siblingIndex - 1;
		if (index < 0) {
			return null;
		}
		return siblings.get(index);

	}

	/**
	 * Returns this node's previous sibling of <b>element</b> type
	 * or <code>null</code> if this is the first sibling.
	 */
	public Node getPreviousSiblingElement() {
		parentNode.initChildElementNodes();
		if (siblingElementIndex == -1) {
			for (int i = siblingIndex - 1; i >= 0; i--) {
				Node sibling = parentNode.childNodes.get(i);
				if (sibling.getNodeType() == NodeType.ELEMENT) {
					return sibling;
				}
			}
			return null;
		}
		int index = siblingElementIndex -1;
		if (index < 0) {
			return null;
		}
		return parentNode.childElementNodes[index];
	}

	/**
	 * Returns this node's previous sibling element with the same name.
	 */
	public Node getPreviousSiblingName() {
		if (nodeName == null) {
			return null;
		}
		initSiblingNames();
		int index = siblingNameIndex -1;
		for (int i = siblingIndex; i >= 0; i--) {
			Node sibling = parentNode.childNodes.get(i);
			if ((index == sibling.siblingNameIndex) && nodeName.equals(sibling.getNodeName())) {
				return sibling;
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- html

	/**
	 * Returns the text content of this node and its descendants.
	 */
	public String getTextContent() {
		StringBuilder sb = new StringBuilder(getChildNodesCount() + 1);
		if ((nodeValue != null) && (nodeType != NodeType.COMMENT)) {
			sb.append(nodeValue);
		}
		if (childNodes != null) {
			for (Node childNode : childNodes) {
				sb.append(childNode.getTextContent());
			}
		}
		return sb.toString();
	}

	/**
	 * Generates inner HTML.
	 */
	public String getInnerHtml() {
		StringBuilder sb = new StringBuilder();
		try {
			toHtml(sb);
		} catch (IOException ioex) {
			throw new LagartoDOMException(ioex);
		}
		return sb.toString();
	}

	/**
	 * Generates HTML by appending it to the provided <code>Appendable</code>.
	 */
	public void toHtml(Appendable appendable) throws IOException {
		if (nodeValue != null) {
			appendable.append(nodeValue);
		}
		writeChildNodesAsHtml(appendable);
	}

	protected void writeChildNodesAsHtml(Appendable appendable) throws IOException {
		if (childNodes != null) {
			for (Node childNode : childNodes) {
				childNode.toHtml(appendable);
			}
		}
	}

	// ---------------------------------------------------------------- deep

	/**
	 * Returns deep level.
	 */
	public int getDeepLevel() {
		return deepLevel;
	}

	/**
	 * Returns CSS path to this node from document root.
	 */
	public String getCssPath() {
		StringBuilder path = new StringBuilder();

		Node node = this;
		while (node != null) {
			String nodeName = node.getNodeName();
			if (nodeName != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(' ');
				sb.append(nodeName);
				String id = node.getAttribute("id");
				if (id != null) {
					sb.append('#').append(id);
				}
				path.insert(0, sb);
			}
			node = node.getParentNode();
		}

		if (path.charAt(0) == ' ') {
			return path.substring(1);
		}
		return path.toString();
	}

}
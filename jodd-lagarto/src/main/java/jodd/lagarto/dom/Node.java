// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.lagarto.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DOM node.
 */
@SuppressWarnings({"ForLoopReplaceableByForEach", "ClassReferencesSubclass"})
public abstract class Node implements Cloneable {

	/**
	 * Node types.
	 */
	public enum NodeType {
		DOCUMENT, ELEMENT, TEXT, COMMENT, CDATA, DOCUMENT_TYPE, XML_DECLARATION
	}

	// node values

	protected final String nodeName;
	protected final String nodeRawName;
	protected final NodeType nodeType;
	protected Document ownerDocument;	// root document node
	protected String nodeValue;

	// attributes

	protected List<Attribute> attributes;

	// parent

	protected Node parentNode;

	// children

	protected List<Node> childNodes;
	protected int childElementNodesCount;
	protected Element[] childElementNodes;

	// siblings

	protected int siblingIndex;
	protected int siblingElementIndex = -1;
	protected int siblingNameIndex = -1;

	/**
	 * Creates new node.
	 */
	protected Node(Document document, NodeType nodeType, String nodeName) {
		this.ownerDocument = document;
		this.nodeRawName = nodeName;
		if (nodeName != null) {
			this.nodeName = ownerDocument.config.isCaseSensitive() ? nodeName : nodeName.toLowerCase();
		} else {
			this.nodeName = null;
		}
		this.nodeType = nodeType;
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Copies all non-final values to the empty cloned object.
	 * Cache-related values are not copied.
	 */
	protected <T extends Node> T cloneTo(T dest) {
//		dest.nodeValue = nodeValue;		// already  in clone implementations!
		dest.parentNode = parentNode;

		if (attributes != null) {
			dest.attributes = new ArrayList<>(attributes.size());
			for (int i = 0, attributesSize = attributes.size(); i < attributesSize; i++) {
				Attribute attr = attributes.get(i);
				dest.attributes.add(attr.clone());
			}
		}

		if (childNodes != null) {
			dest.childNodes = new ArrayList<>(childNodes.size());
			for (int i = 0, childNodesSize = childNodes.size(); i < childNodesSize; i++) {
				Node child = childNodes.get(i);
				Node childClone = child.clone();

				childClone.parentNode = dest;    // fix parent!
				dest.childNodes.add(childClone);
			}
		}

		return dest;
	}

	@Override
	public abstract Node clone();

	// ---------------------------------------------------------------- basic

	/**
	 * Returns {@link NodeType node type}.
	 */
	public NodeType getNodeType() {
		return nodeType;
	}

	/**
	 * Returns nodes name or <code>null</code> if name is not available.
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * Returns nodes raw name - exactly as it was given in the input.
	 */
	public String getNodeRawName() {
		return nodeRawName;
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

	/**
	 * Returns owner document, root node for this DOM tree.
	 */
	public Document getOwnerDocument() {
		return ownerDocument;
	}

	// ---------------------------------------------------------------- tree

	/**
	 * Removes this node from DOM tree.
	 */
	public void detachFromParent() {
		if (parentNode == null) {
			return;
		}
		if (parentNode.childNodes != null) {
			parentNode.childNodes.remove(siblingIndex);
			parentNode.reindexChildren();
		}
		parentNode = null;
	}

	/**
	 * Appends child node. Don't use this node in the loop,
	 * since it might be slow due to {@link #reindexChildren()}.
	 */
	public void addChild(Node node) {
		node.detachFromParent();
		node.parentNode = this;
		initChildNodes(node);
		childNodes.add(node);
		reindexChildrenOnAdd(1);
	}

	/**
	 * Appends several child nodes at once.
	 * Reindex is done only once, after all children are added.
	 */
	public void addChild(Node... nodes) {
		if (nodes.length == 0) {
			return;	// nothing to add
		}
		for (Node node : nodes) {
			node.detachFromParent();
			node.parentNode = this;
			initChildNodes(node);
			childNodes.add(node);
		}
		reindexChildrenOnAdd(nodes.length);
	}

	/**
	 * Inserts node at given index.
	 */
	public void insertChild(Node node, int index) {
		node.detachFromParent();
		node.parentNode = this;
		try {
			initChildNodes(node);
			childNodes.add(index, node);
		} catch (IndexOutOfBoundsException ignore) {
			throw new LagartoDOMException("Invalid node index: " + index);
		}
		reindexChildren();
	}

	/**
	 * Inserts several nodes at ones. Reindex is done onl once,
	 * after all children are added.
	 */
	public void insertChild(Node[] nodes, int index) {
		for (Node node : nodes) {
			node.detachFromParent();
			node.parentNode = this;
			try {
				initChildNodes(node);
				childNodes.add(index, node);
				index++;
			} catch (IndexOutOfBoundsException ignore) {
				throw new LagartoDOMException("Invalid node index: " + index);
			}
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
	 * Inserts several child nodes before provided node.
	 */
	public void insertBefore(Node[] newChilds, Node refChild) {
		if (newChilds.length == 0) {
			return;
		}
		int siblingIndex = refChild.getSiblingIndex();
		refChild.parentNode.insertChild(newChilds, siblingIndex);
	}

	/**
	 * Inserts node after provided node.
	 */
	public void insertAfter(Node newChild, Node refChild) {
		int siblingIndex = refChild.getSiblingIndex() + 1;
		if (siblingIndex == refChild.parentNode.getChildNodesCount()) {
			refChild.parentNode.addChild(newChild);
		} else {
			refChild.parentNode.insertChild(newChild, siblingIndex);
		}
	}

	/**
	 * Inserts several child nodes after referent node.
	 */
	public void insertAfter(Node[] newChilds, Node refChild) {
		if (newChilds.length == 0) {
			return;
		}

		int siblingIndex = refChild.getSiblingIndex() + 1;
		if (siblingIndex == refChild.parentNode.getChildNodesCount()) {
			refChild.parentNode.addChild(newChilds);
		} else {
			refChild.parentNode.insertChild(newChilds, siblingIndex);
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
		} catch (IndexOutOfBoundsException ignore) {
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
	 * Removes all child nodes. Each child node will be detached from this parent.
	 */
	public void removeAllChilds() {
		List<Node> removedNodes = childNodes;
		childNodes = null;
		childElementNodes = null;
		childElementNodesCount = 0;

		if (removedNodes != null) {
			for (int i = 0, removedNodesSize = removedNodes.size(); i < removedNodesSize; i++) {
				Node removedNode = removedNodes.get(i);
				removedNode.detachFromParent();
			}
		}
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
		if (!ownerDocument.config.isCaseSensitive()) {
			name = name.toLowerCase();
		}
		for (int i = 0, attributesSize = attributes.size(); i < attributesSize; i++) {
			Attribute attr = attributes.get(i);
			if (attr.getName().equals(name)) {
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
		Attribute attribute = getAttributeInstance(name);
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	protected Attribute getAttributeInstance(String name) {
		if (attributes == null) {
			return null;
		}

		if (!ownerDocument.config.isCaseSensitive()) {
			name = name.toLowerCase();
		}

		for (int i = 0, attributesSize = attributes.size(); i < attributesSize; i++) {
			Attribute attr = attributes.get(i);
			if (attr.getName().equals(name)) {
				return attr;
			}
		}
		return null;
	}

	protected int indexOfAttributeInstance(String name) {
		if (attributes == null) {
			return -1;
		}

		if (!ownerDocument.config.isCaseSensitive()) {
			name = name.toLowerCase();
		}

		for (int i = 0, attributesSize = attributes.size(); i < attributesSize; i++) {
			Attribute attr = attributes.get(i);
			if (attr.getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

	public boolean removeAttribute(String name) {
		int index = indexOfAttributeInstance(name);
		if (index == -1) {
			return false;
		}
		attributes.remove(index);
		return true;
	}

	/**
	 * Sets attribute value. Value may be <code>null</code>.
	 */
	public void setAttribute(String name, String value) {
		initAttributes();

		String rawAttributeName = name;
		if (!ownerDocument.config.isCaseSensitive()) {
			name = name.toLowerCase();
		}

		// search if attribute with the same name exist
		for (int i = 0, attributesSize = attributes.size(); i < attributesSize; i++) {
			Attribute attr = attributes.get(i);
			if (attr.getName().equals(name)) {
				attr.setValue(value);
				return;
			}
		}
		attributes.add(new Attribute(rawAttributeName, name, value));
	}

	/**
	 * Sets attribute that doesn't need a value.
	 */
	public void setAttribute(String name) {
		setAttribute(name, null);
	}

	/**
	 * Returns <code>true</code> if attribute containing some word.
	 */
	public boolean isAttributeContaining(String name, String word) {
		Attribute attr = getAttributeInstance(name);
		if (attr == null) {
			return false;
		}
		return attr.isContaining(word);
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
	public Element[] getChildElements() {
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
	 * Returns a child node with given hierarchy.
	 * Just a shortcut for successive calls of {@link #getChild(int)}.
	 */
	public Node getChild(int... indexes) {
		Node node = this;
		for (int index : indexes) {
			node = node.getChild(index);
		}
		return node;
	}

	/**
	 * Returns a child element node at given index.
	 * If index is out of bounds, <code>null</code> is returned.
	 */
	public Element getChildElement(int index) {
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
	public Element getFirstChildElement() {
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
	public Element getFirstChildElement(String elementName) {
		if (childNodes == null) {
			return null;
		}
		for (int i = 0, childNodesSize = childNodes.size(); i < childNodesSize; i++) {
			Node child = childNodes.get(i);
			if (child.getNodeType() == NodeType.ELEMENT && elementName.equals(child.getNodeName())) {
				child.initSiblingNames();
				return (Element) child;
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
	public Element getLastChildElement() {
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
	public Element getLastChildElement(String elementName) {
		if (childNodes == null) {
			return null;
		}
		int from = childNodes.size() - 1;
		for (int i = from; i >= 0; i--) {
			Node child = childNodes.get(i);
			if (child.getNodeType() == NodeType.ELEMENT && elementName.equals(child.getNodeName())) {
				child.initSiblingNames();
				return (Element) child;
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- internal

	/**
	 * Checks the health of child nodes. Useful during complex tree manipulation,
	 * to check if everything is OK. Not optimized for speed, should be used just
	 * for testing purposes.
	 */
	public boolean check() {

		if (childNodes == null) {
			return true;
		}

		// children
		int siblingElementIndex = 0;
		for (int i = 0, childNodesSize = childNodes.size(); i < childNodesSize; i++) {
			Node childNode = childNodes.get(i);

			if (childNode.siblingIndex != i) {
				return false;
			}

			if (childNode.getNodeType() == NodeType.ELEMENT) {
				if (childNode.siblingElementIndex != siblingElementIndex) {
					return false;
				}
				siblingElementIndex++;
			}
		}

		if (childElementNodesCount != siblingElementIndex) {
			return false;
		}

		// child element nodes
		if (childElementNodes != null) {
			if (childElementNodes.length != childElementNodesCount) {
				return false;
			}

			int childCount = getChildNodesCount();
			for (int i = 0; i < childCount; i++) {
				Node child = getChild(i);
				if (child.siblingElementIndex >= 0) {
					if (childElementNodes[child.siblingElementIndex] != child) {
						return false;
					}
				}
			}
		}

		// sibling names
		if (siblingNameIndex != -1) {
			List<Node> siblings = parentNode.childNodes;
			int index = 0;
			for (int i = 0, siblingsSize = siblings.size(); i < siblingsSize; i++) {
				Node sibling = siblings.get(i);
				if (sibling.siblingNameIndex == -1
						&& nodeType == NodeType.ELEMENT
						&& nodeName.equals(sibling.getNodeName())) {
					if (sibling.siblingNameIndex != index++) {
						return false;
					}
				}
			}
		}

		// process children
		for (Node childNode : childNodes) {
			if (!childNode.check()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reindex children nodes. Must be called on every children addition/removal.
	 * Iterates {@link #childNodes} list and:
	 * <ul>
	 * <li>calculates three different sibling indexes,</li>
	 * <li>calculates total child element node count,</li>
	 * <li>resets child element nodes array (will be init lazy later by @{#initChildElementNodes}.</li>
	 * </ul>
	 */
	protected void reindexChildren() {
		int siblingElementIndex = 0;
		for (int i = 0, childNodesSize = childNodes.size(); i < childNodesSize; i++) {
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
	 * Optimized variant of {@link #reindexChildren()} for addition.
	 * Only added children are optimized.
	 */
	protected void reindexChildrenOnAdd(int addedCount) {
		int childNodesSize = childNodes.size();
		int previousSize = childNodes.size() - addedCount;

		int siblingElementIndex = childElementNodesCount;
		for (int i = previousSize; i < childNodesSize; i++) {
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
			childElementNodes = new Element[childElementNodesCount];

			int childCount = getChildNodesCount();
			for (int i = 0; i < childCount; i++) {
				Node child = getChild(i);
				if (child.siblingElementIndex >= 0) {
					childElementNodes[child.siblingElementIndex] = (Element) child;
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
			for (int i = 0, siblingsSize = siblings.size(); i < siblingsSize; i++) {
				Node sibling = siblings.get(i);
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
			attributes = new ArrayList<>(5);
		}
	}

	/**
	 * Initializes child nodes list when needed.
	 * Also fix owner document for new node, if needed.
	 */
	protected void initChildNodes(Node newNode) {
		if (childNodes == null) {
			childNodes = new ArrayList<>();
		}
		if (ownerDocument != null) {
			if (newNode.ownerDocument != ownerDocument) {
				changeOwnerDocument(newNode, ownerDocument);
			}
		}
	}

	/**
	 * Changes owner document for given node and all its children.
	 */
	protected void changeOwnerDocument(Node node, Document ownerDocument) {
		node.ownerDocument = ownerDocument;

		int childCount = node.getChildNodesCount();
		for (int i = 0; i < childCount; i++) {
			Node child = node.getChild(i);
			changeOwnerDocument(child, ownerDocument);
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
		int index = siblingElementIndex - 1;
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
	 * @see #appendTextContent(Appendable)
	 */
	public String getTextContent() {
		StringBuilder sb = new StringBuilder(getChildNodesCount() + 1);
		appendTextContent(sb);
		return sb.toString();
	}

	/**
	 * Appends the text content to an <code>Appendable</code>
	 * (<code>StringBuilder</code>, <code>CharBuffer</code>...).
	 * This way we can reuse the <code>Appendable</code> instance
	 * during the creation of text content and have better performances.
	 */
	public void appendTextContent(Appendable appendable) {
		if (nodeValue != null) {
			if ((nodeType == NodeType.TEXT) || (nodeType == NodeType.CDATA)) {
				try {
					appendable.append(nodeValue);
				} catch (IOException ioex) {
					throw new LagartoDOMException(ioex);
				}
			}
		}
		if (childNodes != null) {
			for (int i = 0, childNodesSize = childNodes.size(); i < childNodesSize; i++) {
				Node childNode = childNodes.get(i);
				childNode.appendTextContent(appendable);
			}
		}
	}

	// ---------------------------------------------------------------- visit

	/**
	 * Generates HTML.
	 */
	public String getHtml() {
		LagartoDomBuilderConfig lagartoDomBuilderConfig;
		if (ownerDocument == null) {
			lagartoDomBuilderConfig = ((Document) this).getConfig();
		} else {
			lagartoDomBuilderConfig = ownerDocument.getConfig();
		}

		LagartoHtmlRenderer lagartoHtmlRenderer =
				lagartoDomBuilderConfig.getLagartoHtmlRenderer();

		return lagartoHtmlRenderer.toHtml(this, new StringBuilder());
	}

	/**
	 * Generates inner HTML.
	 */
	public String getInnerHtml() {
		LagartoDomBuilderConfig lagartoDomBuilderConfig;
		if (ownerDocument == null) {
			lagartoDomBuilderConfig = ((Document) this).getConfig();
		} else {
			lagartoDomBuilderConfig = ownerDocument.getConfig();
		}

		LagartoHtmlRenderer lagartoHtmlRenderer =
				lagartoDomBuilderConfig.getLagartoHtmlRenderer();

		return lagartoHtmlRenderer.toInnerHtml(this, new StringBuilder());
	}

	/**
	 * Visits the DOM tree.
	 */
	public void visit(NodeVisitor nodeVisitor) {
		visitNode(nodeVisitor);
	}

	/**
	 * Visits children nodes.
	 */
	protected void visitChildren(NodeVisitor nodeVisitor) {
		if (childNodes != null) {
			for (int i = 0, childNodesSize = childNodes.size(); i < childNodesSize; i++) {
				Node childNode = childNodes.get(i);
				childNode.visit(nodeVisitor);
			}
		}
	}

	/**
	 * Visits single node. Implementations just needs to call
	 * the correct visitor callback function.
	 */
	protected abstract void visitNode(NodeVisitor nodeVisitor);

	// ---------------------------------------------------------------- misc

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
				sb.append(' ').append(nodeName);
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
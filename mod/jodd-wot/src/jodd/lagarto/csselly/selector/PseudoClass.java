// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.csselly.CSSellyException;
import jodd.lagarto.dom.Node;

/**
 * Pseudo classes.
 */
public enum PseudoClass {

	/**
	 * Same as <code>:nth-child(1)</code>. Represents an element that is the first child of some other element.
	 */
	FIRST_CHILD {
		@Override
		public boolean match(Node node) {
			return node.getSiblingElementIndex() == 0;
		}
	},

	/**
	 * Same as <code>:nth-last-child(1)</code>. Represents an element that is the last child of some other element.
	 */
	LAST_CHILD {
		@Override
		public boolean match(Node node) {
			return node.getSiblingElementIndex() == node.getParentNode().getChildElementsCount() - 1;
		}
	},

	/**
	 * Represents an element that has a parent element and whose parent element has no other element children.
	 * Same as <code>:first-child:last-child</code> or <code>:nth-child(1):nth-last-child(1)</code>, but with
	 * a lower specificity.
	 */
	ONLY_CHILD {
		@Override
		public boolean match(Node node) {
			return (node.getSiblingElementIndex() == 0) && (node.getParentNode().getChildElementsCount() == 1);
		}
	},

	/**
	 * Same as <code>:nth-of-type(1)</code>. Represents an element that is the first sibling of its
	 * type in the list of children of its parent element.
	 */
	FIRST_OF_TYPE {
		@Override
		public boolean match(Node node) {
			return node.getSiblingNameIndex() == 0;
		}
	},

	/**
	 * Same as <code>:nth-last-of-type(1)</code>. Represents an element that is the last sibling of its
	 * type in the list of children of its parent element.
	 */
	LAST_OF_TYPE {
		@Override
		public boolean match(Node node) {
			return node.getNextSiblingName() == null;
		}
	},

	/**
	 * Represents an element that is the root of the document.
	 * In HTML 4, this is always the HTML element.
	 */
	ROOT {
		@Override
		public boolean match(Node node) {
			return node.getParentNode().getNodeType() == Node.NodeType.DOCUMENT;
		}
	},

	/**
	 * Represents an element that has no children at all.
	 */
	EMPTY {
		@Override
		public boolean match(Node node) {
			return node.getChildNodesCount() == 0;
		}
	},

	/**
	 * Represents an element that has a parent element and whose parent
	 * element has no other element children with the same expanded element
	 * name. Same as <code>:first-of-type:last-of-type</code> or
	 * <code>:nth-of-type(1):nth-last-of-type(1)</code>, but with a lower specificity.
	 */
	ONLY_OF_TYPE {
		@Override
		public boolean match(Node node) {
			return (node.getSiblingNameIndex() == 0) && (node.getNextSiblingName() == null);
		}
	}
	;

	/**
	 * Returns <code>true</code> if node matches the pseudoclass.
	 */
	public abstract boolean match(Node node);

	/**
	 * Resolves pseudo class from the name.
	 */
	public static PseudoClass valueOfName(String name) {
		name = name.toUpperCase();
		name = name.replace('-', '_');
		try {
			return valueOf(name);
		} catch (IllegalArgumentException iaex) {
			throw new CSSellyException("Invalid or unsupported pseudo class: " + name);
		}
	}

	/**
	 * Returns pseudo-class name.
	 */
	public String getPseudoClassName() {
		String name = name().toLowerCase();
		name = name.replace('_', '-');
		return name;
	}
}

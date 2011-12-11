// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.dom.Node;

import java.util.LinkedList;

/**
 * Pseudo classes.
 */
public abstract class PseudoClass {

	// ---------------------------------------------------------------- STANDARD PSEUDO CLASSES

	/**
	 * Same as <code>:nth-child(1)</code>. Represents an element that is the first child of some other element.
	 */
	public static class FIRST_CHILD extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getSiblingElementIndex() == 0;
		}
	}

	/**
	 * Same as <code>:nth-last-child(1)</code>. Represents an element that is the last child of some other element.
	 */
	public static class LAST_CHILD extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getSiblingElementIndex() == node.getParentNode().getChildElementsCount() - 1;
		}
	}

	/**
	 * Represents an element that has a parent element and whose parent element has no other element children.
	 * Same as <code>:first-child:last-child</code> or <code>:nth-child(1):nth-last-child(1)</code>, but with
	 * a lower specificity.
	 */
	public static class ONLY_CHILD extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return (node.getSiblingElementIndex() == 0) && (node.getParentNode().getChildElementsCount() == 1);
		}
	}

	/**
	 * Same as <code>:nth-of-type(1)</code>. Represents an element that is the first sibling of its
	 * type in the list of children of its parent element.
	 */
	public static class FIRST_OF_TYPE extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getSiblingNameIndex() == 0;
		}
	}

	/**
	 * Same as <code>:nth-last-of-type(1)</code>. Represents an element that is the last sibling of its
	 * type in the list of children of its parent element.
	 */
	public static class LAST_OF_TYPE extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getNextSiblingName() == null;
		}
	}

	/**
	 * Represents an element that is the root of the document.
	 * In HTML 4, this is always the HTML element.
	 */
	public static class ROOT extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getParentNode().getNodeType() == Node.NodeType.DOCUMENT;
		}
	}

	/**
	 * Represents an element that has no children at all.
	 */
	public static class EMPTY extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getChildNodesCount() == 0;
		}
	}

	/**
	 * Represents an element that has a parent element and whose parent
	 * element has no other element children with the same expanded element
	 * name. Same as <code>:first-of-type:last-of-type</code> or
	 * <code>:nth-of-type(1):nth-last-of-type(1)</code>, but with a lower specificity.
	 */
	public static class ONLY_OF_TYPE extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return (node.getSiblingNameIndex() == 0) && (node.getNextSiblingName() == null);
		}
	}

	// ---------------------------------------------------------------- CUSTOM PSEUDO CLASSES

	/**
	 * Selects the first matched element.
	 */
	public static class FIRST extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return true;
		}

		@Override
		public boolean match(LinkedList<Node> currentResults, Node node) {
			Node firstNode = currentResults.getFirst();
			if (firstNode == null) {
				return false;
			}
			return firstNode == node;
		}
	}

	/**
	 * Selects the last matched element. Note that <code>:last</code> selects
	 * a single element by filtering the current collection and matching the
	 * last element within it.
	 */
	public static class LAST extends PseudoClass {

		@Override
		public boolean match(Node node) {
			return true;
		}

		@Override
		public boolean match(LinkedList<Node> currentResults, Node node) {
			Node lastNode = currentResults.getLast();
			if (lastNode == null) {
				return false;
			}
			return lastNode == node;
		}
	}

	/**
	 * Selects all button elements and elements of type button.
	 */
	public static class BUTTON extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("button");
		}
	}


	// ---------------------------------------------------------------- interface

	/**
	 * Returns <code>true</code> if node matches the pseudoclass.
	 */
	public abstract boolean match(Node node);

	public boolean match(LinkedList<Node> currentResults, Node node) {
		return true;
	}

	/**
	 * Returns pseudo-class name from simple class name.
	 */
	public String getPseudoClassName() {
		String name = getClass().getSimpleName().toLowerCase();
		name = name.replace('_', '-');
		return name;
	}
}

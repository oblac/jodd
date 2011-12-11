// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.dom.Node;

/**
 * Pseudo functions.
 */
public abstract class PseudoFunction {

	/**
	 * The <code>:nth-child(an+b)</code> pseudo-class notation represents an element that has an+b-1
	 * siblings before it in the document tree, for any positive integer or zero value of n,
	 * and has a parent element. For values of a and b greater than zero, this effectively divides
	 * the element's children into groups of a elements (the last group taking the remainder),
	 * and selecting the bth element of each group. For example, this allows the selectors
	 * to address every other row in a table, and could be used to alternate the color of
	 * paragraph text in a cycle of four. The a and b values must be integers (positive, negative, or zero).
	 * The index of the first child of an element is 1.
	 */
	public static class NTH_CHILD extends PseudoFunction {
		@Override
		public int resolveValue(Node node) {
			return node.getSiblingNameIndex() + 1;
		}
	}

	/**
	 * The <code>:nth-last-child(an+b)</code> pseudo-class notation represents an element that has
	 * an+b-1 siblings after it in the document tree, for any positive integer or zero value
	 * of n, and has a parent element.
	 */
	public static class NTH_LAST_CHILD extends PseudoFunction {
		@Override
		public int resolveValue(Node node) {
			return node.getParentNode().getChildElementsCount() - node.getSiblingElementIndex();
		}
	}

	/**
	 * The <code>:nth-of-type(an+b)</code> pseudo-class notation represents an element that
	 * has an+b-1 siblings with the same expanded element name before it in the document tree,
	 * for any zero or positive integer value of n, and has a parent element.
	 */
	public static class NTH_OF_TYPE extends PseudoFunction {
		@Override
		public int resolveValue(Node node) {
			return node.getSiblingNameIndex() + 1;
		}
	}

	/**
	 * The <code>:nth-last-of-type(an+b)</code> pseudo-class notation represents an element
	 * that has an+b-1 siblings with the same expanded element name after it in the document tree,
	 * for any zero or positive integer value of n, and has a parent element.
	 */
	public static class NTH_LAST_OF_TYPE extends PseudoFunction {
		@Override
		public int resolveValue(Node node) {
			Node child = node.getParentNode().getLastChildElement(node.getNodeName());
			return child.getSiblingNameIndex() + 1 - node.getSiblingNameIndex();
		}
	}

	// ---------------------------------------------------------------- interface

	/**
	 * Returns a value for pseudo function matching.
	 */
	public abstract int resolveValue(Node node);

	/**
	 * Returns pseudo-function name.
	 */
	public String getPseudoFunctionName() {
		String name = getClass().getSimpleName().toLowerCase();
		name = name.replace('_', '-');
		return name;
	}

}

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

package jodd.csselly.selector;

import jodd.csselly.CSSelly;
import jodd.csselly.CssSelector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeMatcher;
import jodd.lagarto.dom.NodeSelector;
import jodd.util.StringUtil;

import java.util.List;

/**
 * Pseudo functions.
 */
public abstract class PseudoFunction<E> {
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
	public static class NTH_CHILD extends PseudoFunction<PseudoFunctionExpression> {

		@Override
		public PseudoFunctionExpression parseExpression(String expression) {
			return new PseudoFunctionExpression(expression);
		}

		@Override
		public boolean match(Node node, PseudoFunctionExpression expression) {
			int value = node.getSiblingElementIndex() + 1;

			return expression.match(value);
		}
	}

	/**
	 * The <code>:nth-last-child(an+b)</code> pseudo-class notation represents an element that has
	 * an+b-1 siblings after it in the document tree, for any positive integer or zero value
	 * of n, and has a parent element.
	 */
	public static class NTH_LAST_CHILD extends PseudoFunction<PseudoFunctionExpression> {

		@Override
		public PseudoFunctionExpression parseExpression(String expression) {
			return new PseudoFunctionExpression(expression);
		}

		@Override
		public boolean match(Node node, PseudoFunctionExpression expression) {
			int value = node.getParentNode().getChildElementsCount() - node.getSiblingElementIndex();

			return expression.match(value);
		}

	}

	/**
	 * The <code>:nth-of-type(an+b)</code> pseudo-class notation represents an element that
	 * has an+b-1 siblings with the same expanded element name before it in the document tree,
	 * for any zero or positive integer value of n, and has a parent element.
	 */
	public static class NTH_OF_TYPE extends PseudoFunction<PseudoFunctionExpression> {

		@Override
		public PseudoFunctionExpression parseExpression(String expression) {
			return new PseudoFunctionExpression(expression);
		}

		@Override
		public boolean match(Node node, PseudoFunctionExpression expression) {
			int value = node.getSiblingNameIndex() + 1;

			return expression.match(value);
		}

	}

	/**
	 * The <code>:nth-last-of-type(an+b)</code> pseudo-class notation represents an element
	 * that has an+b-1 siblings with the same expanded element name after it in the document tree,
	 * for any zero or positive integer value of n, and has a parent element.
	 */
	public static class NTH_LAST_OF_TYPE extends PseudoFunction<PseudoFunctionExpression> {

		@Override
		public PseudoFunctionExpression parseExpression(String expression) {
			return new PseudoFunctionExpression(expression);
		}

		@Override
		public boolean match(Node node, PseudoFunctionExpression expression) {
			Node child = node.getParentNode().getLastChildElement(node.getNodeName());
			int value = child.getSiblingNameIndex() + 1 - node.getSiblingNameIndex();

			return expression.match(value);
		}
	}

	// ---------------------------------------------------------------- extension

	/**
	 * Select the element at index n within the matched set.
	 */
	public static class EQ extends PseudoFunction<Integer> {

		@Override
		public Integer parseExpression(String expression) {
			return Integer.valueOf(expression.trim());
		}

		@Override
		public boolean match(Node node, Integer expression) {
			return true;
		}

		@Override
		public boolean match(List<Node> currentResults, Node node, int index, Integer expression) {
			int value = expression.intValue();
			if (value >= 0) {
				return index == value;
			} else {
				return index == currentResults.size() + value;
			}
		}
	}

	/**
	 * Select all elements at an index greater than index within the matched set.
	 */
	public static class GT extends PseudoFunction<Integer> {

		@Override
		public Integer parseExpression(String expression) {
			return Integer.valueOf(expression.trim());
		}

		@Override
		public boolean match(Node node, Integer expression) {
			return true;
		}

		@Override
		public boolean match(List<Node> currentResults, Node node, int index, Integer expression) {
			int value = expression.intValue();
			return index > value;
		}
	}

	/**
	 *  Select all elements at an index less than index within the matched set.
	 */
	public static class LT extends PseudoFunction<Integer> {

		@Override
		public Integer parseExpression(String expression) {
			return Integer.valueOf(expression.trim());
		}

		@Override
		public boolean match(Node node, Integer expression) {
			return true;
		}

		@Override
		public boolean match(List<Node> currentResults, Node node, int index, Integer expression) {
			int value = expression.intValue();
			return index < value;
		}
	}

	/**
	 * Selects all elements that contain the specified text.
	 */
	public static class CONTAINS extends PseudoFunction<String> {

		@Override
		public String parseExpression(String expression) {
			if (StringUtil.startsWithChar(expression, '\'') || StringUtil.startsWithChar(expression, '"')) {
				expression = expression.substring(1, expression.length() - 1);
			}
			return expression;
		}

		@Override
		public boolean match(Node node, String expression) {
			String text = node.getTextContent();
			return text.contains(expression);
		}
	}

	// ---------------------------------------------------------------- advanced

	/**
	 * Selects elements which contain at least one element that matches the specified selector.
	 */
	public static class HAS extends PseudoFunction<List<List<CssSelector>>> {

		@Override
		public List<List<CssSelector>> parseExpression(String expression) {
			if (StringUtil.startsWithChar(expression, '\'') || StringUtil.startsWithChar(expression, '"')) {
				expression = expression.substring(1, expression.length() - 1);
			}
			return CSSelly.parse(expression);
		}

		@Override
		public boolean match(Node node, List<List<CssSelector>> selectors) {
			List<Node> matchedNodes = new NodeSelector(node).select(selectors);

			return !matchedNodes.isEmpty();
		}
	}

	/**
	 * Selects all elements that do not match the given selector.
	 */
	public static class NOT extends PseudoFunction<List<List<CssSelector>>> {

		@Override
		public List<List<CssSelector>> parseExpression(String expression) {
			if (StringUtil.startsWithChar(expression, '\'') || StringUtil.startsWithChar(expression, '"')) {
				expression = expression.substring(1, expression.length() - 1);
			}
			return CSSelly.parse(expression);
		}

		@Override
		public boolean match(Node node, List<List<CssSelector>> selectors) {
			return !new NodeMatcher(node).match(selectors);
		}
	}

	// ---------------------------------------------------------------- interface

	/**
	 * Parses expression before usage.
	 */
	public abstract E parseExpression(String expression);
	
	/**
	 * Matches node using provided parsed expression.
	 */
	public abstract boolean match(Node node, E expression);
	
	/**
	 * Returns <code>true</code> if node matches the pseudoclass within current results.
	 */
	public boolean match(List<Node> currentResults, Node node, int index, E expression) {
		return true;
	}

	/**
	 * Returns pseudo-function name.
	 */
	public String getPseudoFunctionName() {
		String name = getClass().getSimpleName().toLowerCase();
		name = name.replace('_', '-');
		return name;
	}

}
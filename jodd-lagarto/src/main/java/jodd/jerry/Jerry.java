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

package jodd.jerry;

import jodd.lagarto.dom.DOMBuilder;
import jodd.lagarto.dom.Document;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeSelector;
import jodd.lagarto.dom.Text;
import jodd.util.ArraysUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Jerry is JQuery in Java.
 */
@SuppressWarnings("MethodNamesDifferingOnlyByCase")
public class Jerry implements Iterable<Jerry> {

	@SuppressWarnings("CloneableClassWithoutClone")
	private static class NodeList extends ArrayList<Node> {

		private NodeList(int initialCapacity) {
			super(initialCapacity);
		}

		private NodeList() {
		}

		@Override
		public boolean add(Node o) {
			for (Node node : this) {
				if (node == o) {
					return false;
				}
			}
			return super.add(o);
		}
	}

	// ---------------------------------------------------------------- create

	/**
	 * Parses input sequence and creates new <code>Jerry</code>.
	 */
	public static Jerry jerry(char[] content) {
		return jerry().parse(content);
	}

	/**
	 * Parses input content and creates new <code>Jerry</code>.
	 */
	public static Jerry jerry(String content) {
		return jerry().parse(content);
	}

	// ---------------------------------------------------------------- 2-steps init

	/**
	 * Content parser and Jerry factory.
	 */
	public static class JerryParser {

		protected final DOMBuilder domBuilder;

		public JerryParser() {
			this.domBuilder = new LagartoDOMBuilder();
		}
		public JerryParser(DOMBuilder domBuilder) {
			this.domBuilder = domBuilder;
		}

		/**
		 * Returns {@link DOMBuilder} for additional configuration.
		 */
		public DOMBuilder getDOMBuilder() {
			return domBuilder;
		}

		/**
		 * Invokes parsing on {@link DOMBuilder}.
		 */
		public Jerry parse(char[] content) {
			Document doc = domBuilder.parse(content);
			return new Jerry(domBuilder, doc);
		}

		/**
		 * Invokes parsing on {@link DOMBuilder}.
		 */
		public Jerry parse(String content) {
			if (content == null) {
				content = StringPool.EMPTY;
			}
			Document doc = domBuilder.parse(content);
			return new Jerry(domBuilder, doc);
		}
	}

	/**
	 * Just creates new {@link jodd.jerry.Jerry.JerryParser Jerry runner} to separate
	 * parser creation and creation of new Jerry instances.
	 */
	public static JerryParser jerry() {
		return new JerryParser();
	}

	/**
	 * Creates new {@link jodd.jerry.Jerry.JerryParser Jerry runner} with
	 * provided implementation of {@link jodd.lagarto.dom.DOMBuilder}.
	 */
	public static JerryParser jerry(DOMBuilder domBuilder) {
		return new JerryParser(domBuilder);
	}

	// ---------------------------------------------------------------- ctor

	protected final Jerry parent;
	protected final Node[] nodes;
	protected final DOMBuilder builder;

	/**
	 * Creates root Jerry.
	 */
	protected Jerry(DOMBuilder builder, Node... nodes) {
		this.parent = null;
		this.nodes = nodes;
		this.builder = builder;
	}

	/**
	 * Creates child Jerry.
	 */
	protected Jerry(Jerry parent, Node... nodes) {
		this.parent = parent;
		this.nodes = nodes;
		this.builder = parent.builder;
	}

	/**
	 * Creates child Jerry.
	 */
	protected Jerry(Jerry parent, Node[] nodes1, Node[] nodes2) {
		this.parent = parent;
		this.nodes = ArraysUtil.join(nodes1, nodes2);
		this.builder = parent.builder;
	}

	/**
	 * Creates child Jerry.
	 */
	protected Jerry(Jerry parent, List<Node> nodeList) {
		this(parent, nodeList.toArray(new Node[nodeList.size()]));
	}

	// ---------------------------------------------------------------- this

	/**
	 * Returns number of nodes in this Jerry.
	 */
	public int length() {
		return nodes.length;
	}

	/**
	 * Returns number of nodes in this Jerry.
	 */
	public int size() {
		return nodes.length;
	}

	/**
	 * Returns node at given index. Returns <code>null</code>
	 * if index is out of bounds.
	 */
	public Node get(int index) {
		if ((index < 0) || (index >= nodes.length)) {
			return null;
		}
		return nodes[index];
	}

	/**
	 * Retrieve all DOM elements matched by this set.
	 * Warning: returned array is not a clone!
	 */
	public Node[] get() {
		return nodes;
	}

	/**
	 * Searches for a given <code>Node</code> from among the matched elements.
	 */
	public int index(Node element) {
		if (nodes.length == 0) {
			return -1;
		}

		int index = 0;
		for (Node node : nodes) {
			if (node == element) {
				return index;
			}
			index++;
		}
		return -1;
	}

	// ---------------------------------------------------------------- Traversing

	/**
	 * Gets the immediate children of each element in the set of matched elements.
	 */
	public Jerry children() {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				Node[] children = node.getChildElements();

				Collections.addAll(result, children);
			}
		}
		return new Jerry(this, result);
	}

	/**
	* Get the children of each element in the set of matched elements, 
	* including text and comment nodes.
	*/
	public Jerry contents() {
		List<Node> result = new NodeList(nodes.length);
		if (nodes.length > 0) {
			for (Node node : nodes) {
				Node[] contents = node.getChildNodes();
				Collections.addAll(result, contents);
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Gets the parent of each element in the current set of matched elements.
	 */
	public Jerry parent() {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				result.add(node.getParentNode());
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Gets the siblings of each element in the set of matched elements.
	 */
	public Jerry siblings() {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				Node[] allElements = node.getParentNode().getChildElements();
				for (Node sibling : allElements) {
					if (sibling != node) {
						result.add(sibling);
					}
				}
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Gets the immediately following sibling of each element in the
	 * set of matched elements.
	 */
	public Jerry next() {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				result.add(node.getNextSiblingElement());
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Get all following siblings of each element in the set of matched 
	 * elements, optionally filtered by a selector.
	 */
	public Jerry nextAll() {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				Node currentSiblingElement = node.getNextSiblingElement();
				while (currentSiblingElement != null) {
					result.add(currentSiblingElement);
					currentSiblingElement = currentSiblingElement.getNextSiblingElement();
				}
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Gets the immediately preceding sibling of each element in the
	 * set of matched elements.
	 */
	public Jerry prev() {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				result.add(node.getPreviousSiblingElement());
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Get all preceding siblings of each element in the set of matched 
	 * elements, optionally filtered by a selector.
	 */
	public Jerry prevAll() {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				Node currentSiblingElement = node.getPreviousSiblingElement();
				while (currentSiblingElement != null) {
					result.add(currentSiblingElement);
					currentSiblingElement = currentSiblingElement.getPreviousSiblingElement();
				}
			}
		}
		return new Jerry(this, result);
	}

	/**
	 *  Gets the descendants of each element in the current set of matched elements,
	 *  filtered by a selector.
	 */
	public Jerry find(String cssSelector) {
		final List<Node> result = new NodeList();

		if (nodes.length > 0) {
			for (Node node : nodes) {
				NodeSelector nodeSelector = createNodeSelector(node);
				List<Node> filteredNodes = nodeSelector.select(cssSelector);
				result.addAll(filteredNodes);
			}
		}

		return new Jerry(this, result);
	}

	/**
	 * @see #find(String)
	 */
	public Jerry $(String cssSelector) {
		return find(cssSelector);
	}

	/**
	 * Shortcut for <code>context.find(css)</code>.
	 */
	public static Jerry $(String cssSelector, Jerry context) {
		return context.find(cssSelector);
	}

	/**
	 * Creates node selector.
	 */
	protected NodeSelector createNodeSelector(Node node) {
		return new NodeSelector(node);
	}

	/**
	 * Iterates over a jQuery object, executing a function for
	 * each matched element.
	 * @see #eachNode(JerryNodeFunction)
	 */
	public Jerry each(JerryFunction function) {
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			Jerry $this = new Jerry(this, node);
			Boolean result = function.onNode($this, i);
			if (result != null && result == Boolean.FALSE) {
				break;
			}
		}
		return this;
	}

	/**
	 * Iterates over a jQuery object, executing a function for
	 * each matched element.
	 * @see #each(JerryFunction)
	 */
	public Jerry eachNode(JerryNodeFunction function) {
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			if (!function.onNode(node, i)) {
				break;
			}
		}
		return this;
	}

	// ---------------------------------------------------------------- Miscellaneous Traversing

	/**
	 * Adds elements to the set of matched elements.
	 */
	public Jerry add(String selector) {
		return new Jerry(this, nodes, root().find(selector).nodes);
	}

	/**
	 * Ends the most recent filtering operation in the current chain
	 * and returns the set of matched elements to its previous state.
	 */
	public Jerry end() {
		return parent;
	}

	/**
	 * Removes elements from the set of matched elements.
	 */
	public Jerry not(String cssSelector) {
		Node[]  notNodes = root().find(cssSelector).nodes;
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				if (!ArraysUtil.contains(notNodes, node)) {
					result.add(node);
				}
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Returns root Jerry.
	 */
	public Jerry root() {
		Jerry jerry = this.parent;
		if (jerry == null) {
			return this;
		}
		while (jerry.parent != null) {
			jerry = jerry.parent;
		}
		return jerry;
	}


	// ---------------------------------------------------------------- Filtering

	/**
	 * Reduces the set of matched elements to the first in the set.
	 */
	public Jerry first() {
		List<Node> result = new NodeList(nodes.length);
		if (nodes.length > 0) {
			result.add(nodes[0]);
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to the last in the set.
	 */
	public Jerry last() {
		List<Node> result = new NodeList(nodes.length);
		if (nodes.length > 0) {
			result.add(nodes[nodes.length - 1]);
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to the one at the specified index.
	 */
	public Jerry eq(int value) {
		List<Node> result = new NodeList(1);
		int matchingIndex = value >= 0 ? value : nodes.length + value;

		if (nodes.length > 0) {
			int index = 0;
			for (Node node : nodes) {
				if (index == matchingIndex) {
					result.add(node);
					break;
				}
				index++;
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to the one at an index greater
	 * than specified index.
	 */
	public Jerry gt(int value) {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			int index = 0;

			for (Node node : nodes) {
				if (index > value) {
					result.add(node);
				}
				index++;
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to the one at an index less
	 * than specified index.
	 */
	public Jerry lt(int value) {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			int index = 0;
			for (Node node : nodes) {
				if (index < value) {
					result.add(node);
				}
				index++;
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Checks the current matched set of elements against a selector and
	 * return <code>true</code> if at least one of these elements matches
	 * the given arguments.
	 */
	public boolean is(String cssSelectors) {
		if (nodes.length == 0) {
			return false;
		}

		for (Node node : nodes) {
			Node parentNode = node.getParentNode();
			if (parentNode == null) {
				continue;
			}

			NodeSelector nodeSelector = createNodeSelector(parentNode);
			List<Node> selectedNodes = nodeSelector.select(cssSelectors);

			for (Node selected : selectedNodes) {
				if (node == selected) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Reduces the set of matched elements to those that match the selector.
	 */
	public Jerry filter(String cssSelectors) {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				Node parentNode = node.getParentNode();
				if (parentNode == null) {
					continue;
				}

				NodeSelector nodeSelector = createNodeSelector(parentNode);
				List<Node> selectedNodes = nodeSelector.select(cssSelectors);

				for (Node selected : selectedNodes) {
					if (node == selected) {
						result.add(node);
					}
				}
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to those that pass the
	 * {@link JerryFunction function's} test.
	 */
	public Jerry filter(JerryFunction jerryFunction) {
		List<Node> result = new NodeList(nodes.length);

		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			Node parentNode = node.getParentNode();
			if (parentNode == null) {
				continue;
			}

			Jerry $this = new Jerry(this, node);

			boolean accept = jerryFunction.onNode($this, i);

			if (accept) {
				result.add(node);
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduce the set of matched elements to those that have a descendant that
	 * matches the selector or DOM element.
	 */
	public Jerry has(String cssSelectors) {
		List<Node> result = new NodeList(nodes.length);

		if (nodes.length > 0) {
			for (Node node : nodes) {
				NodeSelector nodeSelector = createNodeSelector(node);
				List<Node> selectedNodes = nodeSelector.select(cssSelectors);

				if (!selectedNodes.isEmpty()) {
					result.add(node);
				}
			}
		}

		return new Jerry(this, result);
	}

	// ---------------------------------------------------------------- Attributes

	/**
	 * Gets the value of an attribute for the first element in the set of matched elements.
	 * Returns <code>null</code> if set is empty.
	 */
	public String attr(String name) {
		if (nodes.length == 0) {
			return null;
		}
		if (name == null) {
			return null;
		}
		return nodes[0].getAttribute(name);
	}

	/**
	 * Sets one or more attributes for the set of matched elements.
	 */
	public Jerry attr(String name, String value) {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			node.setAttribute(name, value);
		}
		return this;
	}

	/**
	 * Removes an attribute from each element in the set of matched elements.
	 */
	public Jerry removeAttr(String name) {
		if (name == null) {
			return this;
		}
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			node.removeAttribute(name);
		}
		return this;
	}

	/**
	 * Gets the value of a style property for the first element
	 * in the set of matched elements. Returns <code>null</code>
	 * if set is empty.
	 */
	public String css(String propertyName) {
		if (nodes.length == 0) {
			return null;
		}

		propertyName = StringUtil.fromCamelCase(propertyName, '-');

		String styleAttrValue = nodes[0].getAttribute("style");
		if (styleAttrValue == null) {
			return null;
		}

		Map<String, String> styles = createPropertiesMap(styleAttrValue, ';', ':');
		return styles.get(propertyName);
	}

	/**
	 * Sets one or more CSS properties for the set of matched elements.
	 * By passing an empty value, that property will be removed.
	 * Note that this is different from jQuery, where this means
	 * that property will be reset to previous value if existed.
	 */
	public Jerry css(String propertyName, String value) {
		propertyName = StringUtil.fromCamelCase(propertyName, '-');

		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			String styleAttrValue = node.getAttribute("style");
			Map<String, String> styles = createPropertiesMap(styleAttrValue, ';', ':');
			if (value.length() == 0) {
				styles.remove(propertyName);
			} else {
				styles.put(propertyName, value);
			}

			styleAttrValue = generateAttributeValue(styles, ';', ':');
			node.setAttribute("style", styleAttrValue);
		}
		return this;
	}

	/**
	 * Sets one or more CSS properties for the set of matched elements.
	 */
	public Jerry css(String... css) {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			String styleAttrValue = node.getAttribute("style");
			Map<String, String> styles = createPropertiesMap(styleAttrValue, ';', ':');

			for (int i = 0; i < css.length; i += 2) {
				String propertyName = css[i];
				propertyName = StringUtil.fromCamelCase(propertyName, '-');
				String value = css[i + 1];
				if (value.length() == 0) {
					styles.remove(propertyName);
				} else {
					styles.put(propertyName, value);
				}
			}
			styleAttrValue = generateAttributeValue(styles, ';', ':');
			node.setAttribute("style", styleAttrValue);
		}
		return this;
	}

	/**
	 * Adds the specified class(es) to each of the set of matched elements.
	 */
	public Jerry addClass(String... classNames) {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			String attrClass = node.getAttribute("class");
			Set<String> classes = createPropertiesSet(attrClass, ' ');
			boolean wasChange = false;
			for (String className : classNames) {
				if (classes.add(className)) {
					wasChange = true;
				}
			}
			if (wasChange) {
				String attrValue = generateAttributeValue(classes, ' ');
				node.setAttribute("class", attrValue);
			}
		}
		return this;
	}

	/**
	 * Determines whether any of the matched elements are assigned the given class.
	 */
	public boolean hasClass(String... classNames) {
		if (nodes.length == 0) {
			return false;
		}
		for (Node node : nodes) {
			String attrClass = node.getAttribute("class");
			Set<String> classes = createPropertiesSet(attrClass, ' ');
			for (String className : classNames) {
				if (classes.contains(className)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes a single class, multiple classes, or all classes
	 * from each element in the set of matched elements.
	 */
	public Jerry removeClass(String... classNames) {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			String attrClass = node.getAttribute("class");
			Set<String> classes = createPropertiesSet(attrClass, ' ');
			boolean wasChange = false;
			for (String className : classNames) {
				if (classes.remove(className)) {
					wasChange = true;
				}
			}
			if (wasChange) {
				String attrValue = generateAttributeValue(classes, ' ');
				node.setAttribute("class", attrValue);
			}
		}
		return this;
	}

	/**
	 * Adds or remove one or more classes from each element in the set of
	 * matched elements, depending on either the class's presence or
	 * the value of the switch argument.
	 */
	public Jerry toggleClass(String... classNames) {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			String attrClass = node.getAttribute("class");
			Set<String> classes = createPropertiesSet(attrClass, ' ');
			for (String className : classNames) {
				if (classes.contains(className)) {
					classes.remove(className);
				} else {
					classes.add(className);
				}
			}
			String attrValue = generateAttributeValue(classes, ' ');
			node.setAttribute("class", attrValue);
		}
		return this;
	}

	// ---------------------------------------------------------------- content

	/**
	 * Gets the combined text contents of each element in the set of
	 * matched elements, including their descendants.
	 * Text is HTML decoded for text nodes.
	 */
	public String text() {
		if (nodes.length == 0) {
			return StringPool.EMPTY;
		}

		StringBuilder sb = new StringBuilder();
		for (Node node : nodes) {
			sb.append(node.getTextContent());
		}
		return sb.toString();
	}

	/**
	 * Sets the content of each element in the set of matched elements to the specified text.
	 */
	public Jerry text(String text) {
		if (nodes.length == 0) {
			return this;
		}
		if (text == null) {
			text = StringPool.EMPTY;
		}
		for (Node node : nodes) {
			node.removeAllChilds();
			Text textNode = new Text(node.getOwnerDocument(), text);
			node.addChild(textNode);
		}
		return this;
	}

	/**
	 * Gets the HTML contents of the first element in the set of matched elements.
	 * Content is raw, not HTML decoded.
	 * @see #htmlAll(boolean)
	 */
	public String html() {
		if (nodes.length == 0) {
			return null;
		}
		return nodes[0].getInnerHtml();
	}

	/**
	 * Gets the combined HTML contents of each element in the set of
	 * matched elements, including their descendants.
	 * @see #html()
	 * @param setIncluded if <code>true</code> than sets node are included in the output
	 */
	public String htmlAll(boolean setIncluded) {
		if (nodes.length == 0) {
			return StringPool.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		for (Node node : nodes) {
			sb.append(setIncluded ? node.getHtml() : node.getInnerHtml());
		}
		return sb.toString();
	}

	/**
	 * Sets the HTML contents of each element in the set of matched elements.
	 */
	public Jerry html(String html) {
		if (html == null) {
			html = StringPool.EMPTY;
		}

		final Document doc = builder.parse(html);

		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			node.removeAllChilds();

			// clone to preserve for next iteration
			// as nodes will be detached from parent
			Document workingDoc = doc.clone();

			node.addChild(workingDoc.getChildNodes());
		}
		return this;
	}
	
	// ---------------------------------------------------------------- DOM

	/**
	 * Inserts content, specified by the parameter, to the end of each
	 * element in the set of matched elements.
	 */
	public Jerry append(String html) {
		if (html == null) {
			html = StringPool.EMPTY;
		}
		final Document doc = builder.parse(html);

		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			Document workingDoc = doc.clone();
			node.addChild(workingDoc.getChildNodes());
		}
		return this;
	}

	/**
	 * Insert content, specified by the parameter, to the beginning of each 
	 * element in the set of matched elements.
	 */
	public Jerry prepend(String html) {
		if (html == null) {
			html = StringPool.EMPTY;
		}
		final Document doc = builder.parse(html);

		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			Document workingDoc = doc.clone();
			node.insertChild(workingDoc.getChildNodes(), 0);
		}
		return this;
	}

	/**
	 * Inserts content, specified by the parameter, before each
	 * element in the set of matched elements.
	 */
	public Jerry before(String html) {
		if (html == null) {
			html = StringPool.EMPTY;
		}
		final Document doc = builder.parse(html);
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			Document workingDoc = doc.clone();
			node.insertBefore(workingDoc.getChildNodes(), node);
		}
		return this;
	}

	/**
	 * Inserts content, specified by the parameter, after each
	 * element in the set of matched elements.
	 */
	public Jerry after(String html) {
		if (html == null) {
			html = StringPool.EMPTY;
		}
		final Document doc = builder.parse(html);
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			Document workingDoc = doc.clone();
			node.insertAfter(workingDoc.getChildNodes(), node);
		}
		return this;
	}

	/**
	 * Replace each element in the set of matched elements with the provided 
	 * new content and return the set of elements that was removed.
	 */
	public Jerry replaceWith(String html) {
 		if (html == null) {
			html = StringPool.EMPTY;
		}
		final Document doc = builder.parse(html);

		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			Node parent = node.getParentNode();
			// if a node already is the root element, don't unwrap
			if (parent == null) {
				continue;
			}

			// replace, if possible
			Document workingDoc = doc.clone();
			int index = node.getSiblingIndex();
			parent.insertChild(workingDoc.getFirstChild(), index);
			node.detachFromParent();
		}

		return this;
	}

	/**
	 * Removes the set of matched elements from the DOM.
	 */
	public Jerry remove() {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			node.detachFromParent();
		}
		return this;
	}

	/**
	 * Removes the set of matched elements from the DOM.
	 * Identical to {@link #remove()}.
	 */
	public Jerry detach() {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			node.detachFromParent();
		}
		return this;
	}

	/**
	 * Removes all child nodes of the set of matched elements from the DOM.
	 */
	public Jerry empty() {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			node.removeAllChilds();
		}
		return this;
	}

	// ---------------------------------------------------------------- wrap

	/**
	 * Wraps an HTML structure around each element in the set of matched elements.
	 * Returns the original set of elements for chaining purposes.
	 */
	public Jerry wrap(String html) {
		if (html == null) {
			html = StringPool.EMPTY;
		}
		final Document doc = builder.parse(html);

		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			Document workingDoc = doc.clone();
			Node inmostNode = workingDoc;
			while (inmostNode.hasChildNodes()) {
				inmostNode = inmostNode.getFirstChild();
			}

			// replace
			Node parent = node.getParentNode();
			int index = node.getSiblingIndex();
			inmostNode.addChild(node);
			parent.insertChild(workingDoc.getFirstChild(), index);
		}

		return this;
	}

	/**
	 * Remove the parents of the set of matched elements from the DOM, leaving 
	 * the matched elements (and siblings, if any) in their place. 
	 */
	public Jerry unwrap() {
		if (nodes.length == 0) {
			return this;
		}
		for (Node node : nodes) {
			Node parent = node.getParentNode();
			// if a node already is the root element, don't unwrap
			if (parent == null) {
				continue;
			}

			// replace, if possible
			Node grandparent = parent.getParentNode();
			if (grandparent == null) {
				continue;
			}

			Node[] siblings = parent.getChildNodes();
			int index = parent.getSiblingIndex();
			grandparent.insertChild(siblings, index);
			parent.detachFromParent();
		}

		return this;
	}

	// ---------------------------------------------------------------- iterator

	/**
	 * Returns iterator over nodes contained in the Jerry object.
	 * Each node is wrapped. Similar to {@link #each(JerryFunction)}.
	 */
	public Iterator<Jerry> iterator() {
		final Jerry jerry = this;

		return new Iterator<Jerry>() {
			private int index = 0;

			public boolean hasNext() {
				return index < jerry.nodes.length;
			}

			public Jerry next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				Jerry nextJerry = new Jerry(jerry, jerry.get(index));
				index++;
				return nextJerry;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	// ---------------------------------------------------------------- form

	/**
	 * Processes all forms, collects all form parameters and calls back the
	 * {@link JerryFormHandler}.
	 */
	public Jerry form(String formCssSelector, JerryFormHandler jerryFormHandler) {
		Jerry form = find(formCssSelector);

		// process each form
		for (Node node : form.nodes) {
			Jerry singleForm = new Jerry(this, node);

			final Map<String, String[]> parameters = new HashMap<>();

			// process all input elements

			singleForm.$("input").each(($inputTag, index) -> {

				String type = $inputTag.attr("type");

				// An input element with no type attribute specified represents
				// the same thing as an input element with its type attribute set to "text".

				if (type == null) {
					type = "text";
				}

				boolean isCheckbox = type.equals("checkbox");
				boolean isRadio = type.equals("radio");

				if (isRadio || isCheckbox) {
					if (!($inputTag.nodes[0].hasAttribute("checked"))) {
						return true;
					}
				}

				String name = $inputTag.attr("name");
				if (name == null) {
					return true;
				}

				String tagValue = $inputTag.attr("value");

				if (tagValue == null) {
					if (isCheckbox) {
						tagValue = "on";
					}
				}

				// add tag value
				String[] value = parameters.get(name);

				if (value == null) {
					value = new String[] {tagValue};
				} else {
					value = ArraysUtil.append(value, tagValue);
				}

				parameters.put(name, value);
				return true;
			});

			// process all select elements

			singleForm.$("select").each(($selectTag, index) -> {
				final String name = $selectTag.attr("name");

				$selectTag.$("option").each(($optionTag, index1) -> {
					if ($optionTag.nodes[0].hasAttribute("selected")) {
						String tagValue = $optionTag.attr("value");

						// add tag value
						String[] value = parameters.get(name);

						if (value == null) {
							value = new String[] {tagValue};
						} else {
							value = ArraysUtil.append(value, tagValue);
						}

						parameters.put(name, value);
					}
					return true;
				});

				return true;
			});

			// process all text areas

			singleForm.$("textarea").each(($textarea, index) -> {
				String name = $textarea.attr("name");
				String value = $textarea.text();

				parameters.put(name, new String[] {value});
				return true;
			});

			// done

			jerryFormHandler.onForm(singleForm, parameters);
		}

		return this;
	}

	// ---------------------------------------------------------------- internal

	protected Set<String> createPropertiesSet(String attrValue, char propertiesDelimiter) {
		if (attrValue == null) {
			return new LinkedHashSet<>();
		}
		String[] properties = StringUtil.splitc(attrValue, propertiesDelimiter);
		LinkedHashSet<String> set = new LinkedHashSet<>(properties.length);

		Collections.addAll(set, properties);
		return set;
	}
	
	protected String generateAttributeValue(Set<String> set, char propertiesDelimiter) {
		StringBuilder sb = new StringBuilder(set.size() * 16);
		boolean first = true;
		for (String entry : set) {
			if (!first) {
				sb.append(propertiesDelimiter);
			} else {
				first = false;
			}
			sb.append(entry);
		}
		return sb.toString();
	}
	
	protected Map<String, String> createPropertiesMap(String attrValue, char propertiesDelimiter, char valueDelimiter) {
		if (attrValue == null) {
			return new LinkedHashMap<>();
		}
		String[] properties = StringUtil.splitc(attrValue, propertiesDelimiter);
		LinkedHashMap<String, String> map = new LinkedHashMap<>(properties.length);
		for (String property : properties) {
			int valueDelimiterIndex = property.indexOf(valueDelimiter);
			if (valueDelimiterIndex != -1) {
				String propertyName = property.substring(0, valueDelimiterIndex).trim();
				String propertyValue = property.substring(valueDelimiterIndex + 1).trim();
				map.put(propertyName, propertyValue);
			}
		}
		return map;
	}
	
	protected String generateAttributeValue(Map<String, String> map, char propertiesDelimiter, char valueDelimiter) {
		StringBuilder sb = new StringBuilder(map.size() * 32);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey());
			sb.append(valueDelimiter);
			sb.append(entry.getValue());
			sb.append(propertiesDelimiter);
		}
		return sb.toString();
	}

}
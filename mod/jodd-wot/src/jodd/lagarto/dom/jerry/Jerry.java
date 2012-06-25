// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom.jerry;

import jodd.lagarto.dom.Document;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeSelector;
import jodd.lagarto.dom.Text;
import jodd.util.ArraysUtil;
import jodd.util.StringUtil;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jerry is JQuery in Java.
 */
public class Jerry {

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
	public static Jerry jerry(CharSequence content) {
		return jerry().parse(content);
	}

	/**
	 * Parses input content and creates new <code>Jerry</code>.
	 */
	public static Jerry jerry(CharBuffer content) {
		return jerry().parse(content);
	}

	// ---------------------------------------------------------------- 2-steps init

	/**
	 * Content parser and Jerry factory.
	 */
	public static class JerryParser {

		protected final LagartoDOMBuilder builder = new LagartoDOMBuilder();

		/**
		 * Returns builder for more configuration.
		 */
		public LagartoDOMBuilder getDOMBuilder() {
			return builder;
		}

		public JerryParser enableHtmlMode() {
			builder.enableHtmlMode();
			return this;
		}

		public JerryParser enableXhtmlMode() {
			builder.enableXhtmlMode();
			return this;
		}

		public JerryParser enableXmlMode() {
			builder.enableXmlMode();
			return this;
		}

		public Jerry parse(CharSequence content) {
			Document doc = builder.parse(content);
			return new Jerry(builder, doc);
		}

		public Jerry parse(CharBuffer content) {
			Document doc = builder.parse(content);
			return new Jerry(builder, doc);
		}
	}

	/**
	 * Just creates new {@link jodd.lagarto.dom.jerry.Jerry.JerryParser Jerry runner} to separate
	 * parser creation and creation of new Jerry instances.
	 */
	public static JerryParser jerry() {
		return new JerryParser();
	}

	// ---------------------------------------------------------------- ctor

	protected final Jerry parent;
	protected final Node[] nodes;
	protected final LagartoDOMBuilder builder;

	/**
	 * Creates root Jerry.
	 */
	protected Jerry(LagartoDOMBuilder builder, Node... nodes) {
		this.parent = null;
		this.nodes = nodes;
		this.builder = builder;
	}

	/**
	 * Creates parent Jerry.
	 */
	protected Jerry(Jerry parent, Node... nodes) {
		this.parent = parent;
		this.nodes = nodes;
		this.builder = parent.builder;
	}

	protected Jerry(Jerry parent, Node[] nodes1, Node[] nodes2) {
		this.parent = parent;
		this.nodes = ArraysUtil.merge(nodes1, nodes2);
		this.builder = parent.builder;
	}

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
	 * Returns node at given index.
	 */
	public Node get(int index) {
		return nodes[index];
	}

	/**
	 * Retrieve all DOM elements matched by this set.
	 */
	public Node[] get() {
		return nodes;
	}

	/**
	 * Searches for a given Node from among the matched elements.
	 */
	public int index(Node element) {
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

		for (Node node : nodes) {
			Node[] children = node.getChildElements();
			for (Node child : children) {
				result.add(child);
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Gets the parent of each element in the current set of matched elements.
	 */
	public Jerry parent() {
		List<Node> result = new NodeList(nodes.length);

		for (Node node : nodes) {
			result.add(node.getParentNode());
		}
		return new Jerry(this, result);
	}

	/**
	 * Gets the siblings of each element in the set of matched elements.
	 */
	public Jerry siblings() {
		List<Node> result = new NodeList(nodes.length);
		for (Node node : nodes) {
			Node[] allElements = node.getParentNode().getChildElements();
			for (Node sibling : allElements) {
				if (sibling != node) {
					result.add(sibling);
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

		for (Node node : nodes) {
			result.add(node.getNextSiblingElement());
		}
		return new Jerry(this, result);
	}

	/**
	 * Gets the immediately preceding sibling of each element in the
	 * set of matched elements.
	 */
	public Jerry prev() {
		List<Node> result = new NodeList(nodes.length);

		for (Node node : nodes) {
			result.add(node.getPreviousSiblingElement());
		}
		return new Jerry(this, result);
	}

	/**
	 *  Gets the descendants of each element in the current set of matched elements,
	 *  filtered by a selector.
	 */
	public Jerry find(String cssSelector) {
		final List<Node> result = new NodeList();

		for (Node node : nodes) {
			NodeSelector nodeSelector = createNodeSelector(node);
			List<Node> filteredNodes = nodeSelector.select(cssSelector);
			result.addAll(filteredNodes);
		}

		return new Jerry(this, result);
	}
	
	public Jerry $(String cssSelector) {
		return find(cssSelector);
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
	 */
	public Jerry each(JerryFunction function) {
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			Jerry $this = new Jerry(this, node);
			if (function.onNode($this, i) == false) {
				break;
			}
		}
		return this;
	}

	/**
	 * Iterates over a jQuery object, executing a function for
	 * each matched element.
	 */
	public Jerry each(JerryNodeFunction function) {
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			if (function.onNode(node, i) == false) {
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
		for (Node node : nodes) {
			if (ArraysUtil.contains(notNodes, node) == false) {
				result.add(node);
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
		for (Node node : nodes) {
			if (node.getSiblingElementIndex() == 0) {
				result.add(node);
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to the last in the set.
	 */
	public Jerry last() {
		List<Node> result = new NodeList(nodes.length);
		for (Node node : nodes) {
			int elementsLastIndex = node.getParentNode().getChildElementsCount() - 1;
			if (node.getSiblingElementIndex() == elementsLastIndex) {
				result.add(node);
			}
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to the one at the specified index.
	 */
	public Jerry eq(int value) {
		List<Node> result = new NodeList(1);
		int index = 0;
		int matchingIndex = value >= 0 ? value : nodes.length + value;
		for (Node node : nodes) {
			if (index == matchingIndex) {
				result.add(node);
				break;
			}
			index++;
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to the one at an index greater
	 * than specified index.
	 */
	public Jerry gt(int value) {
		List<Node> result = new NodeList(nodes.length);
		int index = 0;
		for (Node node : nodes) {
			if (index > value) {
				result.add(node);
			}
			index++;
		}
		return new Jerry(this, result);
	}

	/**
	 * Reduces the set of matched elements to the one at an index less
	 * than specified index.
	 */
	public Jerry lt(int value) {
		List<Node> result = new NodeList(nodes.length);
		int index = 0;
		for (Node node : nodes) {
			if (index < value) {
				result.add(node);
			}
			index++;
		}
		return new Jerry(this, result);
	}

	/**
	 * Checks the current matched set of elements against a selector and
	 * return <code>true</code> if at least one of these elements matches
	 * the given arguments.
	 */
	public boolean is(String cssSelectors) {
		
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

	// ---------------------------------------------------------------- Attributes

	/**
	 * Gets the value of an attribute for the first element in the set of matched elements.
	 * Returns <code>null</code> if set is empty.
	 */
	public String attr(String name) {
		if (nodes.length == 0) {
			return null;
		}
		return nodes[0].getAttribute(name);
	}

	/**
	 * Sets one or more attributes for the set of matched elements.
	 */
	public Jerry attr(String name, String value) {
		for (Node node : nodes) {
			node.setAttribute(name, value);
		}
		return this;
	}

	/**
	 * Removes an attribute from each element in the set of matched elements.
	 */
	public Jerry removeAttr(String name) {
		for (Node node : nodes) {
			node.removeAttribute(name);
		}
		return this;
	}


	/**
	 * Gets the value of a style property for the first element
	 * in the set of matched elements. Returns <code>null</code>
	 * if set s empty.
	 */
	public String css(String propertyName) {
		if (nodes.length == 0) {
			return null;
		}

		propertyName = StringUtil.camelCaseToWords(propertyName, '-');

		String styleAttrValue = nodes[0].getAttribute("style");
		if (styleAttrValue == null) {
			return null;
		}

		Map<String, String> styles = createPropertiesMap(styleAttrValue, ';', ':');
		return styles.get(propertyName);
	}

	/**
	 * Sets one or more CSS properties for the set of matched elements.
	 */
	public Jerry css(String propertyName, String value) {
		propertyName = StringUtil.camelCaseToWords(propertyName, '-');

		for (Node node : nodes) {
			String styleAttrValue = node.getAttribute("style");
			Map<String, String> styles = createPropertiesMap(styleAttrValue, ';', ':');
			styles.put(propertyName, value);

			styleAttrValue = generateAttributeValue(styles, ';', ':');
			node.setAttribute("style", styleAttrValue);
		}
		return this;
	}

	/**
	 * Sets one or more CSS properties for the set of matched elements.
	 */
	public Jerry css(String... css) {
		for (Node node : nodes) {
			String styleAttrValue = node.getAttribute("style");
			Map<String, String> styles = createPropertiesMap(styleAttrValue, ';', ':');

			for (int i = 0; i < css.length; i += 2) {
				String propertyName = css[i];
				propertyName = StringUtil.camelCaseToWords(propertyName, '-');
				styles.put(propertyName, css[i + 1]);
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
		for (Node node : nodes) {
			String attrClass = node.getAttribute("class");
			Set<String> classes = createPropertiesSet(attrClass, ' ');
			boolean wasChange = false;
			for (String className : classNames) {
				className = StringUtil.camelCaseToWords(className, '-');
				if (classes.add(className) == true) {
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
		for (Node node : nodes) {
			String attrClass = node.getAttribute("class");
			Set<String> classes = createPropertiesSet(attrClass, ' ');
			for (String className : classNames) {
				className = StringUtil.camelCaseToWords(className, '-');
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
		for (Node node : nodes) {
			String attrClass = node.getAttribute("class");
			Set<String> classes = createPropertiesSet(attrClass, ' ');
			boolean wasChange = false;
			for (String className : classNames) {
				className = StringUtil.camelCaseToWords(className, '-');
				if (classes.remove(className) == true) {
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
		for (Node node : nodes) {
			String attrClass = node.getAttribute("class");
			Set<String> classes = createPropertiesSet(attrClass, ' ');
			for (String className : classNames) {
				className = StringUtil.camelCaseToWords(className, '-');
				if (classes.contains(className) == true) {
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
		for (Node node : nodes) {
			node.removeAllChilds();
			Text textNode = new Text();
			textNode.setTextContent(text);
			node.appendChild(textNode);
		}
		return this;
	}

	/**
	 * Gets the HTML contents of the first element in the set of matched elements.
	 * Content is raw, not HTML decoded.
	 */
	public String html() {
		if (nodes.length == 0) {
			return null;
		}
		return nodes[0].getInnerHtml();
	}

	/**
	 * Sets the HTML contents of each element in the set of matched elements.
	 */
	public Jerry html(String html) {
		final Document doc = builder.parse(html);

		for (Node node : nodes) {
			node.removeAllChilds();
			Document workingDoc = doc.clone();
			node.appendChild(workingDoc.getChildNodes());
		}
		return this;
	}
	
	// ---------------------------------------------------------------- DOM

	/**
	 * Inserts content, specified by the parameter, to the end of each
	 * element in the set of matched elements.
	 */
	public Jerry append(String html) {
		final Document doc = builder.parse(html);

		for (Node node : nodes) {
			Document workingDoc = doc.clone();
			node.appendChild(workingDoc);
		}
		return this;
	}

	/**
	 * Inserts content, specified by the parameter, before each
	 * element in the set of matched elements.
	 */
	public Jerry before(String html) {
		final Document doc = builder.parse(html);

		for (Node node : nodes) {
			Document workingDoc = doc.clone();
			node.insertBefore(workingDoc, node);
		}
		return this;
	}

	/**
	 * Removes the set of matched elements from the DOM.
	 */
	public Jerry remove() {
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
		for (Node node : nodes) {
			node.detachFromParent();
		}
		return this;
	}

	/**
	 * Removes all child nodes of the set of matched elements from the DOM.
	 */
	public Jerry empty() {
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
		final Document doc = builder.parse(html);

		for (Node node : nodes) {
			Document workingDoc = doc.clone();
			Node inmostNode = workingDoc;
			while (inmostNode.hasChildNodes()) {
				inmostNode = inmostNode.getFirstChild();
			}

			// replace
			Node parent = node.getParentNode();
			int index = node.getSiblingIndex();
			inmostNode.appendChild(node);
			parent.insertChild(workingDoc.getFirstChild(), index);
		}

		return this;
	}

	// ---------------------------------------------------------------- internal

	protected Set<String> createPropertiesSet(String attrValue, char propertiesDelimiter) {
		if (attrValue == null) {
			return new LinkedHashSet<String>();
		}
		String[] properties = StringUtil.splitc(attrValue, propertiesDelimiter);
		LinkedHashSet<String> set = new LinkedHashSet<String>(properties.length);
		for (String property : properties) {
			set.add(property);
		}
		return set;
	}
	
	protected String generateAttributeValue(Set<String> set, char propertiesDelimiter) {
		StringBuilder sb = new StringBuilder(set.size() * 16);
		boolean first = true;
		for (String entry : set) {
			if (first == false) {
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
			return new LinkedHashMap<String, String>();
		}
		String[] properties = StringUtil.splitc(attrValue, propertiesDelimiter);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(properties.length);
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
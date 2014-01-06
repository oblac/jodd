// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.csselly;

import jodd.csselly.selector.AttributeSelector;
import jodd.csselly.selector.PseudoClassSelector;
import jodd.csselly.selector.PseudoFunctionSelector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeFilter;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static jodd.csselly.selector.Match.EQUALS;
import static jodd.csselly.selector.Match.INCLUDES;

/**
 * CSS selector.
 */
public class CssSelector implements NodeFilter {

	private static final String ID = "id";
	private static final String CLASS = "class";

	protected CssSelector prevCssSelector;
	protected CssSelector nextCssSelector;

	protected final String element;

	protected Combinator combinator = Combinator.DESCENDANT;
	protected List<Selector> selectors;

	public CssSelector() {
		this(null);
	}

	public CssSelector(String element) {
		if (element == null) {
			element = StringPool.STAR;
		}
		this.element = unescape(element);
		this.selectors = new ArrayList<Selector>();
	}

	/**
	 * Returns previous <code>CssSelector</code>.
	 */
	public CssSelector getPrevCssSelector() {
		return prevCssSelector;
	}

	/**
	 * Returns next <code>CssSelector</code>.
	 */
	public CssSelector getNextCssSelector() {
		return nextCssSelector;
	}

	// ---------------------------------------------------------------- init

	void setPrevCssSelector(CssSelector prevCssSelector) {
		this.prevCssSelector = prevCssSelector;
		prevCssSelector.nextCssSelector = this;
	}

	// ---------------------------------------------------------------- selector

	/**
	 * Returns selector element name. Returns <code>*</code> for
	 * universal selectors.
	 */
	public String getElement() {
		return element;
	}

	/**
	 * Returns selector for given index.
	 */
	public Selector getSelector(int index) {
		return selectors.get(index);
	}

	/**
	 * Returns number of selectors.
	 */
	public int selectorsCount() {
		return selectors.size();
	}

	// ---------------------------------------------------------------- combinator

	/**
	 * Sets combinator.
	 */
	public void setCombinator(Combinator combinator) {
		this.combinator = combinator;
	}

	/**
	 * Returns combinator, may be <code>null</code>.
	 */
	public Combinator getCombinator() {
		return combinator;
	}

	// ---------------------------------------------------------------- attributes

	public void addIdSelector(String id) {
		id = unescape(id);
		selectors.add(new AttributeSelector(ID, EQUALS, id));
	}

	public void addClassSelector(String clazz) {
		clazz = unescape(clazz);
		selectors.add(new AttributeSelector(CLASS, INCLUDES, clazz));
	}

	public void addAttributeSelector(String attribute) {
		attribute = unescape(attribute);
		selectors.add(new AttributeSelector(attribute));
	}

	// ---------------------------------------------------------------- pseudo class

	public void addPseudoClassSelector(String pseudoClass) {
		selectors.add(new PseudoClassSelector(pseudoClass));
	}

	public void addPseudoFunctionSelector(String pseudoFunction, String expression) {
		selectors.add(new PseudoFunctionSelector(pseudoFunction, expression));
	}

	// ---------------------------------------------------------------- string

	/**
	 * Generates CSS selector for the output.
	 */
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(element);

		for (Selector selector : selectors) {
			switch (selector.getType()) {
				case ATTRIBUTE:
					AttributeSelector attrSelector = (AttributeSelector) selector;
					String attrName = attrSelector.getName();
					if (attrName.equals(ID)) {
						out.append('#').append(attrSelector.getValue());
					} else if (attrName.equals(CLASS)) {
						out.append('.').append(attrSelector.getValue());
					} else {
						out.append('[').append(attrSelector.getName());
						String value = attrSelector.getValue();
						if (value != null) {
							out.append(attrSelector.getMatch().getSign());
							char quote = attrSelector.getQuoteChar();
							if (quote != 0) {
								out.append(quote);
							}
							out.append(value);
							if (quote != 0) {
								out.append(quote);
							}
						}
						out.append(']');
					}
					break;
				case PSEUDO_CLASS:
					PseudoClassSelector psc = (PseudoClassSelector) selector;
					out.append(':').append(psc.getPseudoClass().getPseudoClassName());
					break;
				case PSEUDO_FUNCTION:
					PseudoFunctionSelector pfns = (PseudoFunctionSelector) selector;
					out.append(':').append(pfns.getPseudoFunction().getPseudoFunctionName()).append('(');
					out.append(pfns.getExpression()).append(')');
					break;
			}
		}

		if (nextCssSelector != null) {
			if (combinator != Combinator.DESCENDANT) {
				out.append(' ');
			}
			out.append(combinator.getSign());
			if (combinator != Combinator.DESCENDANT) {
				out.append(' ');
			}
		}
		return out.toString();
	}


	// ---------------------------------------------------------------- match

	/**
	 * Accepts single node.
	 */
	public boolean accept(Node node) {
		// match element name with node name
		if (!matchElement(node)) {
			return false;
		}

		// match attributes
		int totalSelectors = selectorsCount();
		for (int i = 0; i < totalSelectors; i++) {
			Selector selector = getSelector(i);

			// just attr name existence
			switch (selector.getType()) {
				case ATTRIBUTE:
					if (!((AttributeSelector) selector).accept(node)) {
						return false;
					}
					break;
				case PSEUDO_CLASS:
					if (!((PseudoClassSelector) selector).accept(node)) {
						return false;
					}
					break;
				case PSEUDO_FUNCTION:
					if (!((PseudoFunctionSelector) selector).accept(node)) {
						return false;
					}
					break;
			}
		}
		return true;
	}

	/**
	 * Matches element to css selector. All non-element types are ignored.
	 */
	protected boolean matchElement(Node node) {
		if (node.getNodeType() != Node.NodeType.ELEMENT) {
			return false;
		}
		String element = getElement();
		String nodeName = node.getNodeName();
		return element.equals(StringPool.STAR) || element.equals(nodeName);
	}


	// ---------------------------------------------------------------- post process

	/**
	 * Accepts node within current results.
	 */
	public boolean accept(List<Node> currentResults, Node node, int index) {
		// match attributes
		int totalSelectors = selectorsCount();
		for (int i = 0; i < totalSelectors; i++) {
			Selector selector = getSelector(i);

			// just attr name existence
			switch (selector.getType()) {
				case PSEUDO_FUNCTION:
					if (!((PseudoFunctionSelector) selector).accept(currentResults, node, index)) {
						return false;
					}
					break;
				case PSEUDO_CLASS:
					if (!((PseudoClassSelector) selector).accept(currentResults, node, index)) {
						return false;
					}
					break;
				default:
			}
		}
		return true;
	}


	// ---------------------------------------------------------------- util

	/**
	 * Unescapes CSS string by removing all backslash characters from it.
	 */
	protected String unescape(String value) {
		if (value.indexOf('\\') == -1) {
			return value;
		}

		return StringUtil.remove(value, '\\');
	}

}

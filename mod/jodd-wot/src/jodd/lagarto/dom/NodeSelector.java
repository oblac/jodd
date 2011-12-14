// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.csselly.CSSelly;
import jodd.lagarto.csselly.Combinator;
import jodd.lagarto.csselly.CssSelector;
import jodd.util.StringUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Node selector selects DOM nodes using {@link CSSelly CSS3 selectors}.
 * Group of queries are supported.
 */
public class NodeSelector {

	protected final Node rootNode;

	public NodeSelector(Node rootNode) {
		this.rootNode = rootNode;
	}

	// ---------------------------------------------------------------- selector

	/**
	 * Selects nodes using CSS3 selector query.
	 */
	public LinkedList<Node> select(String query) {
		String[] singleQueries = StringUtil.splitc(query, ',');
		
		LinkedList<Node> results = new LinkedList<Node>();

		for (String singleQuery : singleQueries) {
			CSSelly csselly = createCSSelly(singleQuery);

			List<CssSelector> selectors = csselly.parse();

			List<Node> selectedNodes = select(rootNode, selectors);

			for (Node selectedNode : selectedNodes) {
				if (results.contains(selectedNode) == false) {
					results.add(selectedNode);
				}
			}
		}
		return results;
	}

	/**
	 * Creates {@link CSSelly} instance for parsing files.
	 */
	protected CSSelly createCSSelly(String cssQuery) {
		return new CSSelly(cssQuery);
	}

	/**
	 * Selects nodes using CSS3 selector query and returns the very first one.
	 */
	public Node selectFirst(String query) {
		List<Node> selectedNodes = select(query);
		if (selectedNodes.isEmpty()) {
			return null;
		}
		return selectedNodes.get(0);
	}


	public LinkedList<Node> select(NodeFilter nodeFilter) {
		LinkedList<Node> nodes = new LinkedList<Node>();
		walk(rootNode, nodeFilter, nodes);
		return nodes;
	}

	public Node selectFirst(NodeFilter nodeFilter) {
		List<Node> selectedNodes = select(nodeFilter);
		if (selectedNodes.isEmpty()) {
			return null;
		}
		return selectedNodes.get(0);
	}

	// ---------------------------------------------------------------- internal

	protected void walk(Node rootNode, NodeFilter nodeFilter, LinkedList<Node> result) {
		int childCount = rootNode.getChildNodesCount();
		for (int i = 0; i < childCount; i++) {
			Node node = rootNode.getChild(i);
			if (nodeFilter.accept(node)) {
				result.add(node);
			}
			walk(node, nodeFilter, result);
		}
	}

	protected LinkedList<Node> select(Node rootNode, List<CssSelector> selectors) {

		// start with the root node
		LinkedList<Node> nodes = new LinkedList<Node>();
		nodes.add(rootNode);

		// iterate all selectors
		for (CssSelector cssSelector : selectors) {

			// create new set of results for current css selector
			LinkedList<Node> selectedNodes = new LinkedList<Node>();
			for (Node node : nodes) {
				walk(node, cssSelector, selectedNodes);
			}

			// post-processing: filter out the results
			LinkedList<Node> resultNodes = new LinkedList<Node>();
			int index = 0;
			for (Node node : selectedNodes) {
				boolean match = filter(selectedNodes, node, cssSelector, index);
				if (match == true) {
					resultNodes.add(node);
				}
				index++;
			}

			// continue with results
			nodes = resultNodes;
		}

		return nodes;
	}

	/**
	 * Finds nodes in the tree that matches single selector.
	 */
	protected void walk(Node rootNode, CssSelector cssSelector, LinkedList<Node> result) {

		// previous combinator determines the behavior
		CssSelector previousCssSelector = cssSelector.getPrevCssSelector();
		Combinator combinator = previousCssSelector != null ?
				previousCssSelector.getCombinator() :
				Combinator.DESCENDANT;

		switch (combinator) {
			case DESCENDANT:
				int childCount = rootNode.getChildNodesCount();
				for (int i = 0; i < childCount; i++) {
					Node node = rootNode.getChild(i);
					selectAndAdd(node, cssSelector, result);
					walk(node, cssSelector, result);
				}
				break;
			case CHILD:
				childCount = rootNode.getChildNodesCount();
				for (int i = 0; i < childCount; i++) {
					Node node = rootNode.getChild(i);
					selectAndAdd(node, cssSelector, result);
				}
				break;
			case ADJACENT_SIBLING:
				Node node = rootNode.getNextSiblingElement();
				if (node != null) {
					selectAndAdd(node, cssSelector, result);
				}
				break;
			case GENERAL_SIBLING:
				node = rootNode;
				while (true) {
					node = node.getNextSiblingElement();
					if (node == null) {
						break;
					}
					selectAndAdd(node, cssSelector, result);
				}
				break;
		}	}

	/**
	 * Selects single node for single selector and appends it to the results.
	 */
	protected void selectAndAdd(Node node, CssSelector cssSelector, LinkedList<Node> result) {
		// ignore all nodes that are not elements
		if (node.getNodeType() != Node.NodeType.ELEMENT) {
			return;
		}
		boolean matched = cssSelector.accept(node);
		if (matched) {
			// check for duplicates
			if (result.contains(node)) {
				return;
			}
			// no duplicate found, add it to the results
			result.add(node);
		}
	}

	/**
	 * Filter nodes.
	 */
	protected boolean filter(LinkedList<Node> currentResults, Node node, CssSelector cssSelector, int index) {
		return cssSelector.accept(currentResults, node, index);
	}

}
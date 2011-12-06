// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.csselly.CSSelly;
import jodd.lagarto.csselly.Combinator;
import jodd.lagarto.csselly.CssSelector;

import java.util.LinkedList;
import java.util.List;

/**
 * Node selector selects DOM nodes using {@link CSSelly CSS3 selectors}.
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
		CSSelly csselly = new CSSelly(query);

		List<CssSelector> selectors = csselly.parse();

		return select(rootNode, selectors);
	}

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

	// ---------------------------------------------------------------- internal #1

	protected LinkedList<Node> select(Node rootNode, List<CssSelector> selectors) {

		// start with the root node
		LinkedList<Node> nodes = new LinkedList<Node>();
		nodes.add(rootNode);

		// iterate all selectors
		for (CssSelector cssSelector : selectors) {

			// new set of results
			LinkedList<Node> selectedNodes = new LinkedList<Node>();
			for (Node node : nodes) {
				walk(node, cssSelector, selectedNodes);
			}
			nodes = selectedNodes;
		}

		return nodes;
	}

	/**
	 * Finds nodes in tree that matches selector.
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
					select(node, cssSelector, result);
					walk(node, cssSelector, result);
				}
				break;
			case CHILD:
				childCount = rootNode.getChildNodesCount();
				for (int i = 0; i < childCount; i++) {
					Node node = rootNode.getChild(i);
					select(node, cssSelector, result);
				}
				break;
			case ADJACENT_SIBLING:
				Node node = rootNode.getNextSiblingElement();
				if (node != null) {
					select(node, cssSelector, result);
				}
				break;
			case GENERAL_SIBLING:
				node = rootNode;
				while (true) {
					node = node.getNextSiblingElement();
					if (node == null) {
						break;
					}
					select(node, cssSelector, result);
				}
				break;
		}
	}

	/**
	 * Selects single node and single selector.
	 */
	protected void select(Node node, CssSelector cssSelector, LinkedList<Node> result) {
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
}
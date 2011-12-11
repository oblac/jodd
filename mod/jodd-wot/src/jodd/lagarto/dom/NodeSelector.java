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
	 * Finds nodes in the tree that matches single selector.
	 */
	protected void walk(Node rootNode, CssSelector cssSelector, LinkedList<Node> result) {

		// previous combinator determines the behavior
		CssSelector previousCssSelector = cssSelector.getPrevCssSelector();
		Combinator combinator = previousCssSelector != null ?
				previousCssSelector.getCombinator() :
				Combinator.DESCENDANT;

		int childCount = rootNode.getChildNodesCount();

		// collect all results for this selector
		LinkedList<Node> currentResults = new LinkedList<Node>();
		
		switch (combinator) {
			case DESCENDANT: {
				// process children
				for (int i = 0; i < childCount; i++) {
					Node node = rootNode.getChild(i);
					boolean matched = select2(node, cssSelector);
					if (matched) {
						currentResults.add(node);
					}
				}
				
				// post-process current results and merge with results
				for (Node node : currentResults) {
					boolean matched = select3(currentResults, node, cssSelector);
					if (matched) {
						addNodeToResults(result, node);
					}
				}
				
				// walk all children
				for (int i = 0; i < childCount; i++) {
					Node node = rootNode.getChild(i);
					walk(node, cssSelector, result);
				}
			}
			break;

			case CHILD: {
				// process children
				for (int i = 0; i < childCount; i++) {
					Node node = rootNode.getChild(i);
					boolean matched = select2(node, cssSelector);
					if (matched) {
						currentResults.add(node);
					}
				}

				// post-process current results and merge with results
				for (Node node : currentResults) {
					boolean matched = select3(currentResults, node, cssSelector);
					if (matched) {
						addNodeToResults(result, node);
					}
				}
			}
			break;

			case ADJACENT_SIBLING: {
				// process children
				Node node = rootNode.getNextSiblingElement();
				if (node != null) {
					boolean matched = select2(node, cssSelector);
					if (matched) {
						currentResults.add(node);
					}

					// post-process results
					if (matched) {
						matched = select3(currentResults, node, cssSelector);
						if (matched) {
							addNodeToResults(result, node);
						}
					}
				}
			}
			break;

			case GENERAL_SIBLING: {
				// process children
				Node node = rootNode;
				while (true) {
					node = node.getNextSiblingElement();
					if (node == null) {
						break;
					}
					boolean matched = select2(node, cssSelector);
					if (matched) {
						currentResults.add(node);
					}
				}

				// post-process current results and merge with results
				for (Node node2 : currentResults) {
					boolean matched = select3(currentResults, node2, cssSelector);
					if (matched) {
						addNodeToResults(result, node2);
					}
				}
			}
			break;
		}
	}

	/**
	 * Adds node to results.
	 */
	protected void addNodeToResults(LinkedList<Node> result, Node node) {
		// check for duplicates
		if (result.contains(node)) {
			return;
		}
		// no duplicate found, add it to the results
		result.add(node);
	}

	/**
	 * Selects single node by single selector.
	 */
	protected boolean select2(Node node, CssSelector cssSelector) {
		// ignore all nodes that are not elements
		if (node.getNodeType() != Node.NodeType.ELEMENT) {
			return false;
		}
		return cssSelector.accept(node);
	}

	/**
	 * Post-selects node.
	 */
	protected boolean select3(LinkedList<Node> currentResults, Node node, CssSelector cssSelector) {
		return cssSelector.accept(currentResults, node);
	}

}
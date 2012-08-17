// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoLexer;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;
import jodd.lagarto.TagVisitor;
import jodd.log.Log;
import jodd.util.StringPool;

import java.util.LinkedList;

/**
 * Lagarto tag visitor that builds DOM tree.
 */
public class DOMBuilderTagVisitor implements TagVisitor {

	private static final Log log = Log.getLogger(DOMBuilderTagVisitor.class);

	protected final LagartoDOMBuilder domBuilder;
	protected final HtmlImplicitClosingRules implRules = new HtmlImplicitClosingRules();

	private long startTime;
	protected Document rootNode;
	protected Node parentNode;
	/**
	 * While enabled, nodes will be added to the DOM tree.
	 * Useful for skipping some tags.
	 */
	protected boolean enabled;

	public DOMBuilderTagVisitor(LagartoDOMBuilder domBuilder) {
		this.domBuilder = domBuilder;
	}

	/**
	 * Returns root {@link Document document} node of parsed DOM tree.
	 */
	public Document getDocument() {
		return rootNode;
	}

	// ---------------------------------------------------------------- start/end

	public void start() {
		startTime = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
			log.debug("DomTree builder started.");
		}

		rootNode = new Document();
		parentNode = rootNode;
		enabled = true;
	}

	public void end() {
		if (parentNode != rootNode) {

			if (!domBuilder.isImpliedEndTags()) {
				if (log.isWarnEnabled()) {
					String positionString = StringPool.EMPTY;
					if (domBuilder.isCalculatePosition()) {
						positionString = parentNode.position.toString();
					}
					log.warn("Some unclosed tags are closed: <" + parentNode.getNodeName() + "> " + positionString);
				}
			}
				// nothing to fix here, assuming all tags are implicitly closed on EOF
		}

		// remove whitespaces
		if (domBuilder.isIgnoreWhitespacesBetweenTags()) {
			removeLastChildNodeIfEmptyText(parentNode, true);
		}

		// elapsed
		domBuilder.elapsed = System.currentTimeMillis() - startTime;

		if (log.isDebugEnabled()) {
			log.debug("LagartoDom tree created in " + domBuilder.elapsed + " ms.");
		}
	}

	// ---------------------------------------------------------------- tag

	/**
	 * Creates new element with correct configuration.
	 */
	protected Element createElementNode(Tag tag) {
		boolean isVoid = domBuilder.isVoidTag(tag.getName());
		boolean selfClosed = false;

		if (domBuilder.hasVoidTags()) {
			// HTML ad XHTML
			if (isVoid) {
				// it's void tag, lookup the flag
				selfClosed = domBuilder.isSelfCloseVoidTags();
			}
		} else {
			// XML, no voids, lookup the flag
			selfClosed = domBuilder.isSelfCloseVoidTags();
		}
		
		Element element = new Element(tag, isVoid, selfClosed, domBuilder.isCaseSensitive());

		if (domBuilder.isCalculatePosition()) {
			element.position = calculatePosition(tag);
		}

		return element;
	}

	public void tag(Tag tag) {
		if (!enabled) {
			return;
		}

		TagType tagType = tag.getType();
		Element node;

		switch (tagType) {
			case START:
				if (domBuilder.isIgnoreWhitespacesBetweenTags()) {
					removeLastChildNodeIfEmptyText(parentNode, false);
				}

				node = createElementNode(tag);

				if (domBuilder.isImpliedEndTags()) {
					while (true) {
						String parentNodeName = parentNode.getNodeName();
						if (!implRules.implicitlyCloseParentTagOnNewTag(parentNodeName, node.getNodeName())) {
							break;
						}
						parentNode = parentNode.getParentNode();

						if (log.isDebugEnabled()) {
							String positionString = StringPool.EMPTY;
							if (domBuilder.isCalculatePosition()) {
								positionString = parentNode.position.toString();
							}
							log.debug("Implicitly closed tag <" + node.getNodeName() + "> " + positionString);
						}
					}
				}

				parentNode.addChild(node);

				if (node.isVoidElement() == false) {
					parentNode = node;
				}
				break;

			case END:
				if (domBuilder.isIgnoreWhitespacesBetweenTags()) {
					removeLastChildNodeIfEmptyText(parentNode, true);
				}

				String tagName = tag.getName();

				Node matchingParent = findMatchingParentOpenTag(tagName);

				if (matchingParent == parentNode) {		// regular situation
					parentNode = parentNode.getParentNode();
					break;
				}

				if (matchingParent == null) {			// matching open tag not found, remove it

					String positionString = StringPool.EMPTY;
					if (domBuilder.isCalculatePosition()) {
						positionString = calculatePosition(tag).toString();
					}
					error("Orphan closed tag ignored: </" + tagName + "> " + positionString);
					break;
				}

				// try to close it implicitly
				if (domBuilder.isImpliedEndTags()) {
					boolean fixed = false;
					while (implRules.implicitlyCloseParentTagOnTagEnd(parentNode.getNodeName(), tagName)) {
						parentNode = parentNode.getParentNode();

						if (log.isDebugEnabled()) {
							String positionString = StringPool.EMPTY;
							if (domBuilder.isCalculatePosition()) {
								positionString = parentNode.position.toString();
							}
							log.debug("Implicitly closed tag <" + tagName + "> " + positionString);
						}

						if (parentNode == matchingParent) {
							parentNode = matchingParent.parentNode;
							fixed = true;
							break;
						}
					}
					if (fixed) {
						break;
					}
				}


				// matching tag found, but it is not a regular situation
				// therefore close all unclosed tags in between
				fixUnclosedTagsUpToMatchingParent(matchingParent);

				break;

			case SELF_CLOSING:
				if (domBuilder.isIgnoreWhitespacesBetweenTags()) {
					removeLastChildNodeIfEmptyText(parentNode, false);
				}

				node = createElementNode(tag);
				parentNode.addChild(node);
				break;
		}
	}

	// ---------------------------------------------------------------- util

	/**
	 * Removes last child node if contains just empty text.
	 */
	protected void removeLastChildNodeIfEmptyText(Node parentNode, boolean closedTag) {
		if (parentNode == null) {
			return;
		}

		Node lastChild = parentNode.getLastChild();
		if (lastChild == null) {
			return;
		}
		
		if (lastChild.getNodeType() != Node.NodeType.TEXT) {
			return;
		}

		if (closedTag) {
			if (parentNode.getChildNodesCount() == 1) {
				return;
			}
		}

		Text text = (Text) lastChild;

		if (text.isBlank()) {
			lastChild.detachFromParent();
		}
	}

	/**
	 * Finds matching parent open tag or <code>null</code> if not found.
	 */
	protected Node findMatchingParentOpenTag(String tagName) {
		Node parent = parentNode;
		
		if (domBuilder.isCaseSensitive() == false) {
			tagName = tagName.toLowerCase();
		}
		
		while (parent != null) {
			
			String parentNodeName = parent.getNodeName();

			if (parentNodeName != null) {
				if (domBuilder.isCaseSensitive() == false) {
					parentNodeName = parentNodeName.toLowerCase();
				}
			}
			
			if (tagName.equals(parentNodeName)) {
				return parent;
			}
			parent = parent.getParentNode();
		}
		return null;
	}

	/**
	 * Fixes all unclosed tags up to matching parent.
	 */
	protected void fixUnclosedTagsUpToMatchingParent(Node matchingParent) {
		LinkedList<Node> finalNodes = new LinkedList<Node>();

		while (true) {

			if (parentNode == matchingParent) {
				parentNode = parentNode.getParentNode();
				break;
			}

			Node parentParentNode = parentNode.getParentNode();

			parentNode.detachFromParent();

			String positionString = StringPool.EMPTY;
			if (domBuilder.isCalculatePosition()) {
				positionString = parentNode.position.toString();
			}

			error("Unclosed tag closed: <" + parentNode.getNodeName() + "> " + positionString);

			finalNodes.addFirst(parentNode);
			parentNode = parentParentNode;
		}

		Node[] newChilds = new Node[finalNodes.size()];

		for (int i = 0; i < finalNodes.size(); i++) {
			Node node = finalNodes.get(i);
			newChilds[i] = node;
		}

		matchingParent.addChild(newChilds);
	}

	// ---------------------------------------------------------------- tree

	public void xmp(Tag tag, CharSequence body) {
		if (!enabled) {
			return;
		}

		Node node = createElementNode(tag);
		parentNode.addChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.addChild(text);
		}
	}

	public void style(Tag tag, CharSequence body) {
		if (!enabled) {
			return;
		}

		Element node = createElementNode(tag);
		parentNode.addChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.addChild(text);
		}
	}

	public void script(Tag tag, CharSequence body) {
		if (!enabled) {
			return;
		}

		Element node = createElementNode(tag);
		parentNode.addChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.addChild(text);
		}
	}

	public void comment(CharSequence comment) {
		if (!enabled) {
			return;
		}

		if (domBuilder.isIgnoreWhitespacesBetweenTags()) {
			removeLastChildNodeIfEmptyText(parentNode, false);
		}
		if (domBuilder.isIgnoreComments()) {
			return;
		}
		Node node = new Comment(comment.toString());
		parentNode.addChild(node);
	}

	public void text(CharSequence text) {
		if (!enabled) {
			return;
		}

		String textValue = text.toString();
		Node node = new Text(textValue);
		parentNode.addChild(node);
	}

	public void cdata(CharSequence cdata) {
		if (!enabled) {
			return;
		}

		CData cdataNode = new CData(cdata.toString());
		parentNode.addChild(cdataNode);
	}

	public void xml(Tag tag) {
		if (!enabled) {
			return;
		}

		XmlDeclaration xmlDeclaration = new XmlDeclaration(tag, domBuilder.isCaseSensitive());
		parentNode.addChild(xmlDeclaration);
	}

	public void doctype(String name, String publicId, String baseUri) {
		if (!enabled) {
			return;
		}

		DocumentType documentType = new DocumentType(name, publicId, baseUri);
		parentNode.addChild(documentType);
	}

	public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, CharSequence comment) {
		String defaultExpression = domBuilder.getConditionalCommentExpression();

		if (defaultExpression != null) {
			String expressionString = expression.toString().trim();

			if (expressionString.equals(defaultExpression) == false) {
				enabled = expressionString.equals("endif");
			}
		} else {
			if (!enabled) {
				return;
			}

			String additionalComment = comment != null ? comment.toString() : null;
			Node commentNode = new Comment(expression.toString(), isStartingTag, isHidden, additionalComment);

			parentNode.addChild(commentNode);
		}
	}

	// ---------------------------------------------------------------- error

	public void error(String message) {
		if (domBuilder.isCollectErrors()) {
			if (domBuilder.errors == null) {
				domBuilder.errors = new LinkedList<String>();
			}
			domBuilder.errors.add(message);
		}
		if (log.isWarnEnabled()) {
			log.warn(message);
		}
	}


	/**
	 * Calculates position of a tag.
	 */
	protected LagartoLexer.Position calculatePosition(Tag tag) {
		LagartoLexer lexer = domBuilder.getLexer();

		LagartoLexer.Position position = lexer.currentPosition();

		int column = position.column;

		if (tag.getName() != null) {
			column -= tag.getName().length();
		}
		for (int i = 0; i < tag.getAttributeCount(); i++) {
			column -= tag.getAttributeName(i).length();
			String value = tag.getAttributeValue(i);
			if (value != null) {
				column -= value.length();
				column--;	// for '='
			}
			column--;		// for attribute separation
		}

		int diff = position.column - column;

		position.column = column;
		position.offset -= diff;

		return position;
	}

}
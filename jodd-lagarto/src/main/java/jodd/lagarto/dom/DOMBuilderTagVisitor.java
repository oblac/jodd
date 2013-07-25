// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoLexer;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;
import jodd.lagarto.TagVisitor;
import jodd.util.StringPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Lagarto tag visitor that builds DOM tree.
 */
public class DOMBuilderTagVisitor implements TagVisitor {

	private static final Logger log = LoggerFactory.getLogger(DOMBuilderTagVisitor.class);

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

		rootNode = domBuilder.createDocument();
		parentNode = rootNode;
		enabled = true;
	}

	public void end() {
		if (parentNode != rootNode) {

			if (!domBuilder.isImpliedEndTags()) {
				if (log.isWarnEnabled()) {
					String positionString = StringPool.EMPTY;
					if (parentNode.position != null) {
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
		
		Element element = domBuilder.createElement(tag, isVoid, selfClosed);

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
							if (parentNode.position != null) {
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
							if (parentNode.position != null) {
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
	 * Fixes all unclosed tags up to matching parent. This may work in two ways, in general:
	 * <li>A) closing tags as soon as possible</li>
	 * <li>B) closing tags as late as possible</li>
	 * <p>
	 * Solution A means that missing end tag will be added right after the starting tag, making
	 * the invalid tag without the body. Solution B means that missing end tag will be added
	 * just before parent tag is closed, making the whole inner content as its tag body.
	 * <p>
	 * These are just generic solutions, and solution B is the closest to the rules.
	 */
	protected void fixUnclosedTagsUpToMatchingParent(Node matchingParent) {
		while (true) {
			if (parentNode == matchingParent) {
				parentNode = parentNode.getParentNode();
				break;
			}

			Node parentParentNode = parentNode.getParentNode();

			if (implRules.implicitlyCloseParentTagOnNewTag(parentParentNode.getNodeName(), parentNode.getNodeName())) {
				// break the tree: detach this node and append it after parent

				parentNode.detachFromParent();

				parentParentNode.getParentNode().addChild(parentNode);
			}

			// debug message

			String positionString = StringPool.EMPTY;
			if (parentNode.position != null) {
				positionString = parentNode.position.toString();
			}

			error("Unclosed tag closed: <" + parentNode.getNodeName() + "> " + positionString);

			// continue looping
			parentNode = parentParentNode;
		}
	}

	// ---------------------------------------------------------------- tree

	public void xmp(Tag tag, CharSequence body) {
		if (!enabled) {
			return;
		}

		Node node = createElementNode(tag);
		parentNode.addChild(node);

		if (body.length() != 0) {
			Node text = domBuilder.createText(body.toString());
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
			Node text = domBuilder.createText(body.toString());
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
			Node text = domBuilder.createText(body.toString());
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
		Node node = domBuilder.createComment(comment.toString());
		parentNode.addChild(node);
	}

	public void text(CharSequence text) {
		if (!enabled) {
			return;
		}

		String textValue = text.toString();
		Node node = domBuilder.createText(textValue);
		parentNode.addChild(node);
	}

	public void cdata(CharSequence cdata) {
		if (!enabled) {
			return;
		}

		CData cdataNode = domBuilder.createCData(cdata.toString());
		parentNode.addChild(cdataNode);
	}

	public void xml(Tag tag) {
		if (!enabled) {
			return;
		}

		XmlDeclaration xmlDeclaration = domBuilder.createXmlDeclaration(tag);
		parentNode.addChild(xmlDeclaration);
	}

	public void doctype(String name, String publicId, String baseUri) {
		if (!enabled) {
			return;
		}

		DocumentType documentType = domBuilder.createDocumentType(name, publicId, baseUri);
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
			Node commentNode = domBuilder.createConditionalComment(expression.toString(), isStartingTag, isHidden, additionalComment);

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
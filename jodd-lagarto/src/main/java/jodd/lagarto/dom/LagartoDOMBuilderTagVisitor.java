// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoParserContext;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;
import jodd.lagarto.TagVisitor;
import jodd.util.StringPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lagarto tag visitor that builds DOM tree.
 */
public class LagartoDOMBuilderTagVisitor implements TagVisitor {

	private static final Logger log = LoggerFactory.getLogger(LagartoDOMBuilderTagVisitor.class);

	protected final LagartoDOMBuilder domBuilder;
	protected final HtmlImplicitClosingRules implRules = new HtmlImplicitClosingRules();

	protected Document rootNode;
	protected Node parentNode;
	/**
	 * While enabled, nodes will be added to the DOM tree.
	 * Useful for skipping some tags.
	 */
	protected boolean enabled;

	public LagartoDOMBuilderTagVisitor(LagartoDOMBuilder domBuilder) {
		this.domBuilder = domBuilder;
	}

	/**
	 * Returns root {@link Document document} node of parsed DOM tree.
	 */
	public Document getDocument() {
		return rootNode;
	}

	// ---------------------------------------------------------------- start/end

	protected LagartoParserContext parserContext;

	public void start(LagartoParserContext parserContext) {
		if (log.isDebugEnabled()) {
			log.debug("DomTree builder started.");
		}

		this.parserContext = parserContext;

		if (rootNode == null) {
			rootNode = createDocument();
		}
		parentNode = rootNode;
		enabled = true;
	}

	public void end() {
		if (parentNode != rootNode) {

			Node thisNode = parentNode;

			while (thisNode != rootNode) {
				if (domBuilder.isImpliedEndTags()) {
					if (implRules.implicitlyCloseTagOnEOF(thisNode.getNodeName())) {
						thisNode = thisNode.getParentNode();
						continue;
					}
				}

				error("Unclosed tag closed: <" +
						thisNode.getNodeName() + "> " +
						thisNode.getPositionString());

				thisNode = thisNode.getParentNode();
			}
		}

		// remove whitespaces
		if (domBuilder.isIgnoreWhitespacesBetweenTags()) {
			removeLastChildNodeIfEmptyText(parentNode, true);
		}

		// foster
		if (domBuilder.isUseFosterRules()) {
			HtmlFosterRules fosterRules = new HtmlFosterRules();
			fosterRules.fixFosterElements(rootNode);
		}

		// elapsed
		rootNode.end();

		if (log.isDebugEnabled()) {
			log.debug("LagartoDom tree created in " + rootNode.getElapsedTime() + " ms.");
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
		
		return createElement(tag, isVoid, selfClosed);
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
							log.debug("Implicitly closed tag <" +
									node.getNodeName() + "> " +
									parentNode.getPositionString());
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
						positionString = tag.calculateTagPosition().toString();
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
							log.debug("Implicitly closed tag <" +
									tagName + "> " + parentNode.getPositionString());
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
				fixUnclosedTagsUpToMatchingParent(tag, matchingParent);

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
		
		if (rootNode.isLowercase()) {
			tagName = tagName.toLowerCase();
		}
		
		while (parent != null) {
			
			String parentNodeName = parent.getNodeName();

			if (parentNodeName != null) {
				if (rootNode.isLowercase()) {
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
	 * Fixes all unclosed tags up to matching parent. Missing end tags will be added
	 * just before parent tag is closed, making the whole inner content as its tag body.
	 * <p>
	 * Tags that can be closed implicitly are checked and closed.
	 * <p>
	 * There is optional check for detecting orphan tags inside the
	 * table or lists. If set, tags can be closed beyond the border of the
	 * table and the list and it is reported as orphan tag.
	 * <p>
	 * This is just a generic solutions, closest to the rules.
	 */
	protected void fixUnclosedTagsUpToMatchingParent(Tag tag, Node matchingParent) {
		if (domBuilder.isUnclosedTagAsOrphanCheck()) {
			Node thisNode = parentNode;

			if (!tag.getName().equals("table")) {

				// check if there is table or list between this node
				// and matching parent
				while (thisNode != matchingParent) {
					String thisNodeName = thisNode.getNodeName().toLowerCase();
					if (thisNodeName.equals("table") || thisNodeName.equals("ul") || thisNodeName.equals("ol")) {

						String positionString = StringPool.EMPTY;
						if (domBuilder.isCalculatePosition()) {
							positionString = tag.calculateTagPosition().toString();
						}

						error("Orphan closed tag ignored: </" + tag.getName() + "> " + positionString);
						return;
					}
					thisNode = thisNode.getParentNode();
				}
			}
		}

		while (true) {
			if (parentNode == matchingParent) {
				parentNode = parentNode.getParentNode();
				break;
			}

			Node parentParentNode = parentNode.getParentNode();

			if (domBuilder.isImpliedEndTags()) {
				if (implRules.implicitlyCloseParentTagOnNewTag(
						parentParentNode.getNodeName(), parentNode.getNodeName())) {
					// break the tree: detach this node and append it after parent

					parentNode.detachFromParent();

					parentParentNode.getParentNode().addChild(parentNode);
				}
			}

			// debug message

			error("Unclosed tag closed: <" + parentNode.getNodeName() + "> " + parentNode.getPositionString());

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
			Node text = createText(body.toString());
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
			Node text = createText(body.toString());
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
			Node text = createText(body.toString());
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
		Node node = createComment(comment.toString());
		parentNode.addChild(node);
	}

	public void text(CharSequence text) {
		if (!enabled) {
			return;
		}

		String textValue = text.toString();
		Node node = createText(textValue);
		parentNode.addChild(node);
	}

	public void cdata(CharSequence cdata) {
		if (!enabled) {
			return;
		}

		CData cdataNode = createCData(cdata.toString());
		parentNode.addChild(cdataNode);
	}

	public void xml(Tag tag) {
		if (!enabled) {
			return;
		}

		XmlDeclaration xmlDeclaration = createXmlDeclaration(tag);
		parentNode.addChild(xmlDeclaration);
	}

	public void doctype(String name, String publicId, String baseUri) {
		if (!enabled) {
			return;
		}

		DocumentType documentType = createDocumentType(name, publicId, baseUri);
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
			Node commentNode = createConditionalComment(expression.toString(), isStartingTag, isHidden, additionalComment);

			parentNode.addChild(commentNode);
		}
	}

	// ---------------------------------------------------------------- error

	public void error(String message) {
		rootNode.addError(message);

		if (log.isWarnEnabled()) {
			log.warn(message);
		}
	}

	// ---------------------------------------------------------------- factory

	/**
	 * Creates root {@link Document} node.
	 */
	protected Document createDocument() {
		return new Document(
				!domBuilder.isCaseSensitive(),
				domBuilder.isCollectErrors(),
				domBuilder.getRenderer(),
				parserContext
				);
	}

	/**
	 * Creates {@link CData tag}.
	 */
	protected CData createCData(String cdata) {
		return new CData(rootNode, cdata);
	}

	/**
	 * Creates {@link Comment}.
	 * @see Comment#Comment(Document, String)
	 */
	protected Comment createComment(String comment) {
		return new Comment(rootNode, comment);
	}

	/**
	 * Creates conditional {@link Comment}.
	 * @see Comment#Comment(Document, String, boolean, boolean, String)
	 */
	protected Comment createConditionalComment(String comment, boolean isStartingTag, boolean conditionalDownlevelHidden, String additionalComment) {
		return new Comment(rootNode, comment, isStartingTag, conditionalDownlevelHidden, additionalComment);
	}


	/**
	 * Creates {@link Element} node from a {@link Tag}.
	 */
	protected Element createElement(Tag tag, boolean voidElement, boolean selfClosed) {
		Element element = new Element(rootNode, tag, voidElement, selfClosed);

		if (domBuilder.isCalculatePosition()) {
			element.position = tag.calculateTagPosition();
		}

		return element;
	}

	/**
	 * Creates empty tag.
	 */
	protected Element createElement(String name) {
		return new Element(rootNode, name, false, false);
	}

	/**
	 * Creates empty {@link Element} node.
	 */
	protected Element createElement(String tagName, boolean voidElement, boolean selfClosed) {
		return new Element(rootNode, tagName, voidElement, selfClosed);
	}

	/**
	 * Creates {@link Text} node.
	 */
	protected Text createText(String text) {
		return new Text(rootNode, text);
	}

	protected DocumentType createDocumentType(String value, String publicId, String baseUri) {
		return new DocumentType(rootNode, value, publicId, baseUri);
	}

	protected XmlDeclaration createXmlDeclaration(Tag tag) {
		return new XmlDeclaration(rootNode, tag);
	}

}
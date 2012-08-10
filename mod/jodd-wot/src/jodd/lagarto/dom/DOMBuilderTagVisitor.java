// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoLexer;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;
import jodd.lagarto.TagVisitor;
import jodd.log.Log;
import jodd.util.StringPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Lagarto tag visitor that builds DOM tree.
 */
public class DOMBuilderTagVisitor implements TagVisitor {

	private static final Log log = Log.getLogger(DOMBuilderTagVisitor.class);

	protected final LagartoDOMBuilder domBuilder;

	private long startTime;
	private List<String> errors;

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

	// ---------------------------------------------------------------- visitor

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
		// fix open tags
		if (parentNode != rootNode) {
			log.warn("Some tags are not closed.");
			fixUpToMatchingPoint(rootNode);
		}

		// remove whitespaces
		if (domBuilder.isIgnoreWhitespacesBetweenTags()) {
			removeLastChildNodeIfEmptyText(parentNode, true);
		}

		// the end
		rootNode.errors = this.errors;

		if (log.isDebugEnabled()) {
			long elapsed = System.currentTimeMillis() - startTime;
			log.debug("DomTree created in " + elapsed + " ms.");
		}
	}

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

		if (domBuilder.isCalculateErrorPosition()) {
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

				parentNode.appendChild(node);

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
					if (log.isWarnEnabled()) {
						String positionString = StringPool.EMPTY;
						if (domBuilder.isCalculateErrorPosition()) {
							positionString = calculatePosition(tag).toString();
						}

						log.warn("Orphan closed tag: </" + tagName + "> " + positionString + " ignored.");
					}
					break;
				}

				// matching tag found, but it is not a regular situation
				// therefore close all unclosed tags in between
				fixUpToMatchingPoint(matchingParent);

				break;

			case SELF_CLOSING:
				if (domBuilder.isIgnoreWhitespacesBetweenTags()) {
					removeLastChildNodeIfEmptyText(parentNode, false);
				}

				node = createElementNode(tag);
				parentNode.appendChild(node);
				break;
		}
	}

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
	 * Fixes unclosed tags. Adds closing tag to wrap one non-blank element.
	 */
	protected void fixUpToMatchingPoint(Node matchingParent) {
		while (true) {
			String nodeName = parentNode.getNodeName();

			if (parentNode == matchingParent) {
				parentNode = parentNode.getParentNode();
				break;
			}

			// [*] get and remove all children of parent node (problematic, the open one)
			Node[] childNodes = parentNode.getChildNodes();
			parentNode.removeAllChilds();

			// [*] find the first non blank node and re-attach to parent
			int ndx = 0;
			while (ndx < childNodes.length) {
				Node child = childNodes[ndx];
				if (child.getNodeType() == Node.NodeType.TEXT) {
					if (((Text)child).isBlank()) {
						parentNode.appendChild(child);	// append blank nodes
						ndx++;
						continue;
					}
				}
				parentNode.appendChild(child);
				break;
			}

			// [*] append remaining children to the parent parent node (good node)
			Node parentParentNode = parentNode.getParentNode();
			ndx++;
			while (ndx < childNodes.length) {
				Node child = childNodes[ndx];
				parentParentNode.appendChild(child);
				ndx++;
			}

			if (log.isWarnEnabled()) {
				String positionString = StringPool.EMPTY;
				if (domBuilder.isCalculateErrorPosition()) {
					positionString = parentNode.position.toString();
				}
				log.warn("Unclosed tag: <" + nodeName + "> " + positionString + " closed.");
			}
			parentNode = parentParentNode;
		}
	}

	public void xmp(Tag tag, CharSequence body) {
		if (!enabled) {
			return;
		}

		Node node = createElementNode(tag);
		parentNode.appendChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.appendChild(text);
		}
	}

	public void style(Tag tag, CharSequence body) {
		if (!enabled) {
			return;
		}

		Element node = createElementNode(tag);
		parentNode.appendChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.appendChild(text);
		}
	}

	public void script(Tag tag, CharSequence body) {
		if (!enabled) {
			return;
		}

		Element node = createElementNode(tag);
		parentNode.appendChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.appendChild(text);
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
		parentNode.appendChild(node);
	}

	public void text(CharSequence text) {
		if (!enabled) {
			return;
		}

		String textValue = text.toString();
		Node node = new Text(textValue);
		parentNode.appendChild(node);
	}

	public void cdata(CharSequence cdata) {
		if (!enabled) {
			return;
		}

		CData cdataNode = new CData(cdata.toString());
		parentNode.appendChild(cdataNode);
	}

	public void xml(Tag tag) {
		if (!enabled) {
			return;
		}

		XmlDeclaration xmlDeclaration = new XmlDeclaration(tag, domBuilder.isCaseSensitive());
		parentNode.appendChild(xmlDeclaration);
	}

	public void doctype(String name, String publicId, String baseUri) {
		if (!enabled) {
			return;
		}

		DocumentType documentType = new DocumentType(name, publicId, baseUri);
		parentNode.appendChild(documentType);
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

			parentNode.appendChild(commentNode);
		}
	}

	public void error(String message) {
		if (domBuilder.collectErrors) {
			if (errors == null) {
				errors = new ArrayList<String>();
			}
			errors.add(message);
		}
		if (log.isWarnEnabled()) {
			log.warn("DOM tree may be corrupted due to parsing error. " + message);
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
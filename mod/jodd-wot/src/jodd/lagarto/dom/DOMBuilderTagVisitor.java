// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;
import jodd.lagarto.TagType;
import jodd.lagarto.TagVisitor;
import jodd.log.Log;
import jodd.util.StringUtil;

/**
 * Lagarto tag visitor that builds DOM tree.
 */
public class DOMBuilderTagVisitor implements TagVisitor {

	private static final Log log = Log.getLogger(DOMBuilderTagVisitor.class);

	protected final LagartoDOMBuilder builder;

	private long startTime;

	protected Document rootNode;
	protected Node parentNode;

	public DOMBuilderTagVisitor(LagartoDOMBuilder builder) {
		this.builder = builder;
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
	}

	public void end() {
		// fix open tags
		if (parentNode != rootNode) {
			log.warn("Some tags are not closed.");
			fixUpToMatchingPoint(rootNode);
		}

		// remove whitespaces
		if (builder.isIgnoreWhitespacesBetweenTags()) {
			removeLastChildNodeIfEmptyText(parentNode, true);
		}

		// the end
		if (log.isDebugEnabled()) {
			long elapsed = System.currentTimeMillis() - startTime;
			log.debug("DomTree created in " + elapsed + " ms.");
		}
	}

	public void tag(Tag tag) {
		TagType tagType = tag.getType();
		Element node;

		switch (tagType) {
			case OPEN:
				if (builder.isIgnoreWhitespacesBetweenTags()) {
					removeLastChildNodeIfEmptyText(parentNode, false);
				}

				node = new Element(tag, builder.isCaseSensitive());
				node.forceCloseTag = true;
				parentNode.appendChild(node);
				parentNode = node;
				break;

			case CLOSE:
				if (builder.isIgnoreWhitespacesBetweenTags()) {
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
						log.warn("Orphan closed tag: </" + tagName + "> ignored.");
					}
					break;
				}

				// matching tag found, but it is not a regular situation
				// therefore close all unclosed tags in between
				fixUpToMatchingPoint(matchingParent);

				break;

			case EMPTY:
				if (builder.isIgnoreWhitespacesBetweenTags()) {
					removeLastChildNodeIfEmptyText(parentNode, false);
				}

				node = new Element(tag, builder.isCaseSensitive());
				parentNode.appendChild(node);
				break;
		}
	}

	/**
	 * Removes last child node if contains just empty text.
	 */
	protected void removeLastChildNodeIfEmptyText(Node parentNode, boolean closedTag) {
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

		if (StringUtil.isBlank(lastChild.getNodeValue())) {
			lastChild.detachFromParent();
		}
	}

	/**
	 * Finds matching parent open tag or <code>null</code> if not found.
	 */
	protected Node findMatchingParentOpenTag(String tagName) {
		Node parent = parentNode;
		while (parent != null) {
			if (tagName.equals(parent.getNodeName())) {
				return parent;
			}
			parent = parent.getParentNode();
		}
		return null;
	}

	protected void fixUpToMatchingPoint(Node matchingParent) {
		while (true) {
			String nodeName = parentNode.getNodeName();
			if (parentNode == matchingParent) {
				parentNode = parentNode.getParentNode();
				break;
			}

			// get all children of parent node
			Node[] childNodes = parentNode.getChildNodes();
			parentNode.removeAllChilds();
			if (parentNode.getNodeType() == Node.NodeType.ELEMENT) {
				((Element) parentNode).forceCloseTag = false;
			}

			// append all children to parent parent node
			Node parentParentNode = parentNode.getParentNode();
			for (Node childNode : childNodes) {
				parentParentNode.appendChild(childNode);
			}

			if (log.isWarnEnabled()) {
				log.warn("Unclosed tag: <" + nodeName + "> closed.");
			}
			parentNode = parentParentNode;
		}
	}

	public void xmp(Tag tag, CharSequence body) {
		Node node = new Element(tag, builder.isCaseSensitive());
		parentNode.appendChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.appendChild(text);
		}
	}

	public void style(Tag tag, CharSequence body) {
		Element node = new Element(tag, builder.isCaseSensitive());
		node.forceCloseTag = true;
		parentNode.appendChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.appendChild(text);
		}
	}

	public void script(Tag tag, CharSequence body) {
		Element node = new Element(tag, builder.isCaseSensitive());
		node.forceCloseTag = true;
		parentNode.appendChild(node);

		if (body.length() != 0) {
			Node text = new Text(body.toString());
			node.appendChild(text);
		}
	}

	public void comment(CharSequence comment) {
		if (builder.isIgnoreWhitespacesBetweenTags()) {
			removeLastChildNodeIfEmptyText(parentNode, false);
		}
		if (builder.isIgnoreComments()) {
			return;
		}
		Node node = new Comment(comment.toString());
		parentNode.appendChild(node);
	}

	public void text(CharSequence text) {
		String textValue = text.toString();
		Node node = new Text(textValue);
		parentNode.appendChild(node);
	}

	public void cdata(CharSequence cdata) {
		CData cdataNode = new CData(cdata.toString());
		parentNode.appendChild(cdataNode);
	}

	public void xml(Tag tag) {
		XmlDeclaration xmlDeclaration = new XmlDeclaration(tag, builder.isCaseSensitive());
		parentNode.appendChild(xmlDeclaration);
	}

	public void doctype(String name, String publicId, String baseUri) {
		DocumentType documentType = new DocumentType(name, publicId, baseUri);
		parentNode.appendChild(documentType);
	}

	public void condComment(CharSequence conditionalComment, boolean isStartingTag, boolean isDownlevelHidden) {
		Node comment = new Comment(conditionalComment.toString(), isStartingTag, isDownlevelHidden);
		parentNode.appendChild(comment);
	}

	public void error(String message) {
		if (log.isWarnEnabled()) {
			log.warn("DOM tree may be corrupted due to parsing error. " + message);
		}
	}
}

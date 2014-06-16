// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

/**
 * Comment node.
 */
public class Comment extends Node {

	/**
	 * Creates a comment.
	 */
	public Comment(Document ownerDocument, String comment) {
		super(ownerDocument, NodeType.COMMENT, null);
		this.nodeValue = comment;
	}

	@Override
	public Comment clone() {
		return cloneTo(new Comment(ownerDocument, nodeValue));
	}

	@Override
	protected void visitNode(NodeVisitor nodeVisitor) {
		nodeVisitor.comment(this);
	}
}

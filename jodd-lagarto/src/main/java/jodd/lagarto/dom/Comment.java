// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.io.IOException;

/**
 * Comment and conditional comment node.
 */
public class Comment extends Node {

	protected final Boolean conditionalDownlevelHidden;
	protected final boolean isStartingTag;
	protected final String additionalComment;

	/**
	 * Creates regular comment.
	 */
	public Comment(Document ownerDocument, String comment) {
		super(ownerDocument, NodeType.COMMENT, null);
		this.nodeValue = comment;
		this.conditionalDownlevelHidden = null;
		this.isStartingTag = false;
		this.additionalComment = null;
	}

	/**
	 * Creates conditional comment.
	 */
	public Comment(Document ownerDocument, String comment, boolean isStartingTag, boolean conditionalDownlevelHidden, String additionalComment) {
		super(ownerDocument, NodeType.COMMENT, null);
		this.nodeValue = comment;
		this.isStartingTag = isStartingTag;
		this.conditionalDownlevelHidden = Boolean.valueOf(conditionalDownlevelHidden);
		this.additionalComment = additionalComment;
	}

	@Override
	public Comment clone() {
		return cloneTo(conditionalDownlevelHidden == null ?
				new Comment(ownerDocument, nodeValue) :
				new Comment(ownerDocument, nodeValue, isStartingTag, conditionalDownlevelHidden.booleanValue(), additionalComment));
	}

	/**
	 * Returns <code>true</code> if this is a conditional comment.
	 */
	public boolean isConditionalComment() {
		return conditionalDownlevelHidden != null;
	}

	/**
	 * If conditional comment, returns <code>true</code>if downlevel is hidden.
	 */
	public boolean isDownlevelHidden() {
		if (conditionalDownlevelHidden == null) {
			return false;
		}
		return conditionalDownlevelHidden.booleanValue();
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		ownerDocument.getRenderer().renderComment(this, appendable);
	}

}

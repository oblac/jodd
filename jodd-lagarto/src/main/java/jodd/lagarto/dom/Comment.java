// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.TagWriterUtil;

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
	protected Comment(LagartoDOMBuilder domBuilder, String comment) {
		super(domBuilder, NodeType.COMMENT, null);
		this.nodeValue = comment;
		this.conditionalDownlevelHidden = null;
		this.isStartingTag = false;
		this.additionalComment = null;
	}

	/**
	 * Creates conditional comment.
	 */
	protected Comment(LagartoDOMBuilder domBuilder, String comment, boolean isStartingTag, boolean conditionalDownlevelHidden, String additionalComment) {
		super(domBuilder, NodeType.COMMENT, null);
		this.nodeValue = comment;
		this.isStartingTag = isStartingTag;
		this.conditionalDownlevelHidden = Boolean.valueOf(conditionalDownlevelHidden);
		this.additionalComment = additionalComment;
	}

	@Override
	public Comment clone() {
		return cloneTo(conditionalDownlevelHidden == null ?
				new Comment(domBuilder, nodeValue) :
				new Comment(domBuilder, nodeValue, isStartingTag, conditionalDownlevelHidden.booleanValue(), additionalComment));
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
		if (conditionalDownlevelHidden == null) {
			TagWriterUtil.writeComment(appendable, nodeValue);
		} else {
			TagWriterUtil.writeConditionalComment(appendable, nodeValue, isStartingTag, conditionalDownlevelHidden.booleanValue(), additionalComment);
		}
	}

}

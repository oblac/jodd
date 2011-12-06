// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.TagWriterUtil;

import java.io.IOException;

/**
 * Comment and conditional comment node.
 */
public class Comment extends Node {

	protected final Boolean conditionalDownlevelHidden;
	protected final boolean isStartingTag;

	/**
	 * Creates regular comment.
	 */
	public Comment(String comment) {
		super(NodeType.COMMENT, null, true);
		this.nodeValue = comment;
		this.conditionalDownlevelHidden = null;
		this.isStartingTag = false;
	}

	/**
	 * Creates conditional comment.
	 */
	public Comment(String comment, boolean isStartingTag, boolean conditionalDownlevelHidden) {
		super(NodeType.COMMENT, null, true);
		this.nodeValue = comment;
		this.isStartingTag = isStartingTag;
		this.conditionalDownlevelHidden = Boolean.valueOf(conditionalDownlevelHidden);
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
			TagWriterUtil.writeConditionalComment(appendable, nodeValue, isStartingTag, conditionalDownlevelHidden.booleanValue());
		}
	}

}

// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import jodd.idea.props.lexer.PropsTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines folding rules on sections. This rules are a bit different
 * than what is usual, since our psi section contains all inner
 * properties, therefore, our text range must contains them all.
 * Usually, folding happens just over a single token.
 */
public class PropsFoldingBuilder implements FoldingBuilder {

	private static final String EMPTY_SECTION = "[]";

	@NotNull
	public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
		List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
		Range range = null;
		range = appendDescriptors(node, descriptors, range);
		stopRange(range, node, descriptors);
		return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
	}

	public String getPlaceholderText(@NotNull ASTNode node) {
		if (node.getElementType() == PropsTokenTypes.TOKEN_SECTION) {
			return node.getText() + '\n';
		}
		return null;
	}

	public boolean isCollapsedByDefault(@NotNull ASTNode node) {
		return false;
	}

	private Range appendDescriptors(final ASTNode node, final List<FoldingDescriptor> descriptors, Range range) {
		if (node.getElementType() == PropsTokenTypes.TOKEN_SECTION) {
			if (range == null) {
				// start token foldable section
				range = startRange(node);
			} else {
				// new token foldable section
				stopRange(range, node, descriptors);
				range = startRange(node);
			}
		}

		// continue recursively
		ASTNode child = node.getFirstChildNode();
		while (child != null) {
			range = appendDescriptors(child, descriptors, range);
			child = child.getTreeNext();
		}
		return range;
	}

	// ---------------------------------------------------------------- range

	private Range startRange(ASTNode node) {
		if (EMPTY_SECTION.equals(node.getText())) {
			return null;
		}
		Range range = new Range();
		range.node = node;
		range.start = node.getTextRange().getEndOffset();	// range starts after section token
		return range;
	}

	private void stopRange(Range range, ASTNode node, List<FoldingDescriptor> descriptors) {
		if (range == null) {
			return;
		}
		range.end = node.getTextRange().getStartOffset();		// range ends with first element of current note
		if (range.end == 0) {
			range.end = node.getTextRange().getEndOffset();
		}
		if (range.end > range.start) {
			descriptors.add(new FoldingDescriptor(range.node, new TextRange(range.start, range.end)));
		}
	}

	/**
	 * Text range offsets.
	 */
	private static class Range {
		ASTNode node;
		int start;
		int end;
	}
}
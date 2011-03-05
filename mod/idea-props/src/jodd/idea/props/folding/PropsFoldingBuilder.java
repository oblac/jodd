// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import jodd.idea.props.lexer.PropsTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines folding rules.
 */
public class PropsFoldingBuilder implements FoldingBuilder {

	@NotNull
	public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
		List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
		appendDescriptors(node, descriptors);
		return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
	}

	public String getPlaceholderText(@NotNull ASTNode node) {
		if (node.getElementType() == PropsTokenTypes.TOKEN_SECTION) {
			return "[...]";
		}
		return null;
	}

	public boolean isCollapsedByDefault(@NotNull ASTNode node) {
		return false;
	}

	private void appendDescriptors(final ASTNode node, final List<FoldingDescriptor> descriptors) {
		if (isFoldableNode(node)) {
			descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
		}

		ASTNode child = node.getFirstChildNode();
		while (child != null) {
			appendDescriptors(child, descriptors);
			child = child.getTreeNext();
		}
	}

	private boolean isFoldableNode(ASTNode node) {
		return node.getElementType() == PropsTokenTypes.TOKEN_SECTION;
	}
}
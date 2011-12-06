// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Prop section element.
 */
public class PropSection extends BasePsiElement {

	public PropSection(@NotNull final ASTNode node) {
		super(node, "Section");
	}

}
// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Prop PSI element.
 */
public class PropElement extends BasePsiElement {

	public PropElement(@NotNull final ASTNode node) {
		super(node, "Prop");
	}

}

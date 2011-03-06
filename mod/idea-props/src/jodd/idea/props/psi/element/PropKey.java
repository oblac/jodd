// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PropKey extends BasePsiElement {

	public PropKey(@NotNull final ASTNode node) {
		super(node, "Key");
	}

}

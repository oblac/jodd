// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Props PSI value.
 */
public class PropsValue extends BasePsiElement {

	public PropsValue(@NotNull final ASTNode node) {
		super(node, "Value");
	}

}

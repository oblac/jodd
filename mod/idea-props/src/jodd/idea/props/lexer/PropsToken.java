// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.lexer;

import com.intellij.psi.tree.IElementType;
import jodd.idea.props.Props;
import org.jetbrains.annotations.NonNls;

/**
 * Lexer token.
 */
public class PropsToken extends IElementType {

	public PropsToken(@NonNls String debugName) {
		super(debugName, Props.LANGUAGE);
	}

	public String toString() {
		return "Token: " + super.toString();
	}
}

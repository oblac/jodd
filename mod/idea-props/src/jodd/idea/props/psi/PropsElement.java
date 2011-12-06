// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import jodd.idea.props.Props;

/**
 * Base PSI element.
 */
public class PropsElement extends IElementType {

	public PropsElement(@NonNls String debugName) {
		super(debugName, Props.LANGUAGE);
	}

	public String toString() {
		return "Element: " + super.toString();
	}
}



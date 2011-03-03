// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import jodd.idea.props.Props;

/**
 * Props base element.
 */
public class PropsElementType extends IElementType {

	public PropsElementType(@NonNls String debugName) {
		super(debugName, Props.LANGUAGE);
	}

	public String toString() {
		return "Props: " + super.toString();
	}
}



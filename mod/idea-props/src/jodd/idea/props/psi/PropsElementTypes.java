// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import jodd.idea.props.Props;

public interface PropsElementTypes {

	IFileElementType FILE = new IFileElementType(Props.LANGUAGE);

	IElementType PROP = new PropsElementType("property");

	IElementType SECTION = new PropsElementType("section");

	TokenSet TOKENS_PROPS = TokenSet.create(PROP);
}

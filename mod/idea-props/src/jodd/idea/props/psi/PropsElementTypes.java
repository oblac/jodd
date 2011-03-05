// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import jodd.idea.props.Props;

/**
 * PSI elements.
 */
public interface PropsElementTypes {

	IFileElementType FILE = new IFileElementType(Props.LANGUAGE);

	IElementType PROP = new PropsElement("prop");

	IElementType SECTION = new PropsElement("section");

	IElementType VALUE = new PropsElement("value");

	TokenSet TOKENS_PROPS = TokenSet.create(PROP);
}

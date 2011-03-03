// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * Props JFLEX based lexer.
 */
public class PropsLexer extends FlexAdapter {
	public PropsLexer() {
		super(new _PropsLexer((Reader) null));
	}
}

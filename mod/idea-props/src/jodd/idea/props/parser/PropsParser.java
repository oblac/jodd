// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import jodd.idea.props.lexer.PropsTokenTypes;
import jodd.idea.props.psi.PropsElementTypes;

/**
 * Parser defines AST elements.
 */
public class PropsParser implements PsiParser {

	@NotNull
	public ASTNode parse(IElementType root, PsiBuilder builder) {

		final PsiBuilder.Marker rootMarker = builder.mark();

		while (!builder.eof()) {
			IElementType tokenType = builder.getTokenType();
			if (tokenType == PropsTokenTypes.KEY_CHARACTERS) {
				parseProperty(builder);
			} else if (tokenType == PropsTokenTypes.SECTION_CHARACTERS) {
				parseSection(builder);
			} else {
				builder.advanceLexer();
				builder.error("Parsing error: property key or section expected.");
			}
		}

		rootMarker.done(root);
		return builder.getTreeBuilt();
	}

	// ---------------------------------------------------------------- elements

	private static void parseProperty(PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.KEY_CHARACTERS) {
			final PsiBuilder.Marker propMarker = builder.mark();

			parseKey(builder);

			if (builder.getTokenType() == PropsTokenTypes.KEY_VALUE_SEPARATOR) {
				parseKeyValueSeparator(builder);
				parseValue(builder);
			}

			propMarker.done(PropsElementTypes.PROP);
		}
	}

	private static void parseSection(final PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.SECTION_CHARACTERS) {
			final PsiBuilder.Marker sectionMarker = builder.mark();

			builder.advanceLexer();

			sectionMarker.done(PropsElementTypes.SECTION);
		}
	}

	// ---------------------------------------------------------------- single tokens

	private static void parseKey(final PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.KEY_CHARACTERS) {
			builder.advanceLexer();
		}
	}

	private static void parseKeyValueSeparator(final PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.KEY_VALUE_SEPARATOR) {
			builder.advanceLexer();
		}
	}

	private static void parseValue(final PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.VALUE_CHARACTERS) {
			builder.advanceLexer();
		}
	}

}
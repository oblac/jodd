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
 * Parser defines AST elements based on parsed tokens.
 * JFlex parses input stream and emits tokens. Parser
 * marks certain set of tokens to build PSI structure.
 * Therefore, PSI structure is build over emitted lexer
 * tokens.
 */
public class PropsParser implements PsiParser {

	private static final String EMPTY_SECTION = "[]";

	/**
	 * PSI parser for lexer tokens. It deals with top level tokens
	 * and continue with each separately.
	 */
	@NotNull
	public ASTNode parse(IElementType root, PsiBuilder builder) {

		final PsiBuilder.Marker rootMarker = builder.mark();

		PsiBuilder.Marker sectionMarker = null;

		while (!builder.eof()) {
			IElementType tokenType = builder.getTokenType();
			if (tokenType == PropsTokenTypes.TOKEN_KEY) {
				parseProperty(builder);
			} else if (tokenType == PropsTokenTypes.TOKEN_SECTION) {
				String text = builder.getTokenText();
				if (sectionMarker == null) {
					if (EMPTY_SECTION.equals(text) == false) {
						sectionMarker = builder.mark();
					}
				} else {
					sectionMarker.done(PropsElementTypes.SECTION);
					sectionMarker = null;
					if (EMPTY_SECTION.equals(text) == false) {
						// start new section
						sectionMarker = builder.mark();
					}
				}
				parseSection(builder);
			} else {
				builder.advanceLexer();
				builder.error("Parsing error!");
			}
		}

		// close section if exist
		if (sectionMarker != null) {
			sectionMarker.done(PropsElementTypes.SECTION);
		}

		rootMarker.done(root);
		return builder.getTreeBuilt();
	}

	// ---------------------------------------------------------------- elements

	/**
	 * Parses a property token and creates property psi element. Property psi
	 * element contain inner elements, like key, value etc.
	 */
	private static void parseProperty(PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.TOKEN_KEY) {
			final PsiBuilder.Marker propMarker = builder.mark();

			parseKey(builder);

			if (builder.getTokenType() == PropsTokenTypes.TOKEN_KEY_VALUE_SEPARATOR) {
				parseKeyValueSeparator(builder);
				parseValue(builder);
			}

			// done with prop psi element, this will invoke
			// PropsParserDefinition#createElement()
			propMarker.done(PropsElementTypes.PROP);
		}
	}

	/**
	 * Parses everything behind key-value separator, i.e. a value.
	 * Value PSI element wraps list of value and macro tokens.
	 */
	private static void parseValue(final PsiBuilder builder) {
		final PsiBuilder.Marker valueMarker = builder.mark();
		boolean hasValue = false;

		while (!builder.eof()) {
			if (builder.getTokenType() == PropsTokenTypes.TOKEN_VALUE) {
				hasValue = true;
				builder.advanceLexer();
			} else if (builder.getTokenType() == PropsTokenTypes.TOKEN_MACRO) {
				hasValue = true;
				builder.advanceLexer();
			} else {
				break;
			}
		}
		// if value is not empty, create PSI element
		if (hasValue) {
			valueMarker.done(PropsElementTypes.VALUE);
		} else {
			valueMarker.drop();
		}
	}

	// ---------------------------------------------------------------- advance

	/**
	 * Just consumes key token and continues.
	 */
	private static void parseKey(final PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.TOKEN_KEY) {
			builder.advanceLexer();
		}
	}

	/**
	 * Just consumes key-value-separator token and continues.
	 */
	private static void parseKeyValueSeparator(final PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.TOKEN_KEY_VALUE_SEPARATOR) {
			builder.advanceLexer();
		}
	}

	/**
	 * Just consumes section token and continues.
	 */
	private static void parseSection(final PsiBuilder builder) {
		if (builder.getTokenType() == PropsTokenTypes.TOKEN_SECTION) {
			builder.advanceLexer();
		}
	}

}
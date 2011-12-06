// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import jodd.idea.props.psi.element.PropKey;
import jodd.idea.props.psi.element.PropsValue;
import org.jetbrains.annotations.NotNull;
import jodd.idea.props.lexer.PropsLexer;
import jodd.idea.props.lexer.PropsTokenTypes;
import jodd.idea.props.psi.PropsElementTypes;
import jodd.idea.props.psi.PropsFile;
import jodd.idea.props.psi.element.PropElement;
import jodd.idea.props.psi.element.PropSection;

/**
 * Parser definition. Creates both the lexer and the parser. Also stands as
 * a factory for PSI elements.
 */
public class PropsParserDefinition implements ParserDefinition {

	private static final Logger LOG = Logger.getInstance(PropsParserDefinition.class.getName());

	@NotNull
	public Lexer createLexer(Project project) {
		return new PropsLexer();
	}

	public PsiParser createParser(Project project) {
		return new PropsParser();
	}

	public IFileElementType getFileNodeType() {
		return PropsElementTypes.FILE;
	}

	@NotNull
	public TokenSet getWhitespaceTokens() {
		return PropsTokenTypes.WHITESPACES;
	}

	@NotNull
	public TokenSet getCommentTokens() {
		return PropsTokenTypes.COMMENTS;
	}

	@NotNull
	public TokenSet getStringLiteralElements() {
		return TokenSet.EMPTY;
	}

	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}

	public PsiFile createFile(FileViewProvider viewProvider) {
		return new PropsFile(viewProvider);
	}

	/**
	 * Factory for PSI elements. Invoked implicitly by {@link PropsParser}.
	 */
	@NotNull
	public PsiElement createElement(ASTNode node) {
		final IElementType type = node.getElementType();
		if (type == PropsElementTypes.PROP) {
			return new PropElement(node);
		} else if (type == PropsElementTypes.SECTION) {
			return new PropSection(node);
		} else if (type == PropsElementTypes.VALUE) {
			return new PropsValue(node);
		} else if (type == PropsElementTypes.KEY) {
			return new PropKey(node);
		}

		LOG.error("Invalid PSI element: [" + type + "].");

		return new ASTWrapperPsiElement(node);
	}
}

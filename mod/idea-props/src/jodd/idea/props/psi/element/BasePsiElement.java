// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi.element;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.Key;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;
import jodd.idea.props.Props;

import javax.swing.Icon;

/**
 * Base PSI element for all prop psi elements.
 */
public abstract class BasePsiElement extends ASTWrapperPsiElement {

	private final String name;

	protected BasePsiElement(@NotNull final ASTNode node, String name) {
		super(node);
		this.name = name;
	}

	@Override
	@NotNull
	public Language getLanguage() {
		return Props.LANGUAGE;
	}

	@Override
	@NotNull
	public SearchScope getUseScope() {
		return GlobalSearchScope.allScope(getProject());
		//return new LocalSearchScope(getContainingFile());
	}

	@Override
	public <T> T getUserData(@NotNull Key<T> key) {
		return null;
	}

	@Override
	public <T> void putUserData(@NotNull Key<T> key, T value) {
	}

	@Override
	public Icon getIcon(int flags) {
		return null;
	}

	public String toString() {
		return "PsiElement: " + name;
	}

	protected boolean isEmpty(ASTNode[] children) {
		return children == null || children.length < 1;
	}
}
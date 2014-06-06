// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Document node is always a root node.
 * Holds various DOM-related flags and information.
 */
public class Document extends Node {

	protected long elapsedTime;
	protected final boolean lowercase;
	protected final boolean collectErrors;
	protected final LagartoNodeHtmlRenderer renderer;
	protected List<String> errors;

	public Document() {
		this(true, false, new LagartoNodeHtmlRenderer());
	}

	/**
	 * Document constructor with all relevant flags.
	 * @param lowercase	should all names be converted to lowercase
	 * @param collectErrors	should we collect errors during the parsing
	 * @param renderer renderer instance
	 */
	public Document(boolean lowercase, boolean collectErrors, LagartoNodeHtmlRenderer renderer) {
		super(null, NodeType.DOCUMENT, null);
		this.lowercase = lowercase;
		this.renderer = renderer;
		this.collectErrors = collectErrors;
		this.elapsedTime = System.currentTimeMillis();
	}
	
	@Override
	public Document clone() {
		Document document = cloneTo(new Document(lowercase, collectErrors, renderer));
		document.elapsedTime = this.elapsedTime;
		return document;
	}

	/**
	 * Notifies document that parsing is done.
	 */
	protected void end() {
		elapsedTime = System.currentTimeMillis() - elapsedTime;
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		super.toInnerHtml(appendable);
	}

	// ---------------------------------------------------------------- errors

	/**
	 * Add new error message to the {@link #getErrors() errors list}.
	 * If errors are not collected error, message is ignored.
	 */
	public void addError(String message) {
		if (collectErrors) {
			if (errors == null) {
				errors = new ArrayList<String>();
			}
			errors.add(message);
		}
	}

	/**
	 * Returns list of warnings and errors occurred during parsing.
	 * Returns <code>null</code> if parsing was successful; or if
	 * errors are not collected.
	 */
	public List<String> getErrors() {
		return errors;
	}

	// ---------------------------------------------------------------- attr

	/**
	 * Document node does not have attributes.
	 */
	@Override
	public void setAttribute(String name, String value) {
	}

	// ---------------------------------------------------------------- getter

	/**
	 * Returns DOM building elapsed time.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Returns <code>true</code> if node names should
	 * be converted to lowercase. Otherwise, name remains
	 * unchanged (ie equals to raw name).
	 */
	public boolean isLowercase() {
		return lowercase;
	}

	/**
	 * Returns <code>true</code> if errors are collected.
	 */
	public boolean isCollectErrors() {
		return collectErrors;
	}

	/**
	 * Returns renderer for nodes.
	 */
	public LagartoNodeHtmlRenderer getRenderer() {
		return renderer;
	}

}
// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

/**
 * Renderer is a factory for {@link jodd.lagarto.dom.NodeVisitor} that
 * renders DOM tree to string.
 */
public class LagartoHtmlRenderer {

	/**
	 * Renders node to appendable.
	 */
	public String toHtml(Node node, Appendable appendable) {
		NodeVisitor renderer = createRenderer(appendable);

		node.visit(renderer);

		return appendable.toString();
	}

	/**
	 * Renders node children to appendable.
	 */
	public String toInnerHtml(Node node, Appendable appendable) {
		NodeVisitor renderer = createRenderer(appendable);

		node.visitChildren(renderer);

		return appendable.toString();
	}

	protected NodeVisitor createRenderer(Appendable appendable) {
		return new LagartoHtmlRendererNodeVisitor(appendable);
	}

}
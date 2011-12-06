// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.csselly.Selector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeFilter;

/**
 * Pseudo class selector.
 * A pseudo-class always consists of a "colon" (:) followed by
 * the name of the pseudo-class and optionally by a value between parentheses.
 * <p>
 * Selectors introduces the concept of structural pseudo-classes to permit
 * selection based on extra information that lies in the document tree
 * but cannot be represented by other simple selectors or combinators.
 * <p>
 * Standalone text and other non-element nodes are not counted when calculating
 * the position of an element in the list of children of its parent.
 * When calculating the position of an element in the list of children
 * of its parent, the index numbering starts at 1.
 */
public class PseudoClassSelector extends Selector implements NodeFilter {

	protected final PseudoClass pseudoClass;

	public PseudoClassSelector(String pseudoClass) {
		super(Type.PSEUDO_CLASS);
		this.pseudoClass = PseudoClass.valueOfName(pseudoClass);
	}

	/**
	 * Returns {@link PseudoClass pseudo class} value.
	 */
	public PseudoClass getPseudoClass() {
		return pseudoClass;
	}

	/**
	 * Matches node to this selector.
	 */
	public boolean accept(Node node) {
		return pseudoClass.match(node);
	}
}

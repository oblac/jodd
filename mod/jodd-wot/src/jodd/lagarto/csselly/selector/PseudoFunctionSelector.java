// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.csselly.Selector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeFilter;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Pseudo function selector.
 */
public class PseudoFunctionSelector extends Selector implements NodeFilter {

	protected final PseudoFunction pseudoFunction;

    protected final int a;

    protected final int b;

	public PseudoFunctionSelector(String functionName, String expression) {
		super(Type.PSEUDO_FUNCTION);

		this.pseudoFunction = PseudoFunction.valueOfName(functionName.trim());

		expression = StringUtil.removeChars(expression, "+ \t\n\r\n");
		if (expression.equals("odd")) {
			a = 2;
			b = 1;
		} else if (expression.equals("even")) {
			a = 2;
			b = 0;
		} else {
			int nndx = expression.indexOf('n');
			if (nndx != -1) {
				String aVal = expression.substring(0, nndx).trim();
				if (aVal.length() == 0) {
					a = 1;
				} else {
					if (aVal.equals(StringPool.DASH)) {
						a = -1;
					} else {
						a = Integer.parseInt(aVal);
					}
				}
				String bVal = expression.substring(nndx + 1);
				if (bVal.length() == 0) {
					b = 0;
				} else {
					b = Integer.parseInt(bVal);
				}
			} else {
				a = 0;
				b = Integer.parseInt(expression);
			}
		}
	}

	/**
	 * Returns {@link PseudoFunction pseudo function}.
	 */
	public PseudoFunction getPseudoFunction() {
		return pseudoFunction;
	}

	/**
	 * Returns <b>a</b> value of the function expression.
	 */
	public int getValueA() {
		return a;
	}

	/**
	 * Returns <b>b</b> value of the function expression.
	 */
	public int getValueB() {
		return b;
	}


	/**
	 * Matches nodes with this pseudo function selector.
	 */
	public boolean accept(Node node) {
		int x = pseudoFunction.resolveValue(node);
		return match(x);
	}

	/**
	 * Matches expression with the value.
	 */
	public boolean match(int value) {
		if (a == 0) {
			return value == b;
		}

		if (a > 0) {
			if (value < b) {
				return false;
			}
			return (value - b) % a == 0;
		}

		if (value > b) {
			return false;
		}
		return (b - value) % (-a) == 0;
	}
}

// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * {@link PseudoFunction Pseudo function expression}, in form: <code>an + b</code>.
 */
public class PseudoFunctionExpression {

	protected final int a;
	protected final int b;

	public PseudoFunctionExpression(String expression) {
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
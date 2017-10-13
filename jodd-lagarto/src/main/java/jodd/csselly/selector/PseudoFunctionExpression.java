// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.csselly.selector;

import jodd.csselly.CSSellyException;
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
						a = parseInt(aVal);
					}
				}
				String bVal = expression.substring(nndx + 1);
				if (bVal.length() == 0) {
					b = 0;
				} else {
					b = parseInt(bVal);
				}
			} else {
				a = 0;
				b = parseInt(expression);
			}
		}
	}

	/**
	 * Parses int value or throws <code>CSSellyException</code> on failure.
	 */
	protected int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfex) {
			throw new CSSellyException(nfex);
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
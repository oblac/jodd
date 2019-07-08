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

package jodd.json;

import jodd.util.StringUtil;

import java.util.Arrays;

import static jodd.util.StringPool.STAR;

/**
 * Path query is used to match properties with some Path.
 * Query is expressed in dot notation. Each term between the dots
 * is a property name of a parent. Query may contains wildcard: '*' that
 * can be used instead of a named term.
 * <p>
 * Included and excluded path matching works a bit differently. Included
 * query matches all sub-paths. Excluded query is strict and match only
 * paths with the same length.
 */
public class PathQuery {

	protected final String[] expression;
	protected final boolean wildcard;
	protected final boolean included;

	public PathQuery(final String expression, final boolean included) {
		this.expression = StringUtil.splitc(expression, '.');
		wildcard = expression.indexOf('*') >= 0;
		this.included = included;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append('[');
		for (int i = 0; i < expression.length; i++) {
			if (i > 0) {
				builder.append('.');
			}
			builder.append(expression[i]);
		}
		builder.append(']');
		return builder.toString();
	}

	/**
	 * Returns <code>true</code> if path matches the query.
	 */
	public boolean matches(final Path path) {
		int exprNdx = 0;
		int pathNdx = 0;

		int pathLen = path.length();
		int exprLen = expression.length;

		while (pathNdx < pathLen) {
			CharSequence current = path.get(pathNdx);

			if (exprNdx < exprLen && expression[exprNdx].equals(STAR)) {
				exprNdx++;
			}
			else if (exprNdx < exprLen && expression[exprNdx].contentEquals(current)) {
				pathNdx++;
				exprNdx++;
			}
			else if (exprNdx - 1 >= 0 && expression[exprNdx - 1].equals(STAR)) {
				pathNdx++;
			}
			else {
				return false;
			}
		}

		if (exprNdx > 0 && expression[exprNdx - 1].equals(STAR)) {
			return pathNdx >= pathLen && exprNdx >= exprLen;
		}
		else {
			return pathLen != 0 &&
					pathNdx >= pathLen &&
					(included || exprNdx >= exprLen);
		}
	}

	/**
	 * Returns <code>true</code> if this query contains a wildcard.
	 */
	public boolean isWildcard() {
		return wildcard;
	}

	/**
	 * Returns <code>true</code> if this query indicates that matching
	 * properties should be included.
	 */
	public boolean isIncluded() {
		return included;
	}


	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PathQuery pathQuery = (PathQuery) o;

		if (included != pathQuery.included) {
			return false;
		}
		if (!Arrays.equals(expression, pathQuery.expression)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(expression);
		result = 31 * result + (included ? 1 : 0);
		return result;
	}

}
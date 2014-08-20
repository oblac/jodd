// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.util.StringUtil;

import java.util.Arrays;

import static jodd.util.StringPool.*;

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

	public PathQuery(String expression, boolean included) {
		this.expression = StringUtil.splitc(expression, '.');
		wildcard = expression.indexOf('*') >= 0;
		this.included = included;
	}

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
	public boolean matches(Path path) {
		int exprNdx = 0;
		int pathNdx = 0;

		int pathLen = path.length();
		int exprLen = expression.length;

		while (pathNdx < pathLen) {
			String current = path.get(pathNdx);

			if (exprNdx < exprLen && expression[exprNdx].equals(STAR)) {
				exprNdx++;
			}
			else if (exprNdx < exprLen && expression[exprNdx].equals(current)) {
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
	public boolean equals(Object o) {
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
// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.util.StringUtil;

/**
 * Validates conditional comments expressions.
 */
public class HtmlCCommentExpressionMatcher {

	/**
	 * Matches conditional comment expression with current mode.
	 * Returns <code>true</code> it conditional comment expression is positive,
	 * otherwise returns <code>false</code>.
	 */
	public boolean match(float ieVersion, String expression) {
		expression = StringUtil.removeChars(expression, "()");
		expression = expression.substring(3);

		String[] andChunks = StringUtil.splitc(expression, '&');

		boolean valid = true;

		for (String andChunk : andChunks) {
			String[] orChunks = StringUtil.splitc(andChunk, '|');

			boolean innerValid = false;

			for (String orChunk : orChunks) {
				orChunk = orChunk.trim();

				if (orChunk.startsWith("IE ")) {
					String value = orChunk.substring(3);
					float number = Float.parseFloat(value);

					if (versionToCompare(ieVersion, number) == number) {
						innerValid = true;
						break;
					}
					continue;
				}
				if (orChunk.startsWith("!IE ")) {
					String value = orChunk.substring(4);
					float number = Float.parseFloat(value);

					if (versionToCompare(ieVersion, number) != number) {
						innerValid = true;
						break;
					}
					continue;
				}
				if (orChunk.startsWith("lt IE ")) {
					String value = orChunk.substring(6);
					float number = Float.parseFloat(value);

					if (ieVersion < number) {
						innerValid = true;
						break;
					}
					continue;
				}
				if (orChunk.startsWith("lte IE ")) {
					String value = orChunk.substring(7);
					float number = Float.parseFloat(value);

					if (versionToCompare(ieVersion, number) <= number) {
						innerValid = true;
						break;
					}
					continue;
				}
				if (orChunk.startsWith("gt IE ")) {
					String value = orChunk.substring(6);
					float number = Float.parseFloat(value);

					if (versionToCompare(ieVersion, number) > number) {
						innerValid = true;
						break;
					}
					continue;
				}
				if (orChunk.startsWith("gte IE ")) {
					String value = orChunk.substring(7);
					float number = Float.parseFloat(value);

					if (ieVersion >= number) {
						innerValid = true;
						break;
					}
					continue;
				}
			}

			valid = valid && innerValid;
		}

		return valid;
	}

	// If in expression IE version is represented as a natural number
	// we should compare only major number
	private float versionToCompare(float ieVersion, float number) {
		return (int) number == number ? (int) ieVersion : ieVersion;
	}
}
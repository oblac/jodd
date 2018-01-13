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
	public boolean match(final float ieVersion, String expression) {
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
	private float versionToCompare(final float ieVersion, final float number) {
		return (int) number == number ? (int) ieVersion : ieVersion;
	}
}
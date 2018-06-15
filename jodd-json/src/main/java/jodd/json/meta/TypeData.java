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

package jodd.json.meta;

import jodd.inex.InExRules;
import jodd.util.ArraysUtil;

import java.util.List;

/**
 * Type information read from annotations.
 */
public class TypeData {
	public final InExRules<String, String, String> rules;
	public final boolean strict;

	public final String[] jsonNames;
	public final String[] realNames;

	public TypeData(final List<String> includes, final List<String> excludes, final boolean strict, final String[] jsonNames, final String[] realNames) {
		rules = new InExRules<>();

		for (String include : includes) {
			rules.include(include);
		}
		for (String exclude : excludes) {
			rules.exclude(exclude);
		}

		this.strict = strict;
		this.jsonNames = jsonNames;
		this.realNames = realNames;
	}

	/**
	 * Resolves real name from JSON name.
	 */
	public String resolveRealName(final String jsonName) {
		if (jsonNames == null) {
			return jsonName;
		}
		int jsonIndex = ArraysUtil.indexOf(jsonNames, jsonName);
		if (jsonIndex == -1) {
			return jsonName;
		}
		return realNames[jsonIndex];
	}

	/**
	 * Resolves JSON name from real name.
	 */
	public String resolveJsonName(final String realName) {
		if (realNames == null) {
			return realName;
		}
		int realIndex = ArraysUtil.indexOf(realNames, realName);
		if (realIndex == -1) {
			return realName;
		}
		return jsonNames[realIndex];
	}
}

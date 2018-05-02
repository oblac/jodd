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

package jodd.madvoc.config;

import jodd.madvoc.scope.MadvocScope;

public class InjectionPoint {

	private final Class type;                // property type
	private final String name;               // property name
	private final String targetName;         // real property name, if different from 'name'
	private final MadvocScope scope;         // dedicated scope instance

	public InjectionPoint(final Class type, final String name, final String targetName, final MadvocScope scope) {
		this.type = type;
		this.name = name;
		this.targetName = targetName;
		this.scope = scope;
	}

	/**
	 * Returns injection point type.
	 */
	public Class type() {
		return type;
	}

	/**
	 * Returns injection point name.
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the scope instance of this injection point.
	 */
	public MadvocScope scope() {
		return scope;
	}

	/**
	 * Returns real targets name, used for writing and reading directly to the target.
	 */
	public String targetName() {
		return targetName != null ? targetName : name;
	}

	/**
	 * Returns matched name or <code>null</code> if name is not matched.
	 * <p>
	 * Matches if attribute name matches the required field name. If the match is positive,
	 * injection is performed on the field.
	 * <p>
	 * Parameter name matches field name if param name starts with field name and has
	 * either '.' or '[' after the field name.
	 * <p>
	 * Returns real property name, once when name is matched.
	 */
	public String matchedName(final String value) {
		// match
		if (!value.startsWith(name)) {
			return null;
		}

		final int requiredLen = name.length();

		if (value.length() >= requiredLen + 1) {
			final char c = value.charAt(requiredLen);
			if ((c != '.') && (c != '[')) {
				return null;
			}
		}

		// get param
		if (targetName == null) {
			return value;
		}

		return targetName + value.substring(name.length());
	}
}

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

package jodd.props;

/**
 * Holds props value.
 */
public class PropsEntry {

	/**
	 * Original value.
	 */
	protected final String value;

	protected PropsEntry next;

	protected final String key;

	protected final String profile;

	protected final boolean hasMacro;

	protected final PropsData propsData;

	public PropsEntry(final String key, final String value, final String profile, final PropsData propsData) {
		this.value = value;
		this.key = key;
		this.profile = profile;
		this.hasMacro = value.contains("${");
		this.propsData = propsData;
	}

	/**
	 * Returns the raw value. Macros are not replaced.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the property value, with replaced macros.
	 */
	public String getValue(final String... profiles) {
		if (hasMacro) {
			return propsData.resolveMacros(value, profiles);
		}
		return value;
	}

	/**
	 * Returns property key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns property profile or <code>null</code> if this is a base property.
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * Returns <code>true</code> if value has a macro to resolve.
	 */
	public boolean hasMacro() {
		return hasMacro;
	}

	@Override
	public String toString() {
		return "PropsEntry{" + key + (profile != null ? '<' + profile + '>' : "") + '=' + value + '}';
	}

}
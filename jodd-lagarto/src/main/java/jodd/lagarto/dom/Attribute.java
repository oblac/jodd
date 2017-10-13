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
 * Elements attribute.
 */
public class Attribute implements Cloneable {

	protected final String rawName;
	protected final String name;
	protected String value;
	protected String[] splits;

	public Attribute(String rawName, String name, String value) {
		this.rawName = rawName;
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Attribute clone() {
		return new Attribute(rawName, name, value);
	}

	/**
	 * Returns attributes raw name.
	 */
	public String getRawName() {
		return rawName;
	}

	/**
	 * Returns attributes name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns attribute value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets attribute value.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	// ---------------------------------------------------------------- splits

	/**
	 * Returns true if attribute is containing some value.
	 */
	public boolean isContaining(String include) {
		if (value == null) {
			return false;
		}
		if (splits == null) {
			splits = StringUtil.splitc(value, ' ');
		}

		for (String s: splits) {
			if (s.equals(include)) {
				return true;
			}
		}
		return false;
	}
}

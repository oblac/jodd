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

package jodd.util;

/**
 * Simple name-value holder.
 */
public class NameValue<N, V> {

	protected N name;
	protected V value;

	public NameValue() {
	}

	public NameValue(N name, V value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Sets name.
	 */
	public void setName(N name) {
		this.name = name;
	}

	/**
	 * Returns name.
	 */
	public N getName() {
		return name;
	}

	/**
	 * Returns value.
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Sets value.
	 */
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NameValue)) {
			return false;
		}
		NameValue that = (NameValue) o;

		Object n1 = getName();
		Object n2 = that.getName();

		if (n1 == n2 || (n1 != null && n1.equals(n2))) {
			Object v1 = getValue();
			Object v2 = that.getValue();
			if (v1 == v2 || (v1 != null && v1.equals(v2))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (name == null ? 0 : name.hashCode()) ^
				(value == null ? 0 : value.hashCode());
	}

}
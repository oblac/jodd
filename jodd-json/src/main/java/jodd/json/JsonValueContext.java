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

/**
 * Context of current serialized value.
 */
public class JsonValueContext {

	protected Object value;
	protected String propertyName;
	protected int index;

	public JsonValueContext(Object value) {
		this.value = value;
	}

	/**
	 * Reuses this instance for better performances.
	 */
	public void reuse(Object value) {
		this.value = value;
		this.propertyName = null;
		this.index = 0;
	}

	/**
	 * Returns current object value.
	 */
	public Object getValue() {
		return value;
	}

	// ---------------------------------------------------------------- index

	public void incrementIndex() {
		index++;
	}

	/**
	 * Returns current index.
	 */
	public int getIndex() {
		return index;
	}

	// ---------------------------------------------------------------- json object

	/**
	 * Returns current property name.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Stores current property name.
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}
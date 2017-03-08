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

package jodd.bean;

/**
 * Default implementation of {@link BeanVisitor} for just setting
 * the properties in fluent way.
 */
public abstract class BeanVisitorImplBase<T> extends BeanVisitor {

	/**
	 * Excludes all properties, i.e. enables blacklist mode.
	 */
	public T excludeAll() {
		blacklist = false;
		return (T) this;
	}

	/**
	 * Defines excluded property names.
	 */
	public T exclude(String... excludes) {
		for (String ex : excludes) {
			rules.exclude(ex);
		}
		return (T) this;
	}

	/**
	 * Exclude a property.
	 */
	public T exclude(String exclude) {
		rules.exclude(exclude);
		return (T) this;
	}

	/**
	 * Defines included property names.
	 */
	public T include(String... includes) {
		for (String in : includes) {
			rules.include(in);
		}
		return (T) this;
	}

	/**
	 * Include a property.
	 */
	public T include(String include) {
		rules.include(include);
		return (T) this;
	}

	/**
	 * Defines included property names as public properties
	 * of given template class. Sets to black list mode.
	 */
	public T includeAs(Class template) {
		blacklist = false;

		String[] properties = getAllBeanPropertyNames(template, false);

		include(properties);

		return (T) this;
	}

	/**
	 * Defines if <code>null</code> values should be ignored.
	 */
	public T ignoreNulls(boolean ignoreNulls) {
		this.ignoreNullValues = ignoreNulls;

		return (T) this;
	}

	/**
	 * Defines if all properties should be copied (when set to <code>true</code>)
	 * or only public (when set to <code>false</code>, default).
	 */
	public T declared(boolean declared) {
		this.declared = declared;
		return (T) this;
	}

	/**
	 * Defines if fields without getters should be copied too.
	 */
	public T includeFields(boolean includeFields) {
		this.includeFields = includeFields;
		return (T) this;
	}

}
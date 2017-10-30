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

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.MadvocException;
import jodd.typeconverter.TypeConverterManager;

import java.lang.reflect.Constructor;

/**
 * Injection target.
 */
public class Target {

	protected final Class type;
	protected Object value;

	/**
	 * Creates target over the value. Injection will be done into the value,
	 * hence the name and the types are irrelevant. Used for action itself
	 * and action non-annotated arguments.
	 */
	public Target(Object value) {
		this.value = value;
		this.type = null;
	}
	public Target(Object value, Class type) {
		this.value = value;
		this.type = type;
	}

	/**
	 * Creates target over a type with given name. Injection is actually a type conversion
	 * from input content to the given type. Used for annotated arguments.
	 */
	public Target(Class type) {
		this.type = type;
		this.value = null;
	}

	/**
	 * Returns targets type, if specified.
	 * @see #resolveType()
	 */
	public Class getType() {
		return type;
	}

	/**
	 * Resolves target type: either using {@link #getType() provided type}
	 * or type of the {@link #getValue() value}.
	 */
	public Class resolveType() {
		if (type != null) {
			return type;
		}
		return value.getClass();
	}

	/**
	 * Returns target value, if specified.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets target value.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	// ---------------------------------------------------------------- read

	/**
	 * Reads value from the target. If something goes wrong, exception
	 * is thrown. We assume that outjection is controlled by developer
	 * and that each reading of a value must be successful operation.
	 */
	public Object readValue(String propertyName) {
		if (type != null) {
			int dotNdx = propertyName.indexOf('.');

			if (dotNdx == -1) {
				return value;
			}

			propertyName = propertyName.substring(dotNdx + 1);
		}

		return BeanUtil.declared.getProperty(value, propertyName);
	}

	// ---------------------------------------------------------------- write

	/**
	 * Writes value to this target. Depending on a flag, setting the value can be
	 * completely silent, when no exception is thrown and with top performances.
	 * Otherwise, an exception is thrown on a failure.
	 */
	public void writeValue(String propertyName, Object propertyValue, boolean silent) {
		if (type != null) {
			// target type specified, save into target value!

			int dotNdx = propertyName.indexOf('.');

			if (dotNdx == -1) {
				value = TypeConverterManager.get().convertType(propertyValue, type);
				return;
			}

			if (value == null) {
				createValueInstance();
			}

			propertyName = propertyName.substring(dotNdx + 1);
		}

		// inject into target value

		if (silent) {
			BeanUtil.declaredForcedSilent.setProperty(value, propertyName, propertyValue);
			return;
		}

		BeanUtil.declaredForced.setProperty(value, propertyName, propertyValue);
	}

	/**
	 * Creates new instance of a type and stores it in the value.
	 */
	@SuppressWarnings({"unchecked", "NullArgumentToVariableArgMethod"})
	protected void createValueInstance() {
		try {
			Constructor ctor = type.getDeclaredConstructor(null);
			ctor.setAccessible(true);
			value = ctor.newInstance();
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}
	}

}
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

import jodd.bean.BeanUtil;
import jodd.introspector.MapperFunction;
import jodd.madvoc.MadvocException;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.ClassUtil;

import java.util.function.Function;

/**
 * Wrapper of a target for IN/OUT operations. It wraps one of these:
 * <ul>
 *     <li>action object</li>
 *     <li>method parameter</li>
 *     <li>third-party object</li>
 * </ul>
 *
 * Target provides a common interface to write or read certain property from a wrapped value.
 */
public class Target {

	private static final Function<Class, Object> VALUE_INSTANCE_CREATOR = type -> {
		try {
			return ClassUtil.newInstance(type);
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}
	};

	/**
	 * Creates a common target over a value, with known scope data.
	 */
	public static Target ofValue(final Object value, final ScopeData scopeData) {
		return new Target(value, null, scopeData, null, VALUE_INSTANCE_CREATOR);
	}
	/**
	 * Creates a common target over a method param.
	 */
	public static Target ofMethodParam(final MethodParam methodParam, final Object object) {
		return new Target(object, methodParam.type(), methodParam.scopeData(), methodParam.mapperFunction(), VALUE_INSTANCE_CREATOR);
	}
	/**
	 * Creates a common target over a method param.
	 */
	public static Target ofMethodParam(final MethodParam methodParam, final Function<Class, Object> valueInstanceCreator) {
		return new Target(null, methodParam.type(), methodParam.scopeData(), methodParam.mapperFunction(), valueInstanceCreator);
	}

	private final Class type;
	private final ScopeData scopeData;
	private Object value;
	private final Function<Class, Object> valueInstanceCreator;
	private final MapperFunction mapperFunction;

	private Target(final Object value, final Class type, final ScopeData scopeData, final MapperFunction mapperFunction, final Function<Class, Object> valueInstanceCreator) {
		this.value = value;
		this.type = type;
		this.scopeData = scopeData;
		this.valueInstanceCreator = valueInstanceCreator;
		this.mapperFunction = mapperFunction;
	}

	/**
	 * Returns targets type, if specified.
	 * @see #resolveType()
	 */
	public Class type() {
		return type;
	}

	/**
	 * Resolves target type: either using {@link #type() provided type}
	 * or type of the {@link #value() value}.
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
	public Object value() {
		return value;
	}

	/**
	 * Returns associated scope data.
	 */
	public ScopeData scopeData() {
		return scopeData;
	}

	// ---------------------------------------------------------------- read

	/**
	 * Reads value from the target. If something goes wrong, exception
	 * is thrown. We assume that outjection is controlled by developer
	 * and that each reading of a value must be a successful operation.
	 */
	public Object readValue(final InjectionPoint injectionPoint) {
		return readValue(injectionPoint.targetName());
	}
	public Object readValue(final String name) {
		String propertyName = name;

		if (type != null) {
			final int dotNdx = propertyName.indexOf('.');

			if (dotNdx == -1) {
				return value;
			}

			propertyName = propertyName.substring(dotNdx + 1);
		}

		return BeanUtil.declared.getProperty(value, propertyName);
	}

	// ---------------------------------------------------------------- write

	/**
	 * Writes value to this target. Depending on a flag, writing the value can be
	 * completely silent, when no exception is thrown and with top performances.
	 * Otherwise, an exception is thrown on a failure.
	 */
	public void writeValue(final InjectionPoint injectionPoint, final Object propertyValue, final boolean silent) {
		writeValue(injectionPoint.targetName(), propertyValue, silent);
	}
	public void writeValue(final String name, final Object propertyValue, final boolean silent) {
		String propertyName = name;

		if (type != null) {
			// target type specified, save into target value!

			int dotNdx = propertyName.indexOf('.');

			if (dotNdx == -1) {

				if (mapperFunction != null) {
					value = mapperFunction.apply(propertyValue);
				}
				else {
					value = TypeConverterManager.get().convertType(propertyValue, type);
				}
				return;
			}

			if (value == null) {
				value = valueInstanceCreator.apply(type);
			}

			propertyName = propertyName.substring(dotNdx + 1);
		}

		// inject into target value

		if (silent) {
			BeanUtil.declaredForcedSilent.setProperty(value, propertyName, propertyValue);
		}
		else {
			BeanUtil.declaredForced.setProperty(value, propertyName, propertyValue);
		}
	}

}
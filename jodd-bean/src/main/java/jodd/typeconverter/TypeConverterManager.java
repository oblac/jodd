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

package jodd.typeconverter;

import java.util.Collection;

/**
 * Provides dynamic object conversion to a type.
 * Contains a map of registered converters. User may add new converter.
 * Static version of {@link TypeConverterManagerBean}.
 */
public class TypeConverterManager {

	private static final TypeConverterManagerBean TYPE_CONVERTER_MANAGER_BEAN = new TypeConverterManagerBean();

	/**
	 * Returns default {@link TypeConverterManager}.
	 */
	public static TypeConverterManagerBean getDefaultTypeConverterManager() {
		return TYPE_CONVERTER_MANAGER_BEAN;
	}

	/**
	 * Registers default set of converters.
	 */
	public static void registerDefaults() {
		TYPE_CONVERTER_MANAGER_BEAN.registerDefaults();
	}

	/**
	 * Registers a converter for specified type.
	 * User must register converter for all super-classes as well.
	 *
	 * @param type class that converter is for
	 * @param typeConverter converter for provided class
	 */
	public static void register(Class type, TypeConverter typeConverter) {
		TYPE_CONVERTER_MANAGER_BEAN.register(type, typeConverter);
	}

	public static void unregister(Class type) {
		TYPE_CONVERTER_MANAGER_BEAN.unregister(type);
	}

	/**
	 * Retrieves converter for provided type. Only registered types are matched,
	 * therefore subclasses must be also registered.
	 *
	 * @return founded converter or <code>null</code>
	 */
	public static TypeConverter lookup(Class type) {
		return TYPE_CONVERTER_MANAGER_BEAN.lookup(type);
	}

	/**
	 * Casts an object to destination type using {@link TypeConverterManager type conversion}.
	 * If destination type is one of common types, consider using {@link jodd.typeconverter.Convert} instead.
	 */
	public static <T> T convertType(Object value, Class<T> destinationType) {
		return TYPE_CONVERTER_MANAGER_BEAN.convertType(value, destinationType);
	}

	/**
	 * Special conversion to collections, when component type is known.
	 */
	public static <T> Collection<T> convertToCollection(Object value, Class<? extends Collection<T>> destinationType, Class componentType) {
		return TYPE_CONVERTER_MANAGER_BEAN.convertToCollection(value, destinationType, componentType);
	}

}
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
 * @see TypeConverterManagerBean
 */
public interface TypeConverterManager {

	/**
	 * Registers default set of converters.
	 */
	void registerDefaults();

	/**
	 * Registers a converter for specified type.
	 * User must register converter for all super-classes as well.
	 *
	 * @param type class that converter is for
	 * @param typeConverter converter for provided class
	 */
	void register(Class type, TypeConverter typeConverter);

	/**
	 * Unregisters certain type from the type converter.
	 * @param type class of a converter to remove
	 */
	void unregister(Class type);

	/**
	 * Retrieves converter for provided type. Only registered types are matched,
	 * therefore subclasses must be also registered.
	 *
	 * @return founded converter or <code>null</code>
	 */
	TypeConverter lookup(Class type);

	/**
	 * Casts an object to destination type using {@link TypeConverterManager type conversion}.
	 * If destination type is one of common types, consider using {@link jodd.typeconverter.Convert} instead.
	 */
	<T> T convertType(Object value, Class<T> destinationType);

	/**
	 * Special conversion to collections, when component type is known.
	 */
	<C extends Collection<T>, T> C convertToCollection(Object value, Class<? extends Collection> destinationType, Class<T> componentType);

}
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

/**
 * Object converter interface.
 *
 * @see TypeConverterManager
 */
@FunctionalInterface
public interface TypeConverter<T> {

	/**
	 * Converts object received as parameter into object of another class.
	 * For example, an <code>Integer</code> converter tries to convert given objects
	 * into target <code>Integer</code> object. Converters should try all reasonable
	 * ways of conversion into target object, depending on target type.
	 *
	 * @param value object to convert from
	 *
	 * @return resulting object converted to target type
	 * @throws TypeConversionException if conversion fails
	 */
	T convert(Object value);

	/**
	 * Converts object and returns default value if conversion fails.
	 */
	default T convert(final Object value, final T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return convert(value);
		}
		catch (Exception e) {
			return defaultValue;
		}
	}

}

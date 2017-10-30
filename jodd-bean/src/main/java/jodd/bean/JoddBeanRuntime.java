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

import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassIntrospector;
import jodd.typeconverter.Converter;
import jodd.typeconverter.TypeConverterManager;

/**
 * Jodd Bean runtime components.
 */
public class JoddBeanRuntime {

	private ClassIntrospector classIntrospector = new CachingIntrospector();
	private Converter converter = new Converter();
	private TypeConverterManager typeConverterManager = new TypeConverterManager(converter);

	/**
	 * Returns the {@link ClassIntrospector} implementation. Default is {@link CachingIntrospector}.
	 */
	public ClassIntrospector classIntrospector() {
		return classIntrospector;
	}

	/**
	 * Changes the {@link ClassIntrospector} implementation.
	 */
	public JoddBeanRuntime classIntrospector(ClassIntrospector introspector) {
		this.classIntrospector = introspector;
		return this;
	}

	/**
	 * Returns the {@link TypeConverterManager}.
	 */
	public TypeConverterManager typeConverterManager() {
		return typeConverterManager;
	}

	/**
	 * Defines the {@link TypeConverterManager} implementation.
	 */
	public JoddBeanRuntime typeConverterManager(TypeConverterManager typeConverterManager) {
		this.typeConverterManager = typeConverterManager;
		return this;
	}

	/**
	 * Returns {@link Converter}.
	 */
	public Converter converter() {
		return converter;
	}

	/**
	 * Defines the {@link Converter}.
	 */
	public JoddBeanRuntime converter(Converter converter) {
		this.converter = converter;
		return this;
	}


}

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

import jodd.introspector.MapperFunction;

import java.lang.annotation.Annotation;

/**
 * Definition of single method parameter.
 */
public class MethodParam {

	private final Class type;
	private final String name;
	private final Class<? extends Annotation> annotationType;
	private final ScopeData scopeData;
	private final MapperFunction mapperFunction;

	public MethodParam(
			final Class type,
			final String name,
			final Class<? extends Annotation> annotationType,
			final ScopeData scopeData,
			final MapperFunction mapperFunction) {
		this.type = type;
		this.name = name;
		this.annotationType = annotationType;
		this.scopeData = scopeData;
		this.mapperFunction = mapperFunction;
	}

	/**
	 * Returns parameter type.
	 */
	public Class type() {
		return type;
	}

	/**
	 * Returns parameter name.
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns parameter Madvoc annotation type, one of
	 * {@link jodd.madvoc.meta.In}, {@link jodd.madvoc.meta.Out}.
	 */
	public Class<? extends Annotation> annotationType() {
		return annotationType;
	}

	/**
	 * Returns scope data.
	 */
	public ScopeData scopeData() {
		return scopeData;
	}

	public MapperFunction mapperFunction() {
		return mapperFunction;
	}
}

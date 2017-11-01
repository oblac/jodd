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

import jodd.Jodd;
import jodd.json.meta.JsonAnnotationManager;

import java.util.Objects;

/**
 * Jodd JSON module.
 */
public class JoddJson {

	private static final JoddJson instance = new JoddJson();

	/**
	 * Returns the module instance.
	 */
	public static JoddJson get() {
		return instance;
	}

	static {
		Jodd.initModule();
	}

	public static void init() {}

	// ---------------------------------------------------------------- instance

	private JoddJsonDefaults defaults = new JoddJsonDefaults();
	private TypeJsonSerializerMap typeSerializers = new TypeJsonSerializerMap();
	private JsonAnnotationManager annotationManager = new JsonAnnotationManager();

	/**
	 * Returns {@link JoddJsonDefaults default configuration}.
	 */
	public JoddJsonDefaults defaults() {
		return defaults;
	}

	/**
	 * Returns {@link TypeJsonSerializerMap type serializer map}
	 */
	public TypeJsonSerializerMap typeSerializers() {
		return typeSerializers;
	}

	/**
	 * Defines new type serializer map.
	 */
	public JoddJson typeSerializers(TypeJsonSerializerMap typeSerializers) {
		Objects.requireNonNull(typeSerializers);
		this.typeSerializers = typeSerializers;
		return this;
	}

	/**
	 * Returns {@link JsonAnnotationManager}.
	 */
	public JsonAnnotationManager annotationManager() {
		return annotationManager;
	}

	/**
	 * Sets new {@link JsonAnnotationManager}.
	 */
	public JoddJson setAnnotationManager(JsonAnnotationManager annotationManager) {
		Objects.requireNonNull(annotationManager);
		this.annotationManager = annotationManager;
		return this;
	}

}
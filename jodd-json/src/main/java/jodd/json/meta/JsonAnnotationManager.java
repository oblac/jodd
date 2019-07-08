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

package jodd.json.meta;

import jodd.cache.TypeCache;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.util.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Cached includes and excludes annotation data per type.
 */
public class JsonAnnotationManager {

	private static final JsonAnnotationManager JSON_ANNOTATION_MANAGER = new JsonAnnotationManager();

	/**
	 * Returns instance of this class.
	 */
	public static JsonAnnotationManager get() {
		return JSON_ANNOTATION_MANAGER;
	}

	private final TypeCache<TypeData> typeDataMap = TypeCache.createDefault();
	private boolean serializationSubclassAware;
	private Class<? extends Annotation> jsonAnnotation;

	@SuppressWarnings("unchecked")
	public JsonAnnotationManager() {
		reset();
	}

	/**
	 * Returns all includes for given type. Returns an empty array
	 * when no includes are defined.
	 */
	public TypeData lookupTypeData(final Class type) {
		TypeData typeData = typeDataMap.get(type);

		if (typeData == null) {
			if (serializationSubclassAware) {
				typeData = findSubclassTypeData(type);
			}

			if (typeData == null) {
				typeData = scanClassForAnnotations(type);
				typeDataMap.put(type, typeData);
			}
		}

		return typeData;
	}

	/**
	 * Lookups type data and creates one if missing.
	 */
	protected TypeData _lookupTypeData(final Class type) {
		TypeData typeData = typeDataMap.get(type);

		if (typeData == null) {
			typeData = scanClassForAnnotations(type);
			typeDataMap.put(type, typeData);
		}

		return typeData;
	}

	/**
	 * Finds type data of first annotated superclass or interface.
	 */
	protected TypeData findSubclassTypeData(final Class type) {
		final Class<? extends Annotation> defaultAnnotation = jsonAnnotation;

		if (type.getAnnotation(defaultAnnotation) != null) {
			// current type has annotation, don't find anything, let type data be created
			return null;
		}

		ClassDescriptor cd = ClassIntrospector.get().lookup(type);

		// lookup superclasses

		Class[] superClasses = cd.getAllSuperclasses();

		for (Class superClass : superClasses) {
			if (superClass.getAnnotation(defaultAnnotation) != null) {
				// annotated subclass founded!
				return _lookupTypeData(superClass);
			}
		}

		Class[] interfaces = cd.getAllInterfaces();

		for (Class interfaze : interfaces) {
			if (interfaze.getAnnotation(defaultAnnotation) != null) {
				// annotated subclass founded!
				return _lookupTypeData(interfaze);
			}
		}

		return null;
	}

	/**
	 * Returns different name of a property if set by annotation.
	 */
	public String resolveJsonName(final Class type, final String name) {
		TypeData typeData = lookupTypeData(type);

		return typeData.resolveJsonName(name);
	}

	/**
	 * Returns real property name for given JSON property.
	 */
	public String resolveRealName(final Class type, final String jsonName) {
		TypeData typeData = lookupTypeData(type);

		return typeData.resolveRealName(jsonName);
	}

	/**
	 * Scans class for annotations and returns {@link TypeData}.
	 */
	private TypeData scanClassForAnnotations(final Class type) {
		ClassDescriptor cd = ClassIntrospector.get().lookup(type);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();

		ArrayList<String> includedList = new ArrayList<>();
		ArrayList<String> excludedList = new ArrayList<>();
		ArrayList<String> jsonNames = new ArrayList<>();
		ArrayList<String> realNames = new ArrayList<>();

		AnnotationParser annotationParser = JSONAnnotationValues.parserFor(jsonAnnotation);

		for (PropertyDescriptor pd : pds) {
			JSONAnnotationValues data = null;
			{
				MethodDescriptor md = pd.getReadMethodDescriptor();

				if (md != null) {
					Method method = md.getMethod();
					data = JSONAnnotationValues.of(annotationParser, method);
				}
			}

			if (data == null) {
				MethodDescriptor md = pd.getWriteMethodDescriptor();

				if (md != null) {
					Method method = md.getMethod();
					data = JSONAnnotationValues.of(annotationParser, method);
				}
			}

			if (data == null) {
				FieldDescriptor fd = pd.getFieldDescriptor();

				if (fd != null) {
					Field field = fd.getField();
					data = JSONAnnotationValues.of(annotationParser, field);
				}
			}

			if (data != null) {
				// annotation found
				String propertyName = pd.getName();

				String newPropertyName = data.name();
				if (newPropertyName != null) {
					realNames.add(propertyName);
					jsonNames.add(newPropertyName);

					propertyName = newPropertyName;
				}

				if (data.include()) {
					includedList.add(propertyName);
				} else {
					excludedList.add(propertyName);
				}
			}
		}


		String[] reals = null;

		if (!realNames.isEmpty()) {
			reals = realNames.toArray(new String[0]);
		}

		String[] jsons = null;
	
		if (!jsonNames.isEmpty()) {
			jsons = jsonNames.toArray(new String[0]);
		}

		// type

		JSONAnnotationValues data = JSONAnnotationValues.of(annotationParser, type);

		return new TypeData(includedList, excludedList, data != null && data.strict(), jsons, reals);
	}

	/**
	 * When set searches for first annotated class or interface and use it's data.
	 */
	public JsonAnnotationManager setSerializationSubclassAware(final boolean serializationSubclassAware) {
		this.serializationSubclassAware = serializationSubclassAware;
		return this;
	}

	/**
	 * Sets different annotation.
	 */
	public JsonAnnotationManager setJsonAnnotation(final Class<? extends Annotation> jsonAnnotation) {
		this.jsonAnnotation = jsonAnnotation;
		return this;
	}

	public void reset() {
		typeDataMap.clear();
		serializationSubclassAware = true;
		jsonAnnotation = JSON.class;
	}
}
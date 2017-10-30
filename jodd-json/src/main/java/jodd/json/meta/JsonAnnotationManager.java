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

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.json.JoddJson;
import jodd.util.ArraysUtil;
import jodd.util.InExRules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cached includes and excludes annotation data per type.
 */
public class JsonAnnotationManager {

	private final Map<Class, TypeData> typeDataMap;

	@SuppressWarnings("unchecked")
	public JsonAnnotationManager() {
		typeDataMap = new HashMap<>();
	}

	/**
	 * Type information read from annotations.
	 */
	public static class TypeData {
		public final InExRules<String, String> rules;
		public final boolean strict;

		public final String[] jsonNames;
		public final String[] realNames;

		public TypeData(List<String> includes, List<String> excludes, boolean strict, String[] jsonNames, String[] realNames) {
			rules = new InExRules<>();

			for (String include : includes) {
				rules.include(include);
			}
			for (String exclude : excludes) {
				rules.exclude(exclude);
			}


			this.strict = strict;
			this.jsonNames = jsonNames;
			this.realNames = realNames;
		}

		/**
		 * Resolves real name from JSON name.
		 */
		public String resolveRealName(String jsonName) {
			if (jsonNames == null) {
				return jsonName;
			}
			int jsonIndex = ArraysUtil.indexOf(jsonNames, jsonName);
			if (jsonIndex == -1) {
				return jsonName;
			}
			return realNames[jsonIndex];
		}

		/**
		 * Resolves JSON name from real name.
		 */
		public String resolveJsonName(String realName) {
			if (realNames == null) {
				return realName;
			}
			int realIndex = ArraysUtil.indexOf(realNames, realName);
			if (realIndex == -1) {
				return realName;
			}
			return jsonNames[realIndex];
		}
	}

	/**
	 * Resets type data map.
	 */
	public void reset() {
		typeDataMap.clear();
	}

	/**
	 * Returns all includes for given type. Returns an empty array
	 * when no includes are defined.
	 */
	public TypeData lookupTypeData(Class type) {
		TypeData typeData = typeDataMap.get(type);

		if (typeData == null) {
			if (JoddJson.defaults().isSerializationSubclassAware()) {
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
	protected TypeData _lookupTypeData(Class type) {
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
	protected TypeData findSubclassTypeData(Class type) {
		if (type.getAnnotation(JoddJson.defaults().getJsonAnnotation()) != null) {
			// current type has annotation, dont find anything, let type data be created
			return null;
		}

		ClassDescriptor cd = ClassIntrospector.get().lookup(type);

		// lookup superclasses

		Class[] superClasses = cd.getAllSuperclasses();

		for (Class superClass : superClasses) {
			if (superClass.getAnnotation(JoddJson.defaults().getJsonAnnotation()) != null) {
				// annotated subclass founded!
				return _lookupTypeData(superClass);
			}
		}

		Class[] interfaces = cd.getAllInterfaces();

		for (Class interfaze : interfaces) {
			if (interfaze.getAnnotation(JoddJson.defaults().getJsonAnnotation()) != null) {
				// annotated subclass founded!
				return _lookupTypeData(interfaze);
			}
		}

		return null;
	}

	/**
	 * Returns different name of a property if set by annotation.
	 */
	public String resolveJsonName(Class type, String name) {
		TypeData typeData = lookupTypeData(type);

		return typeData.resolveJsonName(name);
	}

	/**
	 * Returns real property name for given JSON property.
	 */
	public String resolveRealName(Class type, String jsonName) {
		TypeData typeData = lookupTypeData(type);

		return typeData.resolveRealName(jsonName);
	}

	/**
	 * Scans class for annotations and returns {@link jodd.json.meta.JsonAnnotationManager.TypeData}.
	 */
	private TypeData scanClassForAnnotations(Class type) {
		ClassDescriptor cd = ClassIntrospector.get().lookup(type);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();

		ArrayList<String> includedList = new ArrayList<>();
		ArrayList<String> excludedList = new ArrayList<>();
		ArrayList<String> jsonNames = new ArrayList<>();
		ArrayList<String> realNames = new ArrayList<>();

		JSONAnnotation jsonAnnotation = new JSONAnnotation(JoddJson.defaults().getJsonAnnotation());

		for (PropertyDescriptor pd : pds) {
			JSONAnnotationData data = null;
			{
				MethodDescriptor md = pd.getReadMethodDescriptor();

				if (md != null) {
					Method method = md.getMethod();
					data = jsonAnnotation.readAnnotationData(method);
				}
			}

			if (data == null) {
				MethodDescriptor md = pd.getWriteMethodDescriptor();

				if (md != null) {
					Method method = md.getMethod();
					data = jsonAnnotation.readAnnotationData(method);
				}
			}

			if (data == null) {
				FieldDescriptor fd = pd.getFieldDescriptor();

				if (fd != null) {
					Field field = fd.getField();
					data = jsonAnnotation.readAnnotationData(field);
				}
			}

			if (data != null) {
				// annotation found
				String propertyName = pd.getName();

				String newPropertyName = data.getName();
				if (newPropertyName != null) {
					realNames.add(propertyName);
					jsonNames.add(newPropertyName);

					propertyName = newPropertyName;
				}

				if (data.isIncluded()) {
					includedList.add(propertyName);
				} else {
					excludedList.add(propertyName);
				}
			}
		}


		String[] reals = null;

		if (!realNames.isEmpty()) {
			reals = realNames.toArray(new String[realNames.size()]);
		}

		String[] jsons = null;
	
		if (!jsonNames.isEmpty()) {
			jsons = jsonNames.toArray(new String[jsonNames.size()]);
		}

		// type

		JSONAnnotationData data = (JSONAnnotationData) jsonAnnotation.readAnnotationData(type);

		return new TypeData(includedList, excludedList, data != null && data.isStrict(), jsons, reals);
	}

}
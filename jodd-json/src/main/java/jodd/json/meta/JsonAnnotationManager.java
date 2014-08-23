// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.meta;

import jodd.JoddJson;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
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

	/**
	 * Type information read from annotations.
	 */
	public static class TypeData {
		public final InExRules<String, String> rules;
		public final boolean strict;

		public final String[] jsonNames;
		public final String[] realNames;

		public TypeData(List<String> includes, List<String> excludes, boolean strict, String[] jsonNames, String[] realNames) {
			rules = new InExRules<String, String>();

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

	private final Map<Class, TypeData> typeDataMap = new HashMap<Class, TypeData>();

	private static JsonAnnotationManager jsonAnnotationManager;

	/**
	 * Returns singleton instance of annotation manager.
	 */
	public static JsonAnnotationManager getInstance() {
		if (jsonAnnotationManager == null) {
			jsonAnnotationManager = new JsonAnnotationManager();
		}
		return jsonAnnotationManager;
	}


	private JSONAnnotation jsonAnnotation;

	protected JSONAnnotation getJSONAnnotationReader() {
		if (jsonAnnotation == null) {
			jsonAnnotation = new JSONAnnotation(JoddJson.jsonAnnotation);
		}
		return jsonAnnotation;
	}

	/**
	 * Returns all includes for given type. Returns an empty array
	 * when no includes are defined.
	 */
	public TypeData lookupTypeData(Class type) {
		TypeData typeData = typeDataMap.get(type);

		if (typeData == null) {
			typeData = scanClassForAnnotations(type);
			typeDataMap.put(type, typeData);
		}

		return typeData;
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
		ClassDescriptor cd = ClassIntrospector.lookup(type);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();

		ArrayList<String> includedList = new ArrayList<String>();
		ArrayList<String> excludedList = new ArrayList<String>();
		ArrayList<String> jsonNames = new ArrayList<String>();
		ArrayList<String> realNames = new ArrayList<String>();

		jsonAnnotation = getJSONAnnotationReader();

		for (PropertyDescriptor pd : pds) {
			MethodDescriptor md = pd.getReadMethodDescriptor();

			if (md != null) {
				Method getter = md.getMethod();
				JSONAnnotationData data = jsonAnnotation.readAnnotationData(getter);

				if (data == null) {
					FieldDescriptor fd = pd.getFieldDescriptor();
					if (fd == null) {
						continue;
					}

					Field field = fd.getField();
					data = jsonAnnotation.readAnnotationData(field);
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
		}


		String[] reals = null;

		if (realNames.size() > 0) {
			reals = realNames.toArray(new String[realNames.size()]);
		}

		String[] jsons = null;

		if (jsonNames.size() > 0) {
			jsons = jsonNames.toArray(new String[jsonNames.size()]);
		}

		// type

		JSONAnnotationData data = (JSONAnnotationData) jsonAnnotation.readAnnotationData(type);

		return new TypeData(includedList, excludedList, data != null && data.isStrict(), jsons, reals);
	}

}
// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.meta;

import jodd.JoddJson;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.util.StringPool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Cached includes and excludes annotation data per type.
 */
public class JsonAnnotationManager {

	private static final String[] EMPTY = new String[0];

	/**
	 * Type data.
	 */
	public static class TypeData {
		public final String[] includes;
		public final String[] excludes;
		public final boolean strict;

		public TypeData(String[] includes, String[] excludes, boolean strict) {
			this.includes = includes;
			this.excludes = excludes;
			this.strict = strict;
		}
	}

	private final Map<Class, TypeData> typeDataMap = new HashMap<Class, TypeData>();
	private final Map<String, String> names = new HashMap<String, String>();
	private final Map<String, String> realNames = new HashMap<String, String>();

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
		String signature = type.getName().concat(StringPool.HASH).concat(name);

		String newName = names.get(signature);

		if (newName != null) {
			return newName;
		}

		return name;
	}

	/**
	 * Returns real property name for given JSON property.
	 */
	public String resolveRealName(Class type, String jsonName) {
		String signature = type.getName().concat(StringPool.HASH).concat(jsonName);

		String realName = realNames.get(signature);

		if (realName != null) {
			return realName;
		}

		return jsonName;
	}

	private TypeData scanClassForAnnotations(Class type) {
		ClassDescriptor cd = ClassIntrospector.lookup(type);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();

		ArrayList<String> includedList = new ArrayList<String>();
		ArrayList<String> excludedList = new ArrayList<String>();

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
						String signature = type.getName().concat(StringPool.HASH);
						names.put(signature.concat(propertyName), newPropertyName);
						realNames.put(signature.concat(newPropertyName), propertyName);

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

		String[] incs;

		if (includedList.size() > 0) {
			incs = includedList.toArray(new String[includedList.size()]);
		} else {
			incs = EMPTY;
		}

		String[] excs;

		if (excludedList.size() > 0) {
			excs = excludedList.toArray(new String[excludedList.size()]);
		} else {
			excs = EMPTY;
		}

		// type

		JSONAnnotationData data = (JSONAnnotationData) jsonAnnotation.readAnnotationData(type);

		return new TypeData(incs, excs, data != null && data.isStrict());
	}

}
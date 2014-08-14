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

	private final Map<Class, String[]> includes = new HashMap<Class, String[]>();
	private final Map<Class, String[]> excludes = new HashMap<Class, String[]>();

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
	public String[] lookupIncludes(Class type) {
		String[] incs = includes.get(type);

		if (incs == null) {
			scanClassForAnnotations(type);
			incs = includes.get(type);
		}

		return incs;
	}

	/**
	 * Returns all excludes for given type. Returns an empty array
	 * when no excludes are defined.
	 */
	public String[] lookupExcludes(Class type) {
		String[] excs = excludes.get(type);

		if (excs == null) {
			scanClassForAnnotations(type);
			excs = excludes.get(type);
		}

		return excs;
	}

	/**
	 * Returns different name of a property if set by annotation.
	 */
	public String resolveName(Class type, String name) {
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

	private void scanClassForAnnotations(Class type) {
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

		includes.put(type, incs);

		String[] excs;

		if (excludedList.size() > 0) {
			excs = excludedList.toArray(new String[excludedList.size()]);
		} else {
			excs = EMPTY;
		}

		excludes.put(type, excs);
	}

}
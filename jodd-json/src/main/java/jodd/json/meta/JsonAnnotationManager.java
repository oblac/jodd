package jodd.json.meta;

import jodd.JoddJson;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;

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

	private JSONAnnotation jsonAnnotation;

	protected JSONAnnotation getJSONAnnotationReader() {
		if (jsonAnnotation == null) {
			jsonAnnotation = new JSONAnnotation(JoddJson.jsonAnnotation);
		}
		return jsonAnnotation;
	}

	public String[] lookupIncludes(Class type) {
		String[] incs = includes.get(type);

		if (incs == null) {
			resolveIncludesAndExcludes(type);
			incs = includes.get(type);
		}

		return incs;
	}

	public String[] lookupExcludes(Class type) {
		String[] excs = excludes.get(type);

		if (excs == null) {
			resolveIncludesAndExcludes(type);
			excs = excludes.get(type);
		}

		return excs;
	}

	private void resolveIncludesAndExcludes(Class type) {
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
					String propertyName = pd.getName();

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
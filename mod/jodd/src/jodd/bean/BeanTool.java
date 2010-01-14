// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.util.PrettyStringBuilder;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.bean.loader.BeanLoader;

import java.util.TreeSet;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Various utilities that are applied on whole bean(s). 
 */
public class BeanTool {

	// ---------------------------------------------------------------- copy and apply

	/**
	 * Copies properties of one bean to another. It iterates all getXXX() methods,
	 * reads values and populates destination bean through setXXX().
	 * No exception is thrown on error.
	 *
	 * @param source 		source bean, one to read properties from
	 * @param destination	destination bean, to write properties to
	 * @param suppressSecurity   <code>true</code> to suppress security
	 */
	public static void copy(Object source, Object destination, boolean suppressSecurity) {
		copy(source, destination, true, suppressSecurity);
	}

	public static void apply(Object source, Object destination, boolean suppressSecurity) {
		copy(source, destination, false, suppressSecurity);
	}

	/**
	 * Copies only public properties.
	 * @param source    source bean
	 * @param destination   destination bean
	 * @see #copy(Object, Object, boolean)
	 */
	public static void copy(Object source, Object destination) {
		copy(source, destination, true, false);
	}


	public static void apply(Object source, Object destination) {
		copy(source, destination, false, false);
	}

	public static void copy(Object source, Object destination, boolean copyNulls, boolean suppressSecurity) {
		ClassDescriptor cdSrc = ClassIntrospector.lookup(source.getClass());
		ClassDescriptor cdDest = ClassIntrospector.lookup(destination.getClass());

		String[] mdata = cdSrc.getAllBeanGetterNames(suppressSecurity);
		for (String name : mdata) {
			if (cdDest.getBeanSetter(name, suppressSecurity) != null) {
				Object value = BeanUtil.getProperty(source, name);
				if ((copyNulls == false) && (value == null)) {
					continue;
				}
				BeanUtil.setPropertySilent(destination, name, value);
			}
		}
	}

	// ---------------------------------------------------------------- copy and apply fields

	/**
	 * Copies all fields values from source to destination. It iterates all source fields
	 * and copies data to destination. No exception is thrown on error.
	 */
	public static void copyFields(Object source, Object destination, boolean suppressSecurity) {
		copyFields(source, destination, true, suppressSecurity);
	}

	public static void applyFields(Object source, Object destination, boolean suppressSecurity) {
		copyFields(source, destination, false, suppressSecurity);
	}

	public static void copyFields(Object source, Object destination, boolean copyNulls, boolean suppressSecurity) {
		ClassDescriptor cdSrc = ClassIntrospector.lookup(source.getClass());
		ClassDescriptor cdDest = ClassIntrospector.lookup(destination.getClass());

		Field[] fields = cdSrc.getAllFields(suppressSecurity);
		for (Field field : fields) {
			Field destField = cdDest.getField(field.getName(), suppressSecurity);
			if (destField != null) {
				try {
					Object value = field.get(source);
					if ((copyNulls == false) && (value == null)) {
						continue;
					}
					destField.set(destination, value);
				} catch (IllegalAccessException iaex) {
					// ignore
				}
			}
		}
	}

	public static void copyFields(Object source, Object destination) {
		copyFields(source, destination, true, false);
	}

	public static void applyFields(Object source, Object destination) {
		copyFields(source, destination, false, false);
	}


	// ---------------------------------------------------------------- load

	/**
	 * Populates bean from given object by using a loader for given objects type.
	 */
	public static void load(Object bean, Object source) {
		BeanLoader loader = BeanLoaderManager.lookup(source);
		if (loader == null) {
			throw new BeanException("No registered bean loader for source: " + source.getClass().getName());
		}
		loader.load(bean, source);
	}

	public static void load(Object bean, Object source, Class type) {
		BeanLoader loader = BeanLoaderManager.lookup(type);
		if (loader == null) {
			throw new BeanException("No registered bean loader for type: " + type.getName());
		}
		loader.load(bean, source);
	}


	// ---------------------------------------------------------------- template

	private static final String MACRO_START = "${";

	/**
	 * Replaces named macros with context values. All declared properties are considered during value lookup.
	 */
	public static String parseTemplate(String template, Object context) {
		StringBuilder result = new StringBuilder(template.length());
		int i = 0;
		int len = template.length();
		while (i < len) {
			int ndx = template.indexOf(MACRO_START, i);
			if (ndx == -1) {
				result.append(i == 0 ? template : template.substring(i));
				break;
			}

			// check escaped
			int j = ndx - 1; boolean escape = false; int count = 0;
			while ((j >= 0) && (template.charAt(j) == '\\')) {
				escape = !escape;
				if (escape) {
					count++;
				}
				j--;
			}
			result.append(template.substring(i, ndx - count));
			if (escape == true) {
				result.append(MACRO_START);
				i = ndx + 2;
				continue;
			}

			// find macro end
			ndx += 2;
			int ndx2 = template.indexOf('}', ndx);
			if (ndx2 == -1) {
				throw new BeanException("Bad bean template format - unclosed macro at: " + (ndx - 2));
			}
			String name = template.substring(ndx, ndx2);
			Object value = BeanUtil.getDeclaredProperty(context, name);
			if (value != null) {
				result.append(value.toString());
			}
			i = ndx2 + 1;
		}
		return result.toString();
	}




	// ---------------------------------------------------------------- to string

	/**
	 * Constructs sorted string of all class attributes and their values.
s	 */
	public static String attributesToString(Object bean) {
		PrettyStringBuilder prettyString = new PrettyStringBuilder();
		if (bean == null) {
			return prettyString.toString(bean);
		}

		TreeSet<String> ts = new TreeSet<String>();

		ClassDescriptor cd = ClassIntrospector.lookup(bean.getClass());
		Field[] fields = cd.getAllFields(true);
		for (Field field : fields) {
			StringBuilder s = new StringBuilder();

			int modifiers = field.getModifiers();
			if (Modifier.isPublic(modifiers)) {
				s.append('+');
			} else if (Modifier.isProtected(modifiers)) {
				s.append('#');
			} else if (Modifier.isPrivate(modifiers)) {
				s.append('-');
			} else {
				s.append(' ');
			}
			s.append(field.getName()).append(':');

			try {
				Object value = field.get(bean);
				s.append(prettyString.toString(value));
			} catch (IllegalAccessException iaex) {
				s.append("N/A");
			}
			s.append('\n');
			ts.add(s.toString());
		}

		StringBuilder s = new StringBuilder();
		for (String s1 : ts) {
			s.append(s1);
		}
		return s.toString();
	}


}

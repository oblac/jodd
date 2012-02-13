// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.util.ArraysUtil;
import jodd.util.PrettyStringBuilder;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.bean.loader.BeanLoader;

import java.util.Map;
import java.util.Set;
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
		String[] properties = resolveProperties(source, suppressSecurity);

		for (String name : properties) {
			Object value = BeanUtil.getProperty(source, name);
			if ((copyNulls == false) && (value == null)) {
				continue;
			}
			BeanUtil.setPropertySilent(destination, name, value);
		}
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Returns an array of bean properties. If bean is a map, all its
	 * keys will be returned.
	 */
	public static String[] resolveProperties(Object bean, boolean suppressSecurity) {
		String[] properties;
		if (bean instanceof Map) {
			Set key = ((Map) bean).keySet();
			String[] mdata = new String[key.size()];
			int ndx = 0;
			for (Object o : key) {
				mdata[ndx] = o.toString();
				ndx++;
			}
			properties = mdata;
		} else {
			ClassDescriptor cdSrc = ClassIntrospector.lookup(bean.getClass());
			properties = cdSrc.getAllBeanGetterNames(suppressSecurity);
		}

		return properties;
	}

	// ---------------------------------------------------------------- copy properties

	/**
	 * Copies the property values of the given source bean into the target bean.
	 * The same as {@link #copy(Object, Object)}, but from different angle.
	 */
	public static void copyProperties(Object source, Object destination) {
		copyProperties(source, destination, null, true);
	}

	/**
	 * Copies the property values of the given source bean into the given target bean,
	 * ignoring or including only the given "properties".
	 */
	public static void copyProperties(Object source, Object destination, String[] properties, boolean include) {
		String[] p = resolveProperties(source, false);
		for (String name : p) {
			if (properties != null) {
				if (include)  {
					if (ArraysUtil.contains(properties, name) == false) {
						continue;
					}
				} else {
					if (ArraysUtil.contains(properties, name) == true) {
						continue;
					}
				}
			}
			Object value = BeanUtil.getProperty(source, name);
			BeanUtil.setPropertySilent(destination, name, value);
		}
	}

	public static void copyProperties(Object source, Object destination, Class editable) {
		ClassDescriptor cd = ClassIntrospector.lookup(editable);
		String[] properties = cd.getAllBeanGetterNames();
		copyProperties(source, destination, properties, true);
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
			throw new BeanException("No BeanLoader for: " + source.getClass().getName());
		}
		loader.load(bean, source);
	}

	public static void load(Object bean, Object source, Class type) {
		BeanLoader loader = BeanLoaderManager.lookup(type);
		if (loader == null) {
			throw new BeanException("No BeanLoader for: " + type.getName());
		}
		loader.load(bean, source);
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
			} catch (IllegalAccessException ignore) {
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

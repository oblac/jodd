// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.bean.loader.BeanLoader;

import java.util.Map;
import java.util.Set;

/**
 * Various utilities that are applied on whole bean(s). 
 */
public class BeanTool {

	// ---------------------------------------------------------------- copy and apply

	/**
	 * Copies properties of one bean to another. Iterates all getXXX() methods,
	 * reads values and populates destination bean through setXXX().
	 * No exception is thrown on error.
	 *
	 * @param source 		source bean, one to read properties from
	 * @param destination	destination bean, to write properties to
	 * @param suppressSecurity   <code>true</code> to suppress security
	 */
	public static void copy(Object source, Object destination, boolean suppressSecurity) {
		doCopy(source, destination, true, suppressSecurity);
	}

	/**
	 * Same as {@link #copy(Object, Object, boolean)}, except <code>null</code> values
	 * are not copied. Useful when destination object needs to be only partially updated.
	 */
	public static void apply(Object source, Object destination, boolean suppressSecurity) {
		doCopy(source, destination, false, suppressSecurity);
	}

	/**
	 * Copies only public properties.
	 * @see #copy(Object, Object, boolean)
	 */
	public static void copy(Object source, Object destination) {
		doCopy(source, destination, true, false);
	}

	/**
	 * Applies only public properties.
	 * @see #apply(Object, Object, boolean)
	 */
	public static void apply(Object source, Object destination) {
		doCopy(source, destination, false, false);
	}

	private static void doCopy(Object source, Object destination, boolean copyNulls, boolean suppressSecurity) {
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


}
// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

/**
 * Supreme utility for reading and writing bean properties. However, this one is the fastest available.
 * Although it provides various methods, the whole thing can be easily extended to match most needs.
 * <p>
 * BeanUtil supports:
 * <ul>
 * <li>Nested properties: separated by a dot ('.')</li>
 * <li>Indexed properties: arrays or Lists</li>
 * <li>Simple properties: accessor or Map</li>
 * </ul>
 *
 * <p>
 * Variants includes combinations of forced, declared and silent writing.
 * <ul>
 * <li><i>Forced</i> setting property tries to create destination property so it can be set correctly.</li>
 * <li><i>Silent</i> doesn't throw an exception if destination doesn't exist or if conversion fails.</li>
 * <li><i>Declared</i> includes only declared (public) properties.</li>
 * </ul>
 * <p>
 * This utility considers both bean property methods (set and get accessors), and bean fields.
 * This is done because of several reasons: often there is no need for both set/get accessors, since
 * bean logic requires just one functionality (e.g. just reading). In such case, other bean manipulation
 * libraries still requires to have both accessors in order to set or get value.
 * Another reason is that most common usage is to work with public accessors, and in that case
 * private fields are ignored.
 *
 * @see BeanUtilUtil
 * @see BeanTool
 */
public class BeanUtil {

	private static final BeanUtilBean BEAN_UTIL_BEAN = new BeanUtilBean();

	/**
	 * Returns <code>true</code> if simple property exist.
	 */
	public static boolean hasSimpleProperty(Object bean, String property, boolean suppressSecurity) {
		return BEAN_UTIL_BEAN.hasSimpleProperty(bean, property, suppressSecurity);
	}

	/**
	 * Reads simple property.
	 */
	public static Object getSimpleProperty(Object bean, String property, boolean suppressSecurity) {
		return BEAN_UTIL_BEAN.getSimpleProperty(bean, property, suppressSecurity);
	}

	/**
	 * Reads simple property forced: when property value doesn't exist, it will be created.
	 */
	public static Object getSimplePropertyForced(Object bean, String property, boolean suppressSecurity) {
		return BEAN_UTIL_BEAN.getSimplePropertyForced(bean, property, suppressSecurity);
	}

	public static void setSimpleProperty(Object bean, String property, Object value, boolean suppressSecurity) {
		BEAN_UTIL_BEAN.setSimpleProperty(bean, property, value, suppressSecurity);
	}


	// ---------------------------------------------------------------- indexed property

	public static boolean hasIndexProperty(Object bean, String property, boolean suppressSecurity) {
		return BEAN_UTIL_BEAN.hasIndexProperty(bean, property, suppressSecurity);
	}

	public static Object getIndexProperty(Object bean, String property, boolean suppressSecurity, boolean forced) {
		return BEAN_UTIL_BEAN.getIndexProperty(bean, property, suppressSecurity, forced);
	}

	public static void setIndexProperty(Object bean, String property, Object value, boolean suppressSecurity, boolean forced) {
		BEAN_UTIL_BEAN.setIndexProperty(bean, property, value, suppressSecurity, forced);
	}

	// ---------------------------------------------------------------- SET

	/**
	 * Sets Java Bean property.
	 */
	public static void setProperty(Object bean, String name, Object value) {
		BEAN_UTIL_BEAN.setProperty(bean, name, value);
	}

	/**
	 * Sets Java Bean property silently, without throwing an exception on non-existing properties.
	 */
	public static boolean setPropertySilent(Object bean, String name, Object value) {
		return BEAN_UTIL_BEAN.setPropertySilent(bean, name, value);
	}

	/**
	 * Sets Java Bean property forced.
	 */
	public static void setPropertyForced(Object bean, String name, Object value) {
		BEAN_UTIL_BEAN.setPropertyForced(bean, name, value);
	}
	/**
	 * Sets Java Bean property forced, without throwing an exception on non-existing properties.
	 */
	public static boolean setPropertyForcedSilent(Object bean, String name, Object value) {
		return BEAN_UTIL_BEAN.setPropertyForcedSilent(bean, name, value);
	}

	/**
	 * Sets declared Java Bean property.
	 */
	public static void setDeclaredProperty(Object bean, String name, Object value) {
		BEAN_UTIL_BEAN.setDeclaredProperty(bean, name, value);
	}

	/**
	 * Silently sets declared Java Bean property.
	 */
	public static boolean setDeclaredPropertySilent(Object bean, String name, Object value) {
		return BEAN_UTIL_BEAN.setDeclaredPropertySilent(bean, name, value);
	}

	/**
	 * Sets declared Java Bean property forced.
	 */
	public static void setDeclaredPropertyForced(Object bean, String name, Object value) {
		BEAN_UTIL_BEAN.setDeclaredPropertyForced(bean, name, value);
	}

	/**
	 * Silently sets declared Java Bean property forced.
	 */
	public static boolean setDeclaredPropertyForcedSilent(Object bean, String name, Object value) {
		return BEAN_UTIL_BEAN.setDeclaredPropertyForcedSilent(bean, name, value);
	}

	// ---------------------------------------------------------------- GET

	/**
	 * Returns value of bean's property.
	 */
	public static Object getProperty(Object bean, String name) {
		return BEAN_UTIL_BEAN.getProperty(bean, name);
	}

	/**
	 * Silently returns value of bean's property.
	 * Return value <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 */
	public static Object getPropertySilently(Object bean, String name) {
		return BEAN_UTIL_BEAN.getPropertySilently(bean, name);
	}

	/**
	 * Returns value of declared bean's property.
	 */
	public static Object getDeclaredProperty(Object bean, String name) {
		return BEAN_UTIL_BEAN.getDeclaredProperty(bean, name);
	}

	/**
	 * Silently returns value of declared bean's property.
	 * Return value <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 */
	public static Object getDeclaredPropertySilently(Object bean, String name) {
		return BEAN_UTIL_BEAN.getDeclaredPropertySilently(bean, name);
	}


	// ---------------------------------------------------------------- HAS

	public static boolean hasProperty(Object bean, String name) {
		return BEAN_UTIL_BEAN.hasProperty(bean, name);
	}

	public static boolean hasDeclaredProperty(Object bean, String name) {
		return BEAN_UTIL_BEAN.hasDeclaredProperty(bean, name);
	}

	// ---------------------------------------------------------------- type

	public static Class getPropertyType(Object bean, String name) {
		return BEAN_UTIL_BEAN.getPropertyType(bean, name);
	}

	public static Class getDeclaredPropertyType(Object bean, String name) {
		return BEAN_UTIL_BEAN.getDeclaredPropertyType(bean, name);
	}

	// ---------------------------------------------------------------- utilities

	/**
	 * Extract the first name of this reference. 
	 */
	public static String extractThisReference(String propertyName) {
		return BEAN_UTIL_BEAN.extractThisReference(propertyName);
	}

}
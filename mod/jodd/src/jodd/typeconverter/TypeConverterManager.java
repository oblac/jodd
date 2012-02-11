// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Provides dynamic object conversion to a type.
 * Contains a map of registered converters. User may add new converter.
 * Static version of {@link TypeConverterManagerBean}.
 */
public class TypeConverterManager {

	private static final TypeConverterManagerBean TYPE_CONVERTER_MANAGER_BEAN = new TypeConverterManagerBean();

	/**
	 * Returns default {@link TypeConverterManager}.
	 */
	public static TypeConverterManagerBean getDefaultTypeConverterManager() {
		return TYPE_CONVERTER_MANAGER_BEAN;
	}

	/**
	 * Unregisters all converters.
	 */
	public static void unregisterAll() {
		TYPE_CONVERTER_MANAGER_BEAN.unregisterAll();
	}

	/**
	 * Registers default set of converters.
	 */
	public static void registerDefaults() {
		TYPE_CONVERTER_MANAGER_BEAN.registerDefaults();
	}

	/**
	 * Registers a converter for specified type.
	 * User must register converter for all super-classes as well.
	 *
	 * @param type class that converter is for
	 * @param typeConverter converter for provided class
	 */
	public static void register(Class type, TypeConverter typeConverter) {
		TYPE_CONVERTER_MANAGER_BEAN.register(type, typeConverter);
	}

	public static void unregister(Class type) {
		TYPE_CONVERTER_MANAGER_BEAN.unregister(type);
	}

	/**
	 * Retrieves converter for provided type. Only registered types are matched,
	 * therefore subclasses must be also registered.
	 *
	 * @return founded converter or <code>null</code>
	 */
	public static TypeConverter lookup(Class type) {
		return TYPE_CONVERTER_MANAGER_BEAN.lookup(type);
	}

	/**
	 * Casts an object to destination type using {@link TypeConverterManager type conversion}.
	 * If destination type is one of common types, consider using {@link jodd.typeconverter.Convert} instead.
	 */
	public static <T> T convertType(Object value, Class<T> destinationType) {
		return TYPE_CONVERTER_MANAGER_BEAN.convertType(value, destinationType);
	}

}
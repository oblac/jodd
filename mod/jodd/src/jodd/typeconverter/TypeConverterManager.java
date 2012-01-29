// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import static jodd.JoddDefault.typeConverterManager;

/**
 * Provides dynamic object conversion to a type.
 * Contains a map of registered converters. User may add new converter.
 * Static version of {@link TypeConverterManagerBean}.
 */
public class TypeConverterManager {

	/**
	 * Unregisters all converters.
	 */
	public static void unregisterAll() {
		typeConverterManager.unregisterAll();
	}

	/**
	 * Registers default set of converters.
	 */
	public static void registerDefaults() {
		typeConverterManager.registerDefaults();
	}

	/**
	 * Registers a converter for specified type.
	 * User must register converter for all super-classes as well.
	 *
	 * @param type class that converter is for
	 * @param typeConverter converter for provided class
	 */
	public static void register(Class type, TypeConverter typeConverter) {
		typeConverterManager.register(type, typeConverter);
	}

	public static void unregister(Class type) {
		typeConverterManager.unregister(type);
	}

	/**
	 * Retrieves converter for provided type. Only registered types are matched,
	 * therefore subclasses must be also registered.
	 *
	 * @return founded converter or <code>null</code>
	 */
	public static TypeConverter lookup(Class type) {
		return typeConverterManager.lookup(type);
	}

	/**
	 * Casts an object to destination type using {@link TypeConverterManager type conversion}.
	 * If destination type is one of common types, consider using {@link jodd.typeconverter.Convert} instead.
	 */
	public static <T> T castType(Object value, Class<T> destinationType) {
		return typeConverterManager.castType(value, destinationType);
	}

}
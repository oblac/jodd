// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.json.meta.JSON;

import java.lang.annotation.Annotation;

/**
 * Jodd JSON module.
 */
public class JoddJson {

	static {
		Jodd.module();
	}

	/**
	 * Annotation used for marking the properties.
	 */
	public static Class<? extends Annotation> jsonAnnotation = JSON.class;

	/**
	 * Specifies if 'class' metadata is used. When set, class metadata
	 * is used by {@link jodd.json.JsonSerializer} and all objects
	 * will have additional field with the class type in the resulting JSON.
	 * {@link jodd.json.JsonParser} will also consider this flag to build
	 * correct object type. If <code>null</code>, class information is not used.
	 */
	public static String classMetadataName = null;

	/**
	 * Defines default behavior of a {@link jodd.json.JsonSerializer}.
	 * If set to <code>true</code>, objects will be serialized
	 * deep, so all collections and arrays will get serialized.
	 */
	public static boolean deepSerialization = false;

	/**
	 * Defines if parser will use extended paths information
	 * and path matching.
	 */
	public static boolean useAltPathsByParser = false;

}
// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.meta;

import jodd.util.StringPool;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSON annotation defines if some property has to be included or excluded
 * from the serialization.
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface JSON {

	/**
	 * Defines if JSON serialization of a class
	 * works in a <b>strict</b> mode when only
	 * fields with the annotation are serialized.
	 */
	public boolean strict() default false;

	/**
	 * Defines different property name for annotated item.
	 * Used both for serialization and parsing.
	 */
	public String name() default StringPool.EMPTY;

	/**
	 * Specifies if a property should be included or excluded from the JSON serialization.
	 */
	public boolean include() default true;

}
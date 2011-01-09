// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.madvoc.ScopeType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields and setters where injection should be performed.
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface In {

	/**
	 * Specifies if fields should be created if not found.
	 */
	boolean create() default true;

	/**
	 * Specifies parameter scope.
	 */
	ScopeType scope() default ScopeType.REQUEST;

	/**
	 * Specifies if property should be removed from the scope.
	 * Sometimes it is not possible to do so.
	 */
	boolean remove() default false;

	/**
	 * Specifies non-default parameter name.
	 */
	String value() default "";

}
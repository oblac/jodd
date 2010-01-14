// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.util.StringPool;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for action methods. It is not necessary to mark a method, however, this annotation 
 * may be used to specify non-default action path.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Action {

	/**
	 * Marker for empty action extension.
	 */
	String NO_EXTENSION = StringPool.HASH;

	/**
	 * Action path value.
	 */
	String value() default "";

	/**
	 * Different path extension.
	 */
	String extension() default "";

	/**
	 * Specifies if action method name should be excluded from the the action path.
	 */
	boolean notInPath() default false;

	/**
	 * Defines alias.
	 */
	String alias() default "";

	/**
	 * Defines action method (such as HTTP request method).
	 */
	String method() default "";

}
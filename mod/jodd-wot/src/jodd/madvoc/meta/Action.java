// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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
	 * Marker for empty action method or extension.
	 */
	String IGNORE = StringPool.HASH;

	/**
	 * Action path value. If equals to {@link #IGNORE} action method name
	 * will not be part of the created action path.
	 */
	String value() default "";

	/**
	 * Action path extension. If equals to {@link #IGNORE} extension will be not
	 * part of created action path.
	 */
	String extension() default "";

	/**
	 * Defines alias.
	 */
	String alias() default "";

	/**
	 * Defines action method (such as HTTP request method).
	 */
	String method() default "";

}
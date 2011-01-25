// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CustomUserAction {

	/**
	 * Action path value.
	 */
	String value() default "";

	/**
	 * Different path extension.
	 */
	String extension() default "custom";

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
	String method() default "GET";

}

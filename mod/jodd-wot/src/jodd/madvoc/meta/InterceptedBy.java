// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.madvoc.interceptor.ActionInterceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines array of action interceptors. May be used on whole class and all action methods,
 * or on single method overriding class default value. 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface InterceptedBy {

	/**
	 * Array of action interceptors.
	 */
	Class<? extends ActionInterceptor>[] value();
}
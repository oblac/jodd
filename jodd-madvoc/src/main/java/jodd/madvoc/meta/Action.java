// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.util.StringPool;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for action methods. It is not necessary to mark a method, however, this annotation 
 * may be used to specify non-default action path. Moreover, this annotation may be used
 * to mark custom annotations!
 * @see jodd.madvoc.meta.ActionAnnotationData
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Action {

	/**
	 * Marker for empty action method or extension.
	 */
	String NONE = StringPool.HASH;

	// see: http://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Request_methods

	String ANY = "";
	String GET = "GET";
	String HEAD = "HEAD";
	String POST = "POST";
	String PUT = "PUT";
	String DELETE = "DELETE";
	String TRACE = "TRACE";
	String OPTIONS = "OPTIONS";
	String CONNECT = "CONNECT";
	String PATCH = "PATCH";

	/**
	 * Action path value. If equals to {@link #NONE} action method name
	 * will not be part of the created action path.
	 */
	String value() default "";

	/**
	 * Action path extension. If equals to {@link #NONE} extension will be not
	 * part of created action path.
	 */
	String extension() default "";

	/**
	 * Defines alias.
	 */
	String alias() default "";

	/**
	 * Defines action method (such as HTTP request method: GET, POST....).
	 * Ignore it or use {@link #ANY} to ignore the method.
	 */
	String method() default "";

	/**
	 * Defines if action has to be called asynchronously
	 * using Servlets 3.0 API.
	 */
	boolean async() default false;

}
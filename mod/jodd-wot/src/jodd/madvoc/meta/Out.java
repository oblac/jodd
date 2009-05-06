// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.madvoc.ScopeType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields and getters where outjection should be performed.
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Out {

	/**
	 * Specifies parameter scope.
	 */
	ScopeType scope() default ScopeType.REQUEST;

	/**
	 * Specifies non-default parameter name.
	 */
	String value() default "";

}

// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.meta;

import jodd.petite.scope.Scope;
import jodd.petite.scope.DefaultScope;
import jodd.petite.WiringMode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Petite bean annotation for classes.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PetiteBean {

	/**
	 * Unique bean name.
	 */
	String value() default "";

	/**
	 * Bean scope,
	 */
	Class<? extends Scope> scope() default DefaultScope.class;

	/**
	 * Wiring mode.
	 */
	WiringMode wiring() default WiringMode.DEFAULT;

}

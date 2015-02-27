// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.meta;

import jodd.petite.scope.Scope;
import jodd.petite.WiringMode;
import jodd.petite.scope.SingletonScope;

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
	 * Bean scope, {@link jodd.petite.scope.SingletonScope singleton} by default.
	 */
	Class<? extends Scope> scope() default SingletonScope.class;

	/**
	 * Wiring mode.
	 */
	WiringMode wiring() default WiringMode.DEFAULT;

}

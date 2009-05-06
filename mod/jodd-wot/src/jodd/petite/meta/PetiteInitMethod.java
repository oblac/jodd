// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Points to the Petite bean implementation. If used on an interfaces, Petite will
 * resolve specified implementation. If used on package, all interfaces marked with PetiteBean
 * will be resolved to real implementation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PACKAGE})
public @interface PetiteInitMethod {

	/**
	 * Indicates the order of init method. Order number of first methods starts from 0.
	 * Order number of last methods starts from 0 to negatives.
	 */
	int order() default 0;
}

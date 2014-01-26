// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.meta;

import jodd.madvoc.meta.Action;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSON action annotation. Extension is set to <b>json</b>.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Action(extension = "json")
public @interface JSONAction {

	String value() default "";

	String alias() default "";

	String method() default "";

}

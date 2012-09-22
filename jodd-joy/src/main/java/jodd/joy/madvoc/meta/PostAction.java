// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.meta;

import jodd.joy.madvoc.action.AppAction;
import jodd.madvoc.meta.Action;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Post action annotation. Extension is set to '<b>NONE</b>' and method
 * is set to '<b>POST</b>'.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PostAction {

	String value() default "";

	String extension() default Action.NONE;

	String alias() default "";

	String method() default AppAction.METHOD_POST;

}
// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.madvoc.result.ActionResult;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class annotation that defines which Madvoc {@link jodd.madvoc.result.ActionResult action result}
 * will be used for rendering.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RenderWith {

	/**
	 * Action result class that will be used to render action result return value.
	 */
	Class<? extends ActionResult> value();

}
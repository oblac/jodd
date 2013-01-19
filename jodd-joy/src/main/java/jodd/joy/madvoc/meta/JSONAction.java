// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSON action annotation. Extension is set to '<b>json</b>' and
 * result type to {@link jodd.joy.madvoc.result.JSONResult json}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JSONAction {

	String value() default "";

	String extension() default "json";

	String alias() default "";

	String method() default "";

	String result() default "json";

}

// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for Madvoc action, i.e. classes with action methods.
 * <p>
 * Madvoc action classes may be annotated with this annotation. Annotation is necessary if default
 * auto-magic configuration is used. Its value defines an action path prefix
 * for all {@link Action} methods. If default empty value is used, action path is built implicitly
 * from the class name, by uncapitalizing the first character and removing the action class name suffix.
 * <p>
 * This annotation can be applied to package, changing the default prefix path.
s */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PACKAGE})
public @interface MadvocAction {

	/**
	 * Action path value.
	 */
	String value() default "";

}

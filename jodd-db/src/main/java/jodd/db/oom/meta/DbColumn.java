// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.meta;

import jodd.db.type.SqlType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields marker for mapped columns.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbColumn {

	/**
	 * Column name.
	 */
	String value() default "";

	/**
	 * SqlType class.
	 */
	Class<? extends SqlType> sqlType() default SqlType.class;

}

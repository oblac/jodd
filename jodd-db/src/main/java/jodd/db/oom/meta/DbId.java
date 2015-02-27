// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.meta;

import jodd.db.type.SqlType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Field marker for ID columns, not necessary to use, but might be helpful.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbId {

	/**
	 * ID column name.
	 */
	String value() default "";

	/**
	 * SqlType class.
	 */
	Class<SqlType> sqlType() default SqlType.class;

}
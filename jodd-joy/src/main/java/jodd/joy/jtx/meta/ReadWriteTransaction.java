// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.jtx.meta;

import jodd.jtx.JtxIsolationLevel;
import jodd.jtx.JtxPropagationBehavior;
import jodd.jtx.JtxTransactionMode;
import jodd.jtx.meta.Transaction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Read-write PROPAGATION_REQUIRED tx annotation marker.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Transaction(readOnly = false)
public @interface ReadWriteTransaction {

	JtxPropagationBehavior propagation() default JtxPropagationBehavior.PROPAGATION_REQUIRED;

	JtxIsolationLevel isolation() default JtxIsolationLevel.ISOLATION_DEFAULT;

	int timeout() default JtxTransactionMode.DEFAULT_TIMEOUT;

}

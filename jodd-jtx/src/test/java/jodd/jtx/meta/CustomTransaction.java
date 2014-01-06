// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.meta;

import jodd.jtx.JtxIsolationLevel;
import jodd.jtx.JtxPropagationBehavior;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Transaction(readOnly = false)
public @interface CustomTransaction {

	JtxPropagationBehavior propagation() default JtxPropagationBehavior.PROPAGATION_REQUIRED;

	JtxIsolationLevel isolation() default JtxIsolationLevel.ISOLATION_DEFAULT;

}
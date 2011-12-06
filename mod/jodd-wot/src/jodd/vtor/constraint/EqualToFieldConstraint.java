// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;
import jodd.vtor.VtorException;


public class EqualToFieldConstraint implements ValidationConstraint<EqualToField> {

	public EqualToFieldConstraint() {
	}

	public EqualToFieldConstraint(String fieldName) {
		this.fieldName = fieldName;
	}

	// ---------------------------------------------------------------- properties

	protected String fieldName;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	// ---------------------------------------------------------------- configure

	public void configure(EqualToField annotation) {
		this.fieldName = annotation.value();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(vcc.getTarget(), value, fieldName);
	}

	public static boolean validate(Object target, Object value, String fieldName) {
		if (value == null) {
			return true;
		}
		Object valueToCompare;
		try {
			valueToCompare = BeanUtil.getProperty(target, fieldName);
		} catch (BeanException bex) {
			throw new VtorException("Unable to read value to compare: '" + fieldName + "'.", bex);
		}
		if (valueToCompare == null) {
			return false;
		}
		return value.equals(valueToCompare);
	}


}

// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.util.StringUtil;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;


public class HasSubstringConstraint implements ValidationConstraint<HasSubstring> {

	public HasSubstringConstraint() {
	}

	public HasSubstringConstraint(String substring, boolean ignoreCase) {
		this.substring = substring;
		this.ignoreCase = ignoreCase;
	}

	// ---------------------------------------------------------------- properties

	protected String substring;
	protected boolean ignoreCase;

	public String getSubstring() {
		return substring;
	}

	public void setSubstring(String substring) {
		this.substring = substring;                                          
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	// ---------------------------------------------------------------- configure

	public void configure(HasSubstring annotation) {
		this.substring = annotation.value();
		this.ignoreCase = annotation.ignoreCase();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, substring, ignoreCase);
	}

	public static boolean validate(Object value, String substring, boolean ignoreCase) {
		if (value == null) {
			return true;
		}
		if (ignoreCase) {
			return StringUtil.indexOfIgnoreCase(value.toString(), substring) > -1;
		}
		return value.toString().indexOf(substring) > -1;
	}

}
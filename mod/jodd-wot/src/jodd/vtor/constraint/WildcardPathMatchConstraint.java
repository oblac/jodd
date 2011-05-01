// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.constraint;

import jodd.util.Wildcard;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

public class WildcardPathMatchConstraint implements ValidationConstraint<WildcardPathMatch> {

	public WildcardPathMatchConstraint() {
	}

	public WildcardPathMatchConstraint(String pattern) {
		this.pattern = pattern;
	}

	// ---------------------------------------------------------------- properties

	protected String pattern;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	// ---------------------------------------------------------------- configure
	
	public void configure(WildcardPathMatch annotation) {
		pattern = annotation.value();
	}

	// ---------------------------------------------------------------- valid

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value, pattern);
	}

	public static boolean validate(Object value, String pattern) {
		if (value == null) {
			return true;
		}
		return Wildcard.matchPath(value.toString(), pattern);
	}

}

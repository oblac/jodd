// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.vtor.constraint;

import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;
import jodd.vtor.VtorException;

public class EqualToDeclaredFieldConstraint implements ValidationConstraint<EqualToDeclaredField> {

	public EqualToDeclaredFieldConstraint() {
	}

	public EqualToDeclaredFieldConstraint(String fieldName) {
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

	public void configure(EqualToDeclaredField annotation) {
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
			valueToCompare = BeanUtil.declared.getProperty(target, fieldName);
		} catch (BeanException bex) {
			throw new VtorException("Invalid value: " + fieldName, bex);
		}
		if (valueToCompare == null) {
			return false;
		}
		return value.equals(valueToCompare);
	}
}
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

import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;
import jodd.vtor.ValidationContext;

/**
 * Special annotation that validates inner context of provided value. 
 */
public class AssertValidConstraint implements ValidationConstraint<AssertValid> {

	protected final ValidationContext targetValidationContext;

	public AssertValidConstraint(ValidationContext targetValidationContext) {
		this.targetValidationContext = targetValidationContext;
	}

	// ---------------------------------------------------------------- config

	public void configure(AssertValid annotation) {
	}

	// ---------------------------------------------------------------- valid

	/**
	 * Invokes validation on inner context. Always returns <code>true</code> since
	 * inner context violations will be appended to provided validator.
	 */
	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		if (value == null) {
			return true;
		}
		vcc.validateWithin(targetValidationContext, value);
		return true;
	}
}

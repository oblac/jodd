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

package jodd.vtor;

/**
 * Validation constraint context, used for validation in {@link ValidationConstraint}.
 */
public class ValidationConstraintContext {

	protected final Vtor vtor;
	protected final Object target;
	protected final String name;

	public ValidationConstraintContext(Vtor vtor, Object target, String name) {
		this.vtor = vtor;
		this.target = target;
		this.name = name;
	}

	/**
	 * Returns validator.
	 */
	public Vtor getValidator() {
		return vtor;
	}

	/**
	 * Returns target object containing the value.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Returns context name.
	 */
	public String getName() {
		return name;
	}


	/**
	 * Validates provided context and value withing this constraint content.
	 */
	public void validateWithin(ValidationContext vctx, Object value) {
		vtor.validate(vctx, value, name);
	}
}

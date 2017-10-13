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
 * Validation violation description.
 */
public class Violation {

	private final String name;
	private final Object validatedObject;
	private final Object invalidValue;
	private final Check check;
	private final ValidationConstraint constraint;

	public Violation(String name, Object validatedObject, Object invalidValue) {
		this(name, validatedObject, invalidValue, null);
	}

	/**
	 * Creates new violation.
	 * @param name violation name inside of validation context
	 * @param validatedObject object that is validated
	 * @param invalidValue invalid value that is cause of violation
	 * @param check {@link Check check} that made validation. 
	 */
	public Violation(String name, Object validatedObject, Object invalidValue, Check check) {
		this.name = name;
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.check = check;
		this.constraint = check != null ? check.getConstraint() : null;
	}

	public Object getValidatedObject() {
		return validatedObject;
	}

	public Object getInvalidValue() {
		return invalidValue;
	}

	public Check getCheck() {
		return check;
	}

	public ValidationConstraint getConstraint() {
		return constraint;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Violation{" + name + ':' + constraint.getClass().getName() + '}';
	}
}

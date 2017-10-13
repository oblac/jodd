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

package jodd.joy.madvoc.action;

import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.vtor.Vtor;
import jodd.vtor.Violation;

import java.util.List;

/**
 * Abstract base application action.
 */
public abstract class AppAction {

	public static final String BACK = "#";
	public static final String OK = "ok";

	public static final String REDIRECT = "redirect:";
	public static final String DISPATCH = "dispatch:";
	public static final String CHAIN = "chain:";
	public static final String MOVE = "move:";
	public static final String NONE = "none:";
	public static final String VTOR_JSON = "vtor-json:";

	public static final String ALIAS_INDEX = "<index>";
	public static final String ALIAS_INDEX_NAME = "index";

	/**
	 * Creates alias. 
	 */
	protected String alias(String target) {
		return StringPool.LEFT_CHEV.concat(target).concat(StringPool.RIGHT_CHEV);
	}

	/**
	 * Creates alias from target class and target method name.
	 */
	protected String alias(Class targetClass, String targetMethodName) {
		return '<' + targetClass.getName() + '#' + targetMethodName + '>';
	}

	/**
	 * Creates alias from target object and target method name.
	 * If classname contains a '$' sign, everything will be stripped
	 * after it (to get the real name, if action class is proxified).
	 */
	protected String alias(Object target, String targetMethodName) {
		String targetClassName = target.getClass().getName();
		targetClassName = StringUtil.cutToIndexOf(targetClassName, '$');
		return '<' + targetClassName + '#' + targetMethodName + '>';
	}


	// ---------------------------------------------------------------- validation

	protected Vtor vtor;

	/**
	 * Returns validation violations or <code>null</code> if validation was successful.
	 */
	public List<Violation> violations() {
		if (vtor == null) {
			return null;
		}
		return vtor.getViolations();
	}

	@SuppressWarnings({"NullArgumentToVariableArgMethod"})
	protected boolean validateAction() {
		return validateAction(null);
	}

	/**
	 * Validates action. Profiles are reset after the invocation.
	 * @return <code>true</code> if validation is successful, otherwise returns <code>false</code>
	 */
	protected boolean validateAction(String... profiles) {
		prepareValidator();
		vtor.useProfiles(profiles);
		vtor.validate(this);
		vtor.resetProfiles();
		List<Violation> violations = vtor.getViolations();
		return violations == null;
	}

	/**
	 * Adds action violation.
	 */
	protected void addViolation(String name, Object invalidValue) {
		prepareValidator();
		vtor.addViolation(new Violation(name, this, invalidValue));
	}

	/**
	 * Adds action violation.
	 */
	protected void addViolation(String name) {
		addViolation(name, null);
	}

	protected void prepareValidator() {
		if (vtor == null) {
			vtor = new Vtor();
		}
	}

}
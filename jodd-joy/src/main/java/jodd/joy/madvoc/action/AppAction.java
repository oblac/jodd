// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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
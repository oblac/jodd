// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.action;

import jodd.joy.page.PageRequest;
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

	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_TRACE = "TRACE";
	public static final String METHOD_OPTIONS = "OPTIONS";

	public static final String REDIRECT = "redirect:";
	public static final String DISPATCH = "dispatch:";
	public static final String CHAIN = "chain:";
	public static final String JSON = "json:";
	public static final String MOVE = "move:";
	public static final String RAW = "raw:";
	public static final String NONE = "none:";
	public static final String VTOR_JSON = "vtor-json:";

	public static final String ALIAS_INDEX = "<index>";
	public static final String ALIAS_INDEX_NAME = "index";
	public static final String ALIAS_LOGIN = "<login>";
	public static final String ALIAS_LOGIN_NAME = "login";
	public static final String ALIAS_ACCESS_DENIED = "<accessDenied>";
	public static final String ALIAS_ACCESS_DENIED_NAME = "accessDenied";

	public static final String EXT_JSON = "json";

	/**
	 * Creates alias. 
	 */
	protected String alias(String target) {
		return '<' + target + '>';
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

	// ---------------------------------------------------------------- paging

	/**
	 * Applies page size on given page request.
	 */
	protected PageRequest applyPageSize(PageRequest pageRequest, int pageSize) {
		if (pageSize != 0) {
			if (pageRequest == null) {
				pageRequest = new PageRequest();
				pageRequest.setSize(pageSize);
			}
		}
		return pageRequest;
	}

}

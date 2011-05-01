// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import jodd.bean.BeanUtil;
import jodd.util.StringUtil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Vtor validator.
 */
public class Vtor {

	public static final String DEFAULT_PROFILE = "default";
	public static final String ALL_PROFILES = "*";

	// ---------------------------------------------------------------- violations

	protected List<Violation> violations;

	/**
	 * Adds new {@link Violation violation}. Violations are added during {@link #validate(ValidationContext, Object, String) validation}.
	 * They can be added after the validation as well, with <code>null</code> check (and constraint).
	 */
	public void addViolation(Violation v) {
		if (v == null) {
			return;
		}
		if (violations == null) {
			violations = new LinkedList<Violation>();
		}
		violations.add(v);
	}

	/**
	 * Resets list of all violations.
	 */
	public void resetViolations() {
		violations = null;
	}

	// ---------------------------------------------------------------- validation

	/**
	 * Validate object using context from the annotations.
	 */
	public void validate(Object target) {
		validate(ValidationContext.resolveFor(target.getClass()), target);
	}

	/**
	 * @see #validate(ValidationContext, Object, String)
	 */
	public void validate(ValidationContext vctx, Object target) {
		validate(vctx, target, null);
	}

	/**
	 * Performs validation of provided validation context and appends violations.
	 */
	public void validate(ValidationContext ctx, Object target, String targetName) {
		for (Map.Entry<String, List<Check>> entry : ctx.map.entrySet()) {
			String name = entry.getKey();
			Object value = BeanUtil.getDeclaredPropertySilently(target, name);
			String valueName = targetName != null ? (targetName + '.' + name) : name;		// pomeri gore
			ValidationConstraintContext vcc = new ValidationConstraintContext(this, target, valueName);
			
			for (Check check : entry.getValue()) {
				String[] checkProfiles = check.getProfiles();
				if (matchProfiles(checkProfiles) == false) {
					continue;
				}
				if (check.getSeverity() < severity) {
					continue;
				}
				ValidationConstraint constraint = check.getConstraint();
				if (constraint.isValid(vcc, value) == false) {
					addViolation(new Violation(valueName, target, value, check));
				}
			}
		}
	}

	// ---------------------------------------------------------------- severity

	protected int severity;

	/**
	 * Set validation severity. Only checks with equal and higher severity
	 * will be checked.
	 */
	public void setSeverity(int severity) {
		this.severity = severity;
	}

	// ---------------------------------------------------------------- profiles

	protected HashSet<String> enabledProfiles;

	protected boolean validateAllProfilesByDefault;

	public boolean isValidateAllProfilesByDefault() {
		return validateAllProfilesByDefault;
	}

	/**
	 * Specifies how to validate when no profiles is specified.
	 * If set to <code>true</code>, then <b>all</b> profiles will be validated;
	 * otherwise, only <b>default</b> profiles will be validated.
	 */
	public void setValidateAllProfilesByDefault(boolean validateAllProfilesByDefault) {
		this.validateAllProfilesByDefault = validateAllProfilesByDefault;
	}

	/**
	 * Enables single profile.
	 */
	public void useProfile(String profile) {
		if (profile == null) {
			return;
		}
		if (this.enabledProfiles == null) {
			this.enabledProfiles = new HashSet<String>();
		}
		this.enabledProfiles.add(profile);
	}

	/**
	 * Enables list of profiles.
	 */
	public void useProfiles(String... enabledProfiles) {
		if (enabledProfiles == null) {
			return;
		}
		if (this.enabledProfiles == null) {
			this.enabledProfiles = new HashSet<String>();
		}
		for (String profile : enabledProfiles) {
			this.enabledProfiles.add(profile);
		}
	}

	/**
	 * Reset profiles by clearing all enabled profiles
	 * and setting to default state.
	 * @see #setValidateAllProfilesByDefault(boolean) 
	 */
	public void resetProfiles() {
		enabledProfiles = null;
	}


	/**
	 * Determine if any of checks profiles is among enabled profiles.
	 */
	protected boolean matchProfiles(String[] checkProfiles) {
		// test for all profiles
		if ((checkProfiles != null) && (checkProfiles.length == 1) && checkProfiles[0].equals(ALL_PROFILES)) {
			return true;
		}
		if (enabledProfiles == null || enabledProfiles.isEmpty()) {
			if (validateAllProfilesByDefault) {
				return true;	// all profiles are considered as enabled
			}
			// only default profile is enabled
			if ((checkProfiles == null) || (checkProfiles.length == 0)) {
				return true;
			}
			for (String profile : checkProfiles) {
				if (StringUtil.isEmpty(profile)) {
					return true;	// default profile
				}
				if (profile.equals(DEFAULT_PROFILE)) {
					return true;
				}
			}
			return false;
		}
		// there are enabled profiles
		if ((checkProfiles == null) || (checkProfiles.length == 0)) {
			return enabledProfiles.contains(DEFAULT_PROFILE);
		}
		boolean result = false;
		for (String profile : checkProfiles) {
			boolean b = true;
			if (StringUtil.isEmpty(profile)) {
				profile = DEFAULT_PROFILE;
			} else if (profile.charAt(0) == '-') {
				profile = profile.substring(1);
				b = false;
			}
			if (enabledProfiles.contains(profile)) {
				if (b == false) {
					return false;
				}
				result = true;
			}
		}
		return result;
	}

	// ---------------------------------------------------------------- after validation

	/**
	 * Returns the list of validation violations or <code>null</code> if validation is successful.
	 */
	public List<Violation> getViolations() {
		return violations;
	}

	/**
	 * Returns <code>true</code> if there are validations.
	 */
	public boolean hasViolations() {
		return violations != null;
	}

}

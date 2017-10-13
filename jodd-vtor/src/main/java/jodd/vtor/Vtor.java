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

import jodd.bean.BeanUtil;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Vtor validator.
 */
public class Vtor {

	/**
	 * Static constructor for fluent usage.
	 */
	public static Vtor create() {
		return new Vtor();
	}

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
			violations = new ArrayList<>();
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
	public List<Violation> validate(Object target) {
		return validate(ValidationContext.resolveFor(target.getClass()), target);
	}

	/**
	 * @see #validate(ValidationContext, Object, String)
	 */
	public List<Violation> validate(ValidationContext vctx, Object target) {
		return validate(vctx, target, null);
	}

	/**
	 * Performs validation of provided validation context and appends violations.
	 */
	public List<Violation> validate(ValidationContext ctx, Object target, String targetName) {
		for (Map.Entry<String, List<Check>> entry : ctx.map.entrySet()) {
			String name = entry.getKey();
			Object value = BeanUtil.declaredSilent.getProperty(target, name);
			String valueName = targetName != null ? (targetName + '.' + name) : name;		// move up
			ValidationConstraintContext vcc = new ValidationConstraintContext(this, target, valueName);
			
			for (Check check : entry.getValue()) {
				String[] checkProfiles = check.getProfiles();
				if (!matchProfiles(checkProfiles)) {
					continue;
				}
				if (check.getSeverity() < severity) {
					continue;
				}
				ValidationConstraint constraint = check.getConstraint();
				if (!constraint.isValid(vcc, value)) {
					addViolation(new Violation(valueName, target, value, check));
				}
			}
		}

		return getViolations();
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
			this.enabledProfiles = new HashSet<>();
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
			this.enabledProfiles = new HashSet<>();
		}
		Collections.addAll(this.enabledProfiles, enabledProfiles);
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
			boolean must = false;
			if (StringUtil.isEmpty(profile)) {
				profile = DEFAULT_PROFILE;
			} else if (profile.charAt(0) == '-') {
				profile = profile.substring(1);
				b = false;
			} else if (profile.charAt(0) == '+') {
				profile = profile.substring(1);
				must = true;
			}

			if (enabledProfiles.contains(profile)) {
				if (!b) {
					return false;
				}
				result = true;
			} else {
				if (must) {
					return false;
				}
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

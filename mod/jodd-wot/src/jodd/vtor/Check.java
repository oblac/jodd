// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

/**
 * Holder for validation constraint.
 */
public class Check {

	private final String name;
	private final ValidationConstraint constraint;

	private int severity;
	private String[] profiles;

	/**
	 * Creates new check for provided constraint inside current context. 
	 */
	public Check(String name, ValidationConstraint constraint) {
		this.name = name;
		this.constraint = constraint;
	}

	/**
	 * Returns check name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns constraint to check.
	 */
	public ValidationConstraint getConstraint() {
		return constraint;
	}

	// ---------------------------------------------------------------- common

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public String[] getProfiles() {
		return profiles;
	}

	public void setProfiles(String... profiles) {
		this.profiles = profiles;
	}
}

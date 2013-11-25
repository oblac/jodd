// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

/**
 * Holds original props value and generated one.
 */
public class PropsEntry {

	/**
	 * Original value.
	 */
	protected final String value;

	/**
	 * Value with all macros resolved. May be <code>null</code> when
	 * value doesn't contain anything to resolve.
	 */
	protected String resolved;

	protected PropsEntry next;

	protected final String key;

	protected final String profile;

	public PropsEntry(final String key, final String value, String profile) {
		this.value = value;
		this.key = key;
		this.profile = profile;
	}

	/**
	 * Returns either resolved or real value.
	 */
	public String getValue() {
		return resolved != null ? resolved : value;
	}

	/**
	 * Returns property key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns property profile or <code>null</code> if this is a base property.
	 */
	public String getProfile() {
		return profile;
	}

	@Override
	public String toString() {
		return "PropsEntry{" + key + (profile != null ? '<' + profile + '>' : "") + '=' + value + (resolved == null ? "" : "}{" + resolved) + '}';
	}

}
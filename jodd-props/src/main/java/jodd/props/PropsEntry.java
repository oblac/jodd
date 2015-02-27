// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

/**
 * Holds props value.
 */
public class PropsEntry {

	/**
	 * Original value.
	 */
	protected final String value;

	protected PropsEntry next;

	protected final String key;

	protected final String profile;

	protected final boolean hasMacro;

	protected final PropsData propsData;

	public PropsEntry(String key, String value, String profile, PropsData propsData) {
		this.value = value;
		this.key = key;
		this.profile = profile;
		this.hasMacro = value.contains("${");
		this.propsData = propsData;
	}

	/**
	 * Returns the raw value. Macros are not replaced.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the property value, with replaced macros.
	 */
	public String getValue(String... profiles) {
		if (hasMacro) {
			return propsData.resolveMacros(value, profiles);
		}
		return value;
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

	/**
	 * Returns <code>true</code> if value has a macro to resolve.
	 */
	public boolean hasMacro() {
		return hasMacro;
	}

	@Override
	public String toString() {
		return "PropsEntry{" + key + (profile != null ? '<' + profile + '>' : "") + '=' + value + '}';
	}

}
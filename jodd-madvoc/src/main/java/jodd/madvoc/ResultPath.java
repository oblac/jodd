// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Holder of result path value. Consists of 2 parts:
 * <ul>
 *     <li>path - the first part, that comes usually from action config</li>
 *     <li>value - second part, that comes from action, may be <code>null</code></li>
 * </ul>
 */
public class ResultPath {

	protected final String path;
	protected final String value;

	public ResultPath(String path, String value) {
		this.path = path;
		this.value = value;
	}

	public String getPath() {
		return path;
	}

	public String getValue() {
		return value;
	}

	public String getPathValue() {
		if (value == null) {
			return path;
		}
		return path + '.' + value;
	}

	@Override
	public String toString() {
		return getPathValue();
	}

}
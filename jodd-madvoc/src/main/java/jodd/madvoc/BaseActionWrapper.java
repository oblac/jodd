// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Common base implementation of {@link ActionWrapper}.
 */
public abstract class BaseActionWrapper implements ActionWrapper {

	protected boolean enabled = true;

	/**
	 * Returns <code>true</code> if interceptor is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Defines if interceptor is enabled.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public void init() {
	}

}
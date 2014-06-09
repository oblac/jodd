// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Implementation of {@link jodd.lagarto.Doctype} used during parsing.
 * Only one instance is created per parsing and it is going to be reused.
 */
public class ParsedDoctype implements Doctype {

	protected CharSequence name;
	protected CharSequence publicIdentifier;
	protected CharSequence systemIdentifier;
	protected boolean quirksMode;

	public void setName(CharSequence name) {
		this.name = name;
	}

	public void setQuirksMode(boolean quirksMode) {
		this.quirksMode = quirksMode;
	}

	public void reset() {
		name = null;
		quirksMode = false;
		publicIdentifier = null;
		systemIdentifier = null;
	}

	public void setPublicIdentifier(CharSequence publicIdentifier) {
		this.publicIdentifier = publicIdentifier;
	}

	public void setSystemIdentifier(CharSequence systemIdentifier) {
		this.systemIdentifier = systemIdentifier;
	}


	// ---------------------------------------------------------------- get

	/**
	 * {@inheritDoc}
	 */
	public CharSequence getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isQuirksMode() {
		return quirksMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public CharSequence getPublicIdentifier() {
		return publicIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public CharSequence getSystemIdentifier() {
		return systemIdentifier;
	}

	// ---------------------------------------------------------------- util

}
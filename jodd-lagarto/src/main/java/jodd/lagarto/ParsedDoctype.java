// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.nio.CharBuffer;

/**
 * Implementation of {@link jodd.lagarto.Doctype} used during parsing.
 * Only one instance is created per parsing and it is going to be reused.
 */
public class ParsedDoctype implements Doctype {

	private final char[] input;		// todo NEMOJ da pravis isecak - ali kada pravi DOM ne sme da se koristi

	public ParsedDoctype(char[] input) {
		this.input = input;
	}

	// ---------------------------------------------------------------- set

	protected CharSequence name;
	protected CharSequence publicIdentifier;
	protected CharSequence systemIdentifier;
	protected boolean quirksMode;

	public void setName(int startNdx, int endIndex) {
		this.name = charSequence(startNdx, endIndex);
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

	public void setPublicIdentifier(int startNdx, int endIndex) {
		publicIdentifier = charSequence(startNdx, endIndex);
	}

	public void setSystemIdentifier(int startNdx, int endIndex) {
		systemIdentifier = charSequence(startNdx, endIndex);
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

	/**
	 * Creates either a char buffer or a String.
	 * // todo add behaviour flag
	 */
	protected CharSequence charSequence(int fromNdx, int toNdx) {
		return CharBuffer.wrap(input, fromNdx, toNdx - fromNdx);
	}

}
// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * DOCTYPE tag information.
 */
public interface Doctype {

	/**
	 * Returns DOCTYPE name. Returns <code>null</code> if name was not specified (due to parsing error).
	 */
	public CharSequence getName();

	/**
	 * Returns <code>true</code> if quirks mode was forced.
	 */
	public boolean isQuirksMode();

	/**
	 * Returns public identifier, if specified. Returns <code>null</code>
	 * if not specified, DOCTYPE is SYSTEM or there was a parsing error.
	 */
	public CharSequence getPublicIdentifier();

	/**
	 * Returns system identifier, if specified. Returns <code>null</code>
	 * if not specified or there was a parsing error.
	 */
	public CharSequence getSystemIdentifier();

}
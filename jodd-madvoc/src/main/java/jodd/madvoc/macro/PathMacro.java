// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.macro;

/**
 * Action path macro.
 */
public interface PathMacro {

	/**
	 * Initializes <code>PathMacro</code> and returns
	 * <code>true</code> if macro is found. Otherwise,
	 * returns <code>false</code> and the instance can
	 * be thrown away.
	 */
	boolean init(String chunk);

	/**
	 * Return all macro names.
	 */
	String[] getNames();

	/**
	 * Match provided path chunk with the path macro,
	 * Returns the number of non-macro characters if
	 * value is matched. Returns -1 if value is not
	 * matched.
	 */
	int match(String chunk);

	/**
	 * Extracts array of macro values for matched chunk
	 * for each {@link #getNames() name}.
	 */
	String[] extract(String chunk);

}

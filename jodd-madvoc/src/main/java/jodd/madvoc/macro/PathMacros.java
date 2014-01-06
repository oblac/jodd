// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.macro;

/**
 * Action path macros.
 */
public interface PathMacros {

	/**
	 * Initializes <code>PathMacro</code> and returns
	 * <code>true</code> if macros are found in the action
	 * path. Otherwise, returns <code>false</code> and
	 * the instance can be thrown away.
	 */
	boolean init(String actionPath);

	/**
	 * Returns names of all macros.
	 */
	String[] getNames();

	/**
	 * Returns all patterns. Some elements may be <code>null</code>
	 * if some macro does not define a pattern.
	 */
	String[] getPatterns();

	/**
	 * Returns macros count.
	 */
	int getMacrosCount();

	/**
	 * Match provided action path with the path macros,
	 * Returns the number of matched non-macro characters.
	 * Returns -1 if action path is not matched.
	 */
	int match(String actionPath);

	/**
	 * Extracts array of macro values for matched action path
	 * for each {@link #getNames() name}. It is assumed
	 * that path macro was previously {@link #init(String) initialized}
	 * on this action path, i.e. input is not validated.
	 * <p>
	 * Returned array string of macro values may contain
	 * <code>null</code> on all ignored macros.
	 */
	String[] extract(String actionPath);

}
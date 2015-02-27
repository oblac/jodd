// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Holds IN and OUT information for single scope.
 */
public class ScopeData {

	public In[] in;
	public Out[] out;

	public static class In {
		public Class type;			// property type
		public String name;			// property name
		public String target;		// real property name, if different from 'name'
	}
	public static class Out {
		public Class type;			// property type
		public String name;			// property name
		public String target;		// real property name, if different from 'name'
	}

}

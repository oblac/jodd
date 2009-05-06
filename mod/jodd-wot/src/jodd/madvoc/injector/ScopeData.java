// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;

/**
 * Holds action data for a scope.
 */
public class ScopeData {

	public ScopeData(ScopeType scope) {
		this.scope = scope;
	}

	public ScopeType getScopeType() {
		return scope;
	}

	final ScopeType scope;
	public In[] in;
	public Out[] out;

	public static class In {
		public Class type;         // property type
		public String name;        // property name
		public String target;      // real input name, if different from 'name'
		public boolean create;     // should it be created if null?
		public boolean remove;		// should input be removed?
	}
	public static class Out {
		public Class type;         // property type
		public String name;        // property name
		public String target;      // real output name, if different from 'name'
	}

}

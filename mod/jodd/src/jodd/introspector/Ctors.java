// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.Constructor;

/**
 * Constructors map collection.
 */
class Ctors {

	Constructor[] allCtors;
	Class[][] allArgs;
	boolean locked;
	Constructor defaultCtor;

	void addCtors(Constructor[] ctors) {
		if (locked == true) {
			throw new IllegalStateException("Constructor introspection is already finished.");
		}
		allCtors = ctors;
		allArgs = new Class[allCtors.length][];
		for (int i = 0; i < ctors.length; i++) {
			Constructor ctor = ctors[i];
			allArgs[i] = ctor.getParameterTypes();
			if (allArgs[i].length == 0) {
				defaultCtor = ctor;
			}
		}
	}

	void lock() {
		locked = true;
	}


	// ---------------------------------------------------------------- get

	Constructor getDefaultCtor() {
		return defaultCtor;
	}

	Constructor getCtor(Class[] args) {
		ctors:
		for (int i = 0; i < allArgs.length; i++) {
			Class[] arg = allArgs[i];
			if (arg.length != args.length) {
				continue;
			}
			for (int j = 0; j < arg.length; j++) {
				if (arg[j] != args[j]) {
					continue ctors;
				}
			}
			return allCtors[i];
		}
		return null;
	}

	int getCount() {
		return allCtors.length;
	}

	Constructor[] getAllCtors() {
		return allCtors;
	}

}

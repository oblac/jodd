// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.Constructor;

/**
 * Constructors collection.
 */
class Ctors {

	private final ClassDescriptor classDescriptor;

	private Constructor[] allCtors;
	private Class[][] allArgs;
	private Constructor defaultCtor;

	Ctors(ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
	}

	/**
	 * Add all ctors at once.
	 */
	void addCtors(Constructor... ctors) {
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

	// ---------------------------------------------------------------- get

	/**
	 * Returns default (no-args) ctor.
	 */
	Constructor getDefaultCtor() {
		return defaultCtor;
	}

	/**
	 * Returns ctor for given argument types.
	 */
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

	/**
	 * Returns ctor count.
	 */
	int getCount() {
		return allCtors.length;
	}

	/**
	 * Returns all ctors.
	 */
	Constructor[] getAllCtors() {
		return allCtors;
	}

}

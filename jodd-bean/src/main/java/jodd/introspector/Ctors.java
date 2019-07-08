// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.introspector;

import java.lang.reflect.Constructor;

/**
 * Constructors collection.
 */
public class Ctors {

	protected final ClassDescriptor classDescriptor;
	protected final CtorDescriptor[] allCtors;
	protected CtorDescriptor defaultCtor;

	public Ctors(final ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
		this.allCtors = inspectConstructors();
	}

	/**
	 * Inspects all declared constructors of a target type.
	 */
	protected CtorDescriptor[] inspectConstructors() {
		Class type = classDescriptor.getType();
		Constructor[] ctors = type.getDeclaredConstructors();

		CtorDescriptor[] allCtors = new CtorDescriptor[ctors.length];

		for (int i = 0; i < ctors.length; i++) {
			Constructor ctor = ctors[i];

			CtorDescriptor ctorDescriptor = createCtorDescriptor(ctor);
			allCtors[i] = ctorDescriptor;

			if (ctorDescriptor.isDefault()) {
				defaultCtor = ctorDescriptor;
			}
		}

		return allCtors;
	}

	/**
	 * Creates new {@link CtorDescriptor}.
	 */
	protected CtorDescriptor createCtorDescriptor(final Constructor ctor) {
		return new CtorDescriptor(classDescriptor, ctor);
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns default (no-args) constructor descriptor.
	 */
	public CtorDescriptor getDefaultCtor() {
		return defaultCtor;
	}

	/**
	 * Finds constructor description that matches given argument types.
	 */
	public CtorDescriptor getCtorDescriptor(final Class... args) {
		ctors:
		for (CtorDescriptor ctorDescriptor : allCtors) {
			Class[] arg = ctorDescriptor.getParameters();

			if (arg.length != args.length) {
				continue;
			}

			for (int j = 0; j < arg.length; j++) {
				if (arg[j] != args[j]) {
					continue ctors;
				}
			}

			return ctorDescriptor;
		}
		return null;
	}

	/**
	 * Returns all constructor descriptors.
	 */
	CtorDescriptor[] getAllCtorDescriptors() {
		return allCtors;
	}

}
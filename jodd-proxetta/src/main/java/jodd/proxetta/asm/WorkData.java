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

package jodd.proxetta.asm;

import jodd.asm7.ClassVisitor;
import jodd.util.StringPool;

import java.util.ArrayList;
import java.util.List;

import static jodd.util.StringPool.DOT;

/**
 * Holds various information about the current process of making proxy.
 */
public final class WorkData {

	final ClassVisitor dest;

	WorkData(final ClassVisitor dest) {
		this.dest = dest;
	}

	// ---------------------------------------------------------------- data

	String targetPackage;
	String targetClassname;
	String nextSupername;
	String superName;
	String superReference;
	ProxyAspectData[] proxyAspects;
	String wrapperRef;
	String wrapperType;
	boolean wrapInterface;
	boolean allowFinalMethods;

	public String thisReference;
	public boolean proxyApplied;

	public boolean isWrapper() {
		return wrapperRef != null;
	}

	// ---------------------------------------------------------------- init

	/**
	 * Work data initialization.
	 */
	public void init(String name, final String superName, final String suffix, final String reqProxyClassName) {
		int lastSlash = name.lastIndexOf('/');
		this.targetPackage = lastSlash == -1 ? StringPool.EMPTY : name.substring(0, lastSlash).replace('/', '.');
		this.targetClassname = name.substring(lastSlash + 1);
		this.nextSupername = superName;
		this.superName = name;

		// create proxy name
		if (reqProxyClassName != null) {
			if (reqProxyClassName.startsWith(DOT)) {
				name = name.substring(0, lastSlash) + '/' + reqProxyClassName.substring(1);
			} else if (reqProxyClassName.endsWith(DOT)) {
				name = reqProxyClassName.replace('.', '/') + this.targetClassname;
			} else {
				name = reqProxyClassName.replace('.', '/');
			}
		}

		// add optional suffix
		if (suffix != null) {
			name += suffix;
		}
		this.thisReference = name;
		this.superReference = this.superName;
	}



	// ---------------------------------------------------------------- advice clinits

	List<String> adviceClinits;

	/**
	 * Saves used static initialization blocks (clinit) of advices.
	 */
	void addAdviceClinitMethod(final String name) {
		if (adviceClinits == null) {
			adviceClinits = new ArrayList<>();
		}
		adviceClinits.add(name);
	}

	// ---------------------------------------------------------------- advice inits

	List<String> adviceInits;

	/**
	 * Saves used constructors of advices.
	 */
	void addAdviceInitMethod(final String name) {
		if (adviceInits == null) {
			adviceInits = new ArrayList<>();
		}
		adviceInits.add(name);
	}

}

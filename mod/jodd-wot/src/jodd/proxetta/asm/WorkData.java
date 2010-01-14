// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.ClassVisitor;

import java.util.List;
import java.util.ArrayList;

/**
 * Holds various information about the current process of making proxy.
 */
final class WorkData {

	final ClassVisitor dest;

	WorkData(ClassVisitor dest) {
		this.dest = dest;
	}

	// ---------------------------------------------------------------- data

	String targetPackage;
	String targetClassname;
	boolean proxyApplied;
	String nextSupername;
	String thisReference;
	String superReference;
	ProxyAspectData[] proxyAspects;

	// ---------------------------------------------------------------- advice clinits

	List<String> adviceClinits;

	/**
	 * Saves used static initialization blocks (clinit) of advices.
	 */
	void addAdviceClinitMethod(String name) {
		if (adviceClinits == null) {
			adviceClinits = new ArrayList<String>();
		}
		adviceClinits.add(name);
	}

	// ---------------------------------------------------------------- advice inits

	List<String> adviceInits;

	/**
	 * Saves used constructors of advices.
	 */
	void addAdviceInitMethod(String name) {
		if (adviceInits == null) {
			adviceInits = new ArrayList<String>();
		}
		adviceInits.add(name);
	}

}

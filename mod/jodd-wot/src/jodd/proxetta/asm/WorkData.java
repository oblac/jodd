// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.ClassVisitor;

import java.util.List;
import java.util.ArrayList;

import static jodd.util.StringPool.DOT;

/**
 * Holds various information about the current process of making proxy.
 */
public final class WorkData {

	final ClassVisitor dest;

	WorkData(ClassVisitor dest) {
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

	public String thisReference;
	public boolean proxyApplied;

	// ---------------------------------------------------------------- init

	/**
	 * Work data initialization.
	 */
	public void init(String name, String superName, String suffix, String reqProxyClassName) {
		int lastSlash = name.lastIndexOf('/');
		this.targetPackage = name.substring(0, lastSlash).replace('/', '.');
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

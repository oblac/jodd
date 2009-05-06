// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.ClassVisitor;

import java.util.List;
import java.util.ArrayList;

/**
 * Holds some various information about the destination.
 */
class DestinationData {

	final ClassVisitor dest;

	DestinationData(ClassVisitor dest) {
		this.dest = dest;
	}

	// ---------------------------------------------------------------- advice clinits

	protected List<String> adviceClinits;

	/**
	 * Saves used static initialization blocks (clinit) of advices.
	 */
	protected void addClinitMethod(String name) {
		if (adviceClinits == null) {
			adviceClinits = new ArrayList<String>();
		}
		adviceClinits.add(name);
	}

	// ---------------------------------------------------------------- advice inits

	protected List<String> adviceInits;

	/**
	 * Saves used constructors of advices.
	 */
	protected void addInitMethod(String name) {
		if (adviceInits == null) {
			adviceInits = new ArrayList<String>();
		}
		adviceInits.add(name);
	}


}

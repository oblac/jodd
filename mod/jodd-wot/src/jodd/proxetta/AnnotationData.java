// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import org.objectweb.asm.signature.SignatureReader;

import java.util.Map;
import java.util.HashMap;

import jodd.proxetta.asm.MethodSignatureVisitor;

/**
 * Annotation data for method signatures.
 */
public class AnnotationData {

	public AnnotationData(String signature, boolean visible) {
		this.signature = signature;
		this.isVisible = visible;

		MethodSignatureVisitor sv = new MethodSignatureVisitor(signature);
		new SignatureReader(signature).accept(sv);
		declaration = sv.getDeclaration().substring(9);
	}

	public final String signature;

	public final boolean isVisible;

	public final String declaration;

	public final Map<String, Object> values = new HashMap<String, Object>();

	@Override
	public String toString() {
		return "Annotation: " + declaration;
	}
}

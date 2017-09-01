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

import jodd.proxetta.AnnotationInfo;
import jodd.proxetta.TypeInfo;

import static jodd.proxetta.asm.AnnotationReader.NO_ANNOTATIONS;

/**
 * Implementation of {@link TypeInfo}. Method information is collected during
 * different types of class scanning (signature, method), so not everything
 * is available at one moment.
 */
public class TypeInfoImpl implements TypeInfo {

	private final char opcode;
	private final String type;
	private final String name;
	private final String rawName;

	/**
	 * Argument annotations are not available on method signature scanning.
	 * Therefore, we need to inject them from the outside.
	 */
	protected AnnotationInfo[] annotations = NO_ANNOTATIONS;

	public TypeInfoImpl(char opcode, String type, String name, String rawName) {
		this.opcode = opcode;
		this.type = type;
		this.name = name;
		this.rawName = rawName;
	}

	/**
	 * Returns annotation info of type. SOme types (like for returning value)
	 * can't have annotation information.
	 */
	@Override
	public AnnotationInfo[] getAnnotations() {
		return annotations;
	}

	/**
	 * Returns bytecode opcode.
	 */
	@Override
	public char getOpcode() {
		return opcode;
	}

	/**
	 * Returns java-like, e.g. {@code "java.lang.Integer"}.
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * Returns bytecode-like type name, e.g. {@code "Ljava/lang/Integer;"}.
	 * Note that generics type names are <b>not</b> resolved.
	 * @see #getRawName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Returns bytecode-like type name, e.g. {@code "Ljava/lang/Integer;"}.
	 * @see #getName()
	 */
	@Override
	public String getRawName() {
		return rawName;
	}
}

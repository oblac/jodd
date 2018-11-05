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

import jodd.asm.AsmUtil;
import jodd.asm.EmptyAnnotationVisitor;
import jodd.asm7.AnnotationVisitor;
import jodd.proxetta.AnnotationInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Reads annotation inner data.
 */
@SuppressWarnings({"AnonymousClassVariableHidesContainingMethodVariable"})
public class AnnotationReader extends EmptyAnnotationVisitor implements AnnotationInfo {

	public static final AnnotationInfo[] NO_ANNOTATIONS = new AnnotationInfo[0];

	protected final String desc;
	protected final String className;
	protected final boolean visible;
	protected final Map<String, Object> elements;

	public AnnotationReader(final String desc, final boolean visible) {
		this.desc = desc;
		this.visible = visible;
		this.elements = new HashMap<>();
		this.className = AsmUtil.typeref2Name(desc);
	}

	// ---------------------------------------------------------------- info

	@Override
	public String getAnnotationClassname() {
		return className;
	}

	@Override
	public String getAnnotationSignature() {
		return desc;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public Object getElement(final String name) {
		return elements.get(name);
	}

	@Override
	public Set<String> getElementNames() {
		return elements.keySet();
	}

	// ---------------------------------------------------------------- visitor


	@Override
	public void visit(final String name, final Object value) {
		elements.put(name, value);
	}

	@Override
	public void visitEnum(final String name, final String desc, final String value) {
		elements.put(name, new String[]{desc, value});		
	}

	@Override
	public AnnotationVisitor visitAnnotation(final String name, final String desc) {
		AnnotationReader nestedAnnotation = new AnnotationReader(desc, true);
		elements.put(name, nestedAnnotation);
		return nestedAnnotation;
	}

	@Override
	public AnnotationVisitor visitArray(final String name) {
		final List<Object> array = new ArrayList<>();
		return new EmptyAnnotationVisitor() {

			@Override
			public void visit(final String name, final Object value) {
				array.add(value);
			}

			@Override
			public void visitEnd() {
				Object[] data = array.toArray(new Object[0]);
				elements.put(name, data);
			}
		};
	}

}

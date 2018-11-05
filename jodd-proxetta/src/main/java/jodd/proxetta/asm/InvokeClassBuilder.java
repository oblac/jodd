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

import jodd.asm.ClassAdapter;
import jodd.asm7.ClassVisitor;
import jodd.asm7.MethodVisitor;
import jodd.proxetta.InvokeAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * Invocation replacer class adapter.
 */
public class InvokeClassBuilder extends ClassAdapter {

	protected final WorkData wd;
	protected final InvokeAspect[] aspects;
	protected final String suffix;
	protected final String reqProxyClassName;
	protected final TargetClassInfoReader targetClassInfo;

	public InvokeClassBuilder(final ClassVisitor dest, final InvokeAspect[] invokeAspects, final String suffix, final String reqProxyClassName, final TargetClassInfoReader targetClassInfoReader) {
		super(dest);
		this.aspects = invokeAspects;
		this.suffix = suffix;
		this.wd = new WorkData(dest);
		this.targetClassInfo = targetClassInfoReader;
		this.reqProxyClassName = reqProxyClassName;
	}

	/**
	 * Returns working data.
	 */
	public WorkData getWorkData() {
		return wd;
	}

	// ---------------------------------------------------------------- header

	@Override
	public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
		wd.init(name, superName, suffix, reqProxyClassName);

		// write destination class
		final int v = ProxettaAsmUtil.resolveJavaVersion(version);
		super.visit(v, access, wd.thisReference, signature, wd.superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		final MethodSignatureVisitor msign = targetClassInfo.lookupMethodSignatureVisitor(access, name, desc, wd.superReference);

		if (msign == null) {
			super.visitMethod(access, name, desc, signature, exceptions);
			return null;
		}

		// check aspects that apply to this method
		List<InvokeAspect> applicableAspects = new ArrayList<>(aspects.length);
		for (InvokeAspect aspect : aspects) {
			if (aspect.apply(msign)) {
				applicableAspects.add(aspect);
			}
		}

		if (applicableAspects.isEmpty()) {
			super.visitMethod(access, name, desc, signature, exceptions);
			return null;
		}

		InvokeAspect[] nextAspects = new InvokeAspect[applicableAspects.size()];
		nextAspects = applicableAspects.toArray(nextAspects);

		return new InvokeReplacerMethodAdapter(
				super.visitMethod(access, name, desc, signature, exceptions), msign, wd, nextAspects);
	}

}

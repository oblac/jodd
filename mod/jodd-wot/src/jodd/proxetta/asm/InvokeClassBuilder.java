// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.proxetta.InvokeAspect;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

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

	public InvokeClassBuilder(ClassVisitor dest, InvokeAspect[] invokeAspects, String suffix, String reqProxyClassName, TargetClassInfoReader targetClassInfoReader) {
		super(dest);
		this.aspects = invokeAspects;
		this.suffix = suffix;
		this.wd = new WorkData(dest);
		this.targetClassInfo = targetClassInfoReader;
		this.reqProxyClassName = reqProxyClassName;
	}

	// ---------------------------------------------------------------- header

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		wd.init(name, superName, suffix, reqProxyClassName);

		// write destination class
		super.visit(version, access, wd.thisReference, signature, wd.superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		final MethodSignatureVisitor msign = targetClassInfo.lookupMethodSignatureVisitor(access, name, desc, wd.superReference);

		if (msign == null) {
			super.visitMethod(access, name, desc, signature, exceptions);
			return null;
		}

		// check aspects that apply to this method
		List<InvokeAspect> applicableAspects = new ArrayList<InvokeAspect>(aspects.length);
		for (InvokeAspect aspect : aspects) {
			if (aspect.apply(msign) == true) {
				applicableAspects.add(aspect);
			}
		}

		if (applicableAspects.isEmpty()) {
			super.visitMethod(access, name, desc, signature, exceptions);
			return null;
		}

		InvokeAspect[] nextAspects = new InvokeAspect[applicableAspects.size()];
		nextAspects = applicableAspects.toArray(nextAspects);

		return new InvokeReplacerMethodAdapter(super.visitMethod(access, name, desc, signature, exceptions), msign, wd, nextAspects);
	}

}

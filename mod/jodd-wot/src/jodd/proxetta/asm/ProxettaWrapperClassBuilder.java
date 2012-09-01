// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.asm.AsmConst;
import jodd.asm.AsmUtil;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyAspect;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

import static jodd.proxetta.asm.ProxettaAsmUtil.CLINIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadVirtualMethodArguments;
import static jodd.proxetta.asm.ProxettaAsmUtil.visitReturn;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class ProxettaWrapperClassBuilder extends ProxettaClassBuilder {

	protected final Class targetClassOrInterface;
	protected final Class targetInterface;
	protected final String targetFieldName;

	public ProxettaWrapperClassBuilder(
			Class targetClassOrInterface,
			Class targetInterface,
			String targetFieldName,
			ClassVisitor dest,
			ProxyAspect[] aspects,
			String suffix,
			String reqProxyClassName,
			TargetClassInfoReader targetClassInfoReader) {

		super(dest, aspects, suffix, reqProxyClassName, targetClassInfoReader);
		this.targetClassOrInterface = targetClassOrInterface;
		this.targetInterface = targetInterface;
		this.targetFieldName = targetFieldName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

		wd.init(name, superName, this.suffix, this.reqProxyClassName);

		// no superclass
		wd.superName = AsmConst.SIGNATURE_JAVA_LANG_OBJECT;

		// change access of destination
		access &= ~AsmConst.ACC_ABSTRACT;
		access &= ~AsmConst.ACC_INTERFACE;

		// write destination class
		if (targetClassOrInterface.isInterface()) {
			wd.wrapInterface = true;
			interfaces = new String[] {AsmUtil.typeToTyperef(targetClassOrInterface)};
		} else {
			wd.wrapInterface = false;
			if (targetInterface != null) {
				interfaces = new String[] {AsmUtil.typeToTyperef(targetInterface)};
			} else {
				interfaces = null;
			}
		}
		wd.dest.visit(version, access, wd.thisReference, signature, wd.superName, interfaces);

		wd.proxyAspects = new ProxyAspectData[aspects.length];
		for (int i = 0; i < aspects.length; i++) {
			wd.proxyAspects[i] = new ProxyAspectData(wd, aspects[i], i);
		}

		// create new field wrapper field and store it's reference into work-data
		wd.wrapperRef = targetFieldName;
		wd.wrapperType = 'L' + name + ';';
		FieldVisitor fv  = wd.dest.visitField(AsmConst.ACC_PUBLIC, wd.wrapperRef, wd.wrapperType, null, null);
		fv.visitEnd();

		createEmptyCtor();
	}

	/**
	 * Created empty default constructor.
	 */
	protected void createEmptyCtor() {
		MethodVisitor mv = wd.dest.visitMethod(AsmConst.ACC_PUBLIC, INIT, "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, AsmConst.SIGNATURE_JAVA_LANG_OBJECT, INIT, "()V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodSignatureVisitor msign = targetClassInfo.lookupMethodSignatureVisitor(access, name, desc, wd.superReference);
		if (msign == null) {
			return null;
		}

		// ignore all destination constructors
		if (name.equals(INIT) == true) {
			return null;
		}
		// ignore all destination static block
		if (name.equals(CLINIT) == true) {
			return null;
		}

		return applyProxy(msign);
	}

	@Override
	protected ProxettaMethodBuilder applyProxy(MethodSignatureVisitor msign) {
		List<ProxyAspectData> aspectList = matchMethodPointcuts(msign);

		if (aspectList == null) {
			wd.proxyApplied = true;
			createSimpleMethodWrapper(msign);
			return null;
		}

		wd.proxyApplied = true;
		return new ProxettaMethodBuilder(msign, wd, aspectList);

	}

	/**
	 * Creates simple method wrapper without proxy.
	 */
	protected void createSimpleMethodWrapper(MethodSignatureVisitor msign) {

		int access = msign.getAccessFlags();

		access &= ~ACC_ABSTRACT;
		access &= ~ACC_NATIVE;

		MethodVisitor mv = wd.dest.visitMethod(access, msign.getMethodName(), msign.getDescription(), msign.getSignature(), msign.getExceptionsArray());
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, wd.thisReference, wd.wrapperRef, wd.wrapperType);
		loadVirtualMethodArguments(mv, msign);
		if (wd.wrapInterface) {
			mv.visitMethodInsn(INVOKEINTERFACE, wd.wrapperType.substring(1, wd.wrapperType.length() - 1), msign.getMethodName(), msign.getDescription());
		} else {
			mv.visitMethodInsn(INVOKEVIRTUAL, wd.wrapperType.substring(1, wd.wrapperType.length() - 1), msign.getMethodName(), msign.getDescription());		// todo ovo sa substring prebaci
		}
		ProxettaAsmUtil.prepareReturnValue(mv, msign, 0);
		visitReturn(mv, msign, true, false);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	@Override
	public void visitEnd() {
		makeStaticInitBlock();

		processSuperMethods();

		wd.dest.visitEnd();

	}
}

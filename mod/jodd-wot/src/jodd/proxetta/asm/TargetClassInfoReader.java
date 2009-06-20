// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.signature.SignatureReader;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;

import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.CLINIT;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ClassInfo;
import jodd.util.ClassLoaderUtil;
import jodd.io.StreamUtil;

/**
 * Reads info from target class.
 * todo ovo ce da implementira target class info
 */
public class TargetClassInfoReader extends EmptyClassVisitor implements ClassInfo {

	//protected ClassInfo classInfo;

	protected final Map<String, MethodSignatureVisitor> methodSignatures;
	protected final List<ClassReader> superClassReaders;					// list of all super class readers
	protected final Set<String> topMethodSignatures;						// set of all top methods

	public TargetClassInfoReader() {
		this.methodSignatures = new HashMap<String, MethodSignatureVisitor>();
		this.superClassReaders = new ArrayList<ClassReader>();
		this.topMethodSignatures = new HashSet<String>();
	}


	// ---------------------------------------------------------------- some getters

	/**
	 * Returns method signature for some method. If signature is not found, returns <code>null</code>
	 */
	public MethodSignatureVisitor lookupMethodSignatureVisitor(int access, String name, String desc, String className) {
		String key = ProxettaAsmUtil.createMethodSignaturesKey(access, name, desc, className);
		MethodSignatureVisitor msv = methodSignatures.get(key);
		return msv == null ? null : msv;
	}

	public boolean isTopLevelMethod(MethodSignatureVisitor msgin) {
		return topMethodSignatures.contains(msgin.getSignature());
	}

	// ---------------------------------------------------------------- information

	protected String targetPackage;
	protected String targetClassname;
	protected String superName;
	protected String thisReference;
	protected String nextSupername;

	// ---------------------------------------------------------------- class interface

	public String getPackage() {
		return targetPackage;
	}

	public String getClassname() {
		return targetClassname;
	}

	public String getSuperName() {
		return superName;
	}

	public String getReference() {
		return thisReference;
	}

	// todo dodaj listu svih supera.

	// ---------------------------------------------------------------- visits


	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		int lastSlash = name.lastIndexOf('/');
		this.thisReference = name;
		this.superName = superName;
		this.nextSupername = superName;
		this.targetPackage = name.substring(0, lastSlash).replace('/', '.');
		this.targetClassname = name.substring(lastSlash + 1);

	}

	/**
	 * Stores method signature for target method.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodSignatureVisitor msign = createMethodSignature(access, name, desc, thisReference);
		String key = ProxettaAsmUtil.createMethodSignaturesKey(access, name, desc, thisReference);
		methodSignatures.put(key, msign);
		topMethodSignatures.add(msign.getSignature());
		return null;// todo return visitor to read annotation data for method
	}

	/**
	 * Stores signatures for all super public methods not already overriden by target class.
	 * All this methods will be accepted for proxyfication.
	 */
	@Override
	public void visitEnd() {
		// check all public super methods that are not overriden in superclass
		while (nextSupername != null) {
			InputStream inputStream = null;
			ClassReader cr = null;
			try {
				inputStream = ClassLoaderUtil.getClassAsStream(nextSupername);
				cr = new ClassReader(inputStream);
			} catch (IOException ioex) {
				throw new ProxettaException("Unable to inspect super class: " + nextSupername, ioex);
			} finally {
				StreamUtil.close(inputStream);
			}


			superClassReaders.add(cr);	// remember the super class reader
			cr.accept(new EmptyClassVisitor() {

				String declaredClassName;

				@Override
				public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
					nextSupername = superName;
					declaredClassName = name;
				}

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					if (name.equals(INIT) || name.equals(CLINIT)) {
						return null;
					}
					MethodSignatureVisitor msign = createMethodSignature(access, name, desc, thisReference);
					int acc = msign.getAccessFlags();
					if ((acc & MethodInfo.ACC_PUBLIC) == 0) {   // skip non-public
						return null;
					}
					if ((acc & MethodInfo.ACC_FINAL) != 0) {    // skip finals
						return null;
					}
					if (isTopLevelMethod(msign)) {				// skip overriden
						return null;
					}
					msign.setDeclaredClassName(declaredClassName);
					String key = ProxettaAsmUtil.createMethodSignaturesKey(access, name, desc, thisReference);
					methodSignatures.put(key, msign);
					return null;
				}
			}, 0);
		}
	}


	/**
	 * Creates method signature from method name.
	 */
	protected MethodSignatureVisitor createMethodSignature(int access, String methodName, String description, String classname) {
		MethodSignatureVisitor v = new MethodSignatureVisitor(methodName, access, classname, description, this);
		new SignatureReader(description).accept(v);
		return v;
	}

}

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
import jodd.asm5.MethodVisitor;
import jodd.asm5.ClassReader;
import jodd.asm5.AnnotationVisitor;
import jodd.asm5.signature.SignatureReader;

import java.util.Collections;
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

import jodd.proxetta.GenericsReader;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ClassInfo;
import jodd.proxetta.AnnotationInfo;
import jodd.util.ClassLoaderUtil;
import jodd.io.StreamUtil;
import jodd.asm.EmptyClassVisitor;
import jodd.asm.EmptyMethodVisitor;
import jodd.util.StringPool;

/**
 * Reads info from target class.
 */
@SuppressWarnings({"AnonymousClassVariableHidesContainingMethodVariable"})
public class TargetClassInfoReader extends EmptyClassVisitor implements ClassInfo {

	//protected ClassInfo classInfo;

	protected final Map<String, MethodSignatureVisitor> methodSignatures;
	protected final List<ClassReader> superClassReaders;					// list of all super class readers
	protected final Set<String> allMethodSignatures;
	protected final ClassLoader classLoader;

	public TargetClassInfoReader(ClassLoader classLoader) {
		this.methodSignatures = new HashMap<>();
		this.superClassReaders = new ArrayList<>();
		this.allMethodSignatures = new HashSet<>();
		this.classLoader = classLoader;
	}


	// ---------------------------------------------------------------- some getters

	/**
	 * Returns method signature for some method. If signature is not found, returns <code>null</code>.
	 * Founded signatures means that those method can be proxyfied.
	 */
	public MethodSignatureVisitor lookupMethodSignatureVisitor(int access, String name, String desc, String className) {
		String key = ProxettaAsmUtil.createMethodSignaturesKey(access, name, desc, className);
		return methodSignatures.get(key);
	}

	// ---------------------------------------------------------------- information

	protected String targetPackage;
	protected String targetClassname;
	protected String superName;
	protected String thisReference;
	protected String nextSupername;
	protected String[] superClasses;
	protected AnnotationInfo[] annotations;
	protected List<AnnotationInfo> classAnnotations;
	protected boolean isTargetInterface;
	protected Set<String> nextInterfaces;
	protected Map<String, String> generics;

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

	public String[] getSuperClasses() {
		return superClasses;
	}

	public AnnotationInfo[] getAnnotations() {
		return annotations;
	}

	public Map<String, String> getGenerics() {
		return generics;
	}

	// ---------------------------------------------------------------- visits


	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		int lastSlash = name.lastIndexOf('/');
		this.thisReference = name;
		this.superName = superName;
		this.nextSupername = superName;
		this.targetPackage = lastSlash == -1 ? StringPool.EMPTY : name.substring(0, lastSlash).replace('/', '.');
		this.targetClassname = name.substring(lastSlash + 1);

		this.isTargetInterface = (access & AsmUtil.ACC_INTERFACE) != 0;
		if (this.isTargetInterface) {
			nextInterfaces = new HashSet<>();
			if (interfaces != null) {
				Collections.addAll(nextInterfaces, interfaces);
			}
		}
		generics = new GenericsReader().parseSignatureForGenerics(signature, isTargetInterface);
	}


	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		AnnotationReader ar = new AnnotationReader(desc, visible);
		if (classAnnotations == null) {
			classAnnotations = new ArrayList<>();
		}
		classAnnotations.add(ar);
		return ar;
	}

	/**
	 * Stores method signature for target method.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if ((access & AsmUtil.ACC_FINAL) != 0) {
			return null;	// skip finals
		}
		MethodSignatureVisitor msign = createMethodSignature(access, name, desc, signature, exceptions, thisReference);
		String key = ProxettaAsmUtil.createMethodSignaturesKey(access, name, desc, thisReference);
		methodSignatures.put(key, msign);
		allMethodSignatures.add(msign.getCleanSignature());
		return new MethodAnnotationReader(msign);
	}

	/**
	 * Stores signatures for all super public methods not already overridden by target class.
	 * All this methods will be accepted for proxyfication.
	 */
	@Override
	public void visitEnd() {

		// prepare class annotations
		if (classAnnotations != null) {
			annotations = classAnnotations.toArray(new AnnotationInfo[classAnnotations.size()]);
			classAnnotations = null;
		}

		List<String> superList = new ArrayList<>();

		Set<String> allInterfaces = new HashSet<>();

		if (nextInterfaces != null) {
			allInterfaces.addAll(nextInterfaces);
		}

		// check all public super methods that are not overridden in superclass
		while (nextSupername != null) {
			InputStream inputStream = null;
			ClassReader cr;

			try {
				inputStream = ClassLoaderUtil.getClassAsStream(nextSupername, classLoader);
				cr = new ClassReader(inputStream);
			} catch (IOException ioex) {
				throw new ProxettaException("Unable to inspect super class: " + nextSupername, ioex);
			} finally {
				StreamUtil.close(inputStream);
			}

			superList.add(nextSupername);
			superClassReaders.add(cr);	// remember the super class reader
			cr.accept(new SuperClassVisitor(), 0);

			if (cr.getInterfaces() != null) {
				Collections.addAll(allInterfaces, cr.getInterfaces());
			}
		}
		superClasses = superList.toArray(new String[superList.size()]);

		// check all interface methods that are not overridden in super-interface

		Set<String> todoInterfaces = new HashSet<>(allInterfaces);
		Set<String> newCollectedInterfaces = new HashSet<>();

		while (true) {

			for (String next : todoInterfaces) {
				InputStream inputStream = null;
				ClassReader cr;
				try {
					inputStream = ClassLoaderUtil.getClassAsStream(next, classLoader);
					cr = new ClassReader(inputStream);
				}
				catch (IOException ioex) {
					throw new ProxettaException("Unable to inspect super interface: " + next, ioex);
				}
				finally {
					StreamUtil.close(inputStream);
				}
				superClassReaders.add(cr);				// remember the super class reader
				cr.accept(new SuperClassVisitor(), 0);

				if (cr.getInterfaces() != null) {
					for (String newInterface : cr.getInterfaces()) {
						if (!allInterfaces.contains(newInterface) && !todoInterfaces.contains(newInterface)) {
							// new interface found
							newCollectedInterfaces.add(newInterface);
						}
					}
				}
			}

			// perform collection
			allInterfaces.addAll(todoInterfaces);

			if (newCollectedInterfaces.isEmpty()) {
				// no new interface found
				break;
			}
			todoInterfaces.clear();
			todoInterfaces.addAll(newCollectedInterfaces);

			newCollectedInterfaces.clear();
		}
	}


	/**
	 * Creates method signature from method name.
	 */
	protected MethodSignatureVisitor createMethodSignature(int access, String methodName, String description, String signature, String[] exceptions, String classname) {
		MethodSignatureVisitor v = new MethodSignatureVisitor(methodName, access, classname, description, exceptions, signature, this);
		new SignatureReader(signature != null ? signature : description).accept(v);
		return v;
	}


	// ---------------------------------------------------------------- util class

	/**
	 * Reads method annotations and stores to method info.
	 */
	static class MethodAnnotationReader extends EmptyMethodVisitor {

		final List<AnnotationInfo> methodAnns = new ArrayList<>();
		final List<AnnotationInfo>[] methodParamsAnns;

		final MethodSignatureVisitor msign;

		MethodAnnotationReader(MethodSignatureVisitor msign) {
			this.msign = msign;
			this.methodParamsAnns = new ArrayList[msign.getAllArgumentsSize()];
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			AnnotationReader ar = new AnnotationReader(desc, visible);
			methodAnns.add(ar);
			return ar;
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			AnnotationReader ar = new AnnotationReader(desc, visible);
			if (methodParamsAnns[parameter] == null) {
				methodParamsAnns[parameter] = new ArrayList<>();
			}

			methodParamsAnns[parameter].add(ar);

			return ar;
		}

		@Override
		public void visitEnd() {
			if (!methodAnns.isEmpty()) {
				// method annotations
				msign.annotations = methodAnns.toArray(new AnnotationInfo[methodAnns.size()]);
			}

			// arguments annotations

			for (int i = 0; i < methodParamsAnns.length; i++) {
				List<AnnotationInfo> methodParamsAnn = methodParamsAnns[i];

				if (methodParamsAnn != null) {
					msign.getArgument(i + 1).annotations = methodParamsAnn.toArray(new AnnotationInfo[methodParamsAnn.size()]);
				}
			}
		}
	}

	// ---------------------------------------------------------------- super class visitor

	private class SuperClassVisitor extends EmptyClassVisitor {

		String declaredClassName;

		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			nextSupername = superName;
			declaredClassName = name;

			// append inner interfaces
			if (nextInterfaces != null) {
				if (interfaces != null) {
					Collections.addAll(nextInterfaces, interfaces);
				}
			}

		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if (name.equals(INIT) || name.equals(CLINIT)) {
				return null;
			}

			if ((access & AsmUtil.ACC_PUBLIC) == 0) {   		// skip non-public
				return null;
			}
			if ((access & AsmUtil.ACC_FINAL) != 0) {		// skip finals
				return null;
			}

			MethodSignatureVisitor msign = createMethodSignature(access, name, desc, signature, exceptions, thisReference);
			if (allMethodSignatures.contains(msign.getCleanSignature())) {		// skip overridden method by some in above classes
				return null;
			}

			msign.setDeclaredClassName(declaredClassName);		// indicates it is not a top level class
			String key = ProxettaAsmUtil.createMethodSignaturesKey(access, name, desc, declaredClassName);
			methodSignatures.put(key, msign);
			allMethodSignatures.add(msign.getCleanSignature());
			return new MethodAnnotationReader(msign);
		}
	}

	// ---------------------------------------------------------------- toString


	@Override
	public String toString() {
		return "target: " + this.targetPackage + '.' + this.targetClassname;
	}
}

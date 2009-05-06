// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;
import static jodd.proxetta.asm.ProxettaAsmUtil.*;
import static jodd.proxetta.asm.ProxettaNaming.*;
import jodd.proxetta.ProxyTarget;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.MethodSignature;
import jodd.proxetta.AnnotationData;
import jodd.proxetta.ProxettaException;
import jodd.util.ClassLoaderUtil;
import jodd.io.StreamUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;
import java.io.IOException;

/**
 * Creates the proxy subclass using ASM library.
 */
public class ProxettaCreator extends EmptyVisitor {

	static final String TARGET_CLASS_NAME = ProxyTarget.class.getSimpleName();        // extract ProxyTarget name for recognition
	static final String CLINIT = "<clinit>";
	static final String DESC_VOID = "()V";
	static final String INIT = "<init>";

	protected final ClassVisitor dest;        // destination class writer
	protected ProxyAspectData[] proxyAspects;

	protected String targetClassname;
	protected String nextSupername;
	protected int hierarchyLevel;
	protected String targetPackage;
	protected String thisReference;
	protected String superReference;
	protected boolean proxyApplied;

	// ---------------------------------------------------------------- ctor

	protected ClassWriter cw;
	protected final ProxyAspect[] aspects;
	protected final Set<String> topMethodSignatures;      // set of all top methods

//	protected ProxettaCreator(ClassVisitor cv, ProxyAspect... aspects) {
//		this.dest = cv;
//		this.aspects = aspects;
//		topMethodSignatures = null;
//	}

	public ProxettaCreator(ProxyAspect... aspects) {
		this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		this.dest = cw;
		this.aspects = aspects;
		topMethodSignatures = new HashSet<String>();
	}

	// ---------------------------------------------------------------- work

	private ProxettaCreator accept(ClassReader cr) {
		cr.accept(this, 0);
		return this;
	}

	public ProxettaCreator accept(InputStream in) {
		ClassReader cr;
		try {
			cr = new ClassReader(in);
		} catch (IOException ioex) {
			throw new ProxettaException("Error reading class input stream.", ioex);
		}
		return accept(cr);
	}

	public ProxettaCreator accept(String targetName) {
		InputStream inputStream = null;
		try {
			inputStream = ClassLoaderUtil.getClassAsStream(targetName);
			return accept(inputStream);
		} catch (IOException ioex) {
			throw new ProxettaException("Unable to open stream for class name: " + targetName, ioex);
		} finally {
			StreamUtil.close(inputStream);
		}
	}

	public ProxettaCreator accept(Class target) {
		InputStream inputStream = null;
		try {
			inputStream = ClassLoaderUtil.getClassAsStream(target);
			return accept(inputStream);
		} catch (IOException ioex) {
			throw new ProxettaException("Unable to open stream for: " + target.getName(), ioex);
		} finally {
			StreamUtil.close(inputStream);
		}
	}


	// ---------------------------------------------------------------- after

	/**
	 * Returns raw bytecode.
	 */
	public byte[] toByteArray() {
		return cw.toByteArray();
	}

	/**
	 * Returns <code>true</code> if at least one method was wrapped.
	 */
	public boolean isProxyApplied() {
		return proxyApplied;
	}

	/**
	 * Returns proxy class name.
	 */
	public String getProxyClassName() {
		return this.thisReference.replace('/', '.');
	}


	// ---------------------------------------------------------------- variable name

	protected boolean variableClassName;
	protected static int suffix = 1;        // number appended to make variable class name, incremented on each use

	/**
	 * If <code>true</code> class name will vary for each creation. This prevents
	 * <code>java.lang.LinkageError: duplicate class definition.</code>
	 */
	public void setVariableClassName(boolean varname) {
		this.variableClassName = varname;
	}


	// ---------------------------------------------------------------- visiting target class

	protected DestinationData dd;

	/**
	 * Invoked after destination class is ready for manipulation.
	 */
	protected void destinationReady() {
		dd = new DestinationData(dest);
		this.proxyAspects = new ProxyAspectData[aspects.length];
		for (int i = 0; i < aspects.length; i++) {
			proxyAspects[i] = new ProxyAspectData(dd, thisReference, aspects[i], i);
		}
	}



	/**
	 * Creates destination subclass for current target class.
	 * Upon creation, {@link #destinationReady()} is invoked. 
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
		int lastSlash = name.lastIndexOf('/');
		this.targetPackage = name.substring(0, lastSlash).replace('/', '.');
		this.targetClassname = name.substring(lastSlash + 1);
		this.nextSupername = supername;
		this.hierarchyLevel = 0;
		supername = name;
		name += PROXY_CLASS_NAME_SUFFIX;
		if (variableClassName == true) {
			name += (suffix++);
		}
		thisReference = name;
		superReference = supername;
		dest.visit(version, access, name, signature, supername, null);
		destinationReady();
	}

	/**
	 * Creates methods and constructors.
	 * <p>
	 * Destination proxy will have all constructors as a target class, using {@link jodd.proxetta.asm.ProxettaCreator.ConstructorBuilder}.
	 * <p>
	 * For a method, {@link jodd.proxetta.asm.ProxettaCreator.MethodBuilder} first determines if method matches
	 * pointcut. If so, method will be proxified.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodSignatureVisitor msign = createMethodSignature(access, name, desc, superReference);

		// constructors [A1]
		if (name.equals(INIT) == true) {
			MethodVisitor mv = dest.visitMethod(access, name, desc, msign.getSignature(), null);
			return new ConstructorBuilder(mv, msign);
		}
		topMethodSignatures.add(msign.getSignature());
		return new MethodBuilder(msign);
	}


	/**
	 * Copies all destination type annotations to the target.
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String signature, boolean b) {
		dest.visitAnnotation(signature, b); // [A3]
		return null;
	}

	/**
	 * Finalize destination proxy class.
	 */
	@Override
	public void visitEnd() {

		// creates static initialization block that simply calls all advice static init methods in correct order
		if (dd.adviceClinits != null) {
			MethodVisitor mv = dest.visitMethod(MethodSignature.ACC_STATIC, CLINIT, DESC_VOID, null, null);
			mv.visitCode();
			for (String name : dd.adviceClinits) {
				mv.visitMethodInsn(INVOKESTATIC, thisReference, name, DESC_VOID);
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}

		// creates init method that simply calls all advice constructor methods in correct order
		// this created init method is called from each destination's constructor
		MethodVisitor mv = dest.visitMethod(MethodSignature.ACC_PRIVATE | MethodSignature.ACC_FINAL, INIT_METHOD_NAME, DESC_VOID, null, null);
		mv.visitCode();
		if (dd.adviceInits != null) {
			for (String name : dd.adviceInits) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, thisReference, name, DESC_VOID);
			}
		}
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// check all public super methods that are not overriden in top level class
		while (nextSupername != null) {
			InputStream inputStream = null;
			try {
				inputStream = ClassLoaderUtil.getClassAsStream(nextSupername);
				ClassReader cr = new ClassReader(inputStream);
				cr.accept(new EmptyVisitor() {

					String declaredClassName;

					@Override
					public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
	                    nextSupername = supername;
						hierarchyLevel++;
						declaredClassName = name;
					}

					@Override
					public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
						if (name.equals(INIT) || name.equals(CLINIT)) {
							return null;
						}
						MethodSignatureVisitor msign = createMethodSignature(access, name, desc, superReference);
						int acc = msign.getAccessFlags();
						if ((acc & MethodSignature.ACC_PUBLIC) == 0) {   // skip non-public
						    return null;
						}
						if ((acc & MethodSignature.ACC_FINAL) != 0) {    // skip finals
							return null;
						}
						if (topMethodSignatures.contains(msign.getSignature())) {
							return null;
						}
						msign.setDeclaredClassName(declaredClassName);
						return new MethodBuilder(msign);
					}
				}, 0);
			} catch (IOException ioex) {
				throw new ProxettaException("Unable to inspect super class: " + nextSupername, ioex);
			} finally {
				StreamUtil.close(inputStream);
			}
		}
		dest.visitEnd();
	}


	// ---------------------------------------------------------------- ctor builder

	/**
	 * Builds destination constructors that delegates call to super constructor (of a target)
	 * and invokes a init method that calls constructor codes of all advices in correct order.
	 */
	class ConstructorBuilder extends EmptyVisitor {

		final MethodSignatureVisitor msign;
		final MethodVisitor mv;

		ConstructorBuilder(MethodVisitor mv, MethodSignatureVisitor msign) {
			this.mv = mv;
			this.msign = msign;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String sign, boolean b) {
			mv.visitAnnotation(sign, b);
			return null;
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(int i, String string, boolean b) {
			mv.visitParameterAnnotation(i, string, b);
			return null;
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			mv.visitAnnotationDefault();
			return null;
		}

		@Override
		public void visitEnd() {
			mv.visitCode();

			// call super ctor
			loadMethodArguments(mv, msign);
			mv.visitMethodInsn(INVOKESPECIAL, superReference, msign.getMethodName(), msign.getDescription());

			// invoke advice ctors
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, thisReference, INIT_METHOD_NAME, DESC_VOID);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}


	// ---------------------------------------------------------------- method builder

	/**
	 * Proxy method builder.
	 */
	class MethodBuilder extends EmptyVisitor {

		final MethodSignatureVisitor msign;

		MethodBuilder(MethodSignatureVisitor msign) {
			this.msign = msign;
		}

		/**
		 * Stores target method annotation data in method signature.
		 */
		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			AnnotationData ad = new AnnotationData(desc, visible);
			msign.annotations.add(ad);
			return new AnnotationReader(ad);
		}

		/**
		 * Finally, builds proxy methods if applied to current method.
		 */
		@Override
		public void visitEnd() {
			// match method to all proxy aspects

			List<ProxyAspectData> aspectList = null;
			for (ProxyAspectData aspectData : proxyAspects) {
				if (aspectData.apply(msign) == true) {
					if (aspectList == null) {
						aspectList = new ArrayList<ProxyAspectData>(proxyAspects.length);
					}
					aspectList.add(aspectData);
				}
			}
			if (aspectList == null) {
				return; // no pointcut on this method, return
			}

			// check invalid access flags
			int access = msign.getAccessFlags();
			if ((access & MethodSignature.ACC_FINAL) != 0) {   // detect final
			    throw new ProxettaException("Unable to create proxy for final method: " + msign +". Remove final modifier or change the pointcut definition.");
			}

			// create proxy methods
			TargetMethodData tmd = new TargetMethodData(msign, aspectList);
			createFirstChainDelegate(tmd);
			for (int p = 0; p < tmd.proxyData.length; p++) {
				tmd.selectCurrentProxy(p);
				createProxyMethod(tmd);
			}
			proxyApplied = true;
		}

		/**
		 * Creates the very first method in calling chain that simply delegates invocation to the first proxy method.
		 * This method mirrors the target method.
		 */
		protected void createFirstChainDelegate(TargetMethodData td) {
			int access = td.msign.getAccessFlags();
			access = ProxettaAsmUtil.makeNonNative(access);
			MethodVisitor mv = dest.visitMethod(access, td.msign.getMethodName(), td.msign.getDescription(), td.msign.getSignature(), null);
			mv.visitCode();
			loadMethodArguments(mv, td.msign);
			mv.visitMethodInsn(INVOKESPECIAL, thisReference, td.firstMethodName(), td.msign.getDescription());
			visitReturn(mv, td.msign, false);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			writeAnnotations(mv, td.msign.getAnnotations());      // [A4]
		}


		/**
		 * Write all proxy method annotations.
		 */
		void writeAnnotations(MethodVisitor dest, List<AnnotationData> annotations) {
			for (AnnotationData ann : annotations) {
				AnnotationVisitor av = dest.visitAnnotation(ann.signature, ann.isVisible);
				for (String name : ann.values.keySet()) {
					av.visit(name, ann.values.get(name));
				}
			}
		}

		/**
		 * Creates proxy methods over target method, For each matched proxy, new proxy method is created
		 * by taking advice bytecode and replaces usages of {@link jodd.proxetta.ProxyTarget}.
		 * <p>
		 * Invocation chain example: name -> name$p0 -> name$p1 -> name$p4 -> super
		 */
		public void createProxyMethod(final TargetMethodData td) {
			final ProxyAspectData aspectData = td.getProxyData();

			int access = td.msign.getAccessFlags();
			access = ProxettaAsmUtil.makeNonNative(access);
			access = ProxettaAsmUtil.makePrivateFinalAccess(access);
			final MethodVisitor mv = dest.visitMethod(access, td.methodName(), td.msign.getDescription(), null, null);
			mv.visitCode();

			//*** VISIT ADVICE - called for each aspect and each method
			aspectData.getAdviceClassReader().accept(new EmptyVisitor() {

				@Override
				public MethodVisitor visitMethod(int methodAccess, String name, String desc, String signature, String[] exceptions) {

					if (name.equals(EXECUTE_METHOD_NAME) == false) {
						return null;
					}

					return new IntArgHistoryMethodAdapter(mv) {

						@Override
						public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
							if (owner.equals(aspectData.adviceReference)) {
								owner = thisReference;              // [F5]
								fieldName = adviceFieldName(fieldName, aspectData.aspectIndex);
							}
							super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
						}


						@Override
						public void visitVarInsn(int opcode, int var) {
							var += (var == 0 ? 0 : td.msign.getAllArgumentsSize());
							super.visitVarInsn(opcode, var);   // [F1]
						}

						@Override
						public void visitIincInsn(int var, int increment) {
							var += (var == 0 ? 0 : td.msign.getAllArgumentsSize());
							super.visitIincInsn(var, increment);  // [F1]
						}

						@Override
						public void visitInsn(int opcode) {
							if (opcode == ARETURN) {
								visitReturn(mv, td.msign, true);
								return;
							}
							if (traceNext == true) {
								if ((opcode == POP) || (opcode == POP2)) {      // [F3] - invoke invoked without assignment
									return;
								}
							}
							super.visitInsn(opcode);
						}

						@Override
						public void visitMethodInsn(int opcode, String owner, String methodName, String methodDesc) {
							if ((opcode == INVOKEVIRTUAL) || (opcode == INVOKEINTERFACE)) {
								if (owner.equals(aspectData.adviceReference)) {
									owner = thisReference;
									methodName = adviceMethodName(methodName, aspectData.aspectIndex);
								}
							} else

							if (opcode == INVOKESTATIC) {
								if (owner.equals(aspectData.adviceReference)) {
									owner = thisReference;
									methodName = adviceMethodName(methodName, aspectData.aspectIndex);
								} else

								if (owner.endsWith('/' + TARGET_CLASS_NAME) == true) {

									if (isInvokeMethod(methodName, methodDesc)) {           // [R7]
										if (td.isLastMethodInChain()) {                            // last proxy method just calls super target method
											loadMethodArguments(mv, td.msign);
											mv.visitMethodInsn(INVOKESPECIAL, superReference, td.msign.getMethodName(), td.msign.getDescription());

											prepareReturnValue(mv, td.msign, aspectData.maxLocalVarOffset);     // [F4]
											traceNext = true;
										} else {                                                    // calls next proxy method
											loadMethodArguments(mv, td.msign);
											mv.visitMethodInsn(INVOKESPECIAL, thisReference, td.nextMethodName(), td.msign.getDescription());
											visitReturn(mv, td.msign, false);
										}
										return;
									} else

									if (isArgumentsCountMethod(methodName, methodDesc)) {        // [R2]
										int argsCount = td.msign.getArgumentsCount();
										pushInt(mv, argsCount);
										return;
									} else

									if (isArgumentTypeMethod(methodName, methodDesc)) {      // [R3]
										int argIndex = this.getArgumentIndex();
										checkArgumentIndex(td.msign, argIndex, aspectData.advice);
										mv.visitInsn(POP);
										loadMethodArgumentClass(mv, td.msign, argIndex);
										return;
									} else

									if (isArgumentMethod(methodName, methodDesc)) {           // [R4]
										int argIndex = this.getArgumentIndex();
										checkArgumentIndex(td.msign, argIndex, aspectData.advice);
										mv.visitInsn(POP);
										loadMethodArgumentAsObject(mv, td.msign, argIndex);
										return;
									} else

									if (isSetArgumentMethod(methodName, methodDesc)) {           // [R5]
										int argIndex = this.getArgumentIndex();
										checkArgumentIndex(td.msign, argIndex, aspectData.advice);
										mv.visitInsn(POP);
										storeMethodArgumentFromObject(mv, td.msign, argIndex);
										return;
									} else

									if (isCreateArgumentsArrayMethod(methodName, methodDesc)) {  // [R6]
										int argsCount = td.msign.getArgumentsCount();
										pushInt(mv, argsCount);
										mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
										for (int i = 0; i < argsCount; i++) {
											mv.visitInsn(DUP);
											pushInt(mv, i);
											loadMethodArgumentAsObject(mv, td.msign, i + 1);
											mv.visitInsn(AASTORE);
										}
										return;
									} else

									if (isCreateArgumentsClassArrayMethod(methodName, methodDesc)) {     // [R11]
										int argsCount = td.msign.getArgumentsCount();
										pushInt(mv, argsCount);
										mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
										for (int i = 0; i < argsCount; i++) {
											mv.visitInsn(DUP);
											pushInt(mv, i);
											loadMethodArgumentClass(mv, td.msign, i + 1);
											mv.visitInsn(AASTORE);
										}
										return;
									} else

									if (isTargetMethod(methodName, methodDesc)) {       // [R9.1]
										mv.visitVarInsn(ALOAD, 0);
										return;
									} else

									if (isTargetClassMethod(methodName, methodDesc)) {       // [R9]
										mv.visitLdcInsn(Type.getType('L' + superReference + ';'));
										return;
									} else

									if (isTargetMethodNameMethod(methodName, methodDesc)) {  // [R10]
										mv.visitLdcInsn(td.msign.getMethodName());
										return;
									}

									if (isReturnTypeMethod(methodName, methodDesc)) {        // [R11]
										loadMethodReturnClass(mv, td.msign);
										return;
									}
								}
							}
							super.visitMethodInsn(opcode, owner, methodName, methodDesc);
						}

					};
				}

			}, 0);
		}

	}
}
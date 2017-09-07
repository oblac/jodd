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
import jodd.asm.TraceSignatureVisitor;
import jodd.asm5.Opcodes;
import jodd.asm5.signature.SignatureVisitor;
import jodd.proxetta.*;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.collection.IntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jodd.proxetta.asm.AnnotationReader.NO_ANNOTATIONS;

/**
 * Resolves method signature and holds all information. Uses {@link jodd.asm.TraceSignatureVisitor} from ASM library.
 * <pre>
 * MethodSignature = ( visitFormalTypeParameter visitClassBound? visitInterfaceBound* )* ( visitParameterType* visitReturnType visitExceptionType* )
 * </pre>
 */
public class MethodSignatureVisitor extends TraceSignatureVisitor implements MethodInfo {

	protected final String classname;
	protected final String methodName;
	protected final String[] exceptionsArray;
	protected final boolean isStatic;
	protected final ClassInfo targetClassInfo;
	protected final IntArrayList argumentsOffset;
	protected final List<TypeInfoImpl> arguments;
	protected final int access;
	protected final String description;

	protected TypeInfo returnType;
	protected String signature;
	protected int argumentsCount;
	protected int argumentsWords;
	protected String asmMethodSignature;
	protected AnnotationInfo[] annotations;
	protected String declaredClassName;
	protected Map<String, String> generics;


	// ---------------------------------------------------------------- ctors

	public MethodSignatureVisitor(String methodName, final int access, String classname, String description, String[] exceptions, String signature, ClassInfo targetClassInfo) {
		super(new StringBuilder());
		this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		this.isStatic = (access & Opcodes.ACC_STATIC) != 0;
		this.methodName = methodName;
		this.access = access;
		this.classname = classname;
		this.description = description;
		this.targetClassInfo = targetClassInfo;
		this.asmMethodSignature = signature;
		this.generics = new GenericsReader().parseSignatureForGenerics(signature, isInterface);
		this.exceptionsArray = exceptions;

		this.arguments = new ArrayList<>();
		this.arguments.add(new TypeInfoImpl('L', null, null, null));

		this.argumentsOffset = new IntArrayList();
		this.argumentsOffset.add(0);

		this.annotations = NO_ANNOTATIONS;
	}

	// ---------------------------------------------------------------- method-info signature

	@Override
	public String getSignature() {
		if (signature == null) {
			String decl = getDeclaration();

			int ndx = decl.indexOf(')');
			ndx++;
			String retType = decl.substring(ndx);

			StringBuilder methodDeclaration = new StringBuilder(50);
			methodDeclaration.append(retType).append(' ').append(methodName).append(decl.substring(0, ndx));

			String exceptionsAsString = getExceptionsAsString();
			if (exceptionsAsString != null) {
				methodDeclaration.append(" throws ").append(exceptionsAsString);
			}

			signature = methodDeclaration.toString();
		}
		return signature;
	}

	@Override
	public String getCleanSignature() {
		return methodName + '#' + getDescription();
	}

	public String getAsmMethodSignature() {
		return asmMethodSignature;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	@Override
	public int getArgumentsCount() {
		return argumentsCount;
	}

	@Override
	public TypeInfoImpl getArgument(int ndx) {
		return arguments.get(ndx);
	}

	@Override
	public int getArgumentOffset(int index) {
		return argumentsOffset.get(index);
	}

	@Override
	public int getAllArgumentsSize() {
		return argumentsWords;
	}

	@Override
	public TypeInfo getReturnType() {
		return returnType;
	}

	@Override
	public int getAccessFlags() {
		return access;
	}

	@Override
	public String getClassname() {
		return classname;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public AnnotationInfo[] getAnnotations() {
		return annotations;
	}

	@Override
	public String getDeclaredClassName() {
		if (declaredClassName == null) {
			return classname;
		}
		return declaredClassName;
	}

	public void setDeclaredClassName(String declaredClassName) {
		this.declaredClassName = declaredClassName;
	}

	@Override
	public boolean isTopLevelMethod() {
		return declaredClassName == null;
	}

	@Override
	public ClassInfo getClassInfo() {
		return targetClassInfo;
	}

	@Override
	public String[] getExceptions() {
		return exceptionsArray;
	}

	// ---------------------------------------------------------------- type

	private boolean visitingArgument;
	private boolean visitingReturnType;
	private boolean visitingArray;
	private int declarationTypeOffset;

	@Override
	public SignatureVisitor visitParameterType() {
		super.visitParameterType();

		visitingArgument = true;

		return this;
	}

	@Override
	public SignatureVisitor visitReturnType() {
		super.visitReturnType();

		visitingReturnType = true;

		return this;
	}

	@Override
	public SignatureVisitor visitArrayType() {
		visitingArray = true;
		return super.visitArrayType();
	}

	@Override
	public void visitBaseType(final char descriptor) {
		if (isTopLevelType()) {
			// mark type start
			declarationTypeOffset = declaration.length();
		}

		super.visitBaseType(descriptor);
	}

	@Override
	public void visitClassType(String name) {
		if (isTopLevelType()) {
			// mark type start
			declarationTypeOffset = declaration.length();
		}

		super.visitClassType(name);
	}

	@Override
	protected void startType() {
		super.startType();

		if (isTopLevelType()) {
			// mark type start
			declarationTypeOffset = declaration.length();
		}
	}

	@Override
	protected void endType() {
		super.endType();

		String type = declaration.subSequence(declarationTypeOffset, declaration.length()).toString();

		maybeUseType(type);
	}

	private void maybeUseType(String typeName) {
		if (!isTopLevelType()) {
			return;
		}

		char type;
		String bytecodeName;

		if (visitingArray) {
			type = '[';

			int arrayCount = StringUtil.count(typeName, '[');
			String arrayDepth = StringUtil.repeat('[', arrayCount);

			int ndx = typeName.indexOf('[');
			bytecodeName = typeName.substring(0, ndx);

			char arrayType = AsmUtil.typeNameToOpcode(bytecodeName);
			if (arrayType != 'L') {
				bytecodeName = String.valueOf(arrayType);
			}
			else {
				bytecodeName = resolveBytecodeName(bytecodeName);
			}

			bytecodeName = arrayDepth  + bytecodeName;
		}
		else {
			type = AsmUtil.typeNameToOpcode(typeName);

			if (type != 'L') {
				bytecodeName = String.valueOf(type);
			}
			else {
				bytecodeName = resolveBytecodeName(typeName);
			}
		}

		final TypeInfoImpl typeInfo = new TypeInfoImpl(
			type,
			typeName,
			bytecodeName,
			resolveRawTypeName(bytecodeName));

		if (visitingArgument) {
			if (type == 'V') {
				throw new ProxettaException("Method argument can't be void");
			}

			arguments.add(typeInfo);

			argumentsCount++;
			argumentsOffset.add(argumentsWords + 1);

			if ((type == 'D') || (type == 'J')) {
				argumentsWords += 2;
			} else {
				argumentsWords++;
			}
		}
		else if (visitingReturnType) {
			returnType = typeInfo;
		}

		visitingReturnType = false;
		visitingArgument = false;
		visitingArray = false;
	}

	/**
	 * Returns {@code true} if we are scanning the top-level type and not
	 * the inner ones, like generics.
	 */
	private boolean isTopLevelType() {
		return argumentStack == 0;
	}

	private String resolveBytecodeName(String typeName) {
		int ndx = typeName.indexOf('<');
		if (ndx != -1) {
			int ndx2 = typeName.indexOf('>', ndx);
			ndx2++;

			// it might be a nested generics, so skip all '>'
			while (ndx2 < typeName.length() && typeName.charAt(ndx2) == '>') {
				ndx2++;
			}

			typeName = typeName.substring(0, ndx) + typeName.substring(ndx2);
		}

		if (isGenericType(typeName)) {
			return typeName;
		}

		return 'L' + typeName.replace('.', '/') + ';';
	}

	/**
	 * Resolves raw type name using the generics information from the class
	 * or method information.
	 */
	private String resolveRawTypeName(String typeName) {
		if (typeName == null) {
			return null;
		}

		boolean isArray = typeName.startsWith(StringPool.LEFT_SQ_BRACKET);
		if (isArray) {
			typeName = typeName.substring(1);
		}

		String rawTypeName;

		if (generics.containsKey(typeName)) {
			rawTypeName = generics.get(typeName);
		}
		else {
			rawTypeName = targetClassInfo.getGenerics().getOrDefault(typeName, typeName);
		}

		if (isArray) {
			rawTypeName = '[' + rawTypeName;
		}

		return rawTypeName;
	}

	private boolean isGenericType(String typeName) {
		if (generics.containsKey(typeName)) {
			return true;
		}
		return targetClassInfo.getGenerics().containsKey(typeName);
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return getDeclaredClassName() + '#' + getMethodName() + getDescription();
	}

}

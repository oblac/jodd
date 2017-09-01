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

import jodd.asm5.signature.SignatureVisitor;
import jodd.asm5.Opcodes;
import jodd.proxetta.GenericsReader;
import jodd.util.StringPool;
import jodd.util.collection.IntArrayList;
import jodd.mutable.MutableInteger;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ClassInfo;
import jodd.proxetta.AnnotationInfo;
import jodd.asm.TraceSignatureVisitor;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Resolves method signature and holds all information. Uses {@link jodd.asm.TraceSignatureVisitor} from ASM library.
 * <pre>
 * MethodSignature = ( visitFormalTypeParameter visitClassBound? visitInterfaceBound* )* ( visitParameterType* visitReturnType visitExceptionType* )
 * </pre>
 */
public class MethodSignatureVisitor extends TraceSignatureVisitor implements MethodInfo {

	protected int access;
	protected String methodName;
	protected String signature;
	protected int argumentsCount;
	protected int argumentsWords;

	protected MutableInteger returnOpcodeType;
	protected StringBuilder returnTypeName;
	protected StringBuilder returnTypeRawName;
	protected String classname;
	protected String description;
	protected String asmMethodSignature;
	protected AnnotationInfo[] annotations;

	protected boolean visitingArgument;
	protected StringBuilder argumentsOpcodeType;
	protected IntArrayList argumentsOffset;
	protected List<String> argumentsTypeNames;
	protected List<String> argumentsTypeRawNames;
	protected AnnotationInfo[][] argumentsAnnotation;

	protected String declaredClassName;

	final protected ClassInfo targetClassInfo;
	protected boolean isStatic;
	protected Map<String, String> generics;
	protected String[] exceptionsArray;

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
	}

	private MethodSignatureVisitor(final StringBuilder declaration, MutableInteger returnOpcodeType, StringBuilder returnTypeName, StringBuilder returnTypeRawName, ClassInfo targetClassInfo, Map<String, String> generics) {
		super(declaration);
		this.targetClassInfo = targetClassInfo;
		this.returnOpcodeType = returnOpcodeType;
		this.returnTypeName = returnTypeName;
		this.returnTypeRawName = returnTypeRawName;
		this.generics = generics;
	}

	// ---------------------------------------------------------------- code

	@Override
	public SignatureVisitor visitParameterType() {
		super.visitParameterType();
		visitingArgument = true;

		if (argumentsOpcodeType == null) {
			argumentsOpcodeType = new StringBuilder();
			argumentsOffset = new IntArrayList();
			argumentsTypeNames = new ArrayList<>();
			argumentsTypeRawNames = new ArrayList<>();

			argumentsOpcodeType.append('L');
			argumentsOffset.add(0);
			argumentsTypeNames.add(null);
			argumentsTypeRawNames.add(null);
		}
		return this;
	}


	@Override
	public SignatureVisitor visitReturnType() {
		super.visitReturnType();
		returnOpcodeType = new MutableInteger();
		returnTypeName = new StringBuilder();
		returnTypeRawName = new StringBuilder();
		return new MethodSignatureVisitor(returnType, returnOpcodeType, returnTypeName, returnTypeRawName, targetClassInfo, generics);
	}

	@Override
	public void visitBaseType(final char descriptor) {
		String name = null;
		char type = descriptor;
		if (isArray()) {
			type = '[';
			name = getArrayDepthString() + descriptor;
		}
		super.visitBaseType(descriptor);
		maybeUseType(type,  name);
	}

	/**
	 * Visits a signature corresponding to a type variable.
	 */
	@Override
	public void visitTypeVariable(final String name) {
		maybeUseType('L', name);
		super.visitTypeVariable(name);
	}

	/**
	 * Visits a signature corresponding to an array type.
	 */
	@Override
	public SignatureVisitor visitArrayType() {
		super.visitArrayType();
		return this;
	}

	/**
	 * Starts the visit of a signature corresponding to a class or interface type.
	 */
	@Override
	public void visitClassType(final String name) {
		maybeUseType('L', 'L' + name + ';');
		super.visitClassType(name);
	}

	// ---------------------------------------------------------------- method signature

	/**
	 * Returns java-alike method signature.
	 * @see #createSignature()
	 */
	public String getSignature() {
		if (signature == null) {
			signature = createSignature();
		}
		return signature;
	}

	public String getCleanSignature() {
		return methodName + '#' + getDescription();
	}

	/**
	 * Builds java-alike method signature.
	 */
	private String createSignature() {
		StringBuilder methodDeclaration = new StringBuilder(30);
		methodDeclaration.append(getReturnType()).append(' ').append(methodName).append(getDeclaration());
		String genericExceptions = getExceptionsAsString();
		if (genericExceptions != null) {
			methodDeclaration.append(" throws ").append(genericExceptions);
		}
		return methodDeclaration.toString();
	}

	public String getAsmMethodSignature() {
		return asmMethodSignature;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getArgumentsCount() {
		return argumentsCount;
	}

	/**
	 * @param index 1-base index
	 */
	public char getArgumentOpcodeType(int index) {
		return argumentsOpcodeType.charAt(index);
	}

	public String getArgumentTypeName(int i) {
		return argumentsTypeNames.get(i);
	}

	public String getArgumentTypeRawName(int i) {
		return argumentsTypeRawNames.get(i);
	}

	public int getArgumentOffset(int index) {
		return argumentsOffset.get(index);
	}

	public AnnotationInfo[] getArgumentAnnotations(int index) {
		return argumentsAnnotation[index];
	}

	public int getAllArgumentsSize() {
		return argumentsWords;
	}

	public char getReturnOpcodeType() {
		return (char) returnOpcodeType.value;
	}

	public String getReturnTypeName() {
		return returnTypeName.toString();
	}

	public String getReturnTypeRawName() {
		return returnTypeRawName.toString();
	}

	public int getAccessFlags() {
		return access;
	}

	public String getClassname() {
		return classname;
	}

	public String getDescription() {
		return description;
	}

	public AnnotationInfo[] getAnnotations() {
		return annotations;
	}

	public String getDeclaredClassName() {
		if (declaredClassName == null) {
			return classname;
		}
		return declaredClassName;
	}

	public void setDeclaredClassName(String declaredClassName) {
		this.declaredClassName = declaredClassName;
	}

	public boolean isTopLevelMethod() {
		return declaredClassName == null;
	}

	public ClassInfo getClassInfo() {
		return targetClassInfo;
	}

	public String[] getExceptions() {
		return exceptionsArray;
	}

	// ---------------------------------------------------------------- utilities

	/**
	 * Returns <code>true</code> if we are currently visiting an array.
	 */
	private boolean isArray() {
		return arrayStack != 0;
	}

	/**
	 * Add '[' for current array depth.
	 */
	private String getArrayDepthString() {
		int aStack = arrayStack;    // copy value
		StringBuilder ads = new StringBuilder();
		while (aStack % 2 != 0) {
			aStack /= 2;
			ads.append('[');
		}
		return ads.toString();
	}

	/**
	 * Saves argument type if parameter is currently visiting, otherwise
	 * saves return type. When saving arguments data, stores also current argument offset.
	 */
	private void maybeUseType(char type, String typeName) {
		if (visitingArgument) {
			if (isArray()) {
				type = '[';
				typeName = getArrayDepthString() + typeName;
			}
			if (type == 'V') {
				throw new ProxettaException("Method argument can't be void");
			}
			argumentsCount++;
			argumentsOpcodeType.append(type);
			argumentsOffset.add(argumentsWords + 1);

			argumentsTypeNames.add(typeName);
			argumentsTypeRawNames.add(
				resolveRawTypeName(typeName));

			if ((type == 'D') || (type == 'J')) {
				argumentsWords += 2;
			} else {
				argumentsWords++;
			}
			visitingArgument = false;
		} else if (returnOpcodeType != null) {
			if (isArray()) {
				type = '[';
				typeName = getArrayDepthString() + typeName;
			}
			returnOpcodeType.value = type;

			if (returnTypeName.length() == 0) {
				// only set return type once, first time.
				// otherwise, if method signature has generic information, the returnTypeName
				// will be equals to last defined type in the signature, i.e. from the generics.

				//returnTypeName.setLength(0);

				if (typeName != null) {
					returnTypeName.append(typeName);
					returnTypeRawName.append(resolveRawTypeName(typeName));
				}
			}
		}
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

	// ---------------------------------------------------------------- toString

/*
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MethodSignatureVisitor that = (MethodSignatureVisitor) o;
		return getSignature().equals(that.getSignature());
	}

	@Override
	public int hashCode() {
		return getSignature().hashCode();
	}

*/
	@Override
	public String toString() {
		return getDeclaredClassName() + '#' + getMethodName() + getDescription();
	}

}


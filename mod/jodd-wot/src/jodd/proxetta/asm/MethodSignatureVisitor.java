// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.Opcodes;
import jodd.util.collection.IntArrayList;
import jodd.mutable.MutableInteger;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ClassInfo;
import jodd.proxetta.AnnotationInfo;
import jodd.asm.TraceSignatureVisitor;

import java.util.List;
import java.util.ArrayList;

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
	protected String classname;
	protected String description;
	protected AnnotationInfo[] annotations;

	protected boolean visitingArgument;
	protected IntArrayList argumentsOpcodeType;
	protected IntArrayList argumentsOffset;
	protected List<String> argumentsTypeNames;

	protected String declaredClassName;

	protected ClassInfo targetClassInfo;
	protected int hierarchyLevel;

	// ---------------------------------------------------------------- ctors

	public MethodSignatureVisitor(String description) {
		this();
		this.description = description;
	}

	public MethodSignatureVisitor(String methodName, final int access, String classname, String description, ClassInfo targetClassInfo) {
		this();
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		this.methodName = methodName;
		this.access = access;
		this.classname = classname;
		this.description = description;
		this.targetClassInfo = targetClassInfo;
	}


	private MethodSignatureVisitor() {
        super(new StringBuffer());
    }

	private MethodSignatureVisitor(final StringBuffer declaration) {
        super(declaration);
    }

	private MethodSignatureVisitor(final StringBuffer buf, MutableInteger returnOpcodeType, StringBuilder returnTypeName) {
		this(buf);
		this.returnOpcodeType = returnOpcodeType;
		this.returnTypeName = returnTypeName;
	}

	// ---------------------------------------------------------------- code

	@Override
	public SignatureVisitor visitParameterType() {
		super.visitParameterType();
		visitingArgument = true;
		if (argumentsOpcodeType == null) {
			argumentsOpcodeType = new IntArrayList();
			argumentsOffset = new IntArrayList();
			argumentsTypeNames = new ArrayList<String>();

			argumentsOpcodeType.add('L');
			argumentsOffset.add(0);
			argumentsTypeNames.add(null);
		}
		return this;
	}


	@Override
	public SignatureVisitor visitReturnType() {
		super.visitReturnType();
		returnOpcodeType = new MutableInteger();
		returnTypeName = new StringBuilder();
		return new MethodSignatureVisitor(returnType, returnOpcodeType, returnTypeName);
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		super.visitExceptionType();
		return new MethodSignatureVisitor(exceptions);
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
		super.visitTypeVariable(name);
		maybeUseType('L', name);    // toask what is this?
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
		super.visitClassType(name);
		maybeUseType('L', 'L' + name + ';');
	}

	// ---------------------------------------------------------------- method signature

	/**
	 * Returns signature.
	 */
	public String getSignature() {
		if (signature == null) {
			signature = createSignature();
		}
		return signature;
	}

	private String createSignature() {
		StringBuilder methodDeclaration = new StringBuilder(30);
		methodDeclaration.append(getReturnType()).append(' ').append(methodName).append(getDeclaration());
		String genericExceptions = getExceptions();
		if (genericExceptions != null) {
			methodDeclaration.append(" throws ").append(genericExceptions);
		}
		return methodDeclaration.toString();
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
	public int getArgumentOpcodeType(int index) {
		return argumentsOpcodeType.get(index);
	}

	protected String getArgumentTypeName(int i) {
		return argumentsTypeNames.get(i);
	}

	protected int getArgumentOffset(int i) {
		return argumentsOffset.get(i);
	}

	protected int getAllArgumentsSize() {
		return argumentsWords;
	}

	public int getReturnOpcodeType() {
		return returnOpcodeType.value;
	}

	protected String getReturnTypeName() {
		return returnTypeName.toString();
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

	public int getHierarchyLevel() {
		return hierarchyLevel;
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
	private void maybeUseType(int type, String typeName) {
		if (visitingArgument == true) {
			if (isArray() == true) {
				type = '[';
				typeName = getArrayDepthString() + typeName;
			}
			if (type == 'V') {
				throw new ProxettaException("Method argument can't be void.");
			}
			argumentsCount++;
			argumentsOpcodeType.add(type);
			argumentsOffset.add(argumentsWords + 1);
			argumentsTypeNames.add(typeName);
			if ((type == 'D') || (type == 'J')) {
				argumentsWords += 2;
			} else {
				argumentsWords++;
			}
			visitingArgument = false;
		} else if (returnOpcodeType != null) {
			if (isArray() == true) {
				type = '[';
				typeName = getArrayDepthString() + typeName;
			}
			returnOpcodeType.value = type;
			returnTypeName.setLength(0);
			returnTypeName.append(typeName);
		}
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
		return "MethodSignature: " + classname + "  " + getSignature();
	}

}


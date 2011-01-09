// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Invocation advice represents a method that will replace some invocation pointcut.
 */
public class InvokeReplacer {

	protected final String owner;
	protected final String methodName;

	public InvokeReplacer(Class target, String methodName) {
		this(target.getCanonicalName(), methodName);
	}

	public InvokeReplacer(String classCanonicalName, String methodName) {
		owner = classCanonicalName.replace('.', '/');
		this.methodName = methodName;
	}

	public static InvokeReplacer with(String classCanonicalName, String methodName) {
		return new InvokeReplacer(classCanonicalName, methodName);
	}

	public static InvokeReplacer with(Class target, String methodName) {
		return new InvokeReplacer(target, methodName);
	}

	// ---------------------------------------------------------------- getters

	public String getOwner() {
		return owner;
	}

	public String getMethodName() {
		return methodName;
	}

	// ---------------------------------------------------------------- settings

	protected boolean passOwnerName;
	protected boolean passMethodName;
	protected boolean passMethodSignature;
	protected boolean passThis;
	protected boolean passTargetClass;

	public boolean isPassOwnerName() {
		return passOwnerName;
	}

	public void setPassOwnerName(boolean passOwnerName) {
		this.passOwnerName = passOwnerName;
	}

	public InvokeReplacer passOwnerName(boolean passOwnerName) {
		this.passOwnerName = passOwnerName;
		return this;
	}

	public boolean isPassMethodName() {
		return passMethodName;
	}

	public void setPassMethodName(boolean passMethodName) {
		this.passMethodName = passMethodName;
	}

	public InvokeReplacer passMethodName(boolean passMethodName) {
		this.passMethodName = passMethodName;
		return this;
	}

	public boolean isPassMethodSignature() {
		return passMethodSignature;
	}

	public void setPassMethodSignature(boolean passMethodSignature) {
		this.passMethodSignature = passMethodSignature;
	}

	public InvokeReplacer passMethodSignature(boolean passMethodSignature) {
		this.passMethodSignature = passMethodSignature;
		return this;
	}

	public boolean isPassThis() {
		return passThis;
	}

	public void setPassThis(boolean passThis) {
		this.passThis = passThis;
	}

	public InvokeReplacer passThis(boolean passThis) {
		this.passThis = passThis;
		return this;
	}

	public boolean isPassTargetClass() {
		return passTargetClass;
	}

	public void setPassTargetClass(boolean passTargetClass) {
		this.passTargetClass = passTargetClass;
	}

	public InvokeReplacer passTargetClass(boolean passTargetClass) {
		this.passTargetClass = passTargetClass;
		return this;
	}
}

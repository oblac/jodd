// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Holder for various action names used during path registration.
 */
public class ActionNames {

	protected String packageName;
	protected String packageActionPath;

	protected String className;
	protected String classActionPath;

	protected String methodName;
	protected String methodActionPath;

	protected String extension;
	protected String httpMethod;

	// ---------------------------------------------------------------- setters

	/**
	 * Sets package-related names.
	 * @param packageName name derived from the package
	 * @param packageActionPath action path from package (optional, may be <code>null</code>)
	 */
	public void setPackageNames(String packageName, String packageActionPath) {
		this.packageName = packageName;
		this.packageActionPath = packageActionPath;
	}

	/**
	 * Sets class-related names.
	 * @param className name derived from the class
	 * @param classActionPath action path from class
	 */
	public void setClassNames(String className, String classActionPath) {
		this.className = className;
		this.classActionPath = classActionPath;
	}

	/**
	 * Sets method-related names.
	 * @param methodName name derived from the method
	 * @param methodActionPath action path from method (optional, may be <code>null</code>)
	 */
	public void setMethodNames(String methodName, String methodActionPath) {
		this.methodName = methodName;
		this.methodActionPath = methodActionPath;
	}

	/**
	 * Sets extension.
	 * @param extension extension (optional, may be <code>null</code>)
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * Sets HTTP method.
	 * @param httpMethod HTTP method name (may be <code>null</code>)
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	// ---------------------------------------------------------------- getters

	public String getPackageName() {
		return packageName;
	}

	public String getPackageActionPath() {
		return packageActionPath;
	}

	public String getClassName() {
		return className;
	}

	public String getClassActionPath() {
		return classActionPath;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getMethodActionPath() {
		return methodActionPath;
	}

	public String getExtension() {
		return extension;
	}

	public String getHttpMethod() {
		return httpMethod;
	}
}
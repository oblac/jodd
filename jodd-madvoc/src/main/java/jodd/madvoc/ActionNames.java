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
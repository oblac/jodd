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

package jodd.madvoc.config;

/**
 * Holder for various action names used during path registration.
 */
public class ActionNames {

	private final String packageName;
	private final String packageActionPath;

	private final String className;
	private final String classActionPath;

	private final String methodName;
	private final String methodActionPath;

	private final String httpMethod;

	public ActionNames(final String[] packageActionNames, final String[] classActionNames, final String[] methodActionNames, final String httpMethod) {
		this.packageName = packageActionNames[0];
		this.packageActionPath = packageActionNames[1];
		this.className = classActionNames[0];
		this.classActionPath = classActionNames[1];
		this.methodName = methodActionNames[0];
		this.methodActionPath = methodActionNames[1];
		this.httpMethod = httpMethod;
	}

	// ---------------------------------------------------------------- getters

	public String packageName() {
		return packageName;
	}

	public String packageActionPath() {
		return packageActionPath;
	}

	public String className() {
		return className;
	}

	public String classActionPath() {
		return classActionPath;
	}

	public String methodName() {
		return methodName;
	}

	public String methodActionPath() {
		return methodActionPath;
	}

	public String httpMethod() {
		return httpMethod;
	}
}
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

package jodd.proxetta;

public class JoddProxettaDefaults {

	/**
	 * {@link jodd.proxetta.ProxyAdvice#execute()}
	 */
	private String executeMethodName = "execute";

	/**
	 * Proxy class name suffix.
	 */
	private String proxyClassNameSuffix = "$$Proxetta";

	/**
	 * Invoke proxy class name suffix.
	 */
	private String invokeProxyClassNameSuffix = "$$Clonetou";

	/**
	 * Wrapper class name suffix.
	 */
	private String wrapperClassNameSuffix = "$$Wraporetto";

	/**
	 * Prefix for advice method names.
	 */
	private String methodPrefix = "$__";

	/**
	 * Divider for method names.
	 */
	private String methodDivider = "$";

	/**
	 * Method name for advice 'clinit' methods.
	 */
	private String clinitMethodName = "$clinit";

	/**
	 * Method name for advice default constructor ('init') methods.
	 */
	private String initMethodName = "$init";

	/**
	 * Prefix for advice field names.
	 */
	private String fieldPrefix = "$__";

	/**
	 * Divider for field names.
	 */
	private String fieldDivider = "$";

	/**
	 * Wrapper target field name.
	 */
	private String wrapperTargetFieldName = "_target";


	public String getExecuteMethodName() {
		return executeMethodName;
	}

	public void setExecuteMethodName(String executeMethodName) {
		this.executeMethodName = executeMethodName;
	}

	public String getProxyClassNameSuffix() {
		return proxyClassNameSuffix;
	}

	public void setProxyClassNameSuffix(String proxyClassNameSuffix) {
		this.proxyClassNameSuffix = proxyClassNameSuffix;
	}

	public String getInvokeProxyClassNameSuffix() {
		return invokeProxyClassNameSuffix;
	}

	public void setInvokeProxyClassNameSuffix(String invokeProxyClassNameSuffix) {
		this.invokeProxyClassNameSuffix = invokeProxyClassNameSuffix;
	}

	public String getWrapperClassNameSuffix() {
		return wrapperClassNameSuffix;
	}

	public void setWrapperClassNameSuffix(String wrapperClassNameSuffix) {
		this.wrapperClassNameSuffix = wrapperClassNameSuffix;
	}

	public String getMethodPrefix() {
		return methodPrefix;
	}

	public void setMethodPrefix(String methodPrefix) {
		this.methodPrefix = methodPrefix;
	}

	public String getMethodDivider() {
		return methodDivider;
	}

	public void setMethodDivider(String methodDivider) {
		this.methodDivider = methodDivider;
	}

	public String getClinitMethodName() {
		return clinitMethodName;
	}

	public void setClinitMethodName(String clinitMethodName) {
		this.clinitMethodName = clinitMethodName;
	}

	public String getInitMethodName() {
		return initMethodName;
	}

	public void setInitMethodName(String initMethodName) {
		this.initMethodName = initMethodName;
	}

	public String getFieldPrefix() {
		return fieldPrefix;
	}

	public void setFieldPrefix(String fieldPrefix) {
		this.fieldPrefix = fieldPrefix;
	}

	public String getFieldDivider() {
		return fieldDivider;
	}

	public void setFieldDivider(String fieldDivider) {
		this.fieldDivider = fieldDivider;
	}

	public String getWrapperTargetFieldName() {
		return wrapperTargetFieldName;
	}

	public void setWrapperTargetFieldName(String wrapperTargetFieldName) {
		this.wrapperTargetFieldName = wrapperTargetFieldName;
	}
}

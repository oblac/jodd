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

import jodd.Jodd;

/**
 * Jodd PROXETTA module.
 */
public class JoddProxetta {

	/**
	 * {@link jodd.proxetta.ProxyAdvice#execute()}
	 */
	public static String executeMethodName = "execute";

	/**
	 * Proxy class name suffix.
	 */
	public static String proxyClassNameSuffix = "$$Proxetta";

	/**
	 * Invoke proxy class name suffix.
	 */
	public static String invokeProxyClassNameSuffix = "$$Clonetou";

	/**
	 * Wrapper class name suffix.
	 */
	public static String wrapperClassNameSuffix = "$$Wraporetto";

	/**
	 * Prefix for advice method names.
	 */
	public static String methodPrefix = "$__";

	/**
	 * Divider for method names.
	 */
	public static String methodDivider = "$";

	/**
	 * Method name for advice 'clinit' methods.
	 */
	public static String clinitMethodName = "$clinit";

	/**
	 * Method name for advice default constructor ('init') methods.
	 */
	public static String initMethodName = "$init";

	/**
	 * Prefix for advice field names.
	 */
	public static String fieldPrefix = "$__";

	/**
	 * Divider for field names.
	 */
	public static String fieldDivider = "$";

	/**
	 * Wrapper target field name.
	 */
	public static String wrapperTargetFieldName = "_target";

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.initModule();
	}

}
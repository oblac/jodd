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

package jodd.jtx.proxy;

import jodd.proxetta.ProxyAdvice;

import static jodd.proxetta.ProxyTarget.createArgumentsClassArray;
import static jodd.proxetta.ProxyTarget.targetClass;
import static jodd.proxetta.ProxyTarget.targetMethodDescription;
import static jodd.proxetta.ProxyTarget.targetMethodName;
import static jodd.proxetta.ProxyTarget.invoke;
import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxTransaction;

import static jodd.jtx.proxy.AnnotationTxAdviceSupport.manager;

/**
 * Advice that reads transaction annotations and manage transactions over method invocation.
 */
public class AnnotationTxAdvice implements ProxyAdvice {

	public Object execute() throws Exception {
		Class type = targetClass();
		String methodName = targetMethodName();
		Class[] methodArgsTypes = createArgumentsClassArray();
		String methodDescription = targetMethodDescription();

		// read transaction mode from annotation
		JtxTransactionMode txMode = manager.getTxMode(type, methodName, methodArgsTypes, methodDescription);

		// request transaction
		JtxTransaction tx = null;
		try {
			String scope = manager.resolveScope(type, methodName);
			tx = manager.getJtxWorker().maybeRequestTransaction(txMode, scope);
			Object result = invoke();
			manager.getJtxWorker().maybeCommitTransaction(tx);
			return result;
		} catch (Exception ex) {
			manager.getJtxWorker().markOrRollbackTransaction(tx, ex);
			throw ex;
		}

	}
}

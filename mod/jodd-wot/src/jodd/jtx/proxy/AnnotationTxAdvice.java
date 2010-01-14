// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.proxy;

import jodd.proxetta.ProxyAdvice;
import static jodd.proxetta.ProxyTarget.targetClass;
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

		// read transaction mode from annotation
		JtxTransactionMode txMode = manager.getTxMode(type, methodName);

		// request transaction
		JtxTransaction tx = null;
		try {
			String context = manager.resolveContext(type, methodName);
			tx = manager.getJtxWorker().maybeRequestTransaction(txMode, context);
			Object result = invoke();
			manager.getJtxWorker().maybeCommitTransaction(tx);
			return result;
		} catch (Exception ex) {
			manager.getJtxWorker().markOrRollbackTransaction(tx, ex);
			throw ex;
		}

	}
}

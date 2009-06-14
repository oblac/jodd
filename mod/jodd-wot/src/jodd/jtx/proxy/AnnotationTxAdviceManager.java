// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.proxy;

import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.meta.Transaction;
import jodd.jtx.worker.LeanTransactionWorker;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.proxetta.ProxettaException;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/**
 * Manager for {@link jodd.jtx.proxy.AnnotationTxAdvice}.
 */
public class AnnotationTxAdviceManager {

	protected final Map<String, JtxTransactionMode> txmap = new HashMap<String, JtxTransactionMode>();

	protected final LeanTransactionWorker jtxWorker;

	protected final JtxTransactionMode defaultTransactionMode;

	protected final boolean isMethodContext;

	// ---------------------------------------------------------------- ctors

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager) {
		this(new LeanTransactionWorker(jtxManager));
	}
	
	public AnnotationTxAdviceManager(LeanTransactionWorker jtxWorker) {
		this(jtxWorker, true, null);
	}

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager, boolean isMethodContext) {
		this(new LeanTransactionWorker(jtxManager), isMethodContext);
	}

	public AnnotationTxAdviceManager(LeanTransactionWorker jtxWorker, boolean isMethodContext) {
		this(jtxWorker, isMethodContext, null);
	}

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager, boolean isMethodContext, JtxTransactionMode defaultTxMode) {
		this(new LeanTransactionWorker(jtxManager), isMethodContext, defaultTxMode);
	}

	public AnnotationTxAdviceManager(LeanTransactionWorker jtxWorker, boolean isMethodContext, JtxTransactionMode defaultTxMode) {
		this.jtxWorker = jtxWorker;
		this.defaultTransactionMode = defaultTxMode == null ? new JtxTransactionMode().propagationSupports() : defaultTxMode;
		this.isMethodContext = isMethodContext;
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Returns tx worker.
	 */
	public LeanTransactionWorker getJtxWorker() {
		return jtxWorker;
	}

	/**
	 * Returns default transaction mode.
	 */
	public JtxTransactionMode getDefaultTransactionMode() {
		return defaultTransactionMode;
	}

	/**
	 * Returns <code>true</code> if annotated method is transaction context.
	 */
	public boolean isMethodContext() {
		return isMethodContext;
	}

	/**
	 * Reads transaction mode from method annotation. Annotations are cached for better performances.
	 */
	public synchronized JtxTransactionMode getTxMode(Class type, String methodName) {
		String signature = type.getName() + '#' + methodName;
		JtxTransactionMode txMode = txmap.get(signature);
		if (txMode == null) {
			if (txmap.containsKey(signature) == false) {
				ClassDescriptor cd = ClassIntrospector.lookup(type);
				Method m = cd.getMethod(methodName);
				if (m == null) {
					throw new ProxettaException("Method '" + methodName + "'not found in class: " + type.getName());
				}
				Transaction txAnn = m.getAnnotation(Transaction.class);
				if (txAnn != null) {
					txMode = new JtxTransactionMode();
					txMode.setPropagationBehaviour(txAnn.propagation());
					txMode.setIsolationLevel(txAnn.isolation());
					txMode.setReadOnly(txAnn.readOnly());
				} else {
					txMode = defaultTransactionMode;
				}
				txmap.put(signature, txMode);
			}
		}
		return txMode;
	}

}

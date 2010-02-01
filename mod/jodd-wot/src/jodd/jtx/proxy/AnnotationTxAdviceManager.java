// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.proxy;

import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.meta.Transaction;
import jodd.jtx.worker.LeanTransactionWorker;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.proxetta.ProxettaException;
import jodd.util.StringUtil;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/**
 * Manager for {@link jodd.jtx.proxy.AnnotationTxAdvice}.
 */
public class AnnotationTxAdviceManager {

	protected  static final String JTXCTX_PATTERN_CLASS = "$class";
	protected static final String JTXCTX_PATTERN_METHOD = "$method";

	protected final Map<String, JtxTransactionMode> txmap = new HashMap<String, JtxTransactionMode>();

	protected final LeanTransactionWorker jtxWorker;

	protected final JtxTransactionMode defaultTransactionMode;

	protected final String contextPattern;

	// ---------------------------------------------------------------- ctors

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager) {
		this(new LeanTransactionWorker(jtxManager));
	}
	
	public AnnotationTxAdviceManager(LeanTransactionWorker jtxWorker) {
		this(jtxWorker, JTXCTX_PATTERN_CLASS + '#' + JTXCTX_PATTERN_METHOD, null);
	}

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager, String contextPattern) {
		this(new LeanTransactionWorker(jtxManager), contextPattern);
	}

	public AnnotationTxAdviceManager(LeanTransactionWorker jtxWorker, String contextPattern) {
		this(jtxWorker, contextPattern, null);
	}

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager, String contextPattern, JtxTransactionMode defaultTxMode) {
		this(new LeanTransactionWorker(jtxManager), contextPattern, defaultTxMode);
	}

	public AnnotationTxAdviceManager(LeanTransactionWorker jtxWorker, String contextPattern, JtxTransactionMode defaultTxMode) {
		this.jtxWorker = jtxWorker;
		this.defaultTransactionMode = defaultTxMode == null ? new JtxTransactionMode().propagationSupports() : defaultTxMode;
		this.contextPattern = contextPattern;
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
	 * Resolves tx context from context pattern.
	 */
	public String resolveContext(Class type, String methodName) {
		if (contextPattern == null) {
			return null;
		}
		String ctx = contextPattern;
		ctx = StringUtil.replace(ctx, JTXCTX_PATTERN_CLASS, type.getName());
		ctx = StringUtil.replace(ctx, JTXCTX_PATTERN_METHOD, methodName);
		return ctx;
	}

	/**
	 * Reads transaction mode from method annotation. Annotations are cached for better performances
	 * @param type target class
	 * @param methodName target method name over which the transaction should be wrapped
	 * @param methodArgTypes types of arguments, used to find the method
	 * @param description method description (bytecode-like) or any other unique method signature, used as key for caching annotations 
	 */
	public synchronized JtxTransactionMode getTxMode(Class type, String methodName, Class[] methodArgTypes, String description) {
		JtxTransactionMode txMode = txmap.get(description);
		if (txMode == null) {
			if (txmap.containsKey(description) == false) {
				ClassDescriptor cd = ClassIntrospector.lookup(type);
				Method m = cd.getMethod(methodName, methodArgTypes);
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
				txmap.put(description, txMode);
			}
		}
		return txMode;
	}

}

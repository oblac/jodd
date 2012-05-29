// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.proxy;

import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.meta.Transaction;
import jodd.jtx.meta.TransactionAnnotation;
import jodd.jtx.meta.TransactionAnnotationData;
import jodd.jtx.worker.LeanJtxWorker;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.proxetta.ProxettaException;
import jodd.util.StringUtil;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/**
 * Manager for {@link jodd.jtx.proxy.AnnotationTxAdvice}.
 */
public class AnnotationTxAdviceManager {

	protected static final String JTXCTX_PATTERN_CLASS = "$class";
	protected static final String JTXCTX_PATTERN_METHOD = "$method";

	protected final Map<String, JtxTransactionMode> txmap = new HashMap<String, JtxTransactionMode>();

	protected final LeanJtxWorker jtxWorker;

	protected final JtxTransactionMode defaultTransactionMode;

	protected final String scopePattern;

	protected Class<? extends Annotation>[] annotations;
	protected TransactionAnnotation[] annotationInstances;

	// ---------------------------------------------------------------- ctors

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager) {
		this(new LeanJtxWorker(jtxManager));
	}
	
	public AnnotationTxAdviceManager(LeanJtxWorker jtxWorker) {
		this(jtxWorker, JTXCTX_PATTERN_CLASS + '#' + JTXCTX_PATTERN_METHOD, null);
	}

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager, String scopePattern) {
		this(new LeanJtxWorker(jtxManager), scopePattern);
	}

	public AnnotationTxAdviceManager(LeanJtxWorker jtxWorker, String scopePattern) {
		this(jtxWorker, scopePattern, null);
	}

	public AnnotationTxAdviceManager(JtxTransactionManager jtxManager, String scopePattern, JtxTransactionMode defaultTxMode) {
		this(new LeanJtxWorker(jtxManager), scopePattern, defaultTxMode);
	}

	@SuppressWarnings( {"unchecked"})
	public AnnotationTxAdviceManager(LeanJtxWorker jtxWorker, String scopePattern, JtxTransactionMode defaultTxMode) {
		this.jtxWorker = jtxWorker;
		this.defaultTransactionMode = defaultTxMode == null ? new JtxTransactionMode().propagationSupports() : defaultTxMode;
		this.scopePattern = scopePattern;
		registerAnnotations(new Class[] {Transaction.class});
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Returns tx worker.
	 */
	public LeanJtxWorker getJtxWorker() {
		return jtxWorker;
	}

	/**
	 * Returns default transaction mode.
	 */
	public JtxTransactionMode getDefaultTransactionMode() {
		return defaultTransactionMode;
	}

	/**
	 * Resolves tx scope from scope pattern.
	 */
	public String resolveScope(Class type, String methodName) {
		if (scopePattern == null) {
			return null;
		}
		String ctx = scopePattern;
		ctx = StringUtil.replace(ctx, JTXCTX_PATTERN_CLASS, type.getName());
		ctx = StringUtil.replace(ctx, JTXCTX_PATTERN_METHOD, methodName);
		return ctx;
	}

	/**
	 * Reads transaction mode from method annotation. Annotations are cached for better performances.
	 * @param type target class
	 * @param methodName target method name over which the transaction should be wrapped
	 * @param methodArgTypes types of arguments, used to find the method
	 * @param unique unique method fingerprint that contains return and arguments type information
	 */
	public synchronized JtxTransactionMode getTxMode(Class type, String methodName, Class[] methodArgTypes, String unique) {
		String signature = type.getName() + '#' + methodName + '%' + unique;
		JtxTransactionMode txMode = txmap.get(signature);
		if (txMode == null) {
			if (txmap.containsKey(signature) == false) {
				ClassDescriptor cd = ClassIntrospector.lookup(type);
				Method m = cd.getMethod(methodName, methodArgTypes);
				if (m == null) {
					throw new ProxettaException("Method '" + methodName + "'not found in class: " + type.getName());
				}
				TransactionAnnotationData txAnn = getTransactionAnnotation(m);
				if (txAnn != null) {
					txMode = new JtxTransactionMode();
					txMode.setPropagationBehaviour(txAnn.getPropagation());
					txMode.setIsolationLevel(txAnn.getIsolation());
					txMode.setReadOnly(txAnn.isReadOnly());
					txMode.setTransactionTimeout(txAnn.getTimeout());
				} else {
					txMode = defaultTransactionMode;
				}
				txmap.put(signature, txMode);
			}
		}
		return txMode;
	}

	// ---------------------------------------------------------------- tx annotations

	/**
	 * Registers tx annotations.
	 */
	@SuppressWarnings( {"unchecked"})
	public void registerAnnotations(Class<? extends Annotation>... txAnnotations) {
		this.annotations = txAnnotations;

		this.annotationInstances = new TransactionAnnotation<?>[annotations.length];
		for (int i = 0; i < annotations.length; i++) {
			Class<? extends Annotation> annotationClass = annotations[i];
			annotationInstances[i] = new TransactionAnnotation(annotationClass);
		}

	}

	/**
	 * Finds TX annotation.
	 */
	protected TransactionAnnotationData getTransactionAnnotation(Method method) {
		for (TransactionAnnotation annotationInstance : annotationInstances) {
			TransactionAnnotationData tad = annotationInstance.readAnnotationData(method);
			if (tad != null) {
				return tad;
			}
		}
		return null;
	}

}

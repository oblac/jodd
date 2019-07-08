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

import jodd.jtx.JtxTransactionMode;
import jodd.jtx.meta.ReadOnlyTransaction;
import jodd.jtx.meta.ReadWriteTransaction;
import jodd.jtx.meta.Transaction;
import jodd.jtx.meta.TransactionAnnotationValues;
import jodd.jtx.worker.LeanJtxWorker;
import jodd.proxetta.ProxettaException;
import jodd.util.StringUtil;
import jodd.util.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for {@link jodd.jtx.proxy.AnnotationTxAdvice}.
 */
public class AnnotationTxAdviceManager {

	protected static final String JTXCTX_PATTERN_CLASS = "$class";
	protected static final String JTXCTX_PATTERN_METHOD = "$method";

	protected final Map<String, JtxTransactionMode> txmap = new HashMap<>();

	protected final LeanJtxWorker jtxWorker;

	protected final JtxTransactionMode defaultTransactionMode;

	protected final String scopePattern;

	protected Class<? extends Annotation>[] annotations;
	protected AnnotationParser[] annotationParsers;

	// ---------------------------------------------------------------- ctors

	public AnnotationTxAdviceManager(final LeanJtxWorker jtxWorker) {
		this(jtxWorker, JTXCTX_PATTERN_CLASS + '#' + JTXCTX_PATTERN_METHOD, null);
	}

	public AnnotationTxAdviceManager(final LeanJtxWorker jtxWorker, final String scopePattern) {
		this(jtxWorker, scopePattern, null);
	}

	@SuppressWarnings( {"unchecked"})
	public AnnotationTxAdviceManager(final LeanJtxWorker jtxWorker, final String scopePattern, final JtxTransactionMode defaultTxMode) {
		this.jtxWorker = jtxWorker;
		this.defaultTransactionMode = defaultTxMode == null ? JtxTransactionMode.PROPAGATION_SUPPORTS_READ_ONLY : defaultTxMode;
		this.scopePattern = scopePattern;

		registerAnnotations(new Class[] {
			Transaction.class, ReadWriteTransaction.class, ReadOnlyTransaction.class
		});
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
	public String resolveScope(final Class type, final String methodName) {
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
	public synchronized JtxTransactionMode getTxMode(final Class type, final String methodName, final Class[] methodArgTypes, final String unique) {
		String signature = type.getName() + '#' + methodName + '%' + unique;
		JtxTransactionMode txMode = txmap.get(signature);
		if (txMode == null) {
			if (!txmap.containsKey(signature)) {

				final Method m;
				try {
					m = type.getMethod(methodName, methodArgTypes);
				} catch (NoSuchMethodException nsmex) {
					throw new ProxettaException(nsmex);
				}

				final TransactionAnnotationValues txAnn = readTransactionAnnotation(m);
				if (txAnn != null) {
					txMode = new JtxTransactionMode(
						txAnn.propagation(),
						txAnn.isolation(),
						txAnn.readOnly(),
						txAnn.timeout()
					);
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
	 * Returns current TX annotations.
	 */
	public Class<? extends Annotation>[] getAnnotations() {
		return annotations;
	}

	/**
	 * Registers new TX annotations.
	 */
	@SuppressWarnings( {"unchecked"})
	public void registerAnnotations(final Class<? extends Annotation>[] annotations) {
		this.annotations = annotations;

		this.annotationParsers = new AnnotationParser[annotations.length];

		for (int i = 0; i < annotations.length; i++) {
			annotationParsers[i] = TransactionAnnotationValues.parserFor(annotations[i]);
		}
	}

	/**
	 * Finds TX annotation.
	 */
	protected TransactionAnnotationValues readTransactionAnnotation(final Method method) {
		for (AnnotationParser annotationParser : annotationParsers) {
			TransactionAnnotationValues tad = TransactionAnnotationValues.of(annotationParser, method);
			if (tad != null) {
				return tad;
			}
		}
		return null;
	}

}

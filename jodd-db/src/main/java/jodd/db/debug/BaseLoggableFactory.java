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

package jodd.db.debug;

import jodd.db.DbSqlException;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.asm.ProxettaAsmUtil;
import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.impl.WrapperProxettaBuilder;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class BaseLoggableFactory<T> {

	// ---------------------------------------------------------------- wrap

	protected Class<T> wrappedStatement;
	protected WrapperProxettaBuilder builder;
	protected Field sqlTemplateField;
	protected Method getQueryStringMethod;
	protected final WrapperProxetta proxetta;
	protected final Class<T> targetClass;

	/**
	 * Returns {@link WrapperProxetta} used for building loggable prepared statements.
	 * Initializes proxetta when called for the first time.
	 */
	protected BaseLoggableFactory(Class<T> targetClass) {
		this.targetClass = targetClass;
		this.proxetta = WrapperProxetta.withAspects(new ProxyAspect(LoggableAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				int argumentsCount = methodInfo.getArgumentsCount();
				char argumentType = 0;
				if (argumentsCount >= 1) {
					argumentType = methodInfo.getArgument(1).getOpcode();
				}
				return
					methodInfo.getReturnType().getOpcode() == 'V' &&			// void-returning method
						argumentType == 'I' &&									// first argument type
						methodInfo.isPublicMethod() &&
						methodInfo.getMethodName().startsWith("set") &&			// set*
						(argumentsCount == 2 || argumentsCount == 3);			// number of arguments
			}
		}));
	}

	/**
	 * Wraps prepared statement.
	 */
	@SuppressWarnings("unchecked")
	protected T wrap(T preparedStatement, String sql) {
		if (wrappedStatement == null) {
			builder = proxetta.builder();

			// use just interface
			builder.setTarget(targetClass);

			// define different package
			builder.setTargetProxyClassName(this.getClass().getPackage().getName() + '.' + targetClass.getSimpleName());

			wrappedStatement = builder.define();

			// lookup fields
			try {
				String fieldName = ProxettaAsmUtil.adviceFieldName("sqlTemplate", 0);
				sqlTemplateField = wrappedStatement.getField(fieldName);

				String methodName = ProxettaAsmUtil.adviceMethodName("getQueryString", 0);
				getQueryStringMethod = wrappedStatement.getMethod(methodName);
			} catch (Exception ex) {
				throw new DbSqlException(ex);
			}
		}

		// wrap prepared statement instance

		T wrapper;
		try {
			wrapper = wrappedStatement.newInstance();
		} catch (Exception ex) {
			throw new DbSqlException(ex);
		}

		builder.injectTargetIntoWrapper(preparedStatement, wrapper);

		try {
			sqlTemplateField.set(wrapper, sql);
		} catch (Exception ex) {
			throw new DbSqlException(ex);
		}

		return wrapper;
	}

	/**
	 * Returns the query string from loggable wrapped statement.
	 */
	public String getQueryString(T statement) {
		try {
			return (String) getQueryStringMethod.invoke(statement);
		} catch (Exception ex) {
			throw new DbSqlException(ex);
		}
	}

}

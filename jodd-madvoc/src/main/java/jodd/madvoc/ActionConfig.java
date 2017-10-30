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

package jodd.madvoc;

import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.Result;
import jodd.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Action configuration and shared run-time data, used internally.
 */
public class ActionConfig {

	public static class MethodParam {

		private final Class type;
		private final String name;
		private final Class<? extends Annotation> annotationType;

		public MethodParam(Class type, String name, Class<? extends Annotation> annotationType) {
			this.type = type;
			this.name = name;
			this.annotationType = annotationType;
		}

		/**
		 * Returns parameter type.
		 */
		public Class getType() {
			return type;
		}

		/**
		 * Returns parameter name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns parameter Madvoc annotation type, one of
		 * {@link jodd.madvoc.meta.In}, {@link jodd.madvoc.meta.Out} or {@link jodd.madvoc.meta.InOut}.
		 */
		public Class<? extends Annotation> getAnnotationType() {
			return annotationType;
		}
	}

	// configuration
	public final Class actionClass;
	public final Method actionClassMethod;
	public final Class<? extends ActionResult> actionResult;
	public final String actionPath;
	public final String actionMethod;
	public final String resultBasePath;
	public final Field resultField;
	public final boolean async;

	// scope data information matrix: [scope-type][target-index]
	public final ScopeData[][] scopeData;
	public final MethodParam[] methodParams;

	public final boolean hasArguments;

	// run-time data
	protected ActionConfigSet actionConfigSet;
	public final ActionFilter[] filters;
	public final ActionInterceptor[] interceptors;

	public ActionConfig(
			Class actionClass,
			Method actionClassMethod,
			ActionFilter[] filters,
			ActionInterceptor[] interceptors,
			ActionDef actionDef,
			Class<? extends ActionResult> actionResult,
			boolean async,
			ScopeData[][] scopeData,
			MethodParam[] methodParams
			)
	{
		this.actionClass = actionClass;
		this.actionClassMethod = actionClassMethod;
		this.actionPath = actionDef.getActionPath();
		this.actionMethod = actionDef.getActionMethod() == null ? null : actionDef.getActionMethod().toUpperCase();
		this.resultBasePath = actionDef.getResultBasePath();
		this.hasArguments = actionClassMethod.getParameterTypes().length != 0;
		this.actionResult = actionResult;
		this.async = async;

		this.scopeData = scopeData;

		this.filters = filters;
		this.interceptors = interceptors;
		this.methodParams = methodParams;
		this.resultField = findResultField(actionClass);
	}

	// ---------------------------------------------------------------- result

	/**
	 * Finds result field in the action class.
	 */
	protected Field findResultField(Class actionClass) {
		FieldDescriptor[] fields = ClassIntrospector.get().lookup(actionClass).getAllFieldDescriptors();
		for (FieldDescriptor fd : fields) {
			Field field = fd.getField();
			if (ClassUtil.isTypeOf(field.getType(), Result.class)) {
				field.setAccessible(true);
				return field;
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns action class.
	 */
	public Class getActionClass() {
		return actionClass;
	}

	/**
	 * Returns action class method.
	 */
	public Method getActionClassMethod() {
		return actionClassMethod;
	}

	/**
	 * Returns action path.
	 */
	public String getActionPath() {
		return actionPath;
	}

	/**
	 * Returns action method.
	 */
	public String getActionMethod() {
		return actionMethod;
	}

	/**
	 * Returns action result base path.
	 */
	public String getResultBasePath() {
		return resultBasePath;
	}

	/**
	 * Returns interceptor instances.
	 */
	public ActionInterceptor[] getInterceptors() {
		return interceptors;
	}

	/**
	 * Returns <code>true</code> if action is asynchronous.
	 */
	public boolean isAsync() {
		return async;
	}

	public ActionConfigSet getActionConfigSet() {
		return actionConfigSet;
	}

	/**
	 * Returns method parameters information, or <code>null</code> if method has no params.
	 */
	public MethodParam[] getMethodParams() {
		return methodParams;
	}

	/**
	 * Returns action result class that will render the result.
	 * may be <code>null</code>.
	 */
	public Class<? extends ActionResult> getActionResult() {
		return actionResult;
	}

	// ---------------------------------------------------------------- to string

	/**
	 * Returns action string in form 'actionClass#actionMethod'.
	 */
	public String getActionString() {
		String className = actionClass.getName();

		int ndx = className.indexOf("$$");

		if (ndx != -1) {
			className = className.substring(0, ndx);
		}

		return className + '#' + actionClassMethod.getName();
	}

	@Override
	public String toString() {
		return "action: " + actionPath + (actionMethod == null ? "" : '#' + actionMethod) + "  -->  " + getActionString();
	}

}
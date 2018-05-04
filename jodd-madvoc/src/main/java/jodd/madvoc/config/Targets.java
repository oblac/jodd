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

package jodd.madvoc.config;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.meta.Out;
import jodd.madvoc.scope.MadvocScope;
import jodd.util.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Encapsulation of all {@link Target targets}. It consists of:
 * <ul>
 *     <li>action or third-party object (on the index zero)</li>
 *     <li>optional method targets (starting from index one)</li>
 * </ul>
 */
public class Targets {

	private final Target[] targets;

	public Targets(final Object actionOrTarget, final ScopeData scopeData) {
		targets = new Target[]{Target.ofValue(actionOrTarget, scopeData)};
	}

	public Targets(final ActionRequest actionRequest) {
		targets = makeTargets(
			Target.ofValue(actionRequest.getAction(), actionRequest.getActionRuntime().getScopeData()),
			actionRequest.getActionRuntime().getMethodParams());
	}

	// ---------------------------------------------------------------- iteration

	/**
	 * Iterates all targets.
	 */
	public void forEachTarget(final Consumer<Target> targetConsumer) {
		for (final Target target : targets) {
			targetConsumer.accept(target);
		}
	}

	/**
	 * Iterates all targets and for each target iterates all IN injection points of given scope.
	 */
	public void forEachTargetAndIn(final MadvocScope scope, final BiConsumer<Target, InjectionPoint> biConsumer) {
		for (final Target target : targets) {
			final ScopeData scopeData = target.scopeData();

			if (scopeData.in() == null) {
				continue;
			}
			for (final InjectionPoint in : scopeData.in()) {
				if (in.scope() != scope) {
					continue;
				}
				biConsumer.accept(target, in);
			}
		}
	}

	/**
	 * Iterates all targets and for each target iterates all OUT injection points of given scope.
	 */
	public void forEachTargetAndOut(final MadvocScope scope, final BiConsumer<Target, InjectionPoint> biConsumer) {
		for (final Target target : targets) {
			final ScopeData scopeData = target.scopeData();

			if (scopeData.out() == null) {
				continue;
			}
			for (final InjectionPoint out : scopeData.out()) {
				if (out.scope() != scope) {
					continue;
				}
				biConsumer.accept(target, out);
			}
		}
	}

	// ---------------------------------------------------------------- parameter values

	/**
	 * Collects all parameters from target into an array.
	 */
	public Object[] extractParametersValues() {
		final Object[] values = new Object[targets.length - 1];

		for (int i = 1; i < targets.length; i++) {
			values[i - 1] = targets[i].value();
		}

		return values;
	}

	/**
	 * Joins action and parameters into one single array of Targets.
	 */
	protected Target[] makeTargets(final Target actionTarget, final MethodParam[] methodParams) {
		if (methodParams == null) {
			// action does not have method parameters, so there is just one target
			return new Target[]{actionTarget};
		}

		// action has method arguments, so there is more then one target
		final Target[] target = new Target[methodParams.length + 1];
		target[0] = actionTarget;

		final Object action = actionTarget.value();

		for (int i = 0; i < methodParams.length; i++) {
			final MethodParam methodParam = methodParams[i];
			final Class paramType = methodParam.type();

			final Target paramTarget;

			if (methodParam.annotationType() == null) {
				// parameter is NOT annotated, create new value for the target
				// the class itself will be a base class, and should be scanned

				final ScopeData newScopeData = methodParam.scopeData().inspector().inspectClassScopesWithCache(paramType);
				paramTarget = Target.ofValue(createActionMethodArgument(paramType, action), newScopeData);
			}
			else if (methodParam.annotationType() == Out.class) {
				// parameter is annotated with *only* OUT annotation
				// create the output value now AND to save the type
				paramTarget = Target.ofMethodParam(methodParam, createActionMethodArgument(paramType, action));
			}
			else {
				// parameter is annotated with any IN annotation
				// create target with NO value, as the value will be created later
				paramTarget = Target.ofMethodParam(methodParam, type -> createActionMethodArgument(type, action));
			}

			target[i + 1] = paramTarget;
		}
		return target;
	}

	/**
	 * Creates action method arguments.
	 */
	@SuppressWarnings({"unchecked", "NullArgumentToVariableArgMethod"})
	protected Object createActionMethodArgument(final Class type, final Object action) {
		try {
			if (type.getEnclosingClass() == null || Modifier.isStatic(type.getModifiers())) {
				// regular or static class
				return ClassUtil.newInstance(type);
			} else {
				// member class
				Constructor ctor = type.getDeclaredConstructor(type.getDeclaringClass());
				ctor.setAccessible(true);
				return ctor.newInstance(action);
			}
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}
	}

}

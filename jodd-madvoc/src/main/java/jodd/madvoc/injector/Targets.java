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

package jodd.madvoc.injector;

import jodd.madvoc.MadvocException;
import jodd.madvoc.ScopeType;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.MethodParam;
import jodd.madvoc.config.ScopeData;
import jodd.madvoc.meta.Out;
import jodd.util.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Targets {
	final Target[] targets;
	final ScopeData[][] scopes;     //  [scope-type][target-index]

	public Targets(final Target target, final ScopeData[] scopeData) {
		targets = new Target[]{target};

		if (scopeData == null) {
			scopes = new ScopeData[ScopeType.values().length][1];
		}
		else {
			scopes = new ScopeData[scopeData.length][1];

			for (int i = 0, scopeDataLength = scopeData.length; i < scopeDataLength; i++) {
				scopes[i][0] = scopeData[i];
			}
		}
	}

	public Targets(final ActionRuntime actionRuntime, final Object action) {
		targets = makeTargets(actionRuntime, action);
		scopes = actionRuntime.scopeData();
	}

	public boolean usesScope(final ScopeType scopeType) {
		final ScopeData[] scopeData = scopes[scopeType.value()];
		return scopeData != null;
	}

	public void forEachTarget(final Consumer<Target> targetConsumer) {
		for (Target target : targets) {
			targetConsumer.accept(target);
		}
	}

	public void forEachTargetAndInScopes(final ScopeType scopeType, final BiConsumer<Target, ScopeData.In> biConsumer) {
		final ScopeData[] scopeData = scopes[scopeType.value()];
		if (scopeData == null) {
			return;
		}
		for (int i = 0; i < targets.length; i++) {
			if (scopeData[i] == null) {
				continue;
			}
			ScopeData.In[] ins = scopeData[i].in;
			if (ins == null) {
				continue;
			}
			for (ScopeData.In in : ins) {
				biConsumer.accept(targets[i], in);
			}
		}
	}
	public void forEachTargetAndOutScopes(final ScopeType scopeType, final BiConsumer<Target, ScopeData.Out> biConsumer) {
		final ScopeData[] scopeData = scopes[scopeType.value()];
		if (scopeData == null) {
			return;
		}
		for (int i = 0; i < targets.length; i++) {
			if (scopeData[i] == null) {
				continue;
			}
			ScopeData.Out[] outs = scopeData[i].out;
			if (outs == null) {
				continue;
			}
			for (ScopeData.Out out : outs) {
				biConsumer.accept(targets[i], out);
			}
		}
	}

	/**
	 * Collects all parameters from target into an array.
	 */
	public Object[] extractParametersValues() {
		Object[] values = new Object[targets.length - 1];

		for (int i = 1; i < targets.length; i++) {
			values[i - 1] = targets[i].getValue();
		}

		return values;
	}

	/**
	 * Joins action and parameters into one array of Targets.
	 */
	protected Target[] makeTargets(final ActionRuntime actionRuntime, final Object action) {
		if (!actionRuntime.hasArguments()) {
			return new Target[] {new Target(action)};
		}

		MethodParam[] methodParams = actionRuntime.methodParams();
		Target[] target = new Target[methodParams.length + 1];

		target[0] = new Target(action);

		for (int i = 0; i < methodParams.length; i++) {
			MethodParam mp = methodParams[i];

			Class type = mp.type();

			Target t;

			if (mp.annotationType() == null) {
				// parameter is NOT annotated
				t = new Target(createActionMethodArgument(type, action));
			}
			else if (mp.annotationType() == Out.class) {
				// parameter is annotated with *only* OUT annotation
				// we need to create the output AND to save the type
				t = new Target(createActionMethodArgument(type, action), type);
			}
			else {
				// parameter is annotated with any IN annotation
				t = new Target(type) {
					@Override
					protected void createValueInstance() {
						value = createActionMethodArgument(type, action);
					}
				};
			}

			target[i + 1] = t;
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

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

package jodd.madvoc.component;

import jodd.bean.JoddBean;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.madvoc.ScopeType;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.ScopeData;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.Scope;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolver for {@link ScopeData scope data} for certain types.
 * It does not cache anything as Scope data is cached in {@link ActionRuntime}.
 * Resolving only happens during the initialization, and it might be repeated for
 * certain types (as there is no cache), but that is acceptable to reduce memory
 * usage (no cache) and several lookups (for each interceptor) during every request.
 */
public class ScopeDataResolver {

	/**
	 * Resolve scope data in given type for all scope types.
	 * Returns <code>null</code> if no scope data exist.
	 */
	public ScopeData[] resolveScopeData(final Class type) {
		final ScopeType[] allScopeTypes = ScopeType.values();

		ScopeData[] scopeData = new ScopeData[allScopeTypes.length];

		int count = 0;

		for (ScopeType scopeType : allScopeTypes) {
			ScopeData sd = inspectClassScopeData(type, scopeType);
			if (sd != null) {
				count++;
			}
			scopeData[scopeType.value()] = sd;
		}

		if (count == 0) {
			return null;
		}

		return scopeData;
	}

	/**
	 * Resolves scope data in given annotations for all scope types.
	 * Returns <code>null</code> if no scope data exist.
	 */
	public ScopeData[] resolveScopeData(final String name, final Class type, final Annotation[] annotations) {
		final ScopeType[] allScopeTypes = ScopeType.values();

		ScopeData[] scopeData = new ScopeData[allScopeTypes.length];

		int count = 0;

		for (ScopeType scopeType : allScopeTypes) {
			ScopeData sd = inspectMethodParameterScopeData(name, type, annotations, scopeType);
			if (sd != null) {
				count++;
			}
			scopeData[scopeType.value()] = sd;
		}

		if (count == 0) {
			return null;
		}

		return scopeData;
	}

	/**
	 * Scans annotation and returns type of Madvoc annotations.
	 */
	public Class<? extends Annotation> detectAnnotationType(final Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof In) {
				return annotation.annotationType();
			}
			else if (annotation instanceof Out) {
				return annotation.annotationType();
			}
		}
		return null;
	}


	// ---------------------------------------------------------------- inspect method

	/**
	 * Inspects all method parameters for scope type.
	 */
	protected ScopeData inspectMethodParameterScopeData(final String name, final Class type, final Annotation[] annotations, final ScopeType scopeType) {
		In in = null;
		Out out = null;
		Scope scope = null;

		for (Annotation annotation : annotations) {

			if (annotation instanceof In) {
				in = (In) annotation;
			}
			else if (annotation instanceof Out) {
				out = (Out) annotation;
			}
			else if (annotation instanceof Scope) {
				scope = (Scope) annotation;
			}
		}

		ScopeData sd = new ScopeData();
		int count = 0;

		if (in != null) {
			ScopeData.In scopeDataIn = inspectIn(in, scope, scopeType, name, type);
			if (scopeDataIn != null) {
				count++;
				sd.in = new ScopeData.In[] {scopeDataIn};
			}
		}
		if (out != null) {
			ScopeData.Out scopeDataOut = inspectOut(out, scope, scopeType, name, type);
			if (scopeDataOut != null) {
				count++;
				sd.out = new ScopeData.Out[] {scopeDataOut};
			}
		}

		if (count == 0) {
			return null;
		}

		return sd;
	}

	// ---------------------------------------------------------------- inspect class

	/**
	 * Saves value and property name.
	 */
	protected void saveNameTarget(final ScopeData.In ii, String value, final String propertyName) {
		value = value.trim();
		if (value.length() > 0) {
			ii.name = value;
			ii.target = propertyName;
		}
		else {
			ii.name = propertyName;
			ii.target = null;
		}
	}

	/**
	 * Saves value and property name.
	 */
	protected void saveNameTarget(final ScopeData.Out oi, String value, final String propertyName) {
		value = value.trim();
		if (value.length() > 0) {
			oi.name = value;
			oi.target = propertyName;
		}
		else {
			oi.name = propertyName;
			oi.target = null;
		}
	}

	/**
	 * Inspects single IN annotation for a property.
	 */
	protected ScopeData.In inspectIn(final In in, final Scope scope, final ScopeType matchingScopeType, final String propertyName, final Class propertyType) {
		if (in == null) {
			return null;
		}
		ScopeType inScope = ScopeType.defaultOrScopeType(scope);
		if (inScope != matchingScopeType) {
			return null;
		}
		ScopeData.In ii = new ScopeData.In();
		saveNameTarget(ii, in.value(), propertyName);
		ii.type = propertyType;
		return ii;
	}

	/**
	 * Inspects single OUT annotation for a property.
	 */
	protected ScopeData.Out inspectOut(final Out out, final Scope scope, final ScopeType matchingScopeType, final String propertyName, final Class propertyType) {
		if (out == null) {
			return null;
		}
		ScopeType outScope = ScopeType.defaultOrScopeType(scope);
		if (outScope != matchingScopeType) {
			return null;
		}
		ScopeData.Out oi = new ScopeData.Out();
		saveNameTarget(oi, out.value(), propertyName);
		oi.type = propertyType;
		return oi;
	}

	/**
	 * Inspect action for all In/Out annotations.
	 * Returns <code>null</code> if there are no In and Out data.
	 */
	protected ScopeData inspectClassScopeData(final Class actionClass, final ScopeType scopeType) {
		ClassDescriptor cd = JoddBean.defaults().getClassIntrospector().lookup(actionClass);

		PropertyDescriptor[] allProperties = cd.getAllPropertyDescriptors();

		List<ScopeData.In> listIn = new ArrayList<>(allProperties.length);
		List<ScopeData.Out> listOut = new ArrayList<>(allProperties.length);

		for (PropertyDescriptor pd : allProperties) {
			// collect annotations

			Scope scope = null;
			In in = null;
			Out out = null;

			if (pd.getFieldDescriptor() != null) {
				Field field = pd.getFieldDescriptor().getField();

				in = field.getAnnotation(In.class);
				out = field.getAnnotation(Out.class);
				scope = field.getAnnotation(Scope.class);
			}

			if (pd.getWriteMethodDescriptor() != null) {
				Method method = pd.getWriteMethodDescriptor().getMethod();
				if (in == null) {
					in = method.getAnnotation(In.class);
				}
				if (out == null) {
					out = method.getAnnotation(Out.class);
				}
				if (scope == null) {
					scope = method.getAnnotation(Scope.class);
				}
			}

			if (pd.getReadMethodDescriptor() != null) {
				Method method = pd.getReadMethodDescriptor().getMethod();
				if (in == null) {
					in = method.getAnnotation(In.class);
				}
				if (out == null) {
					out = method.getAnnotation(Out.class);
				}
				if (scope == null) {
					scope = method.getAnnotation(Scope.class);
				}
			}

			// inspect all

			ScopeData.In ii = inspectIn(in, scope, scopeType, pd.getName(), pd.getType());
			if (ii != null) {
				listIn.add(ii);
			}

			ScopeData.Out oi = inspectOut(out,scope, scopeType, pd.getName(), pd.getType());
			if (oi != null) {
				listOut.add(oi);
			}
		}

		if ((listIn.isEmpty()) && (listOut.isEmpty())) {
			return null;
		}

		ScopeData scopeData = new ScopeData();
		if (!listIn.isEmpty()) {
			scopeData.in = listIn.toArray(new ScopeData.In[listIn.size()]);
		}
		if (!listOut.isEmpty()) {
			scopeData.out = listOut.toArray(new ScopeData.Out[listOut.size()]);
		}
		return scopeData;
	}

}
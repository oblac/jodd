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
import jodd.cache.TypeCache;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.madvoc.config.InjectionPoint;
import jodd.madvoc.config.ScopeData;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.Scope;
import jodd.petite.meta.PetiteInject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Inspector for {@link ScopeData}. It works with types and method parameters.
 */
public class ScopeDataInspector {

	@PetiteInject
	protected ScopeResolver scopeResolver;

	private final ScopeData NO_SCOPE_DATA;

	public ScopeDataInspector() {
		NO_SCOPE_DATA = new ScopeData(this, null, null);
	}

	// ---------------------------------------------------------------- detection

	/**
	 * Scans annotation and returns type of Madvoc annotations.
	 */
	public Class<? extends Annotation> detectAnnotationType(final Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof In) {
				return annotation.annotationType();
			} else if (annotation instanceof Out) {
				return annotation.annotationType();
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- inspect method

	/**
	 * Inspects {@link ScopeData} parameters for given method parameter information.
	 */
	public ScopeData inspectMethodParameterScopes(final String name, final Class type, final Annotation[] annotations) {
		In in = null;
		Out out = null;
		Scope scope = null;

		for (Annotation annotation : annotations) {

			if (annotation instanceof In) {
				in = (In) annotation;
			} else if (annotation instanceof Out) {
				out = (Out) annotation;
			} else if (annotation instanceof Scope) {
				scope = (Scope) annotation;
			}
		}

		int count = 0;

		InjectionPoint[] ins = null;
		InjectionPoint[] outs = null;

		if (in != null) {
			InjectionPoint scopeDataIn = inspectIn(in, scope, name, type);
			if (scopeDataIn != null) {
				count++;
				ins = new InjectionPoint[]{scopeDataIn};
			}
		}
		if (out != null) {
			InjectionPoint scopeDataOut = inspectOut(out, scope, name, type);
			if (scopeDataOut != null) {
				count++;
				outs = new InjectionPoint[]{scopeDataOut};
			}
		}

		if (count == 0) {
			return NO_SCOPE_DATA;
		}

		return new ScopeData(this, ins, outs);
	}

	// ---------------------------------------------------------------- inspect class

	/**
	 * Inspects single IN annotation for a property.
	 */
	protected InjectionPoint inspectIn(final In in, final Scope scope, final String propertyName, final Class propertyType) {
		if (in == null) {
			return null;
		}

		final String value = in.value().trim();
		final String name, targetName;

		if (value.length() > 0) {
			name = value;
			targetName = propertyName;
		} else {
			name = propertyName;
			targetName = null;
		}
		return new InjectionPoint(propertyType, name, targetName, scopeResolver.defaultOrScopeType(scope));
	}

	/**
	 * Inspects single OUT annotation for a property.
	 */
	protected InjectionPoint inspectOut(final Out out, final Scope scope, final String propertyName, final Class propertyType) {
		if (out == null) {
			return null;
		}

		final String value = out.value().trim();
		final String name, targetName;

		if (value.length() > 0) {
			name = value;
			targetName = propertyName;
		} else {
			name = propertyName;
			targetName = null;
		}

		return new InjectionPoint(propertyType, name, targetName, scopeResolver.defaultOrScopeType(scope));
	}


	private TypeCache<ScopeData> scopeDataTypeCache = TypeCache.create(TypeCache.Implementation.MAP);

	/**
	 * Cached version of {@link #inspectClassScopes(Class)}. Use it in runtime when
	 * configuration is not known yet.
	 */
	public ScopeData inspectClassScopesWithCache(final Class actionClass) {
		return scopeDataTypeCache.get(actionClass, () -> inspectClassScopes(actionClass));
	}

	/**
	 * Inspects {@link ScopeData} for given class. The results are not cached, so it should
	 * be used only dyring configuration-time.
	 * For cached version, use {@link #inspectClassScopesWithCache(Class)}.
	 */
	public ScopeData inspectClassScopes(final Class actionClass) {
		ClassDescriptor cd = JoddBean.defaults().getClassIntrospector().lookup(actionClass);

		PropertyDescriptor[] allProperties = cd.getAllPropertyDescriptors();

		List<InjectionPoint> listIn = new ArrayList<>(allProperties.length);
		List<InjectionPoint> listOut = new ArrayList<>(allProperties.length);

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

			InjectionPoint ii = inspectIn(in, scope, pd.getName(), pd.getType());
			if (ii != null) {
				listIn.add(ii);
			}

			InjectionPoint oi = inspectOut(out, scope, pd.getName(), pd.getType());
			if (oi != null) {
				listOut.add(oi);
			}
		}

		if ((listIn.isEmpty()) && (listOut.isEmpty())) {
			return NO_SCOPE_DATA;
		}

		InjectionPoint[] in = null;
		InjectionPoint[] out = null;

		if (!listIn.isEmpty()) {
			in = listIn.toArray(new InjectionPoint[0]);
		}
		if (!listOut.isEmpty()) {
			out = listOut.toArray(new InjectionPoint[0]);
		}

		return new ScopeData(this, in, out);
	}

}

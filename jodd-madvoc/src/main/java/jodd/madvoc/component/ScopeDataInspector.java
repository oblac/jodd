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

import jodd.cache.TypeCache;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.PropertyDescriptor;
import jodd.madvoc.MadvocException;
import jodd.madvoc.config.InjectionPoint;
import jodd.madvoc.config.ScopeData;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.Scope;
import jodd.madvoc.scope.MadvocScope;
import jodd.petite.meta.PetiteInject;
import jodd.util.StringUtil;

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
		for (final Annotation annotation : annotations) {
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
	 * Inspects {@link ScopeData} parameters for given method parameter information.
	 */
	public ScopeData inspectMethodParameterScopes(final String name, final Class type, final Annotation[] annotations) {
		In in = null;
		Out out = null;

		for (final Annotation annotation : annotations) {
			if (annotation instanceof In) {
				in = (In) annotation;
			} else if (annotation instanceof Out) {
				out = (Out) annotation;
			}
		}

		final Class<? extends MadvocScope> scope = resolveScopeClassFromAnnotations(annotations);


		int count = 0;

		InjectionPoint[] ins = null;
		InjectionPoint[] outs = null;

		if (in != null) {
			final InjectionPoint scopeDataIn = buildInjectionPoint(in.value(), name, type, scope);
			if (scopeDataIn != null) {
				count++;
				ins = new InjectionPoint[]{scopeDataIn};
			}
		}
		if (out != null) {
			final InjectionPoint scopeDataOut = buildInjectionPoint(out.value(), name, type, scope);
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

	protected Class<? extends MadvocScope> resolveScopeClassFromAnnotations(final Annotation[] annotations) {
		Class<? extends MadvocScope> scope = null;

		for (final Annotation annotation : annotations) {
			if (annotation instanceof Scope) {
				if (scope != null) {
					throw new MadvocException("Scope already defined: " + scope);
				}
				scope = ((Scope) annotation).value();
			} else {
				Class<? extends MadvocScope> annotationScope = null;

				final Annotation[] annotationAnnotations = annotation.annotationType().getAnnotations();
				for (final Annotation innerAnnotation : annotationAnnotations) {
					if (innerAnnotation instanceof Scope) {
						annotationScope = ((Scope) innerAnnotation).value();
					}
				}

				if (annotationScope != null) {
					if (scope == null) {
						scope = annotationScope;
					}
					else {
						throw new MadvocException("Scope already defined: " + scope);
					}
				}
			}
		}

		return scope;
	}

	// ---------------------------------------------------------------- inspect class

	/**
	 * Builds injection point.
	 */
	protected InjectionPoint buildInjectionPoint(
			final String annotationValue,
			final String propertyName,
			final Class propertyType,
			final Class<? extends MadvocScope> scope) {

		final String value = annotationValue.trim();
		final String name, targetName;

		if (StringUtil.isNotBlank(value)) {
			name = value;
			targetName = propertyName;
		}
		else {
			name = propertyName;
			targetName = null;
		}
		return new InjectionPoint(propertyType, name, targetName, scopeResolver.defaultOrScopeType(scope));
	}

	private TypeCache<ScopeData> scopeDataTypeCache = TypeCache.createDefault();

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
		ClassDescriptor cd = ClassIntrospector.get().lookup(actionClass);

		PropertyDescriptor[] allProperties = cd.getAllPropertyDescriptors();

		List<InjectionPoint> listIn = new ArrayList<>(allProperties.length);
		List<InjectionPoint> listOut = new ArrayList<>(allProperties.length);

		for (PropertyDescriptor pd : allProperties) {
			// collect annotations

			Class<? extends MadvocScope> scope = null;
			In in = null;
			Out out = null;

			if (pd.getFieldDescriptor() != null) {
				Field field = pd.getFieldDescriptor().getField();

				in = field.getAnnotation(In.class);
				out = field.getAnnotation(Out.class);
				scope = resolveScopeClassFromAnnotations(field.getAnnotations());
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
					scope = resolveScopeClassFromAnnotations(method.getAnnotations());
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
					scope = resolveScopeClassFromAnnotations(method.getAnnotations());
				}
			}

			// inspect all

			final InjectionPoint ii = in == null ? null : buildInjectionPoint(in.value(), pd.getName(), pd.getType(), scope);
			if (ii != null) {
				listIn.add(ii);
			}

			final InjectionPoint oi = out == null ? null : buildInjectionPoint(out.value(), pd.getName(), pd.getType(), scope);
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

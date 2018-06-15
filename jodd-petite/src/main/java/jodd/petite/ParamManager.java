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

package jodd.petite;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.petite.def.ValueInjectionPoint;
import jodd.petite.meta.PetiteValue;
import jodd.template.ContextTemplateParser;
import jodd.template.MapTemplateParser;
import jodd.util.PropertiesUtil;
import jodd.util.StringPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parameters storage and resolver. Parameters are injected into beans.
 */
public class ParamManager {

	protected final Map<String, Object> params;
	protected final ContextTemplateParser contextTemplateParser;

	public ParamManager() {
		params = new HashMap<>();
		contextTemplateParser =  new MapTemplateParser().of(params);
	}

	/**
	 * Adds a parameter.
	 */
	public void put(final String name, final Object value) {
		params.put(name, value);
	}

	/**
	 * Returns parameter for given name or <code>null</code>
	 * if not found.
	 */
	public Object get(final String name) {
		return params.get(name);
	}

	public String parseKeyTemplate(final String input) {
		return contextTemplateParser.parse(input);
	}

	/**
	 * Returns an array of param keys that belongs to provided bean.
	 * Optionally resolves the value of returned parameters.
	 */
	public String[] filterParametersForBeanName(String beanName, final boolean resolveReferenceParams) {
		beanName = beanName + '.';

		List<String> list = new ArrayList<>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (!key.startsWith(beanName)) {
				continue;
			}
			list.add(key);
			if (!resolveReferenceParams) {
				continue;
			}
			// resolve all references
			String value = PropertiesUtil.resolveProperty(params, key);
			entry.setValue(value);
		}
		if (list.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		} else {
			return list.toArray(new String[0]);
		}
	}

	public ValueInjectionPoint[] resolveParamInjectionPoints(final Class type) {
		final ClassDescriptor cd = ClassIntrospector.get().lookup(type);

		final List<ValueInjectionPoint> valueInjectionPointList = new ArrayList<>();

		for (final PropertyDescriptor pd : cd.getAllPropertyDescriptors()) {
			final FieldDescriptor fd = pd.getFieldDescriptor();

			if (fd != null) {
				final PetiteValue petiteValue = fd.getField().getAnnotation(PetiteValue.class);

				if (petiteValue != null) {
					valueInjectionPointList.add(new ValueInjectionPoint(pd.getName(), petiteValue.value()));
					continue;
				}
			}

			MethodDescriptor md = pd.getWriteMethodDescriptor();
			if (md != null) {
				final PetiteValue petiteValue = md.getMethod().getAnnotation(PetiteValue.class);

				if (petiteValue != null) {
					valueInjectionPointList.add(new ValueInjectionPoint(pd.getName(), petiteValue.value()));
					continue;
				}
			}
		}

		return valueInjectionPointList.toArray(new ValueInjectionPoint[0]);
	}

}
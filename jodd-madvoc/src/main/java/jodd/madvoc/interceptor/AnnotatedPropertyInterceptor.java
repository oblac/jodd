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

package jodd.madvoc.interceptor;

import jodd.introspector.PropertyDescriptor;
import jodd.madvoc.ActionRequest;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Performs some operation on all annotated properties. Helpful with injection of
 * application context into action objects.
 */
public abstract class AnnotatedPropertyInterceptor extends BaseActionInterceptor {

	protected final Class<Annotation> annotations;

	protected AnnotatedPropertyInterceptor(Class<Annotation> annotations) {
		this.annotations = annotations;
	}

	public Object intercept(ActionRequest actionRequest) throws Exception {
		Object action = actionRequest.getAction();
		Class actionType = action.getClass();

		PropertyDescriptor[] allProperties = lookupAnnotatedProperties(actionType);

		for (PropertyDescriptor propertyDescriptor : allProperties) {
			onAnnotatedProperty(actionRequest, propertyDescriptor);
		}
		return actionRequest.invoke();
	}

	/**
	 * Invoked on all annotated properties.
	 */
	protected abstract void onAnnotatedProperty(ActionRequest actionRequest, PropertyDescriptor propertyDescriptor);


	// ---------------------------------------------------------------- cache and lookup

	protected Map<Class<?>, PropertyDescriptor[]> annotatedProperties = new HashMap<>();
	protected static final PropertyDescriptor[] EMPTY = new PropertyDescriptor[0];

	/**
	 * Lookups for annotated properties. Caches all annotated properties on the first
	 * action class scan. 
	 */
	protected PropertyDescriptor[] lookupAnnotatedProperties(Class type) {
		PropertyDescriptor[] properties = annotatedProperties.get(type);

		if (properties != null) {
			return properties;
		}

		ClassDescriptor cd = ClassIntrospector.lookup(type);
		PropertyDescriptor[] allProperties = cd.getAllPropertyDescriptors();

		List<PropertyDescriptor> list = new ArrayList<>();

		for (PropertyDescriptor propertyDescriptor : allProperties) {

			Annotation ann = null;

			if (propertyDescriptor.getFieldDescriptor() != null) {
				ann = propertyDescriptor.getFieldDescriptor().getField().getAnnotation(annotations);
			}
			if (ann == null && propertyDescriptor.getWriteMethodDescriptor() != null) {
				ann = propertyDescriptor.getWriteMethodDescriptor().getMethod().getAnnotation(annotations);
			}
			if (ann == null && propertyDescriptor.getReadMethodDescriptor() != null) {
				ann = propertyDescriptor.getReadMethodDescriptor().getMethod().getAnnotation(annotations);
			}

			if (ann != null) {
				list.add(propertyDescriptor);
			}
		}

		if (list.isEmpty()) {
			properties = EMPTY;
		} else {
			properties = list.toArray(new PropertyDescriptor[list.size()]);
		}

		annotatedProperties.put(type, properties);

		return properties;
	}

}
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

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.PropertyDescriptor;
import jodd.madvoc.ActionRequest;
import jodd.util.TypeCache;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs some operation on all annotated properties. Helpful with injection of
 * application context into action objects.
 */
public abstract class AnnotatedPropertyInterceptor implements ActionInterceptor {

	protected final Class<Annotation> annotations;

	protected AnnotatedPropertyInterceptor(final Class<Annotation> annotations) {
		this.annotations = annotations;
	}

	@Override
	public Object intercept(final ActionRequest actionRequest) throws Exception {
		final Object action = actionRequest.getAction();
		final Class actionType = action.getClass();

		final PropertyDescriptor[] allProperties = lookupAnnotatedProperties(actionType);

		for (final PropertyDescriptor propertyDescriptor : allProperties) {
			onAnnotatedProperty(actionRequest, propertyDescriptor);
		}
		return actionRequest.invoke();
	}

	/**
	 * Invoked on all annotated properties.
	 */
	protected abstract void onAnnotatedProperty(ActionRequest actionRequest, PropertyDescriptor propertyDescriptor);


	// ---------------------------------------------------------------- cache and lookup

	protected TypeCache<PropertyDescriptor[]> annotatedProperties = TypeCache.createDefault();
	protected static final PropertyDescriptor[] EMPTY = new PropertyDescriptor[0];

	/**
	 * Lookups for annotated properties. Caches all annotated properties on the first
	 * action class scan. 
	 */
	protected PropertyDescriptor[] lookupAnnotatedProperties(final Class type) {
		PropertyDescriptor[] properties = annotatedProperties.get(type);

		if (properties != null) {
			return properties;
		}

		final ClassDescriptor cd = ClassIntrospector.get().lookup(type);
		final PropertyDescriptor[] allProperties = cd.getAllPropertyDescriptors();

		final List<PropertyDescriptor> list = new ArrayList<>();

		for (final PropertyDescriptor propertyDescriptor : allProperties) {

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
			properties = list.toArray(new PropertyDescriptor[0]);
		}

		annotatedProperties.put(type, properties);

		return properties;
	}

}

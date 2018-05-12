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

package jodd.json;

import jodd.introspector.Getter;
import jodd.introspector.PropertyDescriptor;

/**
 * Bean visitor that serializes properties of a bean.
 * It analyzes the rules for inclusion/exclusion of a property.
 */
public class BeanSerializer extends TypeJsonVisitor {

	protected final Object source;

	public BeanSerializer(final JsonContext jsonContext, final Object bean) {
		super(jsonContext, bean.getClass());

		this.source = bean;
	}

	/**
	 * Serializes a bean.
	 */
	public void serialize() {
		visit();
	}

	/**
	 * Reads property value and {@link #onSerializableProperty(String, Class, Object) serializes it}.
	 */
	@Override
	protected final void onSerializableProperty(String propertyName, final PropertyDescriptor propertyDescriptor) {
		final Object value;

		if (propertyDescriptor == null) {
			// metadata - classname
			value = source.getClass().getName();
		} else {
			value = readProperty(source, propertyDescriptor);

			if ((value == null) && jsonContext.isExcludeNulls()) {
				return;
			}

			// change name for properties

			propertyName = typeData.resolveJsonName(propertyName);
		}

		onSerializableProperty(
				propertyName,
				propertyDescriptor == null ? null : propertyDescriptor.getType(),
				value);
	}

	/**
	 * Invoked on serializable properties, that have passed all the rules.
	 * Property type is <code>null</code> for metadata class name property.
	 */
	protected void onSerializableProperty(final String propertyName, final Class propertyType, final Object value) {
		jsonContext.pushName(propertyName, count > 0);

		jsonContext.serialize(value);

		if (jsonContext.isNamePopped()) {
			count++;
		}
	}

	/**
	 * Reads property using property descriptor.
	 */
	private Object readProperty(final Object source, final PropertyDescriptor propertyDescriptor) {
		Getter getter = propertyDescriptor.getGetter(declared);

		if (getter != null) {
			try {
				return getter.invokeGetter(source);
			}
			catch (Exception ex) {
				throw new JsonException(ex);
			}
		}

		return null;
	}

}
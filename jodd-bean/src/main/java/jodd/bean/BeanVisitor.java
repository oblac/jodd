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

package jodd.bean;

import jodd.inex.InExRuleMatcher;
import jodd.inex.InExRules;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static jodd.util.StringPool.LEFT_SQ_BRACKET;
import static jodd.util.StringPool.RIGHT_SQ_BRACKET;

/**
 * Visitor for bean properties. It extracts properties names
 * from the source bean and then visits one by one.
 *
 * @see BeanVisitorImplBase
 */
public abstract class BeanVisitor implements InExRuleMatcher<String, String> {

	/**
	 * Source bean.
	 */
	protected Object source;
	/**
	 * Include/exclude rules.
	 */
	protected InExRules<String, String, String> rules = new InExRules<>(this);
	/**
	 * Flag for enabling declared properties, or just public ones.
	 */
	protected boolean declared;
	/**
	 * Defines if null values should be ignored.
	 */
	protected boolean ignoreNullValues;
	/**
	 * Defines if fields should be included.
	 */
	protected boolean includeFields;
	/**
	 * Initial matching mode.
	 */
	protected boolean blacklist = true;
	/**
	 * Indicates the the source is a Map.
	 */
	protected boolean isSourceMap = false;

	// ---------------------------------------------------------------- util

	/**
	 * Returns all bean property names.
	 */
	protected String[] getAllBeanPropertyNames(final Class type, final boolean declared) {
		ClassDescriptor classDescriptor = ClassIntrospector.get().lookup(type);

		PropertyDescriptor[] propertyDescriptors = classDescriptor.getAllPropertyDescriptors();

		ArrayList<String> names = new ArrayList<>(propertyDescriptors.length);

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			MethodDescriptor getter = propertyDescriptor.getReadMethodDescriptor();
			if (getter != null) {
				if (getter.matchDeclared(declared)) {
					names.add(propertyDescriptor.getName());
				}
			}
			else if (includeFields) {
				FieldDescriptor field = propertyDescriptor.getFieldDescriptor();
				if (field != null) {
					if (field.matchDeclared(declared)) {
						names.add(field.getName());
					}
				}
			}
		}

		return names.toArray(new String[0]);
	}

	/**
	 * Returns an array of bean properties. If bean is a <code>Map</code>,
	 * all its keys will be returned.
	 */
	protected String[] resolveProperties(final Object bean, final boolean declared) {
		String[] properties;

		if (bean instanceof Map) {
			Set keys = ((Map) bean).keySet();

			properties = new String[keys.size()];
			int ndx = 0;
			for (Object key : keys) {
				properties[ndx] = key.toString();
				ndx++;
			}
		} else {
			properties = getAllBeanPropertyNames(bean.getClass(), declared);
		}

		return properties;
	}

	/**
	 * Starts visiting properties.
	 */
	public void visit() {
		String[] properties = resolveProperties(source, declared);

		for (String name : properties) {
			if (name == null) {
				continue;
			}

			if (!rules.match(name, blacklist)) {
				continue;
			}

			Object value;

			String propertyName = name;

			if (isSourceMap) {
				propertyName = LEFT_SQ_BRACKET + name + RIGHT_SQ_BRACKET;
			}

			if (declared) {
				value = BeanUtil.declared.getProperty(source, propertyName);
			} else {
				value = BeanUtil.pojo.getProperty(source, propertyName);
			}

			if (value == null && ignoreNullValues) {
				continue;
			}

			visitProperty(name, value);
		}
	}

	/**
	 * Invoked for each visited property. Returns <code>true</code> if
	 * visiting should continue, otherwise <code>false</code> to stop.
	 */
	protected abstract boolean visitProperty(String name, Object value);

	/**
	 * Compares property name to the rules.
	 */
	@Override
	public boolean accept(final String propertyName, final String rule, final boolean include) {
		return propertyName.equals(rule);
	}
}
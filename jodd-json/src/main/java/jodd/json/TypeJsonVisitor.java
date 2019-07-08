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

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.Getter;
import jodd.introspector.PropertyDescriptor;
import jodd.json.meta.JsonAnnotationManager;
import jodd.json.meta.TypeData;

import java.lang.reflect.Modifier;

/**
 * Type's property visitor that follows JSON include/excludes rules.
 */
public abstract class TypeJsonVisitor {

	protected final JsonContext jsonContext;
	protected final boolean declared;
	protected final String classMetadataName;
	protected final Class type;

	protected int count;
	protected final TypeData typeData;

	public TypeJsonVisitor(final JsonContext jsonContext, final Class type) {
		this.jsonContext = jsonContext;
		this.count = 0;
		this.declared = false;
		this.classMetadataName = jsonContext.jsonSerializer.classMetadataName;

		this.type = type;

		this.typeData = JsonAnnotationManager.get().lookupTypeData(type);
	}

	/**
	 * Visits a type.
	 */
	public void visit() {
		ClassDescriptor classDescriptor = ClassIntrospector.get().lookup(type);

		if (classMetadataName != null) {
			// process first 'meta' fields 'class'
			onProperty(classMetadataName, null, false);
		}

		PropertyDescriptor[] propertyDescriptors = classDescriptor.getAllPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

			Getter getter = propertyDescriptor.getGetter(declared);
			if (getter != null) {
				String propertyName = propertyDescriptor.getName();

				boolean isTransient = false;
				// check for transient flag
				FieldDescriptor fieldDescriptor = propertyDescriptor.getFieldDescriptor();

				if (fieldDescriptor != null) {
					isTransient = Modifier.isTransient(fieldDescriptor.getField().getModifiers());
				}

				onProperty(propertyName, propertyDescriptor, isTransient);
			}
		}
	}

	/**
	 * Invoked on each property. Properties are getting matched against the rules.
	 * If property passes all the rules, it will be processed in
	 * {@link #onSerializableProperty(String, jodd.introspector.PropertyDescriptor)}.
	 */
	protected void onProperty(
		String propertyName,
		final PropertyDescriptor propertyDescriptor,
		final boolean isTransient) {

		Class propertyType = propertyDescriptor == null ?  null : propertyDescriptor.getType();

		Path currentPath = jsonContext.path;

		currentPath.push(propertyName);

		// change name for properties

		if (propertyType != null) {
			propertyName = typeData.resolveJsonName(propertyName);
		}

		// determine if name should be included/excluded

		boolean include = !typeData.strict;

		// + don't include transient fields

		if (isTransient) {
			include = false;
		}

		// + all collections are not serialized by default

		include = jsonContext.matchIgnoredPropertyTypes(propertyType, true, include);

		// + annotations

		include = typeData.rules.apply(propertyName, true, include);

		// + path queries: excludes/includes

		include = jsonContext.matchPathToQueries(include);

		// done

		if (!include) {
			currentPath.pop();
			return;
		}

		onSerializableProperty(propertyName, propertyDescriptor);

		currentPath.pop();
	}

	/**
	 * Invoked on serializable properties, that have passed all the rules.
	 * Property descriptor may be <code>null</code> in special case when
	 * class meta data name is used.
	 */
	protected abstract void onSerializableProperty(String propertyName, PropertyDescriptor propertyDescriptor);

}
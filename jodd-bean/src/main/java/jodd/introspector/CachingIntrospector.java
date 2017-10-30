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

package jodd.introspector;

import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link jodd.introspector.ClassIntrospector introspector} that caches all class descriptors.
 * It can examine either <b>accessible</b> or <b>supported</b> fields/methods/constructors.
 * <p>
 * It simply caches <b>all</b> class descriptors.
 */
public class CachingIntrospector implements ClassIntrospector {

	protected final Map<Class, ClassDescriptor> cache;
	protected final boolean scanAccessible;
	protected final boolean enhancedProperties;
	protected final boolean includeFieldsAsProperties;
	protected final String[] propertyFieldPrefix;

	/**
	 * Default constructor.
	 */
	public CachingIntrospector() {
		this(true, true, true, null);
	}

	/**
	 * Creates new caching {@link ClassIntrospector}. It may scan
	 * <b>accessible</b> or <b>supported</b> fields, methods or
	 * constructors.
	 */
	public CachingIntrospector(boolean scanAccessible, boolean enhancedProperties, boolean includeFieldsAsProperties, String[] propertyFieldPrefix) {
		this.cache = new HashMap<>();
		this.scanAccessible = scanAccessible;
		this.enhancedProperties = enhancedProperties;
		this.includeFieldsAsProperties = includeFieldsAsProperties;
		this.propertyFieldPrefix = propertyFieldPrefix;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassDescriptor lookup(Class type) {
		ClassDescriptor cd = cache.get(type);
		if (cd != null) {
			cd.increaseUsageCount();
			return cd;
		}
		cd = describeClass(type);
		cache.put(type, cd);
		return cd;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassDescriptor register(Class type) {
		ClassDescriptor cd = describeClass(type);
		cache.put(type, cd);
		return cd;
	}

	/**
	 * Describes a class by creating a new instance of {@link ClassDescriptor}
	 * that examines all accessible methods and fields.
	 */
	protected ClassDescriptor describeClass(Class type) {
		return new ClassDescriptor(type, scanAccessible, enhancedProperties, includeFieldsAsProperties, propertyFieldPrefix);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		cache.clear();
	}

}
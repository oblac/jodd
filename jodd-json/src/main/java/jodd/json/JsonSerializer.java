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

import jodd.util.ArraysUtil;
import jodd.util.InExRules;
import jodd.util.UnsafeUtil;
import jodd.util.buffer.FastCharBuffer;

import java.util.HashMap;
import java.util.Map;

import static jodd.json.JoddJson.DEFAULT_CLASS_METADATA_NAME;

/**
 * JSON serializer.
 */
public class JsonSerializer {

	/**
	 * Static ctor.
	 */
	public static JsonSerializer create() {
		return new JsonSerializer();
	}

	// ---------------------------------------------------------------- config

	protected Map<Path, TypeJsonSerializer> pathSerializersMap;
	protected TypeJsonSerializerMap typeSerializersMap;

	protected InExRules<Path, PathQuery> rules = new InExRules<Path, PathQuery>() {
		@Override
		public boolean accept(Path value, PathQuery rule, boolean include) {
			return rule.matches(value);
		}
	};

	protected String classMetadataName = JoddJson.classMetadataName;
	protected boolean strictStringEncoding = JoddJson.strictStringEncoding;
	protected boolean deep = JoddJson.deepSerialization;
	protected Class[] excludedTypes = null;
	protected String[] excludedTypeNames = null;
	protected boolean excludeNulls = false;

	/**
	 * Defines custom {@link jodd.json.TypeJsonSerializer} for given path.
	 */
	public JsonSerializer withSerializer(String pathString, TypeJsonSerializer typeJsonSerializer) {
		if (pathSerializersMap == null) {
			pathSerializersMap = new HashMap<>();
		}

		pathSerializersMap.put(Path.parse(pathString), typeJsonSerializer);

		return this;
	}

	/**
	 * Defines custom {@link jodd.json.TypeJsonSerializer} for given type.
	 */
	public JsonSerializer withSerializer(Class type, TypeJsonSerializer typeJsonSerializer) {
		if (typeSerializersMap == null) {
			typeSerializersMap = new TypeJsonSerializerMap(JoddJson.defaultSerializers);
		}

		typeSerializersMap.register(type, typeJsonSerializer);

		return this;
	}

	/**
	 * Adds include path query.
	 */
	public JsonSerializer include(String include) {
		rules.include(new PathQuery(include, true));

		return this;
	}

	/**
	 * Adds a list of included path queries.
	 */
	public JsonSerializer include(String... includes) {
		for (String include : includes) {
			include(include);
		}
		return this;
	}

	/**
	 * Adds exclude path query.
	 */
	public JsonSerializer exclude(String exclude) {
		rules.exclude(new PathQuery(exclude, false));

		return this;
	}

	/**
	 * Adds a list of excluded path queries.
	 */
	public JsonSerializer exclude(String... excludes) {
		for (String exclude : excludes) {
			exclude(exclude);
		}
		return this;
	}

	/**
	 * Adds excludes with optional parent including. When parents are included,
	 * for each exclude query its parent will be included.
	 * For example, exclude of 'aaa.bb.ccc' would include it's parent: 'aaa.bb'.
	 */
	public JsonSerializer exclude(boolean includeParent, String... excludes) {
		for (String exclude : excludes) {
			if (includeParent) {
				int dotIndex = exclude.lastIndexOf('.');
				if (dotIndex != -1) {
					PathQuery pathQuery = new PathQuery(exclude.substring(0, dotIndex), true);

					rules.include(pathQuery);
				}
			}

			PathQuery pathQuery = new PathQuery(exclude, false);

			rules.exclude(pathQuery);
		}

		return this;
	}

	/**
	 * Sets local class meta-data name.
	 */
	public JsonSerializer setClassMetadataName(String name) {
		classMetadataName = name;
		return this;
	}

	/**
	 * Sets local class meta-data name.
	 */
	public JsonSerializer withClassMetadata(boolean useMetadata) {
		if (useMetadata) {
			classMetadataName = DEFAULT_CLASS_METADATA_NAME;
		}
		else {
			classMetadataName = null;
		}
		return this;
	}

	/**
	 * Defines if collections should be followed, i.e. to perform
	 * deep serialization.
	 */
	public JsonSerializer deep(boolean includeCollections) {
		this.deep = includeCollections;
		return this;
	}

	/**
	 * Excludes type names. You can disable
	 * serialization of properties that are of some type.
	 * For example, you can disable properties of <code>InputStream</code>.
	 * You can use wildcards to describe type names.
	 */
	public JsonSerializer excludeTypes(String... typeNames) {
		if (excludedTypeNames == null) {
			excludedTypeNames = typeNames;
		} else {
			excludedTypeNames = ArraysUtil.join(excludedTypeNames, typeNames);
		}
		return this;
	}

	/**
	 * Excludes types. Supports interfaces and subclasses as well.
	 */
	public JsonSerializer excludeTypes(Class... types) {
		if (excludedTypes == null) {
			excludedTypes = types;
		} else {
			excludedTypes = ArraysUtil.join(excludedTypes, types);
		}
		return this;
	}

	/**
	 * Excludes <code>null</code> values while serializing.
	 */
	public JsonSerializer excludeNulls(boolean excludeNulls) {
		this.excludeNulls = excludeNulls;
		return this;
	}

	/**
	 * Specifies strict string encoding.
	 * @see JoddJson#strictStringEncoding
	 */
	public JsonSerializer strictStringEncoding(boolean strictStringEncoding) {
		this.strictStringEncoding = strictStringEncoding;
		return this;
	}

	// ---------------------------------------------------------------- serialize

	/**
	 * Serializes object into provided appendable.
	 */
	public void serialize(Object source, Appendable target) {
		JsonContext jsonContext = createJsonContext(target);

		jsonContext.serialize(source);
	}

	/**
	 * Serializes object into source.
	 */
	public String serialize(Object source) {
		FastCharBuffer fastCharBuffer = new FastCharBuffer();

		serialize(source, fastCharBuffer);

		return UnsafeUtil.createString(fastCharBuffer.toArray());
	}

	// ---------------------------------------------------------------- json context

	/**
	 * Creates new JSON context.
	 */
	public JsonContext createJsonContext(Appendable appendable) {
		return new JsonContext(this, appendable, excludeNulls, strictStringEncoding);
	}
}
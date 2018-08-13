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

import jodd.buffer.FastCharBuffer;
import jodd.inex.InExRules;
import jodd.util.ArraysUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JSON serializer.
 * @see PrettyJsonSerializer
 */
public class JsonSerializer {

	public static class Defaults {

		public static final String DEFAULT_CLASS_METADATA_NAME = "__class";

		/**
		 * Defines default behavior of a {@link jodd.json.JsonSerializer}.
		 * If set to <code>true</code>, objects will be serialized
		 * deep, so all collections and arrays will get serialized.
		 */
		public static boolean deepSerialization = false;
		/**
		 * List of excluded types for serialization.
		 */
		public static Class[] excludedTypes = null;
		/**
		 * List of excluded types names for serialization. Type name
		 * can contain wildcards (<code>*</code> and <code>?</code>).
		 */
		public static String[] excludedTypeNames = null;

		/**
		 * Sets the strict JSON encoding.
		 * JSON specification specifies that certain characters should be
		 * escaped (see: http://json.org/). However, in the real world, not all
		 * needs to be escaped: especially the 'solidus' character (/). If this one
		 * is escaped, many things can go wrong, from URLs to Base64 encodings.
		 * This flag controls the behavior of strict encoding. By default, the
		 * strict encoding is set to {@code false}.
		 */
		public static boolean strictStringEncoding = false;

		/**
		 * Specifies if 'class' metadata is used and its value. When set, class metadata
		 * is used by {@link jodd.json.JsonSerializer} and all objects
		 * will have additional field with the class type in the resulting JSON.
		 * {@link jodd.json.JsonParser} will also consider this flag to build
		 * correct object type. If <code>null</code>, class information is not used.
		 */
		public static String classMetadataName = null;
	}

	/**
	 * Static ctor.
	 */
	public static JsonSerializer create() {
		return new JsonSerializer();
	}

	/**
	 * Static ctor for {@link PrettyJsonSerializer}.
	 */
	public static PrettyJsonSerializer createPrettyOne() {
		return new PrettyJsonSerializer();
	}

	// ---------------------------------------------------------------- config

	protected Map<Path, TypeJsonSerializer> pathSerializersMap;
	protected TypeJsonSerializerMap typeSerializersMap;

	protected InExRules<Path, PathQuery, PathQuery> rules = new InExRules<Path, PathQuery, PathQuery>() {
		@Override
		public boolean accept(final Path value, final PathQuery rule, final boolean include) {
			return rule.matches(value);
		}
	};

	protected String classMetadataName = Defaults.classMetadataName;
	protected boolean strictStringEncoding = Defaults.strictStringEncoding;
	protected boolean deep = Defaults.deepSerialization;
	protected Class[] excludedTypes = Defaults.excludedTypes;
	protected String[] excludedTypeNames = Defaults.excludedTypeNames;
	protected boolean excludeNulls = false;
	protected boolean excludeEmpty = false;
	protected Function<Object, TypeJsonSerializer> serializerResolver = null;

	/**
	 * Defines custom {@link jodd.json.TypeJsonSerializer} for given path.
	 */
	public JsonSerializer withSerializer(final String pathString, final TypeJsonSerializer typeJsonSerializer) {
		if (pathSerializersMap == null) {
			pathSerializersMap = new HashMap<>();
		}

		pathSerializersMap.put(Path.parse(pathString), typeJsonSerializer);

		return this;
	}

	/**
	 * Defines custom {@link jodd.json.TypeJsonSerializer} for given type.
	 */
	public JsonSerializer withSerializer(final Class type, final TypeJsonSerializer typeJsonSerializer) {
		if (typeSerializersMap == null) {
			typeSerializersMap = new TypeJsonSerializerMap(TypeJsonSerializerMap.get());
		}

		typeSerializersMap.register(type, typeJsonSerializer);

		return this;
	}

	/**
	 * Adds include path query.
	 */
	public JsonSerializer include(final String include) {
		rules.include(new PathQuery(include, true));

		return this;
	}

	/**
	 * Adds a list of included path queries.
	 */
	public JsonSerializer include(final String... includes) {
		for (String include : includes) {
			include(include);
		}
		return this;
	}

	/**
	 * Adds exclude path query.
	 */
	public JsonSerializer exclude(final String exclude) {
		rules.exclude(new PathQuery(exclude, false));

		return this;
	}

	/**
	 * Adds a list of excluded path queries.
	 */
	public JsonSerializer exclude(final String... excludes) {
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
	public JsonSerializer exclude(final boolean includeParent, final String... excludes) {
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
	public JsonSerializer setClassMetadataName(final String name) {
		classMetadataName = name;
		return this;
	}

	/**
	 * Sets local class meta-data name.
	 */
	public JsonSerializer withClassMetadata(final boolean useMetadata) {
		if (useMetadata) {
			classMetadataName = Defaults.DEFAULT_CLASS_METADATA_NAME;
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
	public JsonSerializer deep(final boolean includeCollections) {
		this.deep = includeCollections;
		return this;
	}

	/**
	 * Excludes type names. You can disable
	 * serialization of properties that are of some type.
	 * For example, you can disable properties of <code>InputStream</code>.
	 * You can use wildcards to describe type names.
	 */
	public JsonSerializer excludeTypes(final String... typeNames) {
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
	public JsonSerializer excludeTypes(final Class... types) {
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
	public JsonSerializer excludeNulls(final boolean excludeNulls) {
		this.excludeNulls = excludeNulls;
		return this;
	}

	/**
	 * Excludes empty maps and collections.
	 */
	public JsonSerializer excludeEmpty(final boolean excludeEmpty) {
		this.excludeEmpty = excludeEmpty;
		return this;
	}

	/**
	 * Specifies strict string encoding.
	 */
	public JsonSerializer strictStringEncoding(final boolean strictStringEncoding) {
		this.strictStringEncoding = strictStringEncoding;
		return this;
	}

	/**
	 * Defines callback for value serialization. It defines the instance of {@link TypeJsonSerializer}
	 * to be used with the value. If {@code null} is returned, default serializer will be resolved.
	 */
	public JsonSerializer onValue(final Function<Object, TypeJsonSerializer> function) {
		this.serializerResolver = function;
		return this;
	}

	// ---------------------------------------------------------------- serialize

	/**
	 * Serializes object into provided appendable.
	 */
	public void serialize(final Object source, final Appendable target) {
		JsonContext jsonContext = createJsonContext(target);

		jsonContext.serialize(source);
	}

	/**
	 * Serializes object into source.
	 */
	public String serialize(final Object source) {
		FastCharBuffer fastCharBuffer = new FastCharBuffer();

		serialize(source, fastCharBuffer);

		return fastCharBuffer.toString();
	}

	/**
	 * Serializes the object, but returns the {@link CharSequence}.
	 */
	public CharSequence serializeToCharSequence(final Object source) {
		FastCharBuffer fastCharBuffer = new FastCharBuffer();

		serialize(source, fastCharBuffer);

		return fastCharBuffer;
	}

	// ---------------------------------------------------------------- json context

	/**
	 * Creates new JSON context.
	 */
	public JsonContext createJsonContext(final Appendable appendable) {
		return new JsonContext(this, appendable);
	}
}
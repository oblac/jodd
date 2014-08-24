// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.JoddJson;
import jodd.util.ArraysUtil;
import jodd.util.InExRules;
import jodd.util.UnsafeUtil;
import jodd.util.buffer.FastCharBuffer;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON serializer.
 */
public class JsonSerializer {

	// ---------------------------------------------------------------- defaults

	protected static TypeJsonSerializerMap defaultSerializers;

	static {
		defaultSerializers = new TypeJsonSerializerMap();
		defaultSerializers.registerDefaults();
	}

	/**
	 * Returns default map of serializers.
	 */
	public static TypeJsonSerializerMap getDefaultSerializers() {
		return defaultSerializers;
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
	protected boolean deep = JoddJson.deepSerialization;
	protected Class[] excludedTypes = null;
	protected String[] excludedTypeNames = null;

	/**
	 * Defines custom {@link jodd.json.TypeJsonSerializer} for given path.
	 */
	public JsonSerializer use(String pathString, TypeJsonSerializer typeJsonSerializer) {
		if (pathSerializersMap == null) {
			pathSerializersMap = new HashMap<Path, TypeJsonSerializer>();
		}

		pathSerializersMap.put(Path.parse(pathString), typeJsonSerializer);

		return this;
	}

	/**
	 * Defines custom {@link jodd.json.TypeJsonSerializer} for given type.
	 */
	public JsonSerializer use(Class type, TypeJsonSerializer typeJsonSerializer) {
		if (typeSerializersMap == null) {
			typeSerializersMap = new TypeJsonSerializerMap();
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

	// ---------------------------------------------------------------- serialize

	/**
	 * Serializes object into provided appendable.
	 */
	public void serialize(Object source, Appendable target) {
		JsonContext jsonContext = new JsonContext(this, target);

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
		return new JsonContext(this, appendable);
	}
}
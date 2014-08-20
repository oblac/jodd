// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.JoddJson;
import jodd.util.UnsafeUtil;
import jodd.util.buffer.FastCharBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	protected List<PathQuery> pathQueries;
	protected String classMetadataName = JoddJson.classMetadataName;
	protected boolean deep = JoddJson.deepSerialization;

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
	 * Adds a list of included path queries.
	 */
	public JsonSerializer include(String... includes) {
		if (pathQueries == null) {
			pathQueries = new ArrayList<PathQuery>();
		}
		for (String include : includes) {
			PathQuery pathQuery = new PathQuery(include, true);

			if (!pathQueries.contains(pathQuery)) {
				pathQueries.add(pathQuery);
			}
		}

		return this;
	}
	/**
	 * Adds a list of excluded path queries.
	 */
	public JsonSerializer exclude(String... excludes) {
		if (pathQueries == null) {
			pathQueries = new ArrayList<PathQuery>();
		}
		for (String exclude : excludes) {
			PathQuery pathQuery = new PathQuery(exclude, false);

			if (!pathQueries.contains(pathQuery)) {
				pathQueries.add(pathQuery);
			}
		}

		return this;
	}

	/**
	 * Adds excludes with optional parent including. When parents are included,
	 * for each exclude query its parent will be included.
	 * For example, exclude of 'aaa.bb.ccc' would include it's parent: 'aaa.bb'.
	 */
	public JsonSerializer exclude(boolean includeParent, String... excludes) {
		if (pathQueries == null) {
			pathQueries = new ArrayList<PathQuery>();
		}
		for (String exclude : excludes) {
			if (includeParent) {
				int dotIndex = exclude.lastIndexOf('.');
				if (dotIndex != -1) {
					PathQuery pathQuery = new PathQuery(exclude.substring(0, dotIndex), true);

					if (!pathQueries.contains(pathQuery)) {
						pathQueries.add(pathQuery);
					}
				}
			}

			PathQuery pathQuery = new PathQuery(exclude, false);
			if (!pathQueries.contains(pathQuery)) {
				pathQueries.add(pathQuery);
			}
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
	 * Returns <code>true</code> if serialization is deep.
	 */
	public boolean isDeep() {
		return deep;
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
}
package jodd.json;

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
			pathQueries.add(new PathQuery(include, true));
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
		for (String include : excludes) {
			pathQueries.add(new PathQuery(include, false));
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
		StringBuilder stringBuilder = new StringBuilder();

		serialize(source, stringBuilder);

		return stringBuilder.toString();
	}
}
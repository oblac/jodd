package jodd.json;

import jodd.JoddJson;
import jodd.bean.BeanUtil;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.json.meta.JsonAnnotationManager;
import jodd.util.ArraysUtil;

import java.util.List;

/**
 * Bean visitor that serializes properties of a bean.
 * It analyzes the rules for inclusion/exclusion of a property.
 */
public class BeanSerializer {

	private static final JsonAnnotationManager jsonAnnotationManager = new JsonAnnotationManager();

	private final JsonContext jsonContext;
	private final Object source;
	private boolean declared;

	private int count;
	private String[] includes;
	private String[] excludes;

	public BeanSerializer(JsonContext jsonContext, Object bean) {
		this.jsonContext = jsonContext;
		this.source = bean;
		this.count = 0;
		this.declared = false;

		Class type = bean.getClass();

		includes = jsonAnnotationManager.lookupIncludes(type);
		excludes = jsonAnnotationManager.lookupExcludes(type);
	}

	/**
	 * Serializes a bean.
	 */
	public void serialize() {
		Class type = source.getClass();

		ClassDescriptor classDescriptor = ClassIntrospector.lookup(type);

		if (JoddJson.classMetadataName != null) {
			// process first 'meta' fields 'class'
			onProperty(JoddJson.classMetadataName, null);
		}

		PropertyDescriptor[] propertyDescriptors = classDescriptor.getAllPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			MethodDescriptor getter = propertyDescriptor.getReadMethodDescriptor();
			if (getter != null) {
				if (getter.matchDeclared(declared)) {
					onProperty(propertyDescriptor.getName(), propertyDescriptor.getType());
				}
			}
			else {
				FieldDescriptor field = propertyDescriptor.getFieldDescriptor();
				if (field != null) {
					if (field.matchDeclared(declared)) {
						onProperty(propertyDescriptor.getName(), propertyDescriptor.getType());
					}
				}
			}
		}
	}

	/**
	 * Invoked on each property.
	 */
	protected boolean onProperty(String propertyName, Class propertyType) {
		Path currentPath = jsonContext.path;

		currentPath.push(propertyName);

		// determine if name should be included/excluded

		boolean include = true;

		// + all collections are not serialized by default

		if (propertyType != null) {

			ClassDescriptor propertyTypeClassDescriptor = ClassIntrospector.lookup(propertyType);

			if (propertyTypeClassDescriptor.isCollection()) {
				include = false;
			}
		}

		// + annotations

		if (include == true) {
			if (ArraysUtil.contains(excludes, propertyName)) {
				include = false;
			}
		}
		else {
			if (ArraysUtil.contains(includes, propertyName)) {
				include = true;
			}
		}

		// + path queries: excludes/includes

		List<PathQuery> pathQueries = jsonContext.jsonSerializer.pathQueries;

		if (pathQueries != null) {
			for (int iteration = 0; iteration < 2; iteration++) {
				for (PathQuery pathQuery : pathQueries) {
					if (iteration == 0 && !pathQuery.isWildcard()) {
						continue;
					}
					if (iteration == 1 && pathQuery.isWildcard()) {
						continue;
					}
					if (pathQuery.matches(currentPath)) {
						include = pathQuery.isIncluded();
					}
				}
			}
		}

		// done

		if (!include) {
			currentPath.pop();
			return true;
		}

		Object value;

		if (propertyType == null) {
			// metadata - classname
			value = source.getClass().getName();
		} else {
			value = BeanUtil.getProperty(source, propertyName);
		}

		jsonContext.pushName(propertyName, count > 0);
		jsonContext.serialize(value);

		if (jsonContext.isNamePoped()) {
			count++;
		}

		currentPath.pop();

		return true;
	}
}
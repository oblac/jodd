// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Validation context is set of all checks that can be applied on one target.
 * Checks can be added manually or by parsing the class.
 */
public class ValidationContext {

	private static final String ANN_SEVERITY = "severity";
	private static final String ANN_PROFILES = "profiles";

	// ---------------------------------------------------------------- define constraints

	protected final Map<String, List<Check>> map = new HashMap<String, List<Check>>();

	/**
	 * Adds validation checks.
	 */
	public void add(Check check) {
		String name = check.getName();
		List<Check> list = map.get(name);
		if (list == null) {
			list = new LinkedList<Check>();
			map.put(name, list);
		}
		list.add(check);
	}

	/**
	 * Adds all checks from provided list.
	 */
	public void addAll(List<Check> checkList) {
		for (Check check : checkList) {
			add(check);
		}
	}


	// ---------------------------------------------------------------- annotation resolver

	private static Map<Class, List<Check>> cache = new HashMap<Class, List<Check>>();

	/**
	 * Resolve validation context for provided target class.
	 * @see #addClassChecks(Class)
	 */
	public static ValidationContext resolveFor(Class<?> target) {
		ValidationContext vc = new ValidationContext();
		vc.addClassChecks(target);
		return vc;
	}

	/**
	 * Parses class annotations and adds all checks.
	 * @see #resolveFor(Class)
	 */
	public void addClassChecks(Class target) {
		List<Check> list = cache.get(target);
		if (list == null) {
			list = new ArrayList<Check>();
			ClassDescriptor cd = ClassIntrospector.lookup(target);
			Field[] fields = cd.getAllFields(true);
			for (Field field : fields) {
				collectFieldAnnotationChecks(list, field);
			}
			cache.put(target, list);
		}
		addAll(list);
	}


	/**
	 * Process all annotations of provided field.
	 */
	protected void collectFieldAnnotationChecks(List<Check> annChecks, Field field) {
		Annotation[] annotations = field.getAnnotations();
		if (annotations.length > 0) {
			collectAnnotationChecks(annChecks, field.getType(), field.getName(), annotations);
		}
	}

	/**
	 * Collect annotations for some target.
	 */
	@SuppressWarnings({"unchecked"})
	protected void collectAnnotationChecks(List<Check> annChecks, Class targetType, String targetName, Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			Constraint c = annotation.annotationType().getAnnotation(Constraint.class);
			if (c == null) {
				continue;
			}

			Class<? extends ValidationConstraint> constraintClass = c.value();
			ValidationConstraint vc;
			try {
				vc = newConstraint(constraintClass, targetType);
			} catch (Exception ex) {
				throw new VtorException("Unable to create constraint: '" + constraintClass.getClass().getName() + "'.", ex);
			}
			vc.configure(annotation);
			Check check = new Check(targetName, vc);
			copyDefaultCheckProperties(check, annotation);
			annChecks.add(check);
		}
	}


	/**
	 * Create new constraint. The following rules are used:
	 * <li>use default constructor if exist.
	 * <li>otherwise, use constructor with ValidationContext parameter.
	 */
	protected <V extends ValidationConstraint> V newConstraint(Class<V> constraint, Class targetType) throws Exception {
		Constructor<V> ctor;
		try {
			ctor = constraint.getConstructor();
			return ctor.newInstance();
		} catch (NoSuchMethodException ignore) {
			ctor = constraint.getConstructor(ValidationContext.class);
			return ctor.newInstance(resolveFor(targetType));
		}
	}



	/**
	 * Copies default properties from annotation to the check.
	 */
	protected void copyDefaultCheckProperties(Check destCheck, Annotation annotation) {
		Integer severity = (Integer) ReflectUtil.readAnnotationValue(annotation, ANN_SEVERITY);
		destCheck.setSeverity(severity.intValue());

		String[] profiles = (String[]) ReflectUtil.readAnnotationValue(annotation, ANN_PROFILES);
		destCheck.setProfiles(profiles);
	}

}

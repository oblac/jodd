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

package jodd.vtor;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.util.ClassLoaderUtil;
import jodd.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validation context is set of all checks that can be applied on one target.
 * Checks can be added manually or by parsing the class.
 */
public class ValidationContext {

	private static final String ANN_SEVERITY = "severity";
	private static final String ANN_PROFILES = "profiles";
	private static final String ANN_MESSAGE = "message";

	// ---------------------------------------------------------------- define constraints

	protected final Map<String, List<Check>> map = new HashMap<>();

	/**
	 * Adds validation checks.
	 */
	public void add(Check check) {
		String name = check.getName();
		List<Check> list = map.get(name);
		if (list == null) {
			list = new ArrayList<>();
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

	private static Map<Class, List<Check>> cache = new HashMap<>();

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
			list = new ArrayList<>();
			ClassDescriptor cd = ClassIntrospector.lookup(target);

			PropertyDescriptor[] allProperties = cd.getAllPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : allProperties) {
				collectPropertyAnnotationChecks(list, propertyDescriptor);
			}

			cache.put(target, list);
		}
		addAll(list);
	}

	/**
	 * Process all annotations of provided properties.
	 */
	protected void collectPropertyAnnotationChecks(List<Check> annChecks, PropertyDescriptor propertyDescriptor) {
		FieldDescriptor fd = propertyDescriptor.getFieldDescriptor();

		if (fd != null) {
			Annotation[] annotations = fd.getField().getAnnotations();
			collectAnnotationChecks(annChecks, propertyDescriptor.getType(), propertyDescriptor.getName(), annotations);
		}

		MethodDescriptor md = propertyDescriptor.getReadMethodDescriptor();
		if (md != null) {
			Annotation[] annotations = md.getMethod().getAnnotations();
			collectAnnotationChecks(annChecks, propertyDescriptor.getType(), propertyDescriptor.getName(), annotations);
		}

		md = propertyDescriptor.getWriteMethodDescriptor();
		if (md != null) {
			Annotation[] annotations = md.getMethod().getAnnotations();
			collectAnnotationChecks(annChecks, propertyDescriptor.getType(), propertyDescriptor.getName(), annotations);
		}
	}

	/**
	 * Collect annotations for some target.
	 */
	@SuppressWarnings({"unchecked"})
	protected void collectAnnotationChecks(List<Check> annChecks, Class targetType, String targetName, Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			Constraint c = annotation.annotationType().getAnnotation(Constraint.class);
			Class<? extends ValidationConstraint> constraintClass;

			if (c == null) {
				// if constraint is not available, try lookup
				String constraintClassName = annotation.annotationType().getName() + "Constraint";

				try {
					constraintClass = ClassLoaderUtil.loadClass(constraintClassName, this.getClass().getClassLoader());
				}
				catch (ClassNotFoundException ingore) {
					continue;
				}
			}
			else {
				constraintClass = c.value();
			}

			ValidationConstraint vc;
			try {
				vc = newConstraint(constraintClass, targetType);
			} catch (Exception ex) {
				throw new VtorException("Invalid constraint: " + constraintClass.getClass().getName(), ex);
			}
			vc.configure(annotation);
			Check check = new Check(targetName, vc);
			copyDefaultCheckProperties(check, annotation);
			annChecks.add(check);
		}
	}


	/**
	 * Create new constraint. The following rules are used:
	 * <ul>
	 * <li>use default constructor if exist.</li>
	 * <li>otherwise, use constructor with ValidationContext parameter.</li>
	 * </ul>
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
		Integer severity = (Integer) ClassUtil.readAnnotationValue(annotation, ANN_SEVERITY);
		destCheck.setSeverity(severity.intValue());

		String[] profiles = (String[]) ClassUtil.readAnnotationValue(annotation, ANN_PROFILES);
		destCheck.setProfiles(profiles);

		String message = (String) ClassUtil.readAnnotationValue(annotation, ANN_MESSAGE);
		destCheck.setMessage(message);
	}

	/**
	 * Clears the cache map
	 */
	protected void clearCache() {
		cache.clear();
	}
}

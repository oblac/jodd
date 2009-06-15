// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.ActionConfig;
import jodd.util.StringUtil;
import jodd.util.StringPool;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Method;

/**
 * Creates {@link ActionConfig action configurations} from action java method.
 * Reads all annotations and builds action path (i.e. configuration).
 * <p>
 * Invoked only during registration, therefore performance is not most important.
 *
 * 
 * @see ActionPathMapper
 */
public class ActionMethodParser {

	protected static final String MACRO_PACKAGE = "${package}";
	protected static final String MACRO_CLASS = "${class}";
	protected static final String MACRO_METHOD = "${method}";
	protected static final String MACRO_EXTENSION = "${ext}";

	@PetiteInject
	protected MadvocConfig madvocConfig;

	// ---------------------------------------------------------------- 

	public ActionConfig parse(Class actionClass, Method actionMethod) {
		return parse(actionClass, actionMethod, null);
	}

	/**
	 * Parses java action method annotations and returns its action configuration.
	 * Returns <code>null</code> if method is not madvoc action.
	 */
	public ActionConfig parse(Class actionClass, Method actionMethod, String actionPath) {

		// interceptors
		Class<? extends ActionInterceptor>[] interceptorClasses = readMethodInterceptors(actionMethod);
		if (interceptorClasses == null) {
			interceptorClasses = readClassInterceptors(actionClass);
		}

		// action path is already specified explicitly
		if (actionPath != null) {
			return createActionConfig(actionClass, actionMethod, interceptorClasses, actionPath);
		}

		// action path not specified, build it
		String packageActionPath = readPackageActionPath(actionClass);

		// class annotation: class action path
		String classActionPath = readClassActionPath(actionClass);
		if (classActionPath == null) {
			return null;
		}

		// method annotation: action
		String methodActionPath = readMethodActionPath(actionMethod);

		// read other method annotation values
		String extension = readMethodExtension(actionMethod);
		String alias = readMethodAlias(actionMethod);
		String httpMethod = readMethodHttpMethod(actionMethod);

		if (methodActionPath != null) {
			// additional changes
			methodActionPath = StringUtil.replace(methodActionPath, MACRO_EXTENSION, extension);
			// check for defaults
			for (String path : madvocConfig.getDefaultActionMethodNames()) {
				if (methodActionPath.equals(path)) {
					methodActionPath = null;
					break;
				}
			}
		}

		// finally, build the action configuration
		actionPath = buildActionPath(packageActionPath, classActionPath, methodActionPath, extension, httpMethod);
		 
		// register alias
		if (alias != null) {
			String aliasPath = StringUtil.cutFromIndexOf(actionPath, StringPool.HASH);
			madvocConfig.registerResultAlias(alias, aliasPath);
		}
		return createActionConfig(actionClass, actionMethod, interceptorClasses, actionPath);
	}

	/**
	 * Builds action path. Method action path and extension may be <code>null</code>.
	 * @param packageActionPath action path from package (optional)
	 * @param classActionPath action path from class
	 * @param methodActionPath action path from method (optional)
	 * @param extension extension (optional)
	 * @param httpMethod (optional)
	 */
	protected String buildActionPath(String packageActionPath, String classActionPath, String methodActionPath, String extension, String httpMethod) {
		String pathSeparator = StringPool.SLASH;

		String actionPath = classActionPath;

		if (methodActionPath != null) {
			if (methodActionPath.startsWith(pathSeparator)) {
				return methodActionPath;    // absolute path
			}
			if (extension != null) {		// add extension
				methodActionPath += '.' + extension;
			}
			actionPath += StringPool.DOT + methodActionPath; // method separator
		} else {
			if (extension != null) {
				actionPath += '.' + extension;
			}
		}

		if (actionPath.startsWith(pathSeparator)) {
			return actionPath;
		}

		if (packageActionPath != null) {
			actionPath = packageActionPath + actionPath;
		} else {
			actionPath = pathSeparator + actionPath;
		}

		if (httpMethod != null) {
			actionPath += StringPool.HASH + httpMethod;
		}
		return actionPath;
	}

	// ---------------------------------------------------------------- interceptors

	/**
	 * Reads class interceptors when method interceptors are not availiable.
	 */
	protected Class<? extends ActionInterceptor>[] readClassInterceptors(Class actionClass) {
		Class<? extends ActionInterceptor>[] result = null;
		InterceptedBy interceptedBy = ((Class<?>)actionClass).getAnnotation(InterceptedBy.class);
		if (interceptedBy != null) {
			result = interceptedBy.value();
			if (result.length == 0) {
				result = null;
			}
		}
		return result;
	}

	/**
	 * Reads method interceptors.
	 */
	protected Class<? extends ActionInterceptor>[] readMethodInterceptors(Method actionMethod) {
		Class<? extends ActionInterceptor>[] result = null;
		InterceptedBy interceptedBy = actionMethod.getAnnotation(InterceptedBy.class);
		if (interceptedBy != null) {
			result = interceptedBy.value();
			if (result.length == 0) {
				result = null;
			}
		}
		return result;
	}

	// ---------------------------------------------------------------- readers

	/**
	 * Reads action path for package. It can be used only if root package is set in
	 * {@link MadvocConfig madvoc configuration}.
	 * If annotation is not set on package-level, class package will be used for
	 * package action path part.
	 */
	protected String readPackageActionPath(Class actionClass) {
		final String packageRoot = madvocConfig.getRootPackage();

		if (packageRoot == null) {
			return null;
		}

		// read annotation
		MadvocAction madvocActionAnnotation = ((Class<?>)actionClass).getPackage().getAnnotation(MadvocAction.class);
		String packageActionPath = madvocActionAnnotation != null ? madvocActionAnnotation.value().trim() : null;
		if (StringUtil.isEmpty(packageActionPath)) {
			packageActionPath = null;
		}

		// build name from package
		String packagePath = actionClass.getPackage().getName();
		if (packagePath.length() > packageRoot.length()) {
			packagePath = StringUtil.cutPreffix(packagePath, packageRoot + '.');
		} else {
			packagePath = StringUtil.cutPreffix(packagePath, packageRoot);
		}
		packagePath = packagePath.replace('.', '/');

		// decide
		if (packageActionPath == null) {
			packageActionPath = packagePath;
		} else {
			packageActionPath = StringUtil.replace(packageActionPath, MACRO_PACKAGE, packagePath);
		}
		return StringUtil.surround(packageActionPath, StringPool.SLASH);
	}

	/**
	 * Reads action path from class. If the class is annotated with {@link MadvocAction} annotation,
	 * class action path will be read from annotation value. Otherwise, action class path will be built from the
	 * class name. This is done by removing the package name and the last contained word
	 * (if there is more then one) from the class name. Such name is finally uncapitalized.
	 * <p>
	 * If this method returns <code>null</code> class will be ignored.
	 */
	protected String readClassActionPath(Class actionClass) {
		// read annotation
		MadvocAction madvocActionAnnotation = ((Class<?>)actionClass).getAnnotation(MadvocAction.class);
		String classActionPath = madvocActionAnnotation != null ? madvocActionAnnotation.value().trim() : null;
		if (StringUtil.isEmpty(classActionPath)) {
			classActionPath = null;
		}

		// build class action path from the class name
		String name = actionClass.getSimpleName();
		name = StringUtil.uncapitalize(name);
		name = StringUtil.cutLastWord(name);

		// decide
		if (classActionPath == null) {
			classActionPath = name;
		} else {
			classActionPath = StringUtil.replace(classActionPath, MACRO_CLASS, name);
		}
		return classActionPath;
	}


	/**
	 * Reads action method.
	 */
	protected String readMethodActionPath(Method actionMethod) {
		// read annotation
		Action methodAnnotation = actionMethod.getAnnotation(Action.class);
		String methodActionPath = methodAnnotation != null ? methodAnnotation.value().trim() : null;
		if (StringUtil.isEmpty(methodActionPath)) {
			methodActionPath = null;
		}

		// build name
		String methodName = actionMethod.getName();

		// decide
		if (methodActionPath == null) {
			methodActionPath = methodName;
		} else {
			methodActionPath = StringUtil.replace(methodActionPath, MACRO_METHOD, methodName);
		}

		// not in path
		if (methodAnnotation != null && methodAnnotation.notInPath() == true) {
			methodActionPath = null;
		}
		return methodActionPath;
	}

	/**
	 * Reads method's extension.
	 */
	protected String readMethodExtension(Method actionMethod) {
		String extension = madvocConfig.getDefaultExtension();
		Action methodAnnotation = actionMethod.getAnnotation(Action.class);
		if (methodAnnotation != null) {
			String annExtension = methodAnnotation.extension().trim();
			if (annExtension.equals(Action.NO_EXTENSION)) {
				extension = null;
			} else if (annExtension.length() != 0) {
				extension = StringUtil.replace(annExtension, MACRO_EXTENSION, extension);
			}
		}
		return extension;
	}

	/**
	 * Reads method's alias value.
	 */
	protected String readMethodAlias(Method actionMethod) {
		String alias = null;
		Action methodAnnotation = actionMethod.getAnnotation(Action.class);
		if (methodAnnotation != null) {
			alias = methodAnnotation.alias().trim();
			if (alias.length() == 0) {
				alias = null;
			}
		}
		return alias;
	}

	/**
	 * Reads method's http method.
	 */
	private String readMethodHttpMethod(Method actionMethod) {
		String method = null;
		Action methodAnnotation = actionMethod.getAnnotation(Action.class);
		if (methodAnnotation != null) {
			method = methodAnnotation.method().trim();
			if (method.length() == 0) {
				method = null;
			}
		}
		return method;
	}

	// ---------------------------------------------------------------- create action configuration

	/**
	 * Creates new instance of action configuration.
	 */
	protected ActionConfig createActionConfig(Class actionClass, Method actionMethod, Class<? extends ActionInterceptor>[] interceptorClasses, String actionPath) {
		return new ActionConfig(actionClass, actionMethod, interceptorClasses, actionPath);
	}

}

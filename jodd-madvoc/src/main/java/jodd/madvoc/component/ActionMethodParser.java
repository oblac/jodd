// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.introspector.ClassIntrospector;
import jodd.introspector.MethodDescriptor;
import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.RootPackages;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.ActionAnnotationData;
import jodd.madvoc.meta.ActionAnnotation;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.ActionConfig;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringUtil;
import jodd.util.StringPool;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates {@link ActionConfig action configurations} from action java method.
 * Reads all annotations and builds action path (i.e. configuration).
 * <p>
 * Invoked only during registration, therefore performance is not most important.
 * 
 * @see ActionPathMapper
 */
public class ActionMethodParser {

	protected static final String REPL_PACKAGE = "[package]";
	protected static final String REPL_CLASS = "[class]";
	protected static final String REPL_METHOD = "[method]";
	protected static final String REPL_EXTENSION = "[ext]";

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected InterceptorsManager interceptorsManager;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	// ---------------------------------------------------------------- resolve method

	/**
	 * Resolves action method for given string ane method name.
	 */
	public Method resolveActionMethod(Class<?> actionClass, String methodName) {
		MethodDescriptor methodDescriptor = ClassIntrospector.lookup(actionClass).getMethodDescriptor(methodName, false);
		if (methodDescriptor == null) {
			throw new MadvocException("Provided action class '" + actionClass.getSimpleName() + "' doesn't contain public method: " + methodName);
		}
		return methodDescriptor.getMethod();
	}

	// ---------------------------------------------------------------- parse

	public ActionConfig parse(Class<?> actionClass, Method actionMethod) {
		return parse(actionClass, actionMethod, null);
	}

	/**
	 * @see #parse(Class, java.lang.reflect.Method, String)
	 */
	public ActionConfig parse(Class<?> actionClass, String actionMethodName, String actionPath) {
		Method method = resolveActionMethod(actionClass, actionMethodName);
		return parse(actionClass, method, actionPath);
	}

	/**
	 * Parses java action method annotations and returns its action configuration.
	 * Returns <code>null</code> if method is not a madvoc action.
	 */
	public ActionConfig parse(Class<?> actionClass, Method actionMethod, String actionPath) {

		// interceptors
		Class<? extends ActionInterceptor>[] interceptorClasses = readMethodInterceptors(actionMethod);
		if (interceptorClasses == null) {
			interceptorClasses = readClassInterceptors(actionClass);
		}
		if (interceptorClasses == null) {
			interceptorClasses = madvocConfig.getDefaultInterceptors();
		}

		ActionInterceptor[] actionInterceptors = interceptorsManager.resolveAll(interceptorClasses);
		ActionFilter[] actionFilters;
		try {
			actionFilters = interceptorsManager.extractActionFilters(actionInterceptors);
			actionInterceptors = interceptorsManager.extractActionInterceptors(actionInterceptors);
		} catch (MadvocException ignore) {
			throw new MadvocException("Action Filters must be defined before Interceptors: " + actionClass.getName() + '#' + actionMethod.getName());
		}

		for (ActionInterceptor filter : actionFilters) {
			if (filter.isInitialized() == false) {
				interceptorsManager.initializeInterceptor(filter);
			}
		}

		for (ActionInterceptor interceptor : actionInterceptors) {
			if (interceptor.isInitialized() == false) {
				interceptorsManager.initializeInterceptor(interceptor);
			}
		}



		// actions
		HashMap<String, String> replacementMap = new HashMap<String, String>();

		// action path not specified, build it
		String packageActionPath = readPackageActionPath(actionClass, replacementMap);

		// class annotation: class action path
		String classActionPath = readClassActionPath(actionClass, replacementMap);
		if (classActionPath == null) {
			return null;
		}

		// method annotation: detect
		ActionAnnotationData annotationData = null;
		for (ActionAnnotation actionAnnotation : madvocConfig.getActionAnnotationInstances()) {
			annotationData = actionAnnotation.readAnnotationData(actionMethod);
			if (annotationData != null) {
				break;
			}
		}

		// read method annotation values
		String actionMethodName = actionMethod.getName();
		String methodActionPath = readMethodActionPath(actionMethodName, annotationData, replacementMap);
		String extension = readMethodExtension(annotationData);
		String alias = readMethodAlias(annotationData);
		String httpMethod = readMethodHttpMethod(annotationData);
		String resultType = readResultType(annotationData);

		if (methodActionPath != null) {
			// additional changes
			replacementMap.put(REPL_EXTENSION, extension);

			// check for defaults
			for (String path : madvocConfig.getDefaultActionMethodNames()) {
				if (methodActionPath.equals(path)) {
					methodActionPath = null;
					break;
				}
			}
		}

		if (actionPath == null) {
			// finally, build the action path if it is not already specified
			actionPath = buildActionPath(packageActionPath, classActionPath, methodActionPath, extension, httpMethod);
		}

		// apply replacements
		for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
			actionPath = StringUtil.replace(actionPath, entry.getKey(), entry.getValue());
		}
		 
		// register alias
		if (alias != null) {
			String aliasPath = StringUtil.cutToIndexOf(actionPath, StringPool.HASH);
			actionsManager.registerPathAlias(alias, aliasPath);
		} else if (madvocConfig.isCreateDefaultAliases()) {
			alias = actionClass.getName() + '#' + actionMethod.getName();
			actionsManager.registerPathAlias(alias, actionPath);
		}

		return createActionConfig(
				actionClass, actionMethod,
				actionFilters, actionInterceptors,
				actionPath, httpMethod, extension,
				resultType);
	}

	/**
	 * Builds action path. Method action path and extension may be <code>null</code>.
	 * @param packageActionPath action path from package (optional)
	 * @param classActionPath action path from class
	 * @param methodActionPath action path from method (optional)
	 * @param extension extension (optional)
	 * @param httpMethod HTTP method name (not used by default)
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
			if (classActionPath.endsWith(pathSeparator) == false) {
				actionPath += StringPool.DOT;
			}
			actionPath += methodActionPath; // method separator
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

		return actionPath;
	}

	// ---------------------------------------------------------------- interceptors

	/**
	 * Reads class interceptors when method interceptors are not available.
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
	protected String readPackageActionPath(Class actionClass, Map<String, String> replacementMap) {

		Package actionPackage = actionClass.getPackage();
		String actionPackageName = actionPackage.getName();

		final RootPackages rootPackages = madvocConfig.getRootPackages();

		String packagePath = rootPackages.getPackageActionPath(actionPackageName);

		if (packagePath == null) {

			packagePath = rootPackages.findPackagePathForActionPackage(actionPackageName);

			String rootPackage = null;

			if (packagePath != null) {
				rootPackage = rootPackages.findRootPackageForActionPath(packagePath);
			}

			// try locating marker class
			{
				String packageName = actionPackageName;
				String madvocRootPackageClassName = madvocConfig.getMadvocRootPackageClassName();

				if (madvocRootPackageClassName != null) {
					while (true) {
						String className = packageName + '.' + madvocRootPackageClassName;
						try {
							Class<?> madvocRootPackageClass = ClassLoaderUtil.loadClass(className, actionClass.getClassLoader());

							// class found, find the mapping
							String mapping = StringPool.EMPTY;
							MadvocAction madvocAction = madvocRootPackageClass.getAnnotation(MadvocAction.class);

							if (madvocAction != null) {
								mapping = madvocAction.value();
							}

							// register root package - so not to lookup twice
							madvocConfig.getRootPackages().addRootPackage(packageName, mapping);

							// repeat lookup
							packagePath = rootPackages.findPackagePathForActionPackage(actionPackageName);

							break;
						} catch (ClassNotFoundException ignore) {

							// continue
							int dotNdx = packageName.lastIndexOf('.');
							if (dotNdx == -1) {
								break;
							}

							packageName = packageName.substring(0, dotNdx);

							if (rootPackage != null) {
								// don't go beyond found root package
								if (packageName.equals(rootPackage)) {
									break;
								}
							}
						}
					}
				}
			}

			rootPackages.registerPackageActionPath(actionPackageName, packagePath);
		}


		// read package-level annotation

		MadvocAction madvocActionAnnotation = actionPackage.getAnnotation(MadvocAction.class);

		String packageActionPath = madvocActionAnnotation != null ? madvocActionAnnotation.value().trim() : null;

		if (StringUtil.isEmpty(packageActionPath)) {
			packageActionPath = null;
		}

		// package-level annotation overrides everything
		// if not set, resolve value
		if (packageActionPath == null) {
			// no package-level annotation
			if (packagePath == null) {
				// no root package path, just return
				return null;
			}
			packageActionPath = packagePath;
		}

		replacementMap.put(REPL_PACKAGE, StringUtil.stripChar(packagePath, '/'));

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
	protected String readClassActionPath(Class actionClass, Map<String, String> replacementMap) {
		// read annotation
		MadvocAction madvocActionAnnotation = ((Class<?>)actionClass).getAnnotation(MadvocAction.class);
		String classActionPath = madvocActionAnnotation != null ? madvocActionAnnotation.value().trim() : null;
		if (StringUtil.isEmpty(classActionPath)) {
			classActionPath = null;
		}

		String name = actionClass.getSimpleName();
		name = StringUtil.uncapitalize(name);
		name = MadvocUtil.stripLastCamelWord(name);

		if (classActionPath == null) {
			classActionPath = name;
		}

		replacementMap.put(REPL_CLASS, name);
		return classActionPath;
	}

	/**
	 * Reads action method.
	 */
	protected String readMethodActionPath(String methodName, ActionAnnotationData annotationData, Map<String, String> replacementMap) {
		// read annotation
		String methodActionPath = annotationData != null ? annotationData.getValue() : null;

		if (methodActionPath == null) {
			methodActionPath = methodName;
		} else {
			if (methodActionPath.equals(Action.NONE)) {
				return null;
			}
		}

		replacementMap.put(REPL_METHOD, methodName);
		return methodActionPath;
	}

	/**
	 * Reads method's extension.
	 */
	protected String readMethodExtension(ActionAnnotationData annotationData) {
		String extension = madvocConfig.getDefaultExtension();
		if (annotationData != null) {
			String annExtension = annotationData.getExtension();
			if (annExtension != null) {
				if (annExtension.equals(Action.NONE)) {
					extension = null;
				} else {
					extension = StringUtil.replace(annExtension, REPL_EXTENSION, extension);
				}
			}
		}
		return extension;
	}

	/**
	 * Reads method's alias value.
	 */
	protected String readMethodAlias(ActionAnnotationData annotationData) {
		String alias = null;
		if (annotationData != null) {
			alias = annotationData.getAlias();
		}
		return alias;
	}

	/**
	 * Reads method's http method.
	 */
	private String readMethodHttpMethod(ActionAnnotationData annotationData) {
		String method = null;
		if (annotationData != null) {
			method = annotationData.getMethod();
		}
		return method;
	}

	/**
	 * Reads method's result type.
	 */
	private String readResultType(ActionAnnotationData annotationData) {
		String resultType = null;
		if (annotationData != null) {
			resultType = annotationData.getResult();
		}
		return resultType;
	}

	// ---------------------------------------------------------------- create action configuration

	/**
	 * Creates new instance of action configuration.
	 */
	public ActionConfig createActionConfig(
			Class actionClass,
			Method actionClassMethod,
			ActionFilter[] filters,
			ActionInterceptor[] interceptors,
			String actionPath,
			String actionMethod,
			String actionPathExtension,
			String resultType)
	{

		return new ActionConfig(
				actionClass,
				actionClassMethod,
				filters,
				interceptors,
				actionPath,
				actionMethod,
				actionPathExtension,
				resultType);
	}

}

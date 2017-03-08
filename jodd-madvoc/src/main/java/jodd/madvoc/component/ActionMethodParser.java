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

package jodd.madvoc.component;

import jodd.madvoc.ActionNames;
import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.RootPackages;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.injector.Target;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.ActionAnnotationData;
import jodd.madvoc.meta.ActionAnnotation;
import jodd.madvoc.meta.FilteredBy;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionDef;
import jodd.madvoc.path.ActionNamingStrategy;
import jodd.madvoc.result.ActionResult;
import jodd.util.ArraysUtil;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringUtil;
import jodd.util.StringPool;
import jodd.petite.meta.PetiteInject;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * Creates {@link ActionConfig action configurations} from action java method.
 * Reads all annotations and builds action path (i.e. configuration).
 * <p>
 * Invoked only during registration, so performance is not critical.
 */
public class ActionMethodParser {

	@PetiteInject
	protected ContextInjectorComponent contextInjectorComponent;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected InterceptorsManager interceptorsManager;

	@PetiteInject
	protected FiltersManager filtersManager;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ScopeDataResolver scopeDataResolver;

	@PetiteInject
	protected ActionMethodParamNameResolver actionMethodParamNameResolver;

	/**
	 * Parses action class and method and creates {@link jodd.madvoc.ActionDef parsed action definition}.
	 */
	public ActionDef parseActionDef(final Class<?> actionClass, final Method actionMethod) {

		ActionAnnotationData annotationData = detectActionAnnotationData(actionMethod);

		final ActionNames actionNames = new ActionNames();		// collector for all action names

		readPackageActionPath(actionNames, actionClass);

		readClassActionPath(actionNames, actionClass);

		readMethodActionPath(actionNames, actionMethod.getName(), annotationData);

		readMethodExtension(actionNames, annotationData);

		readMethodHttpMethod(actionNames, annotationData);

		final Class<? extends ActionNamingStrategy> actionPathNamingStrategy = parseMethodNamingStrategy(annotationData);

		ActionNamingStrategy namingStrategy;

		try {
			namingStrategy = actionPathNamingStrategy.newInstance();

			contextInjectorComponent.injectContext(new Target(namingStrategy));
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}

		return namingStrategy.buildActionDef(actionClass, actionMethod, actionNames);
	}

	/**
	 * Parses java action method annotation and returns its action configuration.
	 *
	 * @param actionClass action class
	 * @param actionMethod action method
	 * @param actionDef optional action def, usually <code>null</code> so to be parsed
	 */
	public ActionConfig parse(final Class<?> actionClass, final Method actionMethod, ActionDef actionDef) {

		// interceptors
		ActionInterceptor[] actionInterceptors = parseActionInterceptors(actionClass, actionMethod);

		// filters
		ActionFilter[] actionFilters = parseActionFilters(actionClass, actionMethod);

		// build action definition when not provided
		if (actionDef == null) {
			actionDef = parseActionDef(actionClass, actionMethod);
		}

		ActionAnnotationData annotationData = detectActionAnnotationData(actionMethod);

		detectAndRegisterAlias(annotationData, actionDef);

		final boolean async = parseMethodAsyncFlag(annotationData);

		final Class<? extends ActionResult> actionResult = parseActionResult(annotationData);

		return createActionConfig(
				actionClass, actionMethod,
				actionResult,
				actionFilters, actionInterceptors,
				actionDef,
				async);
	}

	/**
	 * Detects {@link jodd.madvoc.meta.ActionAnnotationData}.
	 */
	protected ActionAnnotationData detectActionAnnotationData(Method actionMethod) {
		ActionAnnotationData annotationData = null;
		for (ActionAnnotation actionAnnotation : madvocConfig.getActionAnnotationInstances()) {
			annotationData = actionAnnotation.readAnnotationData(actionMethod);
			if (annotationData != null) {
				break;
			}
		}
		return annotationData;
	}

	/**
	 * Detects if alias is defined in annotation and registers it if so.
	 */
	protected void detectAndRegisterAlias(ActionAnnotationData annotationData, ActionDef actionDef) {
		final String alias = parseMethodAlias(annotationData);

		if (alias != null) {
			String aliasPath = StringUtil.cutToIndexOf(actionDef.getActionPath(), StringPool.HASH);
			actionsManager.registerPathAlias(alias, aliasPath);
		}
	}

	protected Class<? extends ActionResult> parseActionResult(ActionAnnotationData annotationData) {
		if (annotationData == null) {
			return null;
		}

		Class<? extends ActionResult> actionResult = annotationData.getResult();

		if (actionResult == ActionResult.class) {
			return null;
		}

		return actionResult;
	}

	protected ActionInterceptor[] parseActionInterceptors(final Class<?> actionClass, final Method actionMethod) {
		Class<? extends ActionInterceptor>[] interceptorClasses = readActionInterceptors(actionMethod);
		if (interceptorClasses == null) {
			interceptorClasses = readActionInterceptors(actionClass);
		}
		if (interceptorClasses == null) {
			interceptorClasses = madvocConfig.getDefaultInterceptors();
		}

		return interceptorsManager.resolveAll(interceptorClasses);
	}

	protected ActionFilter[] parseActionFilters(Class<?> actionClass, Method actionMethod) {
		Class<? extends ActionFilter>[] filterClasses = readActionFilters(actionMethod);
		if (filterClasses == null) {
			filterClasses = readActionFilters(actionClass);
		}
		if (filterClasses == null) {
			filterClasses = madvocConfig.getDefaultFilters();
		}

		return filtersManager.resolveAll(filterClasses);
	}

	// ---------------------------------------------------------------- interceptors

	/**
	 * Reads class or method annotation for action interceptors.
	 */
	protected Class<? extends ActionInterceptor>[] readActionInterceptors(AnnotatedElement actionClassOrMethod) {
		Class<? extends ActionInterceptor>[] result = null;
		InterceptedBy interceptedBy = actionClassOrMethod.getAnnotation(InterceptedBy.class);
		if (interceptedBy != null) {
			result = interceptedBy.value();
			if (result.length == 0) {
				result = null;
			}
		}
		return result;
	}

	// ---------------------------------------------------------------- filters

	/**
	 * Reads class or method annotation for action filters.
	 */
	protected Class<? extends ActionFilter>[] readActionFilters(AnnotatedElement actionClassOrMethod) {
		Class<? extends ActionFilter>[] result = null;
		FilteredBy filteredBy = actionClassOrMethod.getAnnotation(FilteredBy.class);
		if (filteredBy != null) {
			result = filteredBy.value();
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
	protected void readPackageActionPath(ActionNames actionNames, Class actionClass) {

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
				return;
			}
			packageActionPath = packagePath;
		}

		actionNames.setPackageNames(
			StringUtil.stripChar(packagePath, '/'),
			StringUtil.surround(packageActionPath, StringPool.SLASH)
		);
	}

	/**
	 * Reads action path from class. If the class is annotated with {@link MadvocAction} annotation,
	 * class action path will be read from annotation value. Otherwise, action class path will be built from the
	 * class name. This is done by removing the package name and the last contained word
	 * (if there is more then one) from the class name. Such name is finally uncapitalized.
	 * <p>
	 * If this method returns <code>null</code> class will be ignored.
	 */
	protected void readClassActionPath(ActionNames actionNames, Class actionClass) {
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

		actionNames.setClassNames(name, classActionPath);
	}

	/**
	 * Reads action method. Returns <code>null</code> if action method is {@link Action#NONE}
	 * or if it is equals to {@link MadvocConfig#getDefaultActionMethodNames() default action names}.
	 */
	protected void readMethodActionPath(ActionNames actionNames, String methodName, ActionAnnotationData annotationData) {
		// read annotation
		String methodActionPath = annotationData != null ? annotationData.getValue() : null;

		if (methodActionPath == null) {
			methodActionPath = methodName;
		} else {
			if (methodActionPath.equals(Action.NONE)) {
				return;
			}
		}

		// check for defaults
		for (String path : madvocConfig.getDefaultActionMethodNames()) {
			if (methodActionPath.equals(path)) {
				methodActionPath = null;
				break;
			}
		}

		actionNames.setMethodNames(methodName, methodActionPath);
	}

	/**
	 * Reads method's extension.
	 */
	protected void readMethodExtension(ActionNames actionNames, ActionAnnotationData annotationData) {
		String extension = madvocConfig.getDefaultExtension();
		if (annotationData != null) {
			String annExtension = annotationData.getExtension();
			if (annExtension != null) {
				if (annExtension.equals(Action.NONE)) {
					extension = null;
				} else {
					extension = annExtension;
				}
			}
		}
		actionNames.setExtension(extension);
	}

	/**
	 * Reads method's alias value.
	 */
	protected String parseMethodAlias(ActionAnnotationData annotationData) {
		String alias = null;
		if (annotationData != null) {
			alias = annotationData.getAlias();
		}
		return alias;
	}

	/**
	 * Reads method's http method.
	 */
	private void readMethodHttpMethod(ActionNames actionNames, ActionAnnotationData annotationData) {
		String method = null;
		if (annotationData != null) {
			method = annotationData.getMethod();
		}

		actionNames.setHttpMethod(method);
	}

	/**
	 * Reads method's async flag.
	 */
	private boolean parseMethodAsyncFlag(ActionAnnotationData annotationData) {
		boolean sync = false;
		if (annotationData != null) {
			sync = annotationData.isAsync();
		}
		return sync;
	}

	/**
	 * Reads method's action path naming strategy.
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends ActionNamingStrategy> parseMethodNamingStrategy(ActionAnnotationData annotationData) {
		Class<? extends ActionNamingStrategy> actionNamingStrategyClass = null;

		if (annotationData != null) {
			actionNamingStrategyClass = annotationData.getPath();

			if (actionNamingStrategyClass == ActionNamingStrategy.class) {
				actionNamingStrategyClass = null;
			}
		}

		if (actionNamingStrategyClass == null) {
			actionNamingStrategyClass = madvocConfig.getDefaultNamingStrategy();
		}

		return actionNamingStrategyClass;
	}

	// ---------------------------------------------------------------- create action configuration

	/**
	 * Creates new instance of action configuration.
	 * Initialize caches.
	 */
	public ActionConfig createActionConfig(
			Class actionClass,
			Method actionClassMethod,
			Class<? extends ActionResult> actionResult,
			ActionFilter[] filters,
			ActionInterceptor[] interceptors,
			ActionDef actionDef,
			boolean async)
	{

		// 1) find ins and outs

		Class[] paramTypes = actionClassMethod.getParameterTypes();
		ActionConfig.MethodParam[] params = new ActionConfig.MethodParam[paramTypes.length];

		Annotation[][] paramAnns = actionClassMethod.getParameterAnnotations();
		String[] methodParamNames = null;

		// expand arguments array with action itself, on first position
		Class[] types = ArraysUtil.insert(paramTypes, actionClass, 0);

		ScopeData[][] allScopeData = new ScopeData[ScopeType.values().length][];

		// for all elements: action and method arguments...
		for (int i = 0; i < types.length; i++) {
			Class type = types[i];

			ScopeData[] scopeData = null;

			if (i > 0) {
				// lazy init to postpone bytecode usage, when method has no arguments
				if (methodParamNames == null) {
					methodParamNames = actionMethodParamNameResolver.resolveParamNames(actionClassMethod);
				}

				int paramIndex = i - 1;

				String paramName = methodParamNames[paramIndex];

				scopeData = scopeDataResolver.resolveScopeData(paramName, type, paramAnns[paramIndex]);

				params[paramIndex] = new ActionConfig.MethodParam(
						paramTypes[paramIndex], paramName, scopeDataResolver.detectAnnotationType(paramAnns[paramIndex]));
			}

			if (scopeData == null) {
				// read annotations inside the type for all scope types
				scopeData = scopeDataResolver.resolveScopeData(type);
			}

			if (scopeData == null) {
				continue;
			}

			// for all scope types... merge
			for (int j = 0; j < ScopeType.values().length; j++) {
				if (allScopeData[j] == null) {
					allScopeData[j] = new ScopeData[types.length];
				}
				allScopeData[j][i] = scopeData[j];
			}
		}

		return new ActionConfig(
				actionClass,
				actionClassMethod,
				filters,
				interceptors,
				actionDef,
				actionResult,
				async,
				allScopeData,
				params);
	}

}

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

import jodd.madvoc.ActionConfig;
import jodd.madvoc.MadvocConfig;
import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.ScopeType;
import jodd.madvoc.config.ActionDefinition;
import jodd.madvoc.config.ActionNames;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.MethodParam;
import jodd.madvoc.config.RootPackages;
import jodd.madvoc.config.ScopeData;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.injector.Target;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.ActionAnnotation;
import jodd.madvoc.meta.ActionAnnotationData;
import jodd.madvoc.meta.FilteredBy;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.path.ActionNamingStrategy;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.util.ArraysUtil;
import jodd.util.ClassUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * Creates {@link ActionRuntime action runtime} configuration from action java method.
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
	 * Parses action class and method and creates {@link ActionDefinition parsed action definition}.
	 */
	public ActionDefinition parseActionDef(final Class<?> actionClass, final Method actionMethod) {

		final ActionAnnotationData annotationData = detectActionAnnotationData(actionMethod);

		final ActionConfig actionConfig = madvocConfig.lookupActionConfig(annotationData);

		final ActionNames actionNames = new ActionNames();		// collector for all action names

		readPackageActionPath(actionNames, actionClass);

		readClassActionPath(actionNames, actionClass);

		readMethodActionPath(actionNames, actionMethod.getName(), annotationData, actionConfig);

		readMethodExtension(actionNames, annotationData, actionConfig);

		readMethodHttpMethod(actionNames, annotationData);

		final Class<? extends ActionNamingStrategy> actionPathNamingStrategy = parseMethodNamingStrategy(annotationData, actionConfig);

		ActionNamingStrategy namingStrategy;

		try {
			namingStrategy = ClassUtil.newInstance(actionPathNamingStrategy);

			contextInjectorComponent.injectContext(new Target(namingStrategy));
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}

		return namingStrategy.buildActionDef(actionClass, actionMethod, actionNames);
	}

	/**
	 * Parses java action method annotation and returns its action runtime.
	 *
	 * @param actionClass action class
	 * @param actionMethod action method
	 * @param actionDefinition optional action def, usually <code>null</code> so to be parsed
	 */
	public ActionRuntime parse(final Class<?> actionClass, final Method actionMethod, ActionDefinition actionDefinition) {
		final ActionAnnotationData annotationData = detectActionAnnotationData(actionMethod);

		final ActionConfig actionConfig = madvocConfig.lookupActionConfig(annotationData);

		// interceptors
		ActionInterceptor[] actionInterceptors = parseActionInterceptors(actionClass, actionMethod, actionConfig);

		// filters
		ActionFilter[] actionFilters = parseActionFilters(actionClass, actionMethod, actionConfig);

		// build action definition when not provided
		if (actionDefinition == null) {
			actionDefinition = parseActionDef(actionClass, actionMethod);
		}

		detectAndRegisterAlias(annotationData, actionDefinition);

		final boolean async = parseMethodAsyncFlag(annotationData);

		final Class<? extends ActionResult> actionResult = parseActionResult(annotationData);

		return createActionRuntime(
				actionClass, actionMethod,
				actionResult,
				actionFilters, actionInterceptors,
				actionDefinition,
				async,
				actionConfig);
	}

	/**
	 * Detects {@link jodd.madvoc.meta.ActionAnnotationData}.
	 */
	protected ActionAnnotationData detectActionAnnotationData(Method actionMethod) {
		ActionAnnotationData annotationData = null;
		for (ActionAnnotation actionAnnotation : madvocConfig.getActionAnnotationInstances()) {
			annotationData = actionAnnotation.readAnnotatedElement(actionMethod);
			if (annotationData != null) {
				break;
			}
		}
		return annotationData;
	}

	/**
	 * Detects if alias is defined in annotation and registers it if so.
	 */
	protected void detectAndRegisterAlias(ActionAnnotationData annotationData, ActionDefinition actionDefinition) {
		final String alias = parseMethodAlias(annotationData);

		if (alias != null) {
			String aliasPath = StringUtil.cutToIndexOf(actionDefinition.actionPath(), StringPool.HASH);
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

	protected ActionInterceptor[] parseActionInterceptors(final Class<?> actionClass, final Method actionMethod, final ActionConfig actionConfig) {
		Class<? extends ActionInterceptor>[] interceptorClasses = readActionInterceptors(actionMethod);
		if (interceptorClasses == null) {
			interceptorClasses = readActionInterceptors(actionClass);
		}
		if (interceptorClasses == null) {
			interceptorClasses = actionConfig.getInterceptors();
		}

		return interceptorsManager.resolveAll(actionConfig, interceptorClasses);
	}

	protected ActionFilter[] parseActionFilters(Class<?> actionClass, Method actionMethod, ActionConfig actionConfig) {
		Class<? extends ActionFilter>[] filterClasses = readActionFilters(actionMethod);
		if (filterClasses == null) {
			filterClasses = readActionFilters(actionClass);
		}
		if (filterClasses == null) {
			filterClasses = actionConfig.getFilters();
		}

		return filtersManager.resolveAll(actionConfig, filterClasses);
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
	 * or if it is equals to default action names.
	 */
	protected void readMethodActionPath(ActionNames actionNames, String methodName, ActionAnnotationData annotationData, ActionConfig actionConfig) {
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
		for (String path : actionConfig.getActionMethodNames()) {
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
	protected void readMethodExtension(ActionNames actionNames, ActionAnnotationData annotationData, ActionConfig actionConfig) {
		String extension = actionConfig.getExtension();
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
	private Class<? extends ActionNamingStrategy> parseMethodNamingStrategy(ActionAnnotationData annotationData, ActionConfig actionConfig) {
		Class<? extends ActionNamingStrategy> actionNamingStrategyClass = null;

		if (annotationData != null) {
			actionNamingStrategyClass = annotationData.getPath();

			if (actionNamingStrategyClass == ActionNamingStrategy.class) {
				actionNamingStrategyClass = null;
			}
		}

		if (actionNamingStrategyClass == null) {
			actionNamingStrategyClass = actionConfig.getNamingStrategy();
		}

		return actionNamingStrategyClass;
	}

	// ---------------------------------------------------------------- create action runtime

	/**
	 * Creates new instance of action runtime configuration.
	 * Initialize caches.
	 */
	public ActionRuntime createActionRuntime(
			Class actionClass,
			Method actionClassMethod,
			Class<? extends ActionResult> actionResult,
			ActionFilter[] filters,
			ActionInterceptor[] interceptors,
			ActionDefinition actionDefinition,
			boolean async,
			ActionConfig actionConfig)
	{

		// 1) find ins and outs

		Class[] paramTypes = actionClassMethod.getParameterTypes();
		MethodParam[] params = new MethodParam[paramTypes.length];

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

				params[paramIndex] = new MethodParam(
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

		return new ActionRuntime(
				actionClass,
				actionClassMethod,
				filters,
				interceptors,
				actionDefinition,
				actionResult,
				async,
				allScopeData,
				params,
				actionConfig);
	}

}

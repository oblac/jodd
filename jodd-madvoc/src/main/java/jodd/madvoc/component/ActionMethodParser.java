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

import jodd.introspector.Mapper;
import jodd.introspector.MapperFunction;
import jodd.introspector.MapperFunctionInstances;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionHandler;
import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.config.ActionDefinition;
import jodd.madvoc.config.ActionNames;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.MethodParam;
import jodd.madvoc.config.ScopeData;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.ActionAnnotationValues;
import jodd.madvoc.meta.Async;
import jodd.madvoc.meta.Auth;
import jodd.madvoc.meta.FilteredBy;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.RenderWith;
import jodd.madvoc.meta.method.DELETE;
import jodd.madvoc.meta.method.GET;
import jodd.madvoc.meta.method.HEAD;
import jodd.madvoc.meta.method.OPTIONS;
import jodd.madvoc.meta.method.PATCH;
import jodd.madvoc.meta.method.POST;
import jodd.madvoc.meta.method.PUT;
import jodd.madvoc.meta.method.TRACE;
import jodd.madvoc.path.ActionNamingStrategy;
import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.NoneActionResult;
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

	@SuppressWarnings("unchecked")
	private static final Class<? extends Annotation>[] METHOD_ANNOTATIONS = new Class[] {
		DELETE.class,
		GET.class,
		HEAD.class,
		POST.class,
		PUT.class,
		OPTIONS.class,
		TRACE.class,
		PATCH.class
	};

	@PetiteInject
	protected ContextInjectorComponent contextInjectorComponent;

	@PetiteInject
	protected ActionConfigManager actionConfigManager;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected InterceptorsManager interceptorsManager;

	@PetiteInject
	protected FiltersManager filtersManager;

	@PetiteInject
	protected RootPackages rootPackages;

	@PetiteInject
	protected ScopeDataInspector scopeDataInspector;

	@PetiteInject
	protected ActionMethodParamNameResolver actionMethodParamNameResolver;

	/**
	 * Parses action class and method and creates {@link ActionDefinition parsed action definition}.
	 */
	public ActionDefinition parseActionDefinition(final Class<?> actionClass, final Method actionMethod) {

		final ActionAnnotationValues annotationValues = detectActionAnnotationValues(actionMethod);

		final ActionConfig actionConfig = resolveActionConfig(annotationValues);

		final String[] packageActionNames = readPackageActionPath(actionClass);

		final String[] classActionNames = readClassActionPath(actionClass);

		final String[] methodActionNames = readMethodActionPath(actionMethod.getName(), annotationValues, actionConfig);

		final String method = readMethodHttpMethod(actionMethod);

		final ActionNames actionNames = new ActionNames(packageActionNames, classActionNames, methodActionNames, method);

		final ActionNamingStrategy namingStrategy;

		try {
			namingStrategy = ClassUtil.newInstance(actionConfig.getNamingStrategy());

			contextInjectorComponent.injectContext(namingStrategy);
		} catch (final Exception ex) {
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
		final ActionAnnotationValues annotationValues = detectActionAnnotationValues(actionMethod);

		final ActionConfig actionConfig = resolveActionConfig(annotationValues);

		// interceptors
		final ActionInterceptor[] actionInterceptors = parseActionInterceptors(actionClass, actionMethod, actionConfig);

		// filters
		final ActionFilter[] actionFilters = parseActionFilters(actionClass, actionMethod, actionConfig);

		// build action definition when not provided
		if (actionDefinition == null) {
			actionDefinition = parseActionDefinition(actionClass, actionMethod);
		}

		detectAndRegisterAlias(annotationValues, actionDefinition);

		final boolean async = parseMethodAsyncFlag(actionMethod);

		final boolean auth = parseMethodAuthFlag(actionMethod);

		final Class<? extends ActionResult> actionResult = parseActionResult(actionMethod);
		final Class<? extends ActionResult> defaultActionResult = actionConfig.getActionResult();

		return createActionRuntime(
			null,
			actionClass,
			actionMethod,
			actionResult,
			defaultActionResult,
			actionFilters,
			actionInterceptors,
			actionDefinition,
			async,
			auth);
	}

	/**
	 * Resolves action config.
	 */
	protected ActionConfig resolveActionConfig(final ActionAnnotationValues annotationValues) {
		final Class<? extends Annotation> annotationType;

		if (annotationValues == null) {
			annotationType = Action.class;
		}
		else {
			annotationType = annotationValues.annotationType();
		}
		return actionConfigManager.lookup(annotationType);
	}

	/**
	 * Detects {@link jodd.madvoc.meta.ActionAnnotationValues}. Returns {@code null} if annotation does not exist.
	 */
	protected ActionAnnotationValues detectActionAnnotationValues(final Method actionMethod) {
		return actionConfigManager.readAnnotationValue(actionMethod);
	}

	/**
	 * Detects if alias is defined in annotation and registers it if so.
	 */
	protected void detectAndRegisterAlias(final ActionAnnotationValues annotationValues, final ActionDefinition actionDefinition) {
		final String alias = parseMethodAlias(annotationValues);

		if (alias != null) {
			final String aliasPath = StringUtil.cutToIndexOf(actionDefinition.actionPath(), StringPool.HASH);
			actionsManager.registerPathAlias(alias, aliasPath);
		}
	}

	protected Class<? extends ActionResult> parseActionResult(final Method actionMethod) {
		final RenderWith renderWith = actionMethod.getAnnotation(RenderWith.class);

		if (renderWith != null) {
			return renderWith.value();
		}

		return null;
	}

	protected ActionInterceptor[] parseActionInterceptors(final Class<?> actionClass, final Method actionMethod, final ActionConfig actionConfig) {
		Class<? extends ActionInterceptor>[] interceptorClasses = readActionInterceptors(actionMethod);
		if (interceptorClasses == null) {
			interceptorClasses = readActionInterceptors(actionClass);
		}
		if (interceptorClasses == null) {
			interceptorClasses = actionConfig.getInterceptors();
		}

		return interceptorsManager.resolveAll(interceptorClasses);
	}

	protected ActionFilter[] parseActionFilters(final Class<?> actionClass, final Method actionMethod, final ActionConfig actionConfig) {
		Class<? extends ActionFilter>[] filterClasses = readActionFilters(actionMethod);
		if (filterClasses == null) {
			filterClasses = readActionFilters(actionClass);
		}
		if (filterClasses == null) {
			filterClasses = actionConfig.getFilters();
		}

		return filtersManager.resolveAll(filterClasses);
	}

	// ---------------------------------------------------------------- interceptors

	/**
	 * Reads class or method annotation for action interceptors.
	 */
	protected Class<? extends ActionInterceptor>[] readActionInterceptors(final AnnotatedElement actionClassOrMethod) {
		Class<? extends ActionInterceptor>[] result = null;
		final InterceptedBy interceptedBy = actionClassOrMethod.getAnnotation(InterceptedBy.class);
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
	protected Class<? extends ActionFilter>[] readActionFilters(final AnnotatedElement actionClassOrMethod) {
		Class<? extends ActionFilter>[] result = null;
		final FilteredBy filteredBy = actionClassOrMethod.getAnnotation(FilteredBy.class);
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
	 * Reads action path for package.
	 * If annotation is not set on package-level, class package will be used for
	 * package action path part.
	 */
	protected String[] readPackageActionPath(final Class actionClass) {
		Package actionPackage = actionClass.getPackage();

		final String actionPackageName = actionPackage.getName();

		// 1 - read annotations first

		String packageActionPathFromAnnotation;

		mainloop:
		while (true) {
			final MadvocAction madvocActionAnnotation = actionPackage.getAnnotation(MadvocAction.class);

			packageActionPathFromAnnotation = madvocActionAnnotation != null ? madvocActionAnnotation.value().trim() : null;

			if (StringUtil.isEmpty(packageActionPathFromAnnotation)) {
				packageActionPathFromAnnotation = null;
			}

			if (packageActionPathFromAnnotation == null) {
				// next package
				String newPackage = actionPackage.getName();
				actionPackage = null;

				while (actionPackage == null) {
					final int ndx = newPackage.lastIndexOf('.');
					if (ndx == -1) {
						// end of hierarchy, nothing found
						break mainloop;
					}
					newPackage = newPackage.substring(0, ndx);
					actionClass.getClassLoader();
					actionPackage = Package.getPackage(newPackage);
				}
			}
			else {
				// annotation found, register root
				rootPackages.addRootPackage(actionPackage.getName(), packageActionPathFromAnnotation);
				break;
			}
		}

		// 2 - read root package

		final String packagePath = rootPackages.findPackagePathForActionPackage(actionPackageName);

		if (packagePath == null) {
			return ArraysUtil.array(null, null);
		}

		return ArraysUtil.array(
			StringUtil.stripChar(packagePath, '/'),
			StringUtil.surround(packagePath, StringPool.SLASH)
		);
	}

	/**
	 * Reads action path from class. If the class is annotated with {@link MadvocAction} annotation,
	 * class action path will be read from annotation value. Otherwise, action class path will be built from the
	 * class name. This is done by removing the package name and the last contained word
	 * (if there is more then one) from the class name. Such name is finally uncapitalized.
	 */
	protected String[] readClassActionPath(final Class actionClass) {
		// read class annotation
		final MadvocAction madvocActionAnnotation = ((Class<?>)actionClass).getAnnotation(MadvocAction.class);

		String classActionPath = madvocActionAnnotation != null ? madvocActionAnnotation.value().trim() : null;

		if (StringUtil.isEmpty(classActionPath)) {
			classActionPath = null;
		}

		String actionClassName = actionClass.getSimpleName();
		actionClassName = StringUtil.uncapitalize(actionClassName);
		actionClassName = MadvocUtil.stripLastCamelWord(actionClassName);       // removes 'Action' from the class name

		if (classActionPath == null) {
			classActionPath = actionClassName;
		}

		return ArraysUtil.array(actionClassName, classActionPath);
	}

	/**
	 * Reads action path from the action method.
	 */
	protected String[] readMethodActionPath(final String methodName, final ActionAnnotationValues annotationValues, final ActionConfig actionConfig) {
		// read annotation
		String methodActionPath = annotationValues != null ? annotationValues.value() : null;

		if (methodActionPath == null) {
			methodActionPath = methodName;
		} else {
			if (methodActionPath.equals(Action.NONE)) {
				return ArraysUtil.array(null, null);
			}
		}

		// check for defaults
		for (final String path : actionConfig.getActionMethodNames()) {
			if (methodActionPath.equals(path)) {
				methodActionPath = null;
				break;
			}
		}

		return ArraysUtil.array(methodName, methodActionPath);
	}

	/**
	 * Reads method's alias value.
	 */
	protected String parseMethodAlias(final ActionAnnotationValues annotationValues) {
		String alias = null;
		if (annotationValues != null) {
			alias = annotationValues.alias();
		}
		return alias;
	}

	/**
	 * Reads method's http method or {@code null} if not specified.
	 */
	private String readMethodHttpMethod(final Method actionMethod) {
		for (final Class<? extends Annotation> methodAnnotation : METHOD_ANNOTATIONS) {
			if (actionMethod.getAnnotation(methodAnnotation) != null) {
				return methodAnnotation.getSimpleName();
			}
		}

		return null;
	}

	/**
	 * Reads method's async flag.
	 */
	private boolean parseMethodAsyncFlag(final Method actionMethod) {
		return actionMethod.getAnnotation(Async.class) != null;
	}

	private boolean parseMethodAuthFlag(final Method actionMethod) {
		if (actionMethod.getAnnotation(Auth.class) != null) {
			return true;
		}
		final Class declaringClass = actionMethod.getDeclaringClass();
		if (declaringClass.getAnnotation(Auth.class) != null) {
			return true;
		}
		if (declaringClass.getPackage().getAnnotation(Auth.class) != null) {
			return true;
		}
		return false;
	}

	// ---------------------------------------------------------------- create action runtime

	/**
	 * Creates new instance of action runtime configuration.
	 * Initialize caches.
	 */
	public ActionRuntime createActionRuntime(
		final ActionHandler actionHandler,
		final Class actionClass,
		final Method actionClassMethod,
		final Class<? extends ActionResult> actionResult,
		final Class<? extends ActionResult> defaultActionResult,
		final ActionFilter[] filters,
		final ActionInterceptor[] interceptors,
		final ActionDefinition actionDefinition,
		final boolean async,
		final boolean auth)
	{
		if (actionHandler != null) {

			return new ActionRuntime(
				actionHandler,
				actionClass,
				actionClassMethod,
				filters,
				interceptors,
				actionDefinition,
				NoneActionResult.class,
				NoneActionResult.class,
				async,
				auth,
				null,
				null);

		}

		final ScopeData scopeData = scopeDataInspector.inspectClassScopes(actionClass);

		// find ins and outs

		final Class[] paramTypes = actionClassMethod.getParameterTypes();
		final MethodParam[] params = new MethodParam[paramTypes.length];

		final Annotation[][] paramAnns = actionClassMethod.getParameterAnnotations();
		String[] methodParamNames = null;


		// for all elements: action and method arguments...
		for (int ndx = 0; ndx < paramTypes.length; ndx++) {
			final Class paramType = paramTypes[ndx];

			// lazy init to postpone bytecode usage, when method has no arguments
			if (methodParamNames == null) {
				methodParamNames = actionMethodParamNameResolver.resolveParamNames(actionClassMethod);
			}

			final String paramName = methodParamNames[ndx];

			final Annotation[] parameterAnnotations = paramAnns[ndx];

			final ScopeData paramsScopeData = scopeDataInspector.inspectMethodParameterScopes(paramName, paramType, parameterAnnotations);

			MapperFunction mapperFunction = null;
			for (final Annotation annotation : parameterAnnotations) {
				if (annotation instanceof Mapper) {
					mapperFunction = MapperFunctionInstances.get().lookup(((Mapper) annotation).value());
					break;
				}
			}

			params[ndx] = new MethodParam(
				paramTypes[ndx],
				paramName,
				scopeDataInspector.detectAnnotationType(parameterAnnotations),
				paramsScopeData,
				mapperFunction
			);
		}

		return new ActionRuntime(
				null,
				actionClass,
				actionClassMethod,
				filters,
				interceptors,
				actionDefinition,
				actionResult,
				defaultActionResult,
				async,
				auth,
				scopeData,
				params);
	}

}

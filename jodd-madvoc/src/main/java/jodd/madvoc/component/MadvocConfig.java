// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.RootPackages;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.macro.PathMacros;
import jodd.madvoc.macro.WildcardPathMacros;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.ActionAnnotation;
import jodd.madvoc.result.ServletDispatcherResult;
import jodd.upload.FileUploadFactory;
import jodd.upload.impl.AdaptiveFileUploadFactory;
import jodd.util.StringPool;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Madvoc configuration. This is the single place where component configuration is stored.
 * New custom component that requires configuration may override and enhance this config
 * with new configuration. 
 */
public class MadvocConfig {

	@SuppressWarnings({"unchecked"})
	public MadvocConfig() {
		setActionAnnotations(Action.class);
		encoding = StringPool.UTF_8;
		applyCharacterEncoding = true;
		fileUploadFactory = new AdaptiveFileUploadFactory();
		defaultResultType = ServletDispatcherResult.NAME;
		defaultInterceptors = new Class[] {ServletConfigInterceptor.class};
		defaultFilters = null;
		defaultActionMethodNames = new String[] {"view", "execute"};
		createDefaultAliases = false;
		defaultExtension = "html";
		supplementAction = null;//DefaultActionSupplement.class;
		rootPackages = new RootPackages();
		madvocRootPackageClassName = "MadvocRootPackage";
		detectDuplicatePathsEnabled = true;
		actionPathMappingEnabled = false;
		preventCaching = true;
		requestScopeInjectorConfig = new RequestScopeInjector.Config();
		strictExtensionStripForResultPath = false;
		attributeMoveId = "_m_move_id";
		pathMacroClass = WildcardPathMacros.class;
		resultPathPrefix = null;
	}

	// ---------------------------------------------------------------- action method annotations

	protected Class<? extends Annotation>[] actionAnnotations;
	private ActionAnnotation<?>[] actionAnnotationInstances;

	/**
	 * Returns array of action annotations.
	 */
	public Class<? extends Annotation>[] getActionAnnotations() {
		return actionAnnotations;
	}

	/**
	 * Sets action annotations. User may define custom annotations with predefined values.
	 */
	@SuppressWarnings( {"unchecked"})
	public void setActionAnnotations(Class<? extends Annotation>... actionAnnotations) {
		this.actionAnnotations = actionAnnotations;

		this.actionAnnotationInstances = new ActionAnnotation<?>[actionAnnotations.length];
		for (int i = 0; i < actionAnnotations.length; i++) {
			Class<? extends Annotation> annotationClass = actionAnnotations[i];
			actionAnnotationInstances[i] = new ActionAnnotation(annotationClass);
		}
	}

	/**
	 * Returns instances of action method annotation readers.
	 */
	public ActionAnnotation<?>[] getActionAnnotationInstances() {
		return actionAnnotationInstances;
	}

	// ---------------------------------------------------------------- encoding

	protected String encoding;
	protected boolean applyCharacterEncoding;

	/**
	 * Returns character encoding.
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * Sets web application character encoding. If set to <code>null</code> encoding will be not applied.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Returns if character encoding should be set in request and response by Madvoc.
	 */
	public boolean isApplyCharacterEncoding() {
		return applyCharacterEncoding;
	}

	/**
	 * Defines is character encoding has to be set by Madvoc into the request and response.
	 */
	public void setApplyCharacterEncoding(boolean applyCharacterEncoding) {
		this.applyCharacterEncoding = applyCharacterEncoding;
	}

	// ---------------------------------------------------------------- file upload factory

	protected FileUploadFactory fileUploadFactory;

	/**
	 * Returns file upload factory.
	 */
	public FileUploadFactory getFileUploadFactory() {
		return fileUploadFactory;
	}

	/**
	 * Specifies file upload factory.
	 */
	public void setFileUploadFactory(FileUploadFactory fileUploadFactory) {
		this.fileUploadFactory = fileUploadFactory;
	}

	// ---------------------------------------------------------------- path

	protected String defaultExtension;
	protected String[] defaultActionMethodNames;

	/**
	 * Returns default action extension.
	 */
	public String getDefaultExtension() {
		return defaultExtension;
	}

	/**
	 * Sets default action extension that will be appended when omitted.
	 */
	public void setDefaultExtension(String defaultExtension) {
		this.defaultExtension = defaultExtension;
	}

	/**
	 * Returns default action method names which will have empty method path.
	 */
	public String[] getDefaultActionMethodNames() {
		return defaultActionMethodNames;
	}

	/**
	 * Specifies default action names that do not have method paths.
	 */
	public void setDefaultActionMethodNames(String... defaultActionMethodNames) {
		this.defaultActionMethodNames = defaultActionMethodNames;
	}

	// ---------------------------------------------------------------- default interceptors & filters

	protected Class<? extends ActionInterceptor>[] defaultInterceptors;
	protected Class<? extends ActionFilter>[] defaultFilters;

	/**
	 * Returns default interceptors.
	 */
	public Class<? extends ActionInterceptor>[] getDefaultInterceptors() {
		return defaultInterceptors;
	}

	/**
	 * Set default interceptors.
	 */
	public void setDefaultInterceptors(Class<? extends ActionInterceptor>... defaultInterceptors) {
		this.defaultInterceptors = defaultInterceptors;
	}

	/**
	 * Returns default filters.
	 */
	public Class<? extends ActionFilter>[] getDefaultFilters() {
		return defaultFilters;
	}

	/**
	 * Set default filters.
	 */
	public void setDefaultFilters(Class<? extends ActionFilter>[] defaultFilters) {
		this.defaultFilters = defaultFilters;
	}

	// ---------------------------------------------------------------- default result type

	protected String defaultResultType;

	/**
	 * Specifies default result type.
	 */
	public void setDefaultResultType(String type) {
		defaultResultType = type;
	}

	/**
	 * Returns default action result type.
	 */
	public String getDefaultResultType() {
		return defaultResultType;
	}

	// ---------------------------------------------------------------- path aliases

	protected boolean createDefaultAliases;

	public boolean isCreateDefaultAliases() {
		return createDefaultAliases;
	}

	/**
	 * Specifies if default aliases should be created for all
	 * action paths.
	 */
	public void setCreateDefaultAliases(boolean createDefaultAliases) {
		this.createDefaultAliases = createDefaultAliases;
	}

	// ---------------------------------------------------------------- supplement action

	protected Class supplementAction;

	/**
	 * Returns supplement action class for action requests that are not registered explicitly.
	 */
	public Class getSupplementAction() {
		return supplementAction;
	}

	/**
	 * Specifies new supplement action. If set to <code>null</code> supplement actions
	 * will not be used.
	 */
	public void setSupplementAction(Class supplementAction) {
		this.supplementAction = supplementAction;
	}

	/**
	 * Disable supplement actions.
	 */
	public void disableSupplementAction() {
		this.supplementAction = null;
	}

	// ---------------------------------------------------------------- packageRoot

	protected RootPackages rootPackages;
	protected String madvocRootPackageClassName;

	/**
	 * Returns root packages collection.
	 */
	public RootPackages getRootPackages() {
		return rootPackages;
	}

	/**
	 * Returns root package marker class name.
	 * Returns <code>null</code> if these classes should be ignored.
	 */
	public String getMadvocRootPackageClassName() {
		return madvocRootPackageClassName;
	}

	/**
	 * Sets root package marker name. By setting it to <code>null</code>
	 * this feature will be turned off.
	 */
	public void setMadvocRootPackageClassName(String madvocRootPackageClassName) {
		this.madvocRootPackageClassName = madvocRootPackageClassName;
	}

	// ---------------------------------------------------------------- duplicates

	protected boolean detectDuplicatePathsEnabled;

	public boolean isDetectDuplicatePathsEnabled() {
		return detectDuplicatePathsEnabled;
	}

	/**
	 * Defines if duplicate paths should be detected and if an exception should
	 * be thrown on duplication.
	 */
	public void setDetectDuplicatePathsEnabled(boolean detectDuplicatePathsEnabled) {
		this.detectDuplicatePathsEnabled = detectDuplicatePathsEnabled;
	}

	// ---------------------------------------------------------------- mapping

	protected boolean actionPathMappingEnabled;

	public boolean isActionPathMappingEnabled() {
		return actionPathMappingEnabled;
	}

	/**
	 * Defines if reverse action path mapping should be enabled. This means
	 * that classes are not registered before, but searched in runtime.
	 */
	public void setActionPathMappingEnabled(boolean actionPathMappingEnabled) {
		this.actionPathMappingEnabled = actionPathMappingEnabled;
	}

	// ---------------------------------------------------------------- caching

	protected boolean preventCaching;

	public boolean isPreventCaching() {
		return preventCaching;
	}

	/**
	 * Specifies if Madvoc should add response params to prevent browser caching.
	 */
	public void setPreventCaching(boolean preventCaching) {
		this.preventCaching = preventCaching;
	}

	// ---------------------------------------------------------------- request

	protected RequestScopeInjector.Config requestScopeInjectorConfig;

	public RequestScopeInjector.Config getRequestScopeInjectorConfig() {
		return requestScopeInjectorConfig;
	}

	/**
	 * Sets {@link jodd.madvoc.injector.RequestScopeInjector request scope injector} configuration.
	 */
	public void setRequestScopeInjectorConfig(RequestScopeInjector.Config requestScopeInjectorConfig) {
		this.requestScopeInjectorConfig = requestScopeInjectorConfig;
	}

	// ---------------------------------------------------------------- result

	protected boolean strictExtensionStripForResultPath;
	protected String resultPathPrefix;

	public boolean isStrictExtensionStripForResultPath() {
		return strictExtensionStripForResultPath;
	}

	/**
	 * Specifies if action path extension should be stripped only if it is equal
	 * to defined one, during result path creation.
	 */
	public void setStrictExtensionStripForResultPath(boolean strictExtensionStripForResultPath) {
		this.strictExtensionStripForResultPath = strictExtensionStripForResultPath;
	}

	public String getResultPathPrefix() {
		return resultPathPrefix;
	}

	/**
	 * Defines result path prefix that will be added to all relative result paths.
	 * If set to <code>null</code> will be ignored.
	 */
	public void setResultPathPrefix(String resultPathPrefix) {
		this.resultPathPrefix = resultPathPrefix;
	}

	// ---------------------------------------------------------------- attributes names

	protected String attributeMoveId;

	public String getAttributeMoveId() {
		return attributeMoveId;
	}

	/**
	 * Sets attribute name for {@link jodd.madvoc.result.MoveResult move results}.
	 */
	public void setAttributeMoveId(String attributeMoveId) {
		this.attributeMoveId = attributeMoveId;
	}

	// ---------------------------------------------------------------- path macro class

	protected Class<? extends PathMacros> pathMacroClass;

	/**
	 * Returns current implementation for path macros.
	 */
	public Class<? extends PathMacros> getPathMacroClass() {
		return pathMacroClass;
	}

	/**
	 * Sets implementation for path macros.
	 */
	public void setPathMacroClass(Class<? extends PathMacros> pathMacroClass) {
		this.pathMacroClass = pathMacroClass;
	}


	// ---------------------------------------------------------------- toString

	/**
	 * Prepares string with full configuration.
	 */
	@Override
	public String toString() {
		return "MadvocConfig{" +
				"\n\tactionAnnotations=" + (actionAnnotations == null ? null : toString(actionAnnotations)) +
				",\n\tactionPathMappingEnabled=" + actionPathMappingEnabled +
				",\n\tapplyCharacterEncoding=" + applyCharacterEncoding +
				",\n\tattributeMoveId='" + attributeMoveId + '\'' +
				",\n\tcreateDefaultAliases=" + createDefaultAliases +
				",\n\tdefaultActionMethodNames=" + (defaultActionMethodNames == null ? null : Arrays.asList(defaultActionMethodNames)) +
				",\n\tdefaultExtension='" + defaultExtension + '\'' +
				",\n\tdefaultInterceptors=" + (defaultInterceptors == null ? null : toString(defaultInterceptors)) +
				",\n\tdefaultResultType='" + defaultResultType + '\'' +
				",\n\tdetectDuplicatePathsEnabled=" + detectDuplicatePathsEnabled +
				",\n\tencoding='" + encoding + '\'' +
				",\n\tfileUploadFactory=" + fileUploadFactory +
				",\n\tpathMacroClass=" + pathMacroClass.getName() +
				",\n\tpreventCaching=" + preventCaching +
				",\n\trequestScopeInjectorConfig=" + requestScopeInjectorConfig +
				",\n\trootPackages=" + rootPackages +
				",\n\tmadvocRootPackageClassName='" + madvocRootPackageClassName + '\'' +
				",\n\tstrictExtensionStripForResultPath=" + strictExtensionStripForResultPath +
				",\n\tsupplementAction=" + supplementAction +
				"\n}";
	}

	private static String toString(Class[] classes) {
		String s = "";
		for (Class clazz : classes) {
			s += "\n\t\t" + clazz.getName();
		}
		return s;
	}
}

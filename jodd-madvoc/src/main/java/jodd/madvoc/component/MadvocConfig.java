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

import jodd.madvoc.RootPackages;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.macro.PathMacros;
import jodd.madvoc.macro.WildcardPathMacros;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.ActionAnnotation;
import jodd.madvoc.meta.RestAction;
import jodd.madvoc.path.ActionNamingStrategy;
import jodd.madvoc.path.DefaultActionPath;
import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.ServletDispatcherResult;
import jodd.upload.FileUploadFactory;
import jodd.upload.impl.AdaptiveFileUploadFactory;
import jodd.util.StringPool;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import static jodd.util.StringPool.COLON;
import static jodd.util.StringPool.DOLLAR_LEFT_BRACE;
import static jodd.util.StringPool.RIGHT_BRACE;

/**
 * Madvoc configuration. This is the single place where component configuration is stored.
 * New custom component that requires configuration may override and enhance this config
 * with new configuration. 
 */
public class MadvocConfig {

	@SuppressWarnings({"unchecked"})
	public MadvocConfig() {
		setActionAnnotations(Action.class, RestAction.class);
		encoding = StringPool.UTF_8;
		applyCharacterEncoding = true;
		fileUploadFactory = new AdaptiveFileUploadFactory();
		defaultActionResult = ServletDispatcherResult.class;
		defaultInterceptors = new Class[] {ServletConfigInterceptor.class};
		defaultFilters = null;
		defaultActionMethodNames = new String[] {"view", "execute"};
		defaultExtension = "html";
		defaultNamingStrategy = DefaultActionPath.class;
		rootPackages = new RootPackages();
		madvocRootPackageClassName = "MadvocRootPackage";
		detectDuplicatePathsEnabled = true;
		preventCaching = true;
		attributeMoveId = "_m_move_id";
		pathMacroClass = WildcardPathMacros.class;
		pathMacroSeparators = new String[] {DOLLAR_LEFT_BRACE, COLON, RIGHT_BRACE};
		resultPathPrefix = null;
		asyncConfig = new AsyncConfig();
		routesFileName = "madvoc-routes.txt";
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
	protected Class<? extends ActionNamingStrategy> defaultNamingStrategy;

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

	public Class<? extends ActionNamingStrategy> getDefaultNamingStrategy() {
		return defaultNamingStrategy;
	}

	/**
	 * Specifies default {@link jodd.madvoc.path.ActionNamingStrategy} action naming strategy.
	 */
	public void setDefaultNamingStrategy(Class<? extends ActionNamingStrategy> defaultNamingStrategy) {
		this.defaultNamingStrategy = defaultNamingStrategy;
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

	protected Class<? extends ActionResult> defaultActionResult;

	/**
	 * Specifies default action result.
	 */
	public void setDefaultActionResult(Class<? extends ActionResult> defaultActionResult) {
		this.defaultActionResult = defaultActionResult;
	}

	/**
	 * Returns default action result.
	 */
	public Class<? extends ActionResult> getDefaultActionResult() {
		return defaultActionResult;
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

	// ---------------------------------------------------------------- result

	protected String resultPathPrefix;

	/**
	 * Returns default prefix for all result paths.
	 * Returns <code>null</code> when not used.
	 */
	public String getResultPathPrefix() {
		return resultPathPrefix;
	}

	/**
	 * Defines result path prefix that will be added to all result paths.
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
	protected String[] pathMacroSeparators;

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


	public String[] getPathMacroSeparators() {
		return pathMacroSeparators;
	}

	/**
	 * Sets path macro separators.
	 */
	public void setPathMacroSeparators(String[] pathMacroSeparators) {
		this.pathMacroSeparators = pathMacroSeparators;
	}

	// ---------------------------------------------------------------- async

	public static class AsyncConfig {

		protected int corePoolSize = 10;
		protected int maximumPoolSize = 25;
		protected long keepAliveTimeMillis = 50000L;
		protected int queueCapacity = 100;

		public int getCorePoolSize() {
			return corePoolSize;
		}

		public void setCorePoolSize(int corePoolSize) {
			this.corePoolSize = corePoolSize;
		}

		public int getMaximumPoolSize() {
			return maximumPoolSize;
		}

		public void setMaximumPoolSize(int maximumPoolSize) {
			this.maximumPoolSize = maximumPoolSize;
		}

		public long getKeepAliveTimeMillis() {
			return keepAliveTimeMillis;
		}

		public void setKeepAliveTimeMillis(long keepAliveTimeMillis) {
			this.keepAliveTimeMillis = keepAliveTimeMillis;
		}

		public int getQueueCapacity() {
			return queueCapacity;
		}

		public void setQueueCapacity(int queueCapacity) {
			this.queueCapacity = queueCapacity;
		}

		@Override
		public String toString() {
			return "AsyncConfig{" + corePoolSize + " of " + maximumPoolSize + " in " + queueCapacity + " for " + keepAliveTimeMillis + "ms}";
		}
	}

	protected AsyncConfig asyncConfig;

	/**
	 * Returns asynchronous configuration.
	 */
	public AsyncConfig getAsyncConfig() {
		return asyncConfig;
	}

	// ----------------------------------------------------------------

	protected String routesFileName;

	public String getRoutesFileName() {
		return routesFileName;
	}

	public void setRoutesFileName(String routesFileName) {
		this.routesFileName = routesFileName;
	}

	// ---------------------------------------------------------------- toString

	/**
	 * Prepares string with full configuration.
	 */
	@Override
	public String toString() {
		return "MadvocConfig{" +
				"\n\tactionAnnotations=" + (actionAnnotations == null ? null : toString(actionAnnotations)) +
				",\n\tapplyCharacterEncoding=" + applyCharacterEncoding +
				",\n\tattributeMoveId='" + attributeMoveId + '\'' +
				",\n\tdefaultActionMethodNames=" + (defaultActionMethodNames == null ? null : Arrays.asList(defaultActionMethodNames)) +
				",\n\tdefaultExtension='" + defaultExtension + '\'' +
				",\n\tdefaultInterceptors=" + (defaultInterceptors == null ? null : toString(defaultInterceptors)) +
				",\n\tdefaultResultType='" + defaultActionResult.getName() + '\'' +
				",\n\tdetectDuplicatePathsEnabled=" + detectDuplicatePathsEnabled +
				",\n\tencoding='" + encoding + '\'' +
				",\n\tfileUploadFactory=" + fileUploadFactory +
				",\n\tpathMacroClass=" + pathMacroClass.getName() +
				",\n\tpreventCaching=" + preventCaching +
				",\n\trootPackages=" + rootPackages +
				",\n\tmadvocRootPackageClassName='" + madvocRootPackageClassName + '\'' +
				",\n\tasyncConfig='" + asyncConfig + '\'' +
				",\n\troutesFileName='" + routesFileName + '\'' +
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

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

package jodd.madvoc;

import jodd.io.upload.FileUploadFactory;
import jodd.io.upload.impl.AdaptiveFileUploadFactory;
import jodd.madvoc.config.RootPackages;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.macro.PathMacros;
import jodd.madvoc.macro.RegExpPathMacros;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.ActionConfiguredBy;
import jodd.madvoc.meta.RestAction;
import jodd.madvoc.path.DefaultActionPathNamingStrategy;
import jodd.util.ArraysUtil;
import jodd.util.ClassUtil;
import jodd.util.StringPool;
import jodd.util.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static jodd.util.StringPool.COLON;
import static jodd.util.StringPool.LEFT_BRACE;
import static jodd.util.StringPool.RIGHT_BRACE;

/**
 * Madvoc configuration. This is the single place where component configuration is stored.
 * New custom component that requires configuration may override and enhance this config
 * with new configuration.
 * <p>
 * This class is instantiated in {@link WebApp}. Use {@link WebApp#configure(Consumer)} to change the
 * configuration.
 */
public final class MadvocConfig {

	@SuppressWarnings({"unchecked"})
	public MadvocConfig() {
		actionConfig = new ActionConfig(null);
		actionConfig.setActionMethodNames("view", "execute");
		actionConfig.setActionResult(null);
		actionConfig.setFilters();
		actionConfig.setInterceptors(ServletConfigInterceptor.class);
		actionConfig.setNamingStrategy(DefaultActionPathNamingStrategy.class);

		setActionAnnotations(Action.class, RestAction.class);

		encoding = StringPool.UTF_8;
		applyCharacterEncoding = true;
		fileUploadFactory = new AdaptiveFileUploadFactory();
		rootPackages = new RootPackages();
		detectDuplicatePathsEnabled = true;
		preventCaching = true;
		pathMacroClass = RegExpPathMacros.class; //WildcardPathMacros.class;
		pathMacroSeparators = new String[] {LEFT_BRACE, COLON, RIGHT_BRACE};
		resultPathPrefix = null;

		defaultViewExtensions = new String[] {".jspf", ".jsp"};
		defaultViewPageName = "index";
	}

	// ---------------------------------------------------------------- action configs

	private ActionConfig actionConfig;

	/**
	 * Returns default {@link ActionConfig}.
	 */
	public ActionConfig getActionConfig() {
		return actionConfig;
	}

	/**
	 * Sets default action configuration.
	 */
	public void setActionConfig(final ActionConfig actionConfig) {
		Objects.requireNonNull(actionConfig);
		this.actionConfig = actionConfig;
	}

	// ---------------------------------------------------------------- action method annotations

	private Map<Class<? extends Annotation>, ActionConfig> annotations = new HashMap<>();
	private Class<? extends Annotation>[] actionAnnotations = ClassUtil.emptyClassArray();
	private AnnotationParser[] annotationParsers = new AnnotationParser[0];

	public void setActionAnnotations(final Class<? extends Annotation>... annotationsClasses) {

		for (Class<? extends Annotation> annotationType : annotationsClasses) {
			ActionConfiguredBy actionConfiguredBy = annotationType.getAnnotation(ActionConfiguredBy.class);

			if (actionConfiguredBy != null) {
				Class<? extends ActionConfig> actionConfigClass = actionConfiguredBy.value();
				ActionConfig newActionConfig;

				try {
					Constructor<? extends ActionConfig> ctor = actionConfigClass.getDeclaredConstructor(ActionConfig.class);
					newActionConfig = ctor.newInstance(this.actionConfig);
				}
				catch (Exception ex) {
					throw new MadvocException("Invalid action configuration: " + actionConfigClass.getSimpleName(), ex);
				}

				annotations.put(annotationType, newActionConfig);
			}

			actionAnnotations = ArraysUtil.append(actionAnnotations, annotationType);
			annotationParsers = ArraysUtil.append(annotationParsers, new AnnotationParser(annotationType, Action.class));
		}
	}

	/**
	 * Returns array of action annotations.
	 */
	public Class<? extends Annotation>[] getActionAnnotations() {
		return actionAnnotations;
	}

	/**
	 * Returns instances of action method annotation readers.
	 */
	public AnnotationParser[] getAnnotationParsers() {
		return annotationParsers;
	}

	/**
	 * Lookups action config for given annotation. If annotations is not registered, returns default
	 * action configuration.
	 */
	public ActionConfig lookupActionConfig(final Class<? extends Annotation> annotationType) {
		return annotations.getOrDefault(annotationType, actionConfig);
	}

	// ---------------------------------------------------------------- encoding

	private String encoding;
	private boolean applyCharacterEncoding;

	/**
	 * Returns character encoding.
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * Sets web application character encoding. If set to <code>null</code> encoding will be not applied.
	 */
	public void setEncoding(final String encoding) {
		Objects.requireNonNull(encoding);
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
	public void setApplyCharacterEncoding(final boolean applyCharacterEncoding) {
		this.applyCharacterEncoding = applyCharacterEncoding;
	}

	// ---------------------------------------------------------------- file upload factory

	private FileUploadFactory fileUploadFactory;

	/**
	 * Returns file upload factory.
	 */
	public FileUploadFactory getFileUploadFactory() {
		return fileUploadFactory;
	}

	/**
	 * Specifies file upload factory.
	 */
	public void setFileUploadFactory(final FileUploadFactory fileUploadFactory) {
		this.fileUploadFactory = fileUploadFactory;
	}


	// ---------------------------------------------------------------- packageRoot

	private RootPackages rootPackages;

	/**
	 * Returns root packages collection.
	 */
	public RootPackages getRootPackages() {
		return rootPackages;
	}

	// ---------------------------------------------------------------- duplicates

	private boolean detectDuplicatePathsEnabled;

	public boolean isDetectDuplicatePathsEnabled() {
		return detectDuplicatePathsEnabled;
	}

	/**
	 * Defines if duplicate paths should be detected and if an exception should
	 * be thrown on duplication.
	 */
	public void setDetectDuplicatePathsEnabled(final boolean detectDuplicatePathsEnabled) {
		this.detectDuplicatePathsEnabled = detectDuplicatePathsEnabled;
	}

	// ---------------------------------------------------------------- caching

	private boolean preventCaching;

	public boolean isPreventCaching() {
		return preventCaching;
	}

	/**
	 * Specifies if Madvoc should add response params to prevent browser caching.
	 */
	public void setPreventCaching(final boolean preventCaching) {
		this.preventCaching = preventCaching;
	}

	// ---------------------------------------------------------------- result

	private String resultPathPrefix;

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
	public void setResultPathPrefix(final String resultPathPrefix) {
		this.resultPathPrefix = resultPathPrefix;
	}

	// ---------------------------------------------------------------- path macro class

	private Class<? extends PathMacros> pathMacroClass;
	private String[] pathMacroSeparators;

	/**
	 * Returns current implementation for path macros.
	 */
	public Class<? extends PathMacros> getPathMacroClass() {
		return pathMacroClass;
	}

	/**
	 * Sets implementation for path macros.
	 */
	public void setPathMacroClass(final Class<? extends PathMacros> pathMacroClass) {
		this.pathMacroClass = pathMacroClass;
	}


	public String[] getPathMacroSeparators() {
		return pathMacroSeparators;
	}

	/**
	 * Sets path macro separators.
	 */
	public void setPathMacroSeparators(final String... pathMacroSeparators) {
		this.pathMacroSeparators = pathMacroSeparators;
	}

	// ---------------------------------------------------------------- default

	private String[] defaultViewExtensions;
	private String defaultViewPageName;

	public String[] getDefaultViewExtensions() {
		return defaultViewExtensions;
	}

	public void setDefaultViewExtensions(String[] defaultViewExtensions) {
		this.defaultViewExtensions = defaultViewExtensions;
	}

	public String getDefaultViewPageName() {
		return defaultViewPageName;
	}

	public void setDefaultViewPageName(String defaultViewPageName) {
		this.defaultViewPageName = defaultViewPageName;
	}
}

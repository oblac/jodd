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

import jodd.core.JoddCore;
import jodd.exception.UncheckedException;
import jodd.io.findfile.ClassScanner;
import jodd.props.Props;
import jodd.typeconverter.Converter;
import jodd.util.ClassLoaderUtil;
import jodd.util.ClassUtil;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.nio.charset.StandardCharsets;

/**
 * Maintain the lifecycle of a Madvoc {@link WebApp}.
 */
public class Madvoc {

	/**
	 * Web application.
	 */
	public static final String PARAM_MADVOC_WEBAPP = "madvoc.webapp";
	/**
	 * Madvoc configurator.
	 */
	public static final String PARAM_MADVOC_CONFIGURATOR = "madvoc.configurator";
	/**
	 * List of Madvoc params and properties files to be found on classpath.
	 */
	public static final String PARAM_MADVOC_PARAMS = "madvoc.params";

	private static Logger log;
	
	// ---------------------------------------------------------------- statics

	private static final String MADVOC_ATTR = Madvoc.class.getName();

	/**
	 * Returns <code>Madvoc</code> instance from servlet context.
	 * May return <code>null</code> indicating <code>Madvoc</code>
	 * is not yet initialized.
	 */
	public static Madvoc get(final ServletContext servletContext) {
		return (Madvoc) servletContext.getAttribute(MADVOC_ATTR);
	}

	// ---------------------------------------------------------------- config

	protected String webAppClassName;
	protected Class webAppClass = WebApp.class;
	protected String[] paramsFiles;
	protected String madvocConfiguratorClassName;
	protected Class madvocConfiguratorClass = AutomagicMadvocConfigurator.class;
	
	/**
	 * Sets {@link WebApp} class name.
	 */
	public void setWebAppClassName(final String webAppClass) {
		this.webAppClassName = webAppClass;
		this.webAppClass = null;
	}

	/**
	 * Sets {@link WebApp} class.
	 */
	public void setWebAppClass(final Class webAppClass) {
		this.webAppClass = webAppClass;
		this.webAppClassName = null;
	}

	/**
	 * Sets the name of the class that is going to be used for configuration of user actions.
	 */
	public void setMadvocConfiguratorClassName(final String madvocConfiguratorClassName) {
		this.madvocConfiguratorClassName = madvocConfiguratorClassName;
		this.madvocConfiguratorClass = null;
	}

	/**
	 * Sets class that will be used for configuring the user actions.
	 */
	public void setMadvocConfiguratorClass(final Class madvocConfiguratorClass) {
		this.madvocConfiguratorClass = madvocConfiguratorClass;
		this.madvocConfiguratorClassName = null;
	}

	public void setParamsFiles(final String[] paramsFiles) {
		this.paramsFiles = paramsFiles;
	}

	/**
	 * Configures Madvoc by reading context init parameters.
	 */
	public void configureWith(final ServletContext servletContext) {
		webAppClassName = servletContext.getInitParameter(PARAM_MADVOC_WEBAPP);
		paramsFiles = Converter.get().toStringArray(servletContext.getInitParameter(PARAM_MADVOC_PARAMS));
		madvocConfiguratorClassName = servletContext.getInitParameter(PARAM_MADVOC_CONFIGURATOR);
	}

	// ---------------------------------------------------------------- lifecycle

	protected WebApp webapp;
	protected ServletContext servletContext;

	/**
	 * Returns web application once it is started.
	 */
	public WebApp webapp() {
		return webapp;
	}

	/**
	 * Creates and starts new <code>Madvoc</code> web application.
	 * <code>Madvoc</code> instance is stored in servlet context.
	 * Important: <code>servletContext</code> may be <code>null</code>,
	 * when web application is run out from container.
	 */
	@SuppressWarnings("InstanceofCatchParameter")
	public WebApp startWebApplication(final ServletContext servletContext) {
		try {
			final WebApp webApp = _start(servletContext);

			log.info("Madvoc is up and running.");

			return webApp;
		}
		catch (final Exception ex) {
			if (log != null) {
				log.error("Madvoc startup failure.", ex);
			} else {
				ex.printStackTrace();
			}
			if (ex instanceof MadvocException) {
				throw (MadvocException) ex;
			}
			throw new MadvocException(ex);
		}
	}
	
	private WebApp _start(final ServletContext servletContext) {
		if (servletContext != null) {
			this.servletContext = servletContext;

			servletContext.setAttribute(MADVOC_ATTR, this);
		}

		webapp = createWebApplication();

		// init logger

		log = LoggerFactory.getLogger(Madvoc.class);

		// configure webapp

		webapp.bindServletContext(servletContext);

		if (paramsFiles != null) {
			final Props params = loadMadvocParams(paramsFiles);

			webapp.withParams(params);
		}

		resolveMadvocConfigClass();

		if (madvocConfiguratorClass != null) {
			webapp.registerComponent(madvocConfiguratorClass);
		}

		// initialize

		log.info("Madvoc starting...");

		webapp.start();

		return webapp;
	}

	/**
	 * Stops <em>Madvoc</em> web application.
	 */
	public void stopWebApplication() {
		log.info("Madvoc shutting down...");

		if (servletContext != null) {
			servletContext.removeAttribute(MADVOC_ATTR);
		}

		webapp.shutdown();

		webapp = null;
	}

	// ---------------------------------------------------------------- loading configuration

	/**
	 * Creates {@link WebApp}.
	 */
	protected WebApp createWebApplication() {
		if ((webAppClassName == null) && (webAppClass == null)) {
			return new WebApp();
		}

		final WebApp webApp;
		try {
			if (webAppClassName != null) {
				webAppClass = ClassLoaderUtil.loadClass(webAppClassName);
			}
			webApp = (WebApp) ClassUtil.newInstance(webAppClass);
		}
		catch (final Exception ex) {
			throw new MadvocException("Unable to load Madvoc web application class: " + webAppClassName, ex);
		}

		return webApp;
	}

	/**
	 * Loads Madvoc parameters. New {@link Props} is created from the classpath.
	 */
	protected Props loadMadvocParams(final String[] patterns) {
		if (log.isInfoEnabled()) {
			log.info("Loading Madvoc parameters from: " + Converter.get().toString(patterns));
		}
		try {
			final Props props = new Props();
			loadFromClasspath(props, patterns);
			return props;
		} catch (final Exception ex) {
			throw new MadvocException("Unable to load Madvoc parameters from: " +
					Converter.get().toString(patterns) + ".properties': " + ex.toString(), ex);
		}
	}

	private void loadFromClasspath(final Props props, final String... patterns) {
		ClassScanner.create()
				.registerEntryConsumer(entryData -> {
					String usedEncoding = JoddCore.encoding;
					if (StringUtil.endsWithIgnoreCase(entryData.name(), ".properties")) {
						usedEncoding = StandardCharsets.ISO_8859_1.name();
					}

					final String encoding = usedEncoding;
					UncheckedException.runAndWrapException(() -> props.load(entryData.openInputStream(), encoding));
				})
				.includeResources(true)
				.ignoreException(true)
				.excludeCommonJars()
				.excludeAllEntries(true)
				.includeEntries(patterns)
				.scanDefaultClasspath()
				.start();
	}



	/**
	 * Loads Madvoc component that will be used for configuring the user actions.
	 * If class name is <code>null</code>, default {@link AutomagicMadvocConfigurator}
	 * will be used.
	 */
	protected void resolveMadvocConfigClass() {
		if ((madvocConfiguratorClassName == null) && (madvocConfiguratorClass == null)) {
			return;
		}

		try {
			if (madvocConfiguratorClassName != null) {
				madvocConfiguratorClass = ClassLoaderUtil.loadClass(madvocConfiguratorClassName);
			}

			log.info("Configuring Madvoc using: " + madvocConfiguratorClass.getName());
		} catch (final Exception ex) {
			throw new MadvocException("Unable to load Madvoc configurator class: " + madvocConfiguratorClassName, ex);
		}
	}

}

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

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.config.AutomagicMadvocConfigurator;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.props.Props;
import jodd.props.PropsUtil;
import jodd.typeconverter.Converter;
import jodd.util.ClassLoaderUtil;

import javax.servlet.ServletContext;

/**
 * Maintain the lifecycle of a Madvoc {@link jodd.madvoc.WebApplication}.
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

	/**
	 * Context attribute name.
	 */
	public static final String MADVOC_ATTR = Madvoc.class.getName();

	/**
	 * Returns <code>Madvoc</code> instance from servlet context.
	 * May return <code>null</code> indicating <code>Madvoc</code>
	 * is not yet initialized.
	 */
	public static Madvoc get(ServletContext servletContext) {
		return (Madvoc) servletContext.getAttribute(MADVOC_ATTR);
	}

	// ---------------------------------------------------------------- config

	protected String webAppClassName;
	protected Class webAppClass;
	protected String[] paramsFiles;
	protected String madvocConfiguratorClassName;
	protected Class madvocConfiguratorClass;
	
	/**
	 * Sets {@link WebApplication} class name.
	 */
	public void setWebAppClassName(String webAppClass) {
		this.webAppClassName = webAppClass;
	}

	/**
	 * Sets {@link WebApplication} class.
	 */
	public void setWebAppClass(Class webAppClass) {
		this.webAppClass = webAppClass;
	}

	/**
	 * Sets {@link MadvocConfigurator} class name.
	 */
	public void setMadvocConfiguratorClassName(String madvocConfiguratorClassName) {
		this.madvocConfiguratorClassName = madvocConfiguratorClassName;
	}

	/**
	 * Sets {@link MadvocConfigurator} class.
	 */
	public void setMadvocConfiguratorClass(Class madvocConfiguratorClass) {
		this.madvocConfiguratorClass = madvocConfiguratorClass;
	}

	public void setParamsFiles(String[] paramsFiles) {
		this.paramsFiles = paramsFiles;
	}

	/**
	 * Configures Madvoc by reading context init parameters.
	 */
	public void configureWith(ServletContext servletContext) {
		webAppClassName = servletContext.getInitParameter(PARAM_MADVOC_WEBAPP);
		paramsFiles = Converter.get().toStringArray(servletContext.getInitParameter(PARAM_MADVOC_PARAMS));
		madvocConfiguratorClassName = servletContext.getInitParameter(PARAM_MADVOC_CONFIGURATOR);
	}



	// ---------------------------------------------------------------- lifecycle

	protected WebApplication webapp;
	protected ServletContext servletContext;

	/**
	 * Returns web application once it is started.
	 */
	public WebApplication webapp() {
		return webapp;
	}

	/**
	 * Creates and starts new <code>Madvoc</code> web application.
	 * <code>Madvoc</code> instance is stored in servlet context.
	 * Important: <code>servletContext</code> may be <code>null</code>,
	 * when web application is run out from container.
	 */
	@SuppressWarnings("InstanceofCatchParameter")
	public WebApplication startNewWebApplication(ServletContext servletContext) {
		try {
			WebApplication webApplication = _start(servletContext);

			log.info("Madvoc is up and running.");

			return webApplication;
		}
		catch (Exception ex) {
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
	
	private WebApplication _start(ServletContext servletContext) {
		if (servletContext != null) {
			this.servletContext = servletContext;

			servletContext.setAttribute(MADVOC_ATTR, this);
		}

		webapp = createWebApplication();

		// init logger

		log = LoggerFactory.getLogger(Madvoc.class);

		// configure webapplication

		webapp.withServletContext(servletContext);

		if (paramsFiles != null) {
			Props params = loadMadvocParams(paramsFiles);

			webapp.withParams(params);
		}

		// initialize

		log.info("Madvoc starting...");

		webapp.init();

		// configure with external configurator
		MadvocConfigurator configurator = loadMadvocConfig();
		webapp.configure(configurator);


		webapp.ready();

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
	 * Loads {@link WebApplication}. If class name is <code>null</code>,
	 * default web application will be loaded.
	 */
	protected WebApplication createWebApplication() {
		if ((webAppClassName != null) && (webAppClass != null)) {
			throw new MadvocException("Ambiguous WebApplication setting");
		}
		if ((webAppClassName == null) && (webAppClass == null)) {
			return new WebApplication();
		}

		final WebApplication webApp;
		try {
			if (webAppClass == null) {
				webAppClass = ClassLoaderUtil.loadClass(webAppClassName);
			}
			webApp = (WebApplication) webAppClass.newInstance();
		}
		catch (Exception ex) {
			throw new MadvocException("Unable to load Madvoc web application class: " + webAppClassName, ex);
		}

		return webApp;
	}

	/**
	 * Loads Madvoc parameters. New {@link Props} is created from the classpath.
	 */
	protected Props loadMadvocParams(String[] patterns) {
		if (log.isInfoEnabled()) {
			log.info("Loading Madvoc parameters from: " + Converter.get().toString(patterns));
		}
		try {
			return PropsUtil.createFromClasspath(patterns);
		} catch (Exception ex) {
			throw new MadvocException("Unable to load Madvoc parameters from: " +
					Converter.get().toString(patterns) + ".properties': " + ex.toString(), ex);
		}
	}


	/**
	 * Loads {@link jodd.madvoc.config.MadvocConfigurator}. If class name is <code>null</code>,
	 * {@link jodd.madvoc.config.AutomagicMadvocConfigurator} will be created.
	 */
	protected MadvocConfigurator loadMadvocConfig() {
		if ((madvocConfiguratorClassName != null) && (madvocConfiguratorClass != null)) {
			throw new MadvocException("Ambiguous MadvocConfigurator setting: both class and class name is set.");
		}
		if ((madvocConfiguratorClassName == null) && (madvocConfiguratorClass == null)) {
			log.info("Configuring Madvoc using default automagic configurator");
			return new AutomagicMadvocConfigurator();
		}

		MadvocConfigurator configurator;
		try {
			if (madvocConfiguratorClass == null) {
				madvocConfiguratorClass = ClassLoaderUtil.loadClass(madvocConfiguratorClassName);
			}

			configurator = (MadvocConfigurator) madvocConfiguratorClass.newInstance();
			log.info("Configuring Madvoc using configurator: " + madvocConfiguratorClass.getName());
		} catch (Exception ex) {
			throw new MadvocException("Unable to load Madvoc configurator class: " + madvocConfiguratorClassName, ex);
		}
		return configurator;
	}

}
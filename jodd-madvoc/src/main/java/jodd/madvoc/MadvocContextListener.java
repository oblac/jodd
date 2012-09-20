// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Creates {@link Madvoc} @{link WebApplication}. It's an alternative way how to
 * start web application - before any other Jodd framework is used and called.
 */
public class MadvocContextListener implements ServletContextListener {

	protected Madvoc madvoc;

	/**
	 * Creates new {@link Madvoc} @{link WebApplication} that is configured
	 * by context init parameters.
	 */
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();

		madvoc = new Madvoc();
		madvoc.configure(servletContext);
		madvoc.startNewWebApplication(servletContext);
	}

	/**
	 * Stops Madvoc.
	 */
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		madvoc.stopWebApplication();
	}

}
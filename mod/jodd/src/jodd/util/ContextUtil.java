// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.InitialContext;

/**
 * Simple context helper.
 */
public class ContextUtil {

	public static final String J2EE_ENV_JNDI_NAME       = "java:comp/env/";
	public static final String JDBC_JNDI_NAME           = J2EE_ENV_JNDI_NAME + "jdbc/";
	public static final String JMS_JNDI_NAME            = J2EE_ENV_JNDI_NAME + "jms/";
	public static final String MAIL_JNDI_NAME           = J2EE_ENV_JNDI_NAME + "mail/";
	public static final String URL_JNDI_NAME            = J2EE_ENV_JNDI_NAME + "url/";

	private static InitialContext initContext;

	/**
	 * Gets initial naming context from default name service URL and default name service type.
	 */
	public static InitialContext getInitContext() throws NamingException {
		if (initContext == null) {
			initContext = new InitialContext();
		}
		return initContext;
	}

	/**
	 * Closes naming context.
	 */
	public static void close(Context ctx) {
		if (ctx != null) {
			try {
				ctx.close();
			} catch (NamingException nex) {
				// ignore
			}
		}
	}
}

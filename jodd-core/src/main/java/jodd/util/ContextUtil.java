// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.InitialContext;

/**
 * Global context storage.
 */
public class ContextUtil {

	private static InitialContext initContext;

	/**
	 * Returns initial naming context from default name service URL and default name service type.
	 */
	public static InitialContext getInitialContext() throws NamingException {
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

	/**
	 * Closes naming context.
	 */
	public static void close() {
		close(initContext);
	}
}
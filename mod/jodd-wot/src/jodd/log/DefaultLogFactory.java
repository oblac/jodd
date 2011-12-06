// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.log;

/**
 * Default {@link LogFactory} implementation.
 * Checks if <b>SLF4J</b> is present, if not fails back to dummy log implementation.
 */
public class DefaultLogFactory extends LogFactory {

	private LogFactory logFactory;

	public DefaultLogFactory() {
		logFactory = resolveLogFactory();
	}

	/**
	 * Detects if some logger factory exist.
	 */
	protected LogFactory resolveLogFactory() {
		try {
			Class.forName("org.slf4j.LoggerFactory");
			return new Slf4jLogFactory();
		} catch (ClassNotFoundException ignore) {
		}
		return null;
	}

	@Override
	public Log getLogger(String name) {
		if (logFactory == null) {
			return new DummyLog(name);
		}
		return logFactory.getLogger(name);
	}
}

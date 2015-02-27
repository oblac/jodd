// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.log.Logger;
import jodd.log.LoggerFactory;

/**
 * Returns {@link DbSession} assigned to current thread.
 * @see DbThreadSession
 * @see ThreadDbSessionHolder
 */
public class ThreadDbSessionProvider implements DbSessionProvider {

	private static final Logger log = LoggerFactory.getLogger(ThreadDbSessionProvider.class);

	/**
	 * {@inheritDoc}
	 */
	public DbSession getDbSession() {
		log.debug("Requesting thread session");

		DbSession session = ThreadDbSessionHolder.get();

		if (session == null) {
			throw new DbSqlException(
					"No DbSession associated with current thread." +
					"It seems that ThreadDbSessionHolder is not used.");
		}
		return session;
	}


}

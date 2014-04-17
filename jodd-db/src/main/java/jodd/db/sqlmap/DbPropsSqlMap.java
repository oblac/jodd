// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.sqlmap;

import jodd.db.DbManager;
import jodd.props.Props;
import jodd.props.PropsUtil;

/**
 * <code>SqlMap</code> implementation based on <code>Props</code> files.
 */
public class DbPropsSqlMap implements SqlMap {

	protected final String[] patterns;
	protected Props props;

	public DbPropsSqlMap(String... patterns) {
		this.patterns = patterns;
		load();
	}

	public DbPropsSqlMap() {
		this("*.sql.props", "*.oom.props");
	}

	/**
	 * Returns <code>Props</code> object.
	 */
	public Props getProps() {
		return props;
	}

	/**
	 * {@inheritDoc}
	 */
	public void load() {
		props = PropsUtil.createFromClasspath(patterns);
	}

	// ---------------------------------------------------------------- sql

	/**
	 * {@inheritDoc}
	 */
	public String getQuery(String key) {
		if (DbManager.getInstance().isDebug()) {
			load();
		}
		return props.getValue(key);
	}
}
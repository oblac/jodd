// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.querymap;

import jodd.db.DbManager;
import jodd.props.Props;
import jodd.props.PropsUtil;

/**
 * {@link jodd.db.querymap.QueryMap} implementation based on
 * {@link jodd.props.Props} properties files.
 * Scans for <code>"*.sql.props"</code> and <code>"*.oom.props"</code>
 * properties on class path.
 */
public class DbPropsQueryMap implements QueryMap {

	protected final String[] patterns;
	protected Props props;

	public DbPropsQueryMap(String... patterns) {
		this.patterns = patterns;
		load();
	}

	public DbPropsQueryMap() {
		this("*.sql.props", "*.oom.props");
	}

	/**
	 * Returns <code>Props</code> for additional fine tuning.
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
	 * Returns query for given key.
	 * In debug mode, props are reloaded every time before the lookup.
	 */
	public String getQuery(String key) {
		if (DbManager.getInstance().isDebug()) {
			load();
		}
		return props.getValue(key);
	}
}
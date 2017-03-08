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
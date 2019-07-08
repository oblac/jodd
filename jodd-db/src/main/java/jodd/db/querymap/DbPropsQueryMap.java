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

import jodd.props.Props;

/**
 * {@link jodd.db.querymap.QueryMap} implementation based on
 * {@link jodd.props.Props} properties files.
 * Scans for <code>"*.sql.props"</code> and <code>"*.oom.props"</code>
 * properties on class path.
 */
public class DbPropsQueryMap implements QueryMap {

	protected final String[] patterns;
	protected Props props;

	public DbPropsQueryMap(final String... patterns) {
		this.patterns = patterns;
		reload();
	}

	public DbPropsQueryMap() {
		this("*.sql.props", "*.oom.props", "*.sql.properties", "*.oom.properties");
	}

	/**
	 * Returns <code>Props</code>.
	 */
	public Props props() {
		return props;
	}

	@Override
	public void reload() {
		props = new Props();
		props.loadFromClasspath(patterns);
	}

	@Override
	public int size() {
		return props.countTotalProperties();
	}

	// ---------------------------------------------------------------- sql

	/**
	 * Returns query for given key.
	 * In debug mode, props are reloaded every time before the lookup.
	 */
	@Override
	public String getQuery(final String key) {
		return props.getValue(key);
	}
}
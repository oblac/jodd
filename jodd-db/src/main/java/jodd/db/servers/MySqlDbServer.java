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

package jodd.db.servers;

import jodd.db.oom.DbOomConfig;

/**
 * MySQL.
 */
public class MySqlDbServer implements DbServer {

	private final String version;

	public MySqlDbServer(final String version) {
		this.version = version;
	}

	@Override
	public void accept(final DbOomConfig dbOomConfig) {
		// Database and table names are not case sensitive in Windows,
		// and case sensitive in most varieties of Unix. One notable exception is Mac OS X,
		// which is Unix-based but uses a default file system type (HFS+) that is not case sensitive.
		dbOomConfig.getTableNames().setLowercase(true);

		// Column and index names are not case sensitive on any platform, nor are column aliases.
		dbOomConfig.getColumnNames().setLowercase(true);

		// quote character
		dbOomConfig.getColumnNames().setQuoteChar('`');
	}

	@Override
	public String toString() {
		return "DbServer: MySQL v" + version;
	}
}

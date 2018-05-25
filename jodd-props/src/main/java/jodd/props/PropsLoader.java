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

package jodd.props;

import jodd.core.JoddCore;
import jodd.exception.UncheckedException;
import jodd.io.findfile.ClassScanner;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * {@link Props} loaders.
 */
public class PropsLoader {

	/**
	 * Loads props from classpath.
	 */
	public static void loadFromClasspath(final Props p, final String... patterns) {
		ClassScanner.create()
			.registerEntryConsumer(entryData -> {
				String usedEncoding = JoddCore.encoding;
				if (StringUtil.endsWithIgnoreCase(entryData.name(), ".properties")) {
					usedEncoding = StringPool.ISO_8859_1;
				}

				final String encoding = usedEncoding;
				UncheckedException.runAndWrapException(() -> p.load(entryData.openInputStream(), encoding));
			})
		.includeResources(true)
		.ignoreException(true)
		.excludeAllEntries(true)
		.includeEntries(patterns)
		.scanDefaultClasspath()
		.start();
	}

	/**
	 * Creates new props and {@link #loadFromClasspath(Props, String...) loads from classpath}.
	 */
	public static Props createFromClasspath(final String... patterns) {
		final Props p = new Props();
		loadFromClasspath(p, patterns);
		return p;
	}

}

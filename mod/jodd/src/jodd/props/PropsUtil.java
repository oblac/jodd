// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.JoddDefault;
import jodd.io.findfile.ClasspathScanner;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.IOException;

/**
 * Some {@link Props} utilities.
 */
public class PropsUtil {

	/**
	 * Loads props and properties from the classpath.
	 */
	public static Props createFromClasspath(String... patterns) {
		final Props p = new Props();
		ClasspathScanner scanner = new ClasspathScanner() {
			@Override
			protected void onEntry(EntryData entryData) throws IOException {
				String encoding = JoddDefault.encoding;
				if (StringUtil.endsWithIgnoreCase(entryData.getName(), ".properties")) {
					encoding = StringPool.ISO_8859_1;
				}
				p.load(entryData.openInputStream(), encoding);
			}
		};
		scanner.includeResources(true).
				ignoreException(true).
				include(patterns).
				scanFullClasspath();
		return p;

	}
}

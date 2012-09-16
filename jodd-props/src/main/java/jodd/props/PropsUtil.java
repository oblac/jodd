// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.JoddDefault;
import jodd.io.findfile.ClassScanner;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.IOException;

/**
 * Some {@link Props} utilities.
 */
public class PropsUtil {

	/**
	 * Loads props from classpath.
	 */
	public static void loadFromClasspath(final Props p, String... patterns) {
		ClassScanner scanner = new ClassScanner() {
			@Override
			protected void onEntry(EntryData entryData) throws IOException {
				String encoding = JoddDefault.encoding;
				if (StringUtil.endsWithIgnoreCase(entryData.getName(), ".properties")) {
					encoding = StringPool.ISO_8859_1;
				}
				p.load(entryData.openInputStream(), encoding);
			}
		};
		scanner.setIncludeResources(true);
		scanner.setIgnoreException(true);
		scanner.setIncludedEntries(patterns);
		scanner.scanDefaultClasspath();
	}

	/**
	 * Creates new props and {@link #loadFromClasspath(Props, String...) loads from classpath}.
	 */
	public static Props createFromClasspath(String... patterns) {
		final Props p = new Props();
		loadFromClasspath(p, patterns);
		return p;
	}
}

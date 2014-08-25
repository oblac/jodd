// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.core;

import jodd.Jodd;
import jodd.io.FileUtilParams;
import jodd.util.StringPool;
import jodd.util.cl.ClassLoaderStrategy;
import jodd.util.cl.DefaultClassLoaderStrategy;

/**
 * Jodd CORE module.
 * Contains some global defaults.
 */
public class JoddCore {

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddCore.class);
	}

	/**
	 * Default temp file prefix.
	 */
	public static String tempFilePrefix = "jodd-";

	/**
	 * Default file encoding (UTF8).
	 */
	public static String encoding = StringPool.UTF_8;

	/**
	 * Default IO buffer size (16 KB).
	 */
	public static int ioBufferSize = 16384;

	/**
	 * Default parameters used in {@link jodd.io.FileUtil} operations.
	 */
	public static FileUtilParams fileUtilParams = new FileUtilParams();

	/**
	 * Default class loader strategy.
	 */
	public static ClassLoaderStrategy classLoaderStrategy = new DefaultClassLoaderStrategy();

}
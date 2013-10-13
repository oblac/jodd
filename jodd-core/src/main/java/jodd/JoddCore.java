// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.io.FileUtilParams;
import jodd.util.StringPool;

/**
 * Jodd CORE module.
 * Contains some global defaults.
 */
public class JoddCore {

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

}
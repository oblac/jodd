// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.util.MimeTypes;
import jodd.util.StringPool;

/**
 * Jodd HTTP module.
 */
public class JoddHttp {

	/**
	 * Default HTTP query parameters encoding (UTF-8).
	 */
	public static String defaultQueryEncoding = StringPool.UTF_8;

	/**
	 * Default form encoding (UTF-8).
	 */
	public static String defaultFormEncoding = StringPool.UTF_8;

	/**
	 * Default body media type (text/html).
	 */
	public static String defaultBodyMediaType = MimeTypes.MIME_TEXT_HTML;

	/**
	 * Default body encoding (UTF-8).
	 */
	public static String defaultBodyEncoding = StringPool.UTF_8;

}

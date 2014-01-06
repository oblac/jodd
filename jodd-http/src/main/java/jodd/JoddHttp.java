// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.http.HttpConnectionProvider;
import jodd.http.net.SocketHttpConnectionProvider;
import jodd.util.MimeTypes;
import jodd.util.StringPool;

/**
 * Jodd HTTP module.
 */
public class JoddHttp {

	/**
	 * Default HTTP transport provider.
	 */
	public static HttpConnectionProvider httpConnectionProvider = new SocketHttpConnectionProvider();

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
// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.Jodd;
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

	/**
	 * CSV of default enabled secured protocols. By default the value is
	 * read from system property <code>https.protocols</code>.
	 */
	public static String defaultSecureEnabledProtocols = System.getProperty("https.protocols");

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddHttp.class);
	}

}
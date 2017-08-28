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
	
	/**
	 * Default user agent (Jodd HTTP)
	 */
	public static String defaultUserAgent = "Jodd HTTP";

	/**
	 * Flag that controls if headers should be in strict mode. When strict mode is
	 * enabled, header keys are used as they are passed. If two headers with the same
	 * name but different capitalization are passed, they will both appear in the headers.
	 * When strict mode is off, header keys will be capitalized. Duplicate headers names
	 * with different capitalization will be used as they are named the same.
	 */
	public static boolean defaultStrictHeaders = false;

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddHttp.class);
	}

}

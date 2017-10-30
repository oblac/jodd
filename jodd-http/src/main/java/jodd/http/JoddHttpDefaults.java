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

import jodd.http.net.SocketHttpConnectionProvider;
import jodd.util.MimeTypes;
import jodd.util.StringPool;

public class JoddHttpDefaults {

	/**
	 * Default HTTP transport provider.
	 */
	private HttpConnectionProvider httpConnectionProvider = new SocketHttpConnectionProvider();

	/**
	 * Default HTTP query parameters encoding (UTF-8).
	 */
	private String queryEncoding = StringPool.UTF_8;

	/**
	 * Default form encoding (UTF-8).
	 */
	private String formEncoding = StringPool.UTF_8;

	/**
	 * Default body media type (text/html).
	 */
	private String bodyMediaType = MimeTypes.MIME_TEXT_HTML;

	/**
	 * Default body encoding (UTF-8).
	 */
	private String bodyEncoding = StringPool.UTF_8;

	/**
	 * CSV of default enabled secured protocols. By default the value is
	 * read from system property <code>https.protocols</code>.
	 */
	private String secureEnabledProtocols = System.getProperty("https.protocols");

	/**
	 * Default user agent (Jodd HTTP) value.
	 */
	private String userAgent = "Jodd HTTP";

	/**
	 * Flag that controls if headers should be rewritten and capitalized in PascalCase.
	 * When disabled, header keys are used as they are passed.
	 * When flag is enabled, header keys will be capitalized.
	 */
	private boolean capitalizeHeaderKeys = true;


	public HttpConnectionProvider getHttpConnectionProvider() {
		return httpConnectionProvider;
	}

	public void setHttpConnectionProvider(HttpConnectionProvider httpConnectionProvider) {
		this.httpConnectionProvider = httpConnectionProvider;
	}

	public String getQueryEncoding() {
		return queryEncoding;
	}

	public void setQueryEncoding(String queryEncoding) {
		this.queryEncoding = queryEncoding;
	}

	public String getFormEncoding() {
		return formEncoding;
	}

	public void setFormEncoding(String formEncoding) {
		this.formEncoding = formEncoding;
	}

	public String getBodyMediaType() {
		return bodyMediaType;
	}

	public void setBodyMediaType(String bodyMediaType) {
		this.bodyMediaType = bodyMediaType;
	}

	public String getBodyEncoding() {
		return bodyEncoding;
	}

	public void setBodyEncoding(String bodyEncoding) {
		this.bodyEncoding = bodyEncoding;
	}

	public String getSecureEnabledProtocols() {
		return secureEnabledProtocols;
	}

	public void setSecureEnabledProtocols(String secureEnabledProtocols) {
		this.secureEnabledProtocols = secureEnabledProtocols;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public boolean isCapitalizeHeaderKeys() {
		return capitalizeHeaderKeys;
	}

	public void setCapitalizeHeaderKeys(boolean capitalizeHeaderKeys) {
		this.capitalizeHeaderKeys = capitalizeHeaderKeys;
	}
}

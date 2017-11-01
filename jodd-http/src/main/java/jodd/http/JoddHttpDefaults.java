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

import jodd.util.MimeTypes;
import jodd.util.StringPool;

public class JoddHttpDefaults {

	private String queryEncoding = StringPool.UTF_8;
	private String formEncoding = StringPool.UTF_8;
	private String bodyMediaType = MimeTypes.MIME_TEXT_HTML;
	private String bodyEncoding = StringPool.UTF_8;
	private String secureEnabledProtocols = System.getProperty("https.protocols");
	private String userAgent = "Jodd HTTP";
	private boolean capitalizeHeaderKeys = true;

	/**
	 * Returns default query encoding.
	 */
	public String getQueryEncoding() {
		return queryEncoding;
	}

	/**
	 * Sets default HTTP query parameters encoding (UTF-8).
	 */
	public void setQueryEncoding(String queryEncoding) {
		this.queryEncoding = queryEncoding;
	}

	/**
	 * Returns default form encoding (UTF-8).
	 */
	public String getFormEncoding() {
		return formEncoding;
	}

	/**
	 * Sets default form encoding (UTF-8).
	 */
	public void setFormEncoding(String formEncoding) {
		this.formEncoding = formEncoding;
	}

	/**
	 * Returns body media type.
	 */
	public String getBodyMediaType() {
		return bodyMediaType;
	}

	/**
	 * Sets default body media type (text/html).
	 */
	public void setBodyMediaType(String bodyMediaType) {
		this.bodyMediaType = bodyMediaType;
	}

	/**
	 * Returns default body encoding (UTF-8).
	 */
	public String getBodyEncoding() {
		return bodyEncoding;
	}

	/**
	 * Sets default body encoding (UTF-8).
	 */
	public void setBodyEncoding(String bodyEncoding) {
		this.bodyEncoding = bodyEncoding;
	}

	/**
	 * @see #setSecureEnabledProtocols(String)
	 */
	public String getSecureEnabledProtocols() {
		return secureEnabledProtocols;
	}

	/**
	 * CSV of default enabled secured protocols. By default the value is
	 * read from system property <code>https.protocols</code>.
	 */
	public void setSecureEnabledProtocols(String secureEnabledProtocols) {
		this.secureEnabledProtocols = secureEnabledProtocols;
	}

	/**
	 * Returns default user agent value.
	 */
	public String getUserAgent() {
		return userAgent;
	}
	/**
	 * Sets default user agent value.
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * @see #setCapitalizeHeaderKeys(boolean)
	 */
	public boolean isCapitalizeHeaderKeys() {
		return capitalizeHeaderKeys;
	}

	/**
	 * Flag that controls if headers should be rewritten and capitalized in PascalCase.
	 * When disabled, header keys are used as they are passed.
	 * When flag is enabled, header keys will be capitalized.
	 */
	public void setCapitalizeHeaderKeys(boolean capitalizeHeaderKeys) {
		this.capitalizeHeaderKeys = capitalizeHeaderKeys;
	}
}

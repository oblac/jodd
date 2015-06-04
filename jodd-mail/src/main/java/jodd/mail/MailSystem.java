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

package jodd.mail;

/**
 * Defines mail system behavior. Override this class if you have special needs or
 * simply set parameters manually, but <b>before</b> first Mail use.
 * Be sure to use java mail {@literal >=} v1.4.4.
 */
public class MailSystem {

	protected boolean initialized;

	/**
	 * Defines Java Mail
	 * <a href="http://docs.oracle.com/javaee/6/api/javax/mail/internet/package-summary.html">system properties</a>.
	 */
	public final void defineJavaMailSystemProperties() {
		if (initialized) {
			return;
		}

		defineSystemProperties();

		initialized = true;
	}

	/**
	 * Defines system properties. Invoked only once.
	 */
	protected void defineSystemProperties() {
		/*
			If set to "true", the setFileName method uses the MimeUtility method encodeText to
			encode any non-ASCII characters in the filename. Note that this encoding violates
			the MIME specification, but is useful for interoperating with some mail clients
			that use this convention. The default is false.
		 */
		System.setProperty("mail.mime.encodefilename", "true");

		/*
			If set to "true", the getFileName method uses the MimeUtility method decodeText
			to decode any non-ASCII characters in the filename. Note that this decoding
			violates the MIME specification, but is useful for interoperating with some
			mail clients that use this convention. The default is false.
		 */
		System.setProperty("mail.mime.decodefilename", "true");
	}

}
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

import jodd.io.StreamUtil;
import jodd.util.StringPool;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Developer-friendly class for parsing EML files.
 */
public class EMLParser {

	/**
	 * Starts EML parsing with provided EML content.
	 */
	public static EMLParser loadEML(String emlContent, String charset) throws UnsupportedEncodingException {
		byte[] bytes = emlContent.getBytes(charset);
		return loadEML(bytes);
	}

	/**
	 * Starts EML parsing with provided EML content, assuming UTF-8 charset.
	 */
	public static EMLParser loadEML(String emlContent) throws UnsupportedEncodingException {
		return loadEML(emlContent, StringPool.UTF_8);
	}

	/**
	 * Starts EML parsing with provided EML content.
	 */
	public static EMLParser loadEML(byte[] content) {
		return new EMLParser(new ByteArrayInputStream(content));
	}
	/**
	 * Starts EML parsing with provided EML file.
	 */
	public static EMLParser loadEML(File emlFile) throws FileNotFoundException {
		return new EMLParser(new FileInputStream(emlFile));
	}

	protected final InputStream emlContentInputStream;
	protected Session session;

	protected EMLParser(InputStream emlContent) {
		this.emlContentInputStream = emlContent;
	}

	/**
	 * Sets the custom session.
	 */
	public EMLParser session(Session session) {
		this.session = session;
		return this;
	}

	/**
	 * Creates new session with given properties.
	 */
	public EMLParser session(Properties properties) {
		this.session = createSession(properties);
		return this;
	}

	/**
	 * Uses default session.
	 */
	public EMLParser defaultSession() {
		this.session = Session.getDefaultInstance(System.getProperties());
		return this;
	}


	/**
	 * Sets session property. If session is not defined, default
	 * session will be created.
	 */
	public EMLParser set(String name, String value) {
		if (session == null) {
			session = createSession(null);
		}
		this.session.getProperties().setProperty(name, value);
		return this;
	}

	/**
	 * Parses the EML content. If session is not created, default one
	 * will be used.
	 */
	public ReceivedEmail parse() throws MessagingException {
		if (session == null) {
			session = createSession(null);
		}

		Message message;
		try {
			message = new MimeMessage(session, emlContentInputStream);
		} finally {
			StreamUtil.close(emlContentInputStream);
		}

		return new ReceivedEmail(message);
	}

	/**
	 * Creates new session with or without custom properties.
	 */
	protected Session createSession(Properties properties) {
		if (properties == null) {
			properties = System.getProperties();
		}

		return Session.getInstance(properties);
	}

}
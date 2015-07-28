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

	public static EMLParser create() {
		return new EMLParser();
	}

	protected Session session;
	protected Properties properties;

	/**
	 * Assigns custom session. Any property will be ignored.
	 */
	public EMLParser session(Session session) {
		this.session = session;
		return this;
	}

	/**
	 * Uses default session. Any property will be ignored.
	 */
	public EMLParser defaultSession() {
		this.session = Session.getDefaultInstance(System.getProperties());
		return this;
	}

	/**
	 * Copies properties from given set. If session is already created,
	 * exception will be thrown.
	 */
	public EMLParser set(Properties properties) {
		initProperties();

		this.properties.putAll(properties);

		return this;
	}

	/**
	 * Sets property for the session. If session is already created, exception
	 * will be thrown.
	 */
	public EMLParser set(String name, String value) {
		initProperties();

		properties.setProperty(name, value);

		return this;
	}

	/**
	 * Parses EML with provided EML content.
	 */
	public ReceivedEmail parse(String emlContent, String charset) throws UnsupportedEncodingException, MessagingException {
		byte[] bytes = emlContent.getBytes(charset);
		return parse(bytes);
	}

	/**
	 * Parses EML with provided EML content.
	 */
	public ReceivedEmail parse(String emlContent) throws MessagingException {
		try {
			return parse(emlContent, StringPool.UTF_8);
		}
		catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	/**
	 * Parses EML with provided EML content.
	 */
	public ReceivedEmail parse(byte[] content) throws MessagingException {
		return parse(new ByteArrayInputStream(content));
	}
	/**
	 * Starts EML parsing with provided EML file.
	 */
	public ReceivedEmail parse(File emlFile) throws FileNotFoundException, MessagingException {
		return parse(new FileInputStream(emlFile));
	}

	/**
	 * Parses the EML content. If session is not created, default one
	 * will be used.
	 */
	protected ReceivedEmail parse(InputStream emlContentInputStream) throws MessagingException {
		if (session == null) {
			session = createSession(properties);
		}

		Message message;
		try {
			message = new MimeMessage(session, emlContentInputStream);
		} finally {
			StreamUtil.close(emlContentInputStream);
		}

		return new ReceivedEmail(message);
	}

	protected void initProperties() {
		if (session != null) {
			throw new MailException("Can't set properties after session is assigned");
		}

		if (properties == null) {
			properties = new Properties();
		}
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
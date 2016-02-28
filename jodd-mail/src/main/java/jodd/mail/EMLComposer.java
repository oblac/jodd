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

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EMLComposer {

	public static EMLComposer create() {
		return new EMLComposer();
	}

	protected Session session;
	protected Properties properties;

	/**
	 * Assigns custom session. Any property will be ignored.
	 */
	public EMLComposer session(Session session) {
		this.session = session;
		return this;
	}

	/**
	 * Uses default session. Any property will be ignored.
	 */
	public EMLComposer defaultSession() {
		this.session = Session.getDefaultInstance(System.getProperties());
		return this;
	}

	/**
	 * Copies properties from given set. If session is already created,
	 * exception will be thrown.
	 */
	public EMLComposer set(Properties properties) {
		initProperties();

		this.properties.putAll(properties);

		return this;
	}

	/**
	 * Sets property for the session. If session is already created, exception
	 * will be thrown.
	 */
	public EMLComposer set(String name, String value) {
		initProperties();

		properties.setProperty(name, value);

		return this;
	}

	/**
	 * Creates EML string from given Email.
	 */
	public String compose(Email email) {
		if (session == null) {
			session = createSession(properties);
		}

		OutputStreamTransport ost = new OutputStreamTransport(session);

		SendMailSession sendMailSession = new SendMailSession(session, ost);

		sendMailSession.sendMail(email);

		return ost.getEml();
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

	/**
	 * Special transport that writes message into the output stream.
	 */
	private static class OutputStreamTransport extends Transport {

		public OutputStreamTransport(Session session) {
			super(session, new URLName("JODD_MAIL_2_EML", null, -1, null, null, null));
		}

		@Override
		public void sendMessage(Message msg, Address[] addresses) throws MessagingException {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			try {
				msg.writeTo(outputStream);
			}
			catch (IOException e) {
				throw new MailException(e);
			}

			eml = outputStream.toString();
		}

		public String getEml() {
			return eml;
		}

		private String eml;
	}
}

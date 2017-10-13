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

import javax.mail.Authenticator;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

import static jodd.util.StringPool.TRUE;

/**
 * Represents simple plain POP server for sending emails.
 */
public class Pop3Server implements ReceiveMailSessionProvider {

	protected static final String MAIL_POP3_PORT = "mail.pop3.port";
	protected static final String MAIL_POP3_HOST = "mail.pop3.host";
	protected static final String MAIL_POP3_AUTH = "mail.pop3.auth";

	protected static final String PROTOCOL_POP3 = "pop3";

	protected static final int DEFAULT_POP3_PORT = 110;

	protected final String host;
	protected final int port;
	protected final Authenticator authenticator;
	protected final Properties sessionProperties;

	/**
	 * POP3 server defined with its host and default port.
	 */
	public Pop3Server(String host) {
		this(host, DEFAULT_POP3_PORT, null);
	}
	/**
	 * POP3 server defined with its host and port.
	 */
	public Pop3Server(String host, int port) {
		this(host, port, null);
	}

	public Pop3Server(String host, Authenticator authenticator) {
		this(host, DEFAULT_POP3_PORT, authenticator);
	}

	public Pop3Server(String host, int port, String username, String password) {
		this(host, port, new SimpleAuthenticator(username, password));
	}

	/**
	 * SMTP server defined with its host and authentication.
	 */
	public Pop3Server(String host, int port, Authenticator authenticator) {
		this.host = host;
		this.port = port;
		this.authenticator = authenticator;
		sessionProperties = createSessionProperties();
	}

	/**
	 * Prepares mail session properties.
	 */
	protected Properties createSessionProperties() {
		Properties props = new Properties();
		props.setProperty(MAIL_POP3_HOST, host);
		props.setProperty(MAIL_POP3_PORT, String.valueOf(port));
		if (authenticator != null) {
			props.setProperty(MAIL_POP3_AUTH, TRUE);
		}
		return props;
	}

	public Pop3Server setProperty(String name, String value) {
		sessionProperties.setProperty(name, value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public ReceiveMailSession createSession() {
		Session session = Session.getInstance(sessionProperties, authenticator);
		Store store;
		try {
			store = getStore(session);
		} catch (NoSuchProviderException nspex) {
			throw new MailException("Failed to create POP3 session", nspex);
		}
		return new ReceiveMailSession(session, store);
	}

	/**
	 * Returns email store.
	 */
	protected Store getStore(Session session) throws NoSuchProviderException {
		return session.getStore(PROTOCOL_POP3);
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns POP host address.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns authenticator.
	 */
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	/**
	 * Returns current port.
	 */
	public int getPort() {
		return port;
	}
}
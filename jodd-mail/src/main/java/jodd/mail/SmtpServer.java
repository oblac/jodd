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
import javax.mail.Transport;
import java.util.Properties;

import static jodd.util.StringPool.TRUE;

/**
 * Represents simple plain SMTP server for sending emails.
 */
public class SmtpServer<T extends SmtpServer> implements SendMailSessionProvider {

	public static final String MAIL_HOST = "mail.host";
	public static final String MAIL_SMTP_HOST = "mail.smtp.host";
	public static final String MAIL_SMTP_PORT = "mail.smtp.port";
	public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
	public static final String MAIL_SMTP_FROM = "mail.smtp.from";

	public static final String MAIL_SMTP_CONNECTIONTIMEOUT ="mail.smtp.connectiontimeout";
	public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
	public static final String MAIL_SMTP_WRITETIMEOUT = "mail.smtp.writetimeout";
	public static final String MAIL_DEBUG = "mail.debug";
	public static final String MAIL_MIME_ADDRESS_STRICT = "mail.mime.address.strict";

	protected static final String PROTOCOL_SMTP = "smtp";

	protected static final int DEFAULT_SMTP_PORT = 25;

	protected final String host;
	protected final int port;
	protected Authenticator authenticator;
	protected int timeout = 0;
	protected boolean debug = false;
	protected boolean strictAddress = true;
	private Properties additionalProperties;

	// ---------------------------------------------------------------- create

	public static SmtpServer create(String host) {
		return new SmtpServer(host, DEFAULT_SMTP_PORT);
	}

	public static SmtpServer create(String host, int port) {
		return new SmtpServer(host, port);
	}

	/**
	 * SMTP server defined with its host and default port.
	 */
	public SmtpServer(String host) {
		this.host = host;
		this.port = DEFAULT_SMTP_PORT;
	}
	/**
	 * SMTP server defined with its host and port.
	 */
	public SmtpServer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	// ---------------------------------------------------------------- builder

	public T authenticateWith(String username, String password) {
		this.authenticator = new SimpleAuthenticator(username, password);
		return (T) this;
	}

	public T authenticateWith(Authenticator authenticator) {
		this.authenticator = authenticator;
		return (T) this;
	}

	/**
	 * Defines timeout value in milliseconds for all mail-related operations.
	 */
	public T timeout(int timeout) {
		this.timeout = timeout;
		return (T) this;
	}

	/**
	 * Enables debug mode. By default it is turned off.
	 */
	public T debug(boolean debug) {
		this.debug = true;
		return (T) this;
	}

	/**
	 * Disables the strict address. By default strict mime address checking
	 * is turned on.
	 */
	public T strictAddress(boolean strictAddress) {
		this.strictAddress = strictAddress;
		return (T) this;
	}

	public T properties(Properties properties) {
		this.additionalProperties = properties;
		return (T) this;
	}

	public T property(String name, String value) {
		if (additionalProperties == null) {
			additionalProperties = new Properties();
		}
		this.additionalProperties.put(name, value);
		return (T) this;
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Creates mail session properties.
	 */
	protected Properties createSessionProperties() {
		Properties props = new Properties();

		props.setProperty(MAIL_TRANSPORT_PROTOCOL, PROTOCOL_SMTP);
		props.setProperty(MAIL_HOST, host);
		props.setProperty(MAIL_SMTP_HOST, host);
		props.setProperty(MAIL_SMTP_PORT, String.valueOf(port));

		if (authenticator != null) {
			props.setProperty(MAIL_SMTP_AUTH, TRUE);
		}

		if (timeout > 0) {
			String timeoutValue = String.valueOf(timeout);
			props.put(MAIL_SMTP_CONNECTIONTIMEOUT, timeoutValue);
			props.put(MAIL_SMTP_TIMEOUT, timeoutValue);
			props.put(MAIL_SMTP_WRITETIMEOUT, timeoutValue);
		}

		if (debug == true) {
			props.put(MAIL_DEBUG, "true");
		}

		if (strictAddress == false) {
			props.put(MAIL_MIME_ADDRESS_STRICT, "false");
		}

		return props;
	}

	/**
	 * {@inheritDoc}
	 */
	public SendMailSession createSession() {
		Properties sessionProperties = createSessionProperties();

		if (additionalProperties != null) {
			sessionProperties.putAll(additionalProperties);
		}

		Session mailSession = Session.getInstance(sessionProperties, authenticator);
		Transport mailTransport;
		try {
			mailTransport = getTransport(mailSession);
		} catch (NoSuchProviderException nspex) {
			throw new MailException(nspex);
		}
		return new SendMailSession(mailSession, mailTransport);
	}

	/**
	 * Returns mail transport.
	 */
	protected Transport getTransport(Session session) throws NoSuchProviderException {
		return session.getTransport(PROTOCOL_SMTP);
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns SMTP host address.
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

	/**
	 * Returns timeout in milliseconds.
	 */
	public int getTimeout() {
		return timeout;
	}

}

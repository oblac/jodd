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
public class SmtpServer<T extends SmtpServer<T>> extends MailServer<SendMailSession> {

	public static final String MAIL_HOST = "mail.host";
	public static final String MAIL_SMTP_HOST = "mail.smtp.host";
	public static final String MAIL_SMTP_PORT = "mail.smtp.port";
	public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
	public static final String MAIL_SMTP_FROM = "mail.smtp.from";

	public static final String MAIL_SMTP_CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";
	public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
	public static final String MAIL_SMTP_WRITETIMEOUT = "mail.smtp.writetimeout";
	public static final String MAIL_DEBUG = "mail.debug";
	public static final String MAIL_MIME_ADDRESS_STRICT = "mail.mime.address.strict";

	protected static final String PROTOCOL_SMTP = "smtp";

	/**
	 * Default SMTP port
	 */
	protected static final int DEFAULT_SMTP_PORT = 25;

	/**
	 * Whether debug mode is enabled.
	 */
	protected boolean debug = false;

	/**
	 * Whether strict address checking is turned on.
	 */
	protected boolean strictAddress = true;

	/**
	 * Connection timeout.
	 */
	private int timeout = 0;

	// ---------------------------------------------------------------- create

	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
	}

	public SmtpServer(final String host, final int port, final Authenticator authenticator) {
		super(host, port == -1 ? DEFAULT_SMTP_PORT : port, authenticator, null);
	}

	// ---------------------------------------------------------------- builder

	/**
	 * Defines timeout value in milliseconds for all mail-related operations.
	 *
	 * @param timeout timeout value in milliseconds.
	 * @return this
	 */
	public T timeout(final int timeout) {
		this.timeout = timeout;
		return _this();
	}

	/**
	 * Enable or disable debug mode.
	 *
	 * @param debug {@code true} to turn on debugging. By default, this is {@code false}.
	 * @return this
	 */
	public T debugMode(final boolean debug) {
		this.debug = debug;
		return _this();
	}


	/**
	 * Disables the strict address.
	 *
	 * @param strictAddress {@code true} if strict address checking should be be turned on. By default, this is {@code true}.
	 * @return this
	 */
	public T strictAddress(final boolean strictAddress) {
		this.strictAddress = strictAddress;
		return _this();
	}

	// ---------------------------------------------------------------- properties

	@Override
	protected Properties createSessionProperties() {
		final Properties props = new Properties();

		props.setProperty(MAIL_TRANSPORT_PROTOCOL, PROTOCOL_SMTP);
		props.setProperty(MAIL_HOST, getHost());
		props.setProperty(MAIL_SMTP_HOST, getHost());
		props.setProperty(MAIL_SMTP_PORT, String.valueOf(getPort()));

		if (getAuthenticator() != null) {
			props.setProperty(MAIL_SMTP_AUTH, TRUE);
		}

		if (timeout > 0) {
			final String timeoutValue = String.valueOf(timeout);
			props.put(MAIL_SMTP_CONNECTIONTIMEOUT, timeoutValue);
			props.put(MAIL_SMTP_TIMEOUT, timeoutValue);
			props.put(MAIL_SMTP_WRITETIMEOUT, timeoutValue);
		}

		if (debug) {
			props.put(MAIL_DEBUG, "true");
		}

		if (!strictAddress) {
			props.put(MAIL_MIME_ADDRESS_STRICT, "false");
		}

		return props;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@link SendMailSession}
	 */
	@Override
	public SendMailSession createSession() {
		final Session session = Session.getInstance(getSessionProperties(), getAuthenticator());
		final Transport mailTransport;
		try {
			mailTransport = getTransport(session);
		} catch (final NoSuchProviderException nspex) {
			throw new MailException(nspex);
		}
		return new SendMailSession(session, mailTransport);
	}

	/**
	 * Get the {@link Transport} for {@link Session}.
	 *
	 * @param session The {@link SendMailSession}.
	 * @return SMTP {@link Transport}.
	 * @throws NoSuchProviderException If provider for the given protocol is not found.
	 */
	protected Transport getTransport(final Session session) throws NoSuchProviderException {
		return session.getTransport(PROTOCOL_SMTP);
	}

}

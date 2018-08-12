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

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

import static jodd.util.StringPool.TRUE;

/**
 * Represents simple plain SMTP server for sending emails.
 */
public class SmtpServer extends MailServer<SendMailSession> {

	protected static final String PROTOCOL_SMTP = "smtp";

	/**
	 * Default SMTP port
	 */
	protected static final int DEFAULT_SMTP_PORT = 25;

	// ---------------------------------------------------------------- create

	public SmtpServer(final Builder builder) {
		super(builder, DEFAULT_SMTP_PORT);
	}
	protected SmtpServer(final Builder builder, final int defaultPort) {
		super(builder, defaultPort);
	}

	// ---------------------------------------------------------------- properties

	@Override
	protected Properties createSessionProperties() {
		final Properties props = super.createSessionProperties();

		props.setProperty(MAIL_TRANSPORT_PROTOCOL, PROTOCOL_SMTP);
		props.setProperty(MAIL_HOST, host);
		props.setProperty(MAIL_SMTP_HOST, host);
		props.setProperty(MAIL_SMTP_PORT, String.valueOf(port));

		if (authenticator != null) {
			props.setProperty(MAIL_SMTP_AUTH, TRUE);
		}

		if (timeout > 0) {
			final String timeoutValue = String.valueOf(timeout);
			props.put(MAIL_SMTP_CONNECTIONTIMEOUT, timeoutValue);
			props.put(MAIL_SMTP_TIMEOUT, timeoutValue);
			props.put(MAIL_SMTP_WRITETIMEOUT, timeoutValue);
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
		final Session session = Session.getInstance(createSessionProperties(), authenticator);
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

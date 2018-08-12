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
import java.io.File;
import java.util.Properties;

import static jodd.util.StringPool.TRUE;

/**
 * Represents simple plain POP server for sending emails.
 */
public class Pop3Server extends MailServer<ReceiveMailSession> {

	protected static final String PROTOCOL_POP3 = "pop3";

	/**
	 * Default POP3 port.
	 */
	protected static final int DEFAULT_POP3_PORT = 110;

	public Pop3Server(final Builder builder) {
		super(builder, DEFAULT_POP3_PORT);
	}
	protected Pop3Server(final Builder builder, final int defaultPort) {
		super(builder, defaultPort);
	}

	@Override
	protected Properties createSessionProperties() {
		final Properties props = super.createSessionProperties();

		props.setProperty(MAIL_POP3_HOST, host);
		props.setProperty(MAIL_POP3_PORT, String.valueOf(port));

		if (authenticator != null) {
			props.setProperty(MAIL_POP3_AUTH, TRUE);
		}

		if (timeout > 0) {
			final String timeoutValue = String.valueOf(timeout);
			props.put(MAIL_POP3_CONNECTIONTIMEOUT, timeoutValue);
			props.put(MAIL_POP3_TIMEOUT, timeoutValue);
		}

		return props;
	}

	/**
	 * Returns email store.
	 *
	 * @return {@link com.sun.mail.pop3.POP3Store}
	 * @throws NoSuchProviderException If a provider for the given protocol is not found.
	 */
	protected Store getStore(final Session session) throws NoSuchProviderException {
		return session.getStore(PROTOCOL_POP3);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@link ReceiveMailSession}
	 * @see EmailUtil#createSession(String, Properties, Authenticator, File)
	 */
	@Override
	public ReceiveMailSession createSession() {
		return EmailUtil.createSession(
			PROTOCOL_POP3,
			createSessionProperties(),
			authenticator,
			attachmentStorage);
	}

}
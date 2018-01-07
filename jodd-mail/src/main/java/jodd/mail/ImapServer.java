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

import jodd.util.StringPool;

import javax.mail.Authenticator;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

/**
 * IMAP Server.
 */
public class ImapServer extends MailServer<ReceiveMailSession> {

	protected static final String MAIL_IMAP_PORT = "mail.imap.port";
	protected static final String MAIL_IMAP_HOST = "mail.imap.host";
	protected static final String MAIL_IMAP_PARTIALFETCH = "mail.imap.partialfetch";

	protected static final String PROTOCOL_IMAP = "imap";

	/**
	 * Default IMAP port.
	 */
	protected static final int DEFAULT_IMAP_PORT = 143;

	/**
	 * {@inheritDoc}
	 */
	ImapServer(final String host, final int port, final Authenticator authenticator) {
		super(host, port, authenticator);
	}

	@Override
	protected Properties createSessionProperties() {
		final Properties props = new Properties();
		props.setProperty(MAIL_IMAP_HOST, getHost());
		props.setProperty(MAIL_IMAP_PORT, String.valueOf(getPort()));
		props.setProperty(MAIL_IMAP_PARTIALFETCH, StringPool.FALSE);
		return props;
	}

	/**
	 * Returns email store.
	 *
	 * @return {@link com.sun.mail.imap.IMAPStore}
	 * @throws NoSuchProviderException if a provider for the given protocol is not found.
	 * @see EmailUtil#getStore(Session, String)
	 */
	protected Store getStore(final Session session) throws NoSuchProviderException {
		return EmailUtil.getStore(session, PROTOCOL_IMAP);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@link ReceiveMailSession}
	 */
	@Override
	public ReceiveMailSession createSession() {
		return EmailUtil.createSession(PROTOCOL_IMAP, getSessionProperties(), getAuthenticator());
	}

	// ---------------------------------------------------------------- deprecated

	/**
	 * @deprecated Use {@link MailServer#builder()}
	 */
	@Deprecated
	public ImapServer(final String host) {
		this(host, DEFAULT_IMAP_PORT, null);
	}

	/**
	 * @deprecated Use {@link MailServer#builder()}
	 */
	@Deprecated
	public ImapServer(final String host, final int port) {
		this(host, port, null);
	}

	/**
	 * @deprecated Use {@link MailServer#builder()}
	 */
	@Deprecated
	public ImapServer(final String host, final Authenticator authenticator) {
		this(host, DEFAULT_IMAP_PORT, authenticator);
	}

	/**
	 * @deprecated Use {@link MailServer#builder()}
	 */
	@Deprecated
	public ImapServer(final String host, final int port, final String username, final String password) {
		this(host, port, new SimpleAuthenticator(username, password));
	}

	/**
	 * @deprecated Use {@link #getSessionProperties()} and {@link Properties#setProperty(String, String)}.
	 */
	@Deprecated
	public ImapServer setProperty(final String name, final String value) {
		getSessionProperties().setProperty(name, value);
		return this;
	}
}
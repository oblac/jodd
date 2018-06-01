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
import javax.mail.Session;
import java.io.File;
import java.util.Objects;
import java.util.Properties;

public abstract class MailServer<MailSessionImpl extends MailSession> {

	/**
	 * The host.
	 */
	private final String host;

	/**
	 * The port.
	 */
	private final int port;

	/**
	 * The {@link Authenticator}.
	 */
	private final Authenticator authenticator;

	private final File attachmentStorage;

	/**
	 * The {@link MailSession} {@link Properties}.
	 */
	private final Properties sessionProperties;

	/**
	 * {@link MailServer} defined with its host, port and {@link Authenticator}.
	 *
	 * @param host          The host to use.
	 * @param port          The port to use.
	 * @param authenticator The {@link Authenticator} to use.
	 */
	protected MailServer(final String host, final int port, final Authenticator authenticator, final File attachmentStorage) {
		Objects.requireNonNull(host, "Host cannot be null");

		this.host = host;
		this.port = port;
		this.authenticator = authenticator;
		this.attachmentStorage = attachmentStorage;
		this.sessionProperties = createSessionProperties();
	}


	/**
	 * Creates new mail session.
	 *
	 * @return {@link MailSession} or an implementing class such as {@link ReceiveMailSession}
	 * or {@link SendMailSession}. The {@link Session} properties must be set <b>before</b>
	 * the {@link Session} is created.
	 */
	public abstract MailSessionImpl createSession();

	/**
	 * Creates {@link MailSession} {@link Properties}.
	 *
	 * @return session {@link Properties}
	 */
	protected abstract Properties createSessionProperties();

	// ---------------------------------------------------------------- properties

	/**
	 * Returns the host.
	 *
	 * @return The host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns the {@link Authenticator}.
	 *
	 * @return The current {@link Authenticator}.
	 */
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	/**
	 * Returns current port.
	 *
	 * @return The current port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns {@link MailSession} {@link Properties}.
	 *
	 * @return The {@link MailSession} {@link Properties}.
	 */
	public Properties getSessionProperties() {
		return sessionProperties;
	}

	public File getAttachmentStorage() {
		return attachmentStorage;
	}

	/**
	 * Returns new mail server builder.
	 */
	public static Builder create() {
		return new Builder();
	}

	// ---------------------------------------------------------------- builder

	/**
	 * Used to create implementing instances of {@link MailServer}.
	 *
	 * @see ImapServer
	 * @see ImapSslServer
	 * @see Pop3Server
	 * @see Pop3SslServer
	 * @see SmtpServer
	 * @see SmtpSslServer
	 */
	public static class Builder {
		private String host = null;
		private int port = -1;
		private boolean ssl = false;
		private Authenticator authenticator;
		private File attachmentStorage;

		/**
		 * Sets the host.
		 *
		 * @param host The host to set.
		 * @return this
		 *
		 */
		public Builder host(final String host) {
			this.host = host;
			return this;
		}

		/**
		 * Sets the port.
		 *
		 * @param port The port to set.
		 * @return this
		 *
		 */
		public Builder port(final int port) {
			this.port = port;
			return this;
		}

		/**
		 * Sets the SSL implementation of the Mail server.
		 *
		 * @param ssl SSL flag
		 * @return this
		 */
		public Builder ssl(final boolean ssl) {
			this.ssl = ssl;
			return this;
		}

		public Builder storeAttachmentsIn(final File attachmentStorage) {
			this.attachmentStorage = attachmentStorage;
			return this;
		}

		/**
		 * Sets authenticator as {@link SimpleAuthenticator} using username and password.
		 *
		 * @param username The username to use.
		 * @param password The password to use.
		 * @return this
		 */
		public Builder auth(final String username, final String password) {
			Objects.requireNonNull(username, "Username cannot be null");
			Objects.requireNonNull(password, "Password cannot be null");

			return auth(new SimpleAuthenticator(username, password));
		}

		/**
		 * Sets the authenticator.
		 *
		 * @param authenticator {@link Authenticator} to set.
		 * @return this
		 */
		public Builder auth(final Authenticator authenticator) {
			this.authenticator = authenticator;
			return this;
		}

		// ---------------------------------------------------------------- build

		/**
		 * Create a {@link ImapServer} from current data.
		 *
		 * @return {@link ImapServer} from current data.
		 */
		public ImapServer buildImapMailServer() {
			if (ssl) {
				return new ImapSslServer(host, port, authenticator, attachmentStorage);
			}
			return new ImapServer(host, port, authenticator, attachmentStorage);
		}

		/**
		 * Create a {@link Pop3Server} from current data.
		 *
		 * @return {@link Pop3Server} from current data.
		 *
		 */
		public Pop3Server buildPop3MailServer() {
			if (ssl) {
				return new Pop3SslServer(host, port, authenticator, attachmentStorage);
			}
			return new Pop3Server(host, port, authenticator, attachmentStorage);
		}

		/**
		 * Create a {@link SmtpServer} from current data.
		 *
		 * @return {@link SmtpServer} from current data.
		 *
		 */
		public SmtpServer buildSmtpMailServer() {
			if (ssl) {
				return new SmtpSslServer(host, port, authenticator);
			}
			return new SmtpServer(host, port, authenticator);
		}
	}
}

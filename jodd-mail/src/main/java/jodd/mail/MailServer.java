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
import java.util.Properties;

/**
 * @since 4.0
 */
public abstract class MailServer<MailSessionImpl extends MailSession> {

	/**
	 * The host.
	 *
	 * @since 4.0
	 */
	private final String host;

	/**
	 * The port.
	 *
	 * @since 4.0
	 */
	private final int port;

	/**
	 * The {@link Authenticator}.
	 *
	 * @since 4.0
	 */
	private final Authenticator authenticator;

	/**
	 * The {@link MailSession} {@link Properties}.
	 *
	 * @since 4.0
	 */
	private final Properties sessionProperties;

	/**
	 * {@link MailServer} defined with its host, port and {@link Authenticator}.
	 *
	 * @param host          The host to use.
	 * @param port          The port to use.
	 * @param authenticator The {@link Authenticator} to use.
	 * @since 4.0
	 */
	MailServer(final String host, final int port, final Authenticator authenticator) {
		if (host == null) {
			throw new IllegalArgumentException("Host cannot be null");
		}

		this.host = host;
		this.port = port;
		this.authenticator = authenticator;
		sessionProperties = createSessionProperties();
	}


	/**
	 * Creates new mail session.
	 *
	 * @return {@link MailSession} or an implementing class such as {@link ReceiveMailSession}
	 * or {@link SendMailSession}. The {@link Session} properties must be set <b>before</b>
	 * the {@link Session} is created.
	 * @since 4.0
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
	 * @since 4.0
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns the {@link Authenticator}.
	 *
	 * @return The current {@link Authenticator}.
	 * @since 4.0
	 */
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	/**
	 * Returns current port.
	 *
	 * @return The current port.
	 * @since 4.0
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns {@link MailSession} {@link Properties}.
	 *
	 * @return The {@link MailSession} {@link Properties}.
	 * @since 4.0
	 */
	public Properties getSessionProperties() {
		return sessionProperties;
	}

	public static Builder builder() {
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
	 * @since 4.0
	 */
	static class Builder {
		private String host = null;
		private int port = 0;
		private Authenticator authenticator;

		/**
		 * Sets the host.
		 *
		 * @param host The host to set.
		 * @return this
		 * @since 4.0
		 */
		public Builder host(final String host) {
			if (host == null) {
				throw new IllegalArgumentException("Host cannot be null");
			}

			this.host = host;
			return this;
		}

		/**
		 * Sets the port.
		 *
		 * @param port The port to set.
		 * @return this
		 * @since 4.0
		 */
		public Builder port(final int port) {
			this.port = port;
			return this;
		}

		/**
		 * Sets authenticator as {@link SimpleAuthenticator} using username and password.
		 *
		 * @param username The username to use.
		 * @param password The password to use.
		 * @return this
		 * @since 4.0
		 */
		public Builder auth(final String username, final String password) {
			if (username == null) {
				throw new IllegalArgumentException("Username cannot be null");
			}
			if (password == null) {
				throw new IllegalArgumentException("Password cannot be null");
			}
			return auth(new SimpleAuthenticator(username, password));
		}

		/**
		 * Sets the authenticator.
		 *
		 * @param authenticator {@link Authenticator} to set.
		 * @return this
		 * @since 4.0
		 */
		public Builder auth(final Authenticator authenticator) {
			if (authenticator == null) {
				throw new IllegalArgumentException("Authenticator cannot be null");
			}

			this.authenticator = authenticator;
			return this;
		}

		/**
		 * Create a {@link ImapServer} from current data.
		 *
		 * @return {@link ImapServer} from current data.
		 * @since 4.0
		 */
		public ImapServer buildImap() {
			if (port == 0) {
				port = ImapServer.DEFAULT_IMAP_PORT;
			}
			return new ImapServer(host, port, authenticator);
		}

		/**
		 * Create a {@link ImapSslServer} from current data.
		 *
		 * @return {@link ImapSslServer} from current data.
		 * @since 4.0
		 */
		public ImapSslServer buildImapSsl() {
			if (port == 0) {
				port = ImapSslServer.DEFAULT_SSL_PORT;
			}

			return new ImapSslServer(host, port, authenticator);
		}

		/**
		 * Create a {@link Pop3Server} from current data.
		 *
		 * @return {@link Pop3Server} from current data.
		 * @since 4.0
		 */
		public Pop3Server buildPop3() {
			if (port == 0) {
				port = Pop3Server.DEFAULT_POP3_PORT;
			}

			return new Pop3Server(host, port, authenticator);
		}

		/**
		 * Create a {@link Pop3SslServer} from current data.
		 *
		 * @return {@link Pop3SslServer} from current data.
		 * @since 4.0
		 */
		public Pop3SslServer buildPop3Ssl() {
			if (port == 0) {
				port = Pop3SslServer.DEFAULT_SSL_PORT;
			}

			return new Pop3SslServer(host, port, authenticator);
		}

		/**
		 * Create a {@link SmtpServer} from current data.
		 *
		 * @return {@link SmtpServer} from current data.
		 * @since 4.0
		 */
		public SmtpServer buildSmtp() {
			if (port == 0) {
				port = SmtpServer.DEFAULT_SMTP_PORT;
			}

			return new SmtpServer(host, port, authenticator);
		}

		/**
		 * Create a {@link SmtpSslServer} from current data.
		 *
		 * @return {@link SmtpSslServer} from current data.
		 * @since 4.0
		 */
		public SmtpSslServer buildSmtpSsl() {
			if (port == 0) {
				port = SmtpSslServer.DEFAULT_SSL_PORT;
			}

			return new SmtpSslServer(host, port, authenticator);
		}
	}
}

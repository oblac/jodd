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

	public static final String MAIL_HOST = "mail.host";
	public static final String MAIL_SMTP_HOST = "mail.smtp.host";
	public static final String MAIL_SMTP_PORT = "mail.smtp.port";
	public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
	//public static final String MAIL_SMTP_FROM = "mail.smtp.from";

	public static final String MAIL_SMTP_CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";
	public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
	public static final String MAIL_SMTP_WRITETIMEOUT = "mail.smtp.writetimeout";
	public static final String MAIL_DEBUG = "mail.debug";
	public static final String MAIL_MIME_ADDRESS_STRICT = "mail.mime.address.strict";

	public static final String MAIL_IMAP_CONNECTIONTIMEOUT = "mail.imap.connectiontimeout";
	public static final String MAIL_IMAP_TIMEOUT = "mail.imap.timeout";
	public static final String MAIL_IMAP_PORT = "mail.imap.port";
	public static final String MAIL_IMAP_HOST = "mail.imap.host";
	public static final String MAIL_IMAP_PARTIALFETCH = "mail.imap.partialfetch";

	public static final String MAIL_IMAP_SOCKET_FACTORY_PORT = "mail.imap.socketFactory.port";
	public static final String MAIL_IMAP_SOCKET_FACTORY_CLASS = "mail.imap.socketFactory.class";
	public static final String MAIL_IMAP_SOCKET_FACTORY_FALLBACK = "mail.imap.socketFactory.fallback";

	public static final String MAIL_SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
	public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	public static final String MAIL_SMTP_SOCKET_FACTORY_PORT = "mail.smtp.socketFactory.port";
	public static final String MAIL_SMTP_SOCKET_FACTORY_CLASS = "mail.smtp.socketFactory.class";
	public static final String MAIL_SMTP_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";

	public static final String MAIL_POP3_PORT = "mail.pop3.port";
	public static final String MAIL_POP3_HOST = "mail.pop3.host";
	public static final String MAIL_POP3_AUTH = "mail.pop3.auth";
	public static final String MAIL_POP3_CONNECTIONTIMEOUT = "mail.pop3.connectiontimeout";
	public static final String MAIL_POP3_TIMEOUT = "mail.pop3.timeout";

	public static final String MAIL_POP3_SOCKET_FACTORY_PORT = "mail.pop3.socketFactory.port";
	public static final String MAIL_POP3_SOCKET_FACTORY_CLASS = "mail.pop3.socketFactory.class";
	public static final String MAIL_POP3_SOCKET_FACTORY_FALLBACK = "mail.pop3.socketFactory.fallback";

	/**
	 * The host.
	 */
	protected final String host;

	/**
	 * The port.
	 */
	protected final int port;

	/**
	 * The {@link Authenticator}.
	 */
	protected final Authenticator authenticator;

	protected final File attachmentStorage;

	protected final boolean debugMode;

	/**
	 * Whether strict address checking is turned on.
	 */
	protected final boolean strictAddress;

	/**
	 * Connection timeout.
	 */
	protected final int timeout;

	protected final Properties customProperties;

	/**
	 * {@link MailServer} defined with its host, port and {@link Authenticator}.
	 */
	protected MailServer(final Builder builder, final int defaultPort) {
		Objects.requireNonNull(builder.host, "Host cannot be null");

		this.host = builder.host;
		this.port = builder.port == -1 ? defaultPort : builder.port;
		this.authenticator = builder.authenticator;
		this.attachmentStorage = builder.attachmentStorage;
		this.timeout = builder.timeout;
		this.strictAddress = builder.strictAddress;
		this.debugMode = builder.debug;
		this.customProperties = builder.customProperties;
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
	protected Properties createSessionProperties() {
		final Properties props = new Properties();

		props.putAll(customProperties);

		if (debugMode) {
			props.put(MAIL_DEBUG, "true");
		}

		if (!strictAddress) {
			props.put(MAIL_MIME_ADDRESS_STRICT, "false");
		}

		return props;
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
		private boolean debug;
		private int timeout = 0;
		private boolean strictAddress = true;
		private Properties customProperties = new Properties();

		/**
		 * Sets the host.
		 *
		 * @param host The host to set.
		 * @return this
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

		/**
		 * Defines attachment storage, a folder where attachments will be saved.
		 */
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

		/**
		 * Enable or disable debug mode.
		 *
		 * @param debug {@code true} to turn on debugging. By default, this is {@code false}.
		 * @return this
		 */
		public Builder debugMode(final boolean debug) {
			this.debug = debug;
			return this;
		}


		/**
		 * Defines timeout value in milliseconds for all mail-related operations.
		 *
		 * @param timeout timeout value in milliseconds.
		 * @return this
		 */
		public Builder timeout(final int timeout) {
			this.timeout = timeout;
			return this;
		}

		/**
		 * Disables the strict address.
		 *
		 * @param strictAddress {@code true} if strict address checking should be be turned on. By default, this is {@code true}.
		 * @return this
		 */
		public Builder strictAddress(final boolean strictAddress) {
			this.strictAddress = strictAddress;
			return this;
		}

		public Builder property(final String name, final String value) {
			this.customProperties.put(name, value);
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
				return new ImapSslServer(this);
			}
			return new ImapServer(this);
		}

		/**
		 * Create a {@link Pop3Server} from current data.
		 *
		 * @return {@link Pop3Server} from current data.
		 *
		 */
		public Pop3Server buildPop3MailServer() {
			if (ssl) {
				return new Pop3SslServer(this);
			}
			return new Pop3Server(this);
		}

		/**
		 * Create a {@link SmtpServer} from current data.
		 *
		 * @return {@link SmtpServer} from current data.
		 *
		 */
		public SmtpServer buildSmtpMailServer() {
			if (ssl) {
				return new SmtpSslServer(this);
			}
			return new SmtpServer(this);
		}
	}
}

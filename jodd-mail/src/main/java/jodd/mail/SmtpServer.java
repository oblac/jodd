// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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
public class SmtpServer implements SendMailSessionProvider {

	protected static final String MAIL_HOST = "mail.host";
	protected static final String MAIL_SMTP_HOST = "mail.smtp.host";
	protected static final String MAIL_SMTP_PORT = "mail.smtp.port";
	protected static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	protected static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";

	protected static final String MAIL_SMTP_CONNECTIONTIMEOUT ="mail.smtp.connectiontimeout";
	protected static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
	protected static final String MAIL_SMTP_WRITETIMEOUT = "mail.smtp.writetimeout";

	protected static final String PROTOCOL_SMTP = "smtp";

	protected static final int DEFAULT_SMTP_PORT = 25;

	protected final String host;
	protected final int port;
	protected final Authenticator authenticator;
	protected final Properties sessionProperties;

	protected int timeout = 0;

	/**
	 * SMTP server defined with its host and default port.
	 */
	public SmtpServer(String host) {
		this(host, DEFAULT_SMTP_PORT, null);
	}
	/**
	 * SMTP server defined with its host and port.
	 */
	public SmtpServer(String host, int port) {
		this(host, port, null);
	}

	public SmtpServer(String host, Authenticator authenticator) {
		this(host, DEFAULT_SMTP_PORT, authenticator);
	}

	public SmtpServer(String host, int port, String username, String password) {
		this(host, port, new SimpleAuthenticator(username, password));
	}

	public SmtpServer(String host, String username, String password) {
		this(host, DEFAULT_SMTP_PORT, new SimpleAuthenticator(username, password));
	}

	/**
	 * SMTP server defined with its host and authentication.
	 */
	public SmtpServer(String host, int port, Authenticator authenticator) {
		this.host = host;
		this.port = port;
		this.authenticator = authenticator;
		sessionProperties = createSessionProperties();
	}

	protected SmtpServer(SmtpServerBuilder smtpServerBuilder) {
		this.host = smtpServerBuilder.host;
		this.port = smtpServerBuilder.port;
		this.authenticator = smtpServerBuilder.authenticator;
		this.timeout = smtpServerBuilder.timeout;
		sessionProperties = createSessionProperties(smtpServerBuilder.properties);
	}

	/**
	 * Prepares mail session properties.
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

		return props;
	}

	/**
	 * Adds to or overrides session properties
	 * @param properties
	 */
	protected Properties createSessionProperties(Properties properties) {
		Properties sessionProperties = createSessionProperties();
		sessionProperties.putAll(properties);
		return sessionProperties;
	}

	/**
	 * Defines timeout value in milliseconds for all mail-related operations.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


	/**
	 * {@inheritDoc}
	 */
	public SendMailSession createSession() {
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

	public static SmtpServerBuilder newSmtpServer() {
		return new SmtpServerBuilder();
	}

	public static class SmtpServerBuilder {
		private String host;
		private int port = DEFAULT_SMTP_PORT;
		private Authenticator authenticator;
		private Properties properties = new Properties();
		private int timeout = 0;

		public SmtpServerBuilder host(String host) {
			this.host = host;
			return this;
		}

		/**
		 * Defines timeout value in milliseconds for all mail-related operations.
		 */
		public SmtpServerBuilder timeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		public SmtpServerBuilder port(int port) {
			this.port = port;
			return this;
		}

		public AuthenticationBuilder authenticateWith() {
			return new AuthenticationBuilder(this);
		}

		/**
		 * Adds to or overrides properties created in {@link SmtpServer#createSessionProperties()}
		 * @param properties
		 */
		public SmtpServerBuilder properties(Properties properties) {
			this.properties = properties;
			return this;
		}

		public SmtpServer build() {
			return new SmtpServer(this);
		}
	}

	public static class AuthenticationBuilder {
		private final SmtpServerBuilder smtpServerBuilder;

		public AuthenticationBuilder(SmtpServerBuilder smtpServerBuilder) {
			this.smtpServerBuilder = smtpServerBuilder;
		}

		public SmtpServerBuilder usernameAndPassword(String username, String password) {
			this.smtpServerBuilder.authenticator = new SimpleAuthenticator(username, password);
			return smtpServerBuilder;
		}

		public SmtpServerBuilder authenticator(Authenticator authenticator) {
			this.smtpServerBuilder.authenticator = authenticator;
			return smtpServerBuilder;
		}
	}
}

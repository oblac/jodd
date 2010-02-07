// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Properties;

/**
 * Represents simple plain SMTP server for sending emails.
 */
public class SmtpServer implements SendMailSessionProvider {

	protected static final String MAIL_HOST = "mail.host";
	protected static final String MAIL_SMTP_HOST = "mail.smtp.host";
	protected static final String MAIL_SMTP_PORT = "mail.smtp.port";
	protected static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	protected static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
	protected static final int DEFAULT_SMTP_PORT = 25;

	protected final String host;
	protected final int port;
	protected final Authenticator authenticator;
	protected final Properties sessionProperties;

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

	/**
	 * SMTP server defined with its host and authenitification.
	 */
	public SmtpServer(String host, int port, Authenticator authenticator) {
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
		props.setProperty(MAIL_TRANSPORT_PROTOCOL, "smtp");
		props.setProperty(MAIL_HOST, host);
		props.setProperty(MAIL_SMTP_HOST, host);
		props.setProperty(MAIL_SMTP_PORT, String.valueOf(port));
		if (authenticator != null) {
			props.setProperty(MAIL_SMTP_AUTH, "true");
		}
		return props;
	}


	/**
	 * {@inheritDoc}
	 */
	public SendMailSession createSession() {
		return new SendMailSession(Session.getDefaultInstance(sessionProperties, authenticator));
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
}

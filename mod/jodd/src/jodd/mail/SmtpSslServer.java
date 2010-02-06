// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import javax.mail.Authenticator;
import java.util.Properties;

/**
 * Secure SMTP server (STARTTLS) for sending emails.
 */
public class SmtpSslServer extends SmtpServer {

	protected static final String MAIL_SMTP_STARTTLS_ENABLE 		= "mail.smtp.starttls.enable";
	protected static final String MAIL_SMTP_SOCKET_FACTORY_PORT 	= "mail.smtp.socketFactory.port";
	protected static final String MAIL_SMTP_SOCKET_FACTORY_CLASS 	= "mail.smtp.socketFactory.class";
	protected static final String MAIL_SMTP_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";
	protected static final int DEFAULT_SSL_PORT = 465;

	public SmtpSslServer(String host, Authenticator authenticator) {
		this(host, DEFAULT_SSL_PORT, authenticator);
	}

	public SmtpSslServer(String host, String username, String password) {
		this(host, DEFAULT_SSL_PORT, new SimpleAuthenticator(username, password));
	}

	public SmtpSslServer(String host, int port, Authenticator authenticator) {
		super(host, port, authenticator);
	}

	public SmtpSslServer(String host, int port, String username, String password) {
		this(host, port, new SimpleAuthenticator(username, password));
	}

	@Override
	protected Properties createSessionProperties() {
		Properties props = super.createSessionProperties();
		props.put(MAIL_SMTP_STARTTLS_ENABLE, "true");
		props.put(MAIL_SMTP_SOCKET_FACTORY_PORT, String.valueOf(port));
		props.put(MAIL_SMTP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
		props.put(MAIL_SMTP_SOCKET_FACTORY_FALLBACK, "false");
		props.setProperty("mail.host", host);
		return props;
	}
}

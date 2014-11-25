// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.util.StringPool;

import java.util.Properties;

/**
 * Secure SMTP server (STARTTLS) for sending emails.
 */
public class SmtpSslServer extends SmtpServer<SmtpSslServer> {

	public static final String MAIL_SMTP_STARTTLS_ENABLE 		= "mail.smtp.starttls.enable";
	public static final String MAIL_SMTP_SOCKET_FACTORY_PORT 	= "mail.smtp.socketFactory.port";
	public static final String MAIL_SMTP_SOCKET_FACTORY_CLASS 	= "mail.smtp.socketFactory.class";
	public static final String MAIL_SMTP_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";

	protected static final int DEFAULT_SSL_PORT = 465;

	public static SmtpSslServer create(String host) {
		return new SmtpSslServer(host, DEFAULT_SSL_PORT);
	}

	public static SmtpSslServer create(String host, int port) {
		return new SmtpSslServer(host, port);
	}

	public SmtpSslServer(String host) {
		super(host, DEFAULT_SSL_PORT);
	}

	public SmtpSslServer(String host, int port) {
		super(host, port);
	}


	@Override
	protected Properties createSessionProperties() {
		Properties props = super.createSessionProperties();

		props.setProperty(MAIL_SMTP_STARTTLS_ENABLE, StringPool.TRUE);
		props.setProperty(MAIL_SMTP_SOCKET_FACTORY_PORT, String.valueOf(port));
		props.setProperty(MAIL_SMTP_PORT, String.valueOf(port));
		props.setProperty(MAIL_SMTP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
		props.setProperty(MAIL_SMTP_SOCKET_FACTORY_FALLBACK, StringPool.FALSE);
		props.setProperty(MAIL_HOST, host);

		return props;
	}

}

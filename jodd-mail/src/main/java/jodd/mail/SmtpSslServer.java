// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.util.StringPool;

import java.util.Properties;

/**
 * Secure SMTP server (STARTTLS) for sending emails.
 */
public class SmtpSslServer extends SmtpServer<SmtpSslServer> {

	public static final String MAIL_SMTP_STARTTLS_REQUIRED 		= "mail.smtp.starttls.required";
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

	// ---------------------------------------------------------------- properties

	protected boolean startTlsRequired = false;
	protected boolean plaintextOverTLS = false;

	/**
	 * Sets <code>mail.smtp.starttls.required</code> which according to
	 * Java Mail API means: If true, requires the use of the STARTTLS command.
	 * If the server doesn't support the STARTTLS command, or the command fails,
	 * the connect method will fail. Defaults to <code>false</code>.
	 */
	public SmtpSslServer startTlsRequired(boolean startTlsRequired) {
		this.startTlsRequired = startTlsRequired;
		return this;
	}

	/**
	 * When enabled, SMTP socket factory class will be not set,
	 * and Plaintext Authentication over TLS will be enabled.
	 */
	public SmtpSslServer plaintextOverTLS(boolean plaintextOverTLS) {
		this.plaintextOverTLS = plaintextOverTLS;
		return this;
	}


	@Override
	protected Properties createSessionProperties() {
		Properties props = super.createSessionProperties();

		props.setProperty(MAIL_SMTP_STARTTLS_REQUIRED,
			startTlsRequired ? StringPool.TRUE : StringPool.FALSE);

		props.setProperty(MAIL_SMTP_STARTTLS_ENABLE, StringPool.TRUE);

		props.setProperty(MAIL_SMTP_SOCKET_FACTORY_PORT, String.valueOf(port));

		props.setProperty(MAIL_SMTP_PORT, String.valueOf(port));

		if (!plaintextOverTLS) {
			props.setProperty(MAIL_SMTP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
		}

		props.setProperty(MAIL_SMTP_SOCKET_FACTORY_FALLBACK, StringPool.FALSE);
		props.setProperty(MAIL_HOST, host);

		return props;
	}

}

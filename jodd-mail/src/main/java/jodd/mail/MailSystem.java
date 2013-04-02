// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

/**
 * Defines mail system behavior. Override this class if you have special needs or
 * simply set parameters manually, but <b>before</b> first Mail use.
 * Be sure to use java mail >= v1.4.4.
 */
public class MailSystem {

	protected boolean initialized;

	/**
	 * Defines Java Mail
	 * <a href="http://docs.oracle.com/javaee/6/api/javax/mail/internet/package-summary.html">system properties</a>.
	 */
	public final void defineJavaMailSystemProperties() {
		if (initialized) {
			return;
		}

		defineSystemProperties();

		initialized = true;
	}

	/**
	 * Defines system properties. Invoked only once.
	 */
	protected void defineSystemProperties() {
		/*
			If set to "true", the setFileName method uses the MimeUtility method encodeText to
			encode any non-ASCII characters in the filename. Note that this encoding violates
			the MIME specification, but is useful for interoperating with some mail clients
			that use this convention. The default is false.
		 */
		System.setProperty("mail.mime.encodefilename", "true");

		/*
			If set to "true", the getFileName method uses the MimeUtility method decodeText
			to decode any non-ASCII characters in the filename. Note that this decoding
			violates the MIME specification, but is useful for interoperating with some
			mail clients that use this convention. The default is false.
		 */
		System.setProperty("mail.mime.decodefilename", "true");
	}

}
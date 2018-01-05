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
import java.util.Properties;

/**
 * Secure SMTP server (STARTTLS) for sending emails.
 */
public class SmtpSslServer extends SmtpServer<SmtpSslServer> {

  public static final String MAIL_SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
  public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
  public static final String MAIL_SMTP_SOCKET_FACTORY_PORT = "mail.smtp.socketFactory.port";
  public static final String MAIL_SMTP_SOCKET_FACTORY_CLASS = "mail.smtp.socketFactory.class";
  public static final String MAIL_SMTP_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";

  /**
   * Default SMTP SSL port.
   */
  protected static final int DEFAULT_SSL_PORT = 465;

  /**
   * {@inheritDoc}
   */
  SmtpSslServer(final String host, final int port, final Authenticator authenticator) {
    super(host, port, authenticator);
  }

  // ---------------------------------------------------------------- properties

  /**
   * Defaults to {@code false}.
   */
  protected boolean startTlsRequired = false;

  /**
   * Defaults to {@code false}. Google requires it to be false
   */
  protected boolean plaintextOverTLS = false;

  /**
   * Sets <code>mail.smtp.starttls.required</code>.
   *
   * If the server doesn't support the STARTTLS command, or the command fails,
   * the connect method will fail. Defaults to {@code false}.
   *
   * @param startTlsRequired If {@code true}, requires the use of the STARTTLS command.
   * @return this
   */
  public SmtpSslServer startTlsRequired(final boolean startTlsRequired) {
    this.startTlsRequired = startTlsRequired;
    return this;
  }

  /**
   * When enabled, <code>MAIL_SMTP_SOCKET_FACTORY_CLASS</code> will be not set,
   * and Plaintext Authentication over TLS will be enabled.
   *
   * @param plaintextOverTLS {@code true} when plain text authentication over TLS should be enabled.
   * @return this
   */
  public SmtpSslServer plaintextOverTLS(final boolean plaintextOverTLS) {
    this.plaintextOverTLS = plaintextOverTLS;
    return this;
  }

  @Override
  protected Properties createSessionProperties() {
    final Properties props = super.createSessionProperties();

    props.setProperty(MAIL_SMTP_STARTTLS_REQUIRED,
        startTlsRequired ? StringPool.TRUE : StringPool.FALSE);

    props.setProperty(MAIL_SMTP_STARTTLS_ENABLE, StringPool.TRUE);

    props.setProperty(MAIL_SMTP_SOCKET_FACTORY_PORT, String.valueOf(getPort()));

    props.setProperty(MAIL_SMTP_PORT, String.valueOf(getPort()));

    if (!plaintextOverTLS) {
      props.setProperty(MAIL_SMTP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
    }

    props.setProperty(MAIL_SMTP_SOCKET_FACTORY_FALLBACK, StringPool.FALSE);
    props.setProperty(MAIL_HOST, getHost());

    return props;
  }

  // ---------------------------------------------------------------- deprecated

  /**
   * @deprecated Use {@link MailServer#builder()}
   */
  @Deprecated
  public static SmtpSslServer create(final String host) {
    return MailServer.builder().host(host).port(DEFAULT_SSL_PORT).buildSmtpSsl();
  }

  /**
   * @deprecated Use {@link MailServer#builder()}
   */
  @Deprecated
  public static SmtpSslServer create(final String host, final int port) {
    return MailServer.builder().host(host).port(port).buildSmtpSsl();
  }

  /**
   * @deprecated Use {@link MailServer#builder()}
   */
  @Deprecated
  public SmtpSslServer(final String host) {
    super(host, DEFAULT_SSL_PORT, null);
  }

  /**
   * @deprecated Use {@link MailServer#builder()}
   */
  @Deprecated
  public SmtpSslServer(final String host, final int port) {
    super(host, port, null);
  }
}

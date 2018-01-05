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

import javax.mail.MessagingException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

/**
 * @since 4.0
 */
abstract class MailSession<T extends Service> implements AutoCloseable {

  @Deprecated
  final Session mailSession;

  /**
   * @since 4.0
   */
  private final Session session;

  /**
   * @since 4.0
   */
  final Service service;

  /**
   * Creates new mail session.
   *
   * @param session {@link Session}.
   * @param service {@link Service} such as {@link Store} or {@link Transport}.
   * @since 4.0
   */
  MailSession(final Session session, final Service service) {
    this.session = session;
    mailSession = session;
    this.service = service;
  }

  /**
   * Opens session.
   *
   * @since 4.0
   */
  public void open() {
    try {
      service.connect();
    } catch (final MessagingException msex) {
      throw new MailException("Open session error", msex);
    }
  }

  /**
   * Closes session.
   *
   * @since 4.0
   */
  @Override
  public void close() {
    try {
      service.close();
    } catch (final MessagingException mex) {
      throw new MailException("Failed to close session", mex);
    }
  }

  /**
   * Returns {@code true} if mail session is still connected.
   *
   * @return {@code true} if mail session is still connected.
   * @since 4.0
   */
  public boolean isConnected() {
    return service.isConnected();
  }

  /**
   * Returns the {@link Session}.
   *
   * @return the {@link Session}.
   * @since 4.0
   */
  public Session getSession() {
    return session;
  }

  /**
   * Returns the {@link Service}.
   *
   * @return the {@link Service}.
   * @since 4.0
   */
  public abstract T getService();
}

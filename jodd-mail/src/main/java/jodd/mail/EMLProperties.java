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

import javax.mail.Session;
import java.util.Properties;

abstract class EMLProperties<T extends EMLProperties<T>> {

	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
	}

	/**
	 * {@link Properties}.
	 */
	private Session session;

	/**
	 * Returns the {@link Session}.
	 *
	 * @return the {@link Session}.
	 */
	protected Session getSession() {
		return session;
	}

	/**
	 * Custom {@link Properties}.
	 */
	private final Properties properties = new Properties();

	/**
	 * Returns the {@link Properties}.
	 *
	 * @return the {@link Properties}.
	 */
	protected Properties getProperties() {
		return properties;
	}

	/**
	 * Creates new {@link Session} with or without custom {@link Properties}.
	 *
	 * @param properties Custom properties to be used during {@link Session} creation. It is acceptable is this value is {@code null}.
	 * @return {@link Session} with any custom {@link Properties}
	 */
	protected Session createSession(Properties properties) {
		if (properties == null) {
			properties = System.getProperties();
		}

		this.session = Session.getInstance(properties);

		return session;
	}

	/**
	 * Copies properties from given set. If {@link Session} is already created, exception will be thrown.
	 *
	 * @param properties {@link Properties} to set.
	 * @return this
	 * @throws MailException if the {@link Properties} has already been set.
	 */
	public T set(final Properties properties) throws MailException {
		checkSessionNotSet();
		this.properties.putAll(properties);
		return _this();
	}

	/**
	 * Sets property for the {@link Session}. If {@link Session} is already created, an exception
	 * will be thrown.
	 *
	 * @param name  Property name to set.
	 * @param value Property value to set.
	 * @return this
	 * @throws MailException if the {@link Properties} has already been set.
	 */
	public T set(final String name, final String value) {
		checkSessionNotSet();
		properties.setProperty(name, value);
		return _this();
	}

	/**
	 * Uses default {@link Session}. Any property will be ignored.
	 *
	 * @return this
	 * @see System#getProperties()
	 */
	public T useDefaultSession() {
		this.session = Session.getDefaultInstance(System.getProperties());
		return _this();
	}

	/**
	 * Ensures that {@link Session} has not yet been set.
	 *
	 * @throws MailException if {@link Session} has already been set.
	 */
	private void checkSessionNotSet() throws MailException {
		if (session != null) {
			throw new MailException("Can't set properties after session is assigned");
		}
	}

}

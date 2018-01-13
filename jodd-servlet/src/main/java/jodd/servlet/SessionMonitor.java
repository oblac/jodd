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

package jodd.servlet;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Session monitor keep map of sessions and allow registration of
 * session listeners in runtime.
 */
public class SessionMonitor implements HttpSessionListener {

	protected static SessionMonitor sessionMonitor;

	protected List<HttpSessionListener> listeners;
	protected ConcurrentMap<String, HttpSession> sessionMap;

	/**
	 * Creates new session monitor.
	 */
	public SessionMonitor() {
		if (sessionMonitor == null) {
			sessionMonitor = this;
			listeners = new CopyOnWriteArrayList<>();
			sessionMap = new ConcurrentHashMap<>();
		}
	}

	/**
	 * Returns singleton instance of <code>SessionMonitor</code>.
	 */
	public static SessionMonitor getInstance() {
		return sessionMonitor;
	}

	// ---------------------------------------------------------------- listeners

	/**
	 * Registers new session listener.
	 */
	public void registerListener(final HttpSessionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes existing session listener.
	 */
	public void removeListener(final HttpSessionListener listener) {
		listeners.remove(listener);
	}

	// ---------------------------------------------------------------- broadcast

	/**
	 * Stores session in map and broadcasts event to registered listeners.
	 */
	@Override
	public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
		HttpSession session = httpSessionEvent.getSession();
		sessionMap.putIfAbsent(session.getId(), session);

		for (HttpSessionListener listener : listeners) {
			listener.sessionCreated(httpSessionEvent);
		}
	}

	/**
	 * Removes session from a map and broadcasts event to registered listeners.
	 */
	@Override
	public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
		HttpSession session = httpSessionEvent.getSession();
		sessionMap.remove(session.getId());

		for (HttpSessionListener listener : listeners) {
			listener.sessionDestroyed(httpSessionEvent);
		}
	}

	// ---------------------------------------------------------------- map

	/**
	 * Returns session for given session id. Returns <code>null</code>
	 * if session expired.
	 */
	public HttpSession getSession(final String sessionId) {
		return sessionMap.get(sessionId);
	}

	/**
	 * Returns iterator for all stored session IDs.
	 * Keep in mind that session may <b>NOT</b> longer
	 * exist during the iteration!
	 */
	public Iterator<String> iterator() {
		return sessionMap.keySet().iterator();
	}

	// ---------------------------------------------------------------- close

	/**
	 * Destroys this session monitor by removing all collected resources.
	 */
	public void destroy() {
		listeners.clear();
		sessionMap.clear();
	}

}
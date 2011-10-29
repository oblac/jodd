// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Broadcast session listener event to all listeners registered at run-time.
 */
public class HttpSessionListenerBroadcaster implements HttpSessionListener {

	protected static HttpSessionListenerBroadcaster instance;

	public static HttpSessionListenerBroadcaster getInstance() {
		return instance;
	}

	protected List<HttpSessionListener> listeners;

	public HttpSessionListenerBroadcaster() {
		instance = this;
		listeners = new ArrayList<HttpSessionListener>();
	}

	public void sessionCreated(HttpSessionEvent event) {
		broadcastSessionCreated(event);
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		broadcastSessionDestroyed(event);
	}


	// ---------------------------------------------------------------- listeners

	/**
	 * Registers new listener to the end of the listener list.
	 */
	public void registerListener(HttpSessionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Unregister a listener.
	 */
	public boolean removeListener(HttpSessionListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Returns listeners list for more demanding configuration.
	 */
	public List<HttpSessionListener> getListenerList() {
		return listeners;
	}

	/**
	 * Broadcast all {@link #sessionCreated(javax.servlet.http.HttpSessionEvent)} messages to all
	 * registered session listeners.
	 */
	public void broadcastSessionCreated(HttpSessionEvent event) {
		for (HttpSessionListener listener : listeners) {
			listener.sessionCreated(event);
		}
	}
	/**
	 * Broadcast all {@link #sessionDestroyed(javax.servlet.http.HttpSessionEvent)} messages to all
	 * registered session listeners.
	 */
	public void broadcastSessionDestroyed(HttpSessionEvent event) {
		for (HttpSessionListener listener : listeners) {
			listener.sessionDestroyed(event);
		}
	}

}

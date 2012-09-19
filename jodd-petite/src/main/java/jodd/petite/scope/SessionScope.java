// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.PetiteException;
import jodd.servlet.HttpSessionListenerBroadcaster;
import jodd.servlet.RequestContextListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Session scope stores unique object instances per single http session.
 * Upon creation, new session listener is registered (dynamically) that will
 * keep track on active sessions.{@link RequestContextListener} is used for accessing
 * the request and {@link HttpSessionListenerBroadcaster} is used for listening
 * session lifecycle.
 */
public class SessionScope implements Scope {

	protected Map<String, Map<String, Object>> sessionInstances = new WeakHashMap<String, Map<String, Object>>();

	protected final HttpSessionListenerBroadcaster sessionListeners;

	public SessionScope() {
		sessionListeners = HttpSessionListenerBroadcaster.getInstance();
		if (sessionListeners == null) {
			throw new PetiteException(HttpSessionListenerBroadcaster.class.getSimpleName() + " not available.");
		}
		sessionListeners.registerListener(new HttpSessionListener() {
			public void sessionCreated(HttpSessionEvent httpSessionEvent) {
				sessionInstances.put(httpSessionEvent.getSession().getId(), new HashMap<String, Object>());
			}
			public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
				sessionInstances.remove(httpSessionEvent.getSession().getId());
			}
		});
	}

	public Object lookup(String name) {
		String sessionId = getHttpSessionId();
		Map<String, Object> map = sessionInstances.get(sessionId);
		if (map == null) {
			return null;
		}
		return map.get(name);
	}

	public void register(String name, Object bean) {
		String sessionId = getHttpSessionId();
		Map<String, Object> map = sessionInstances.get(sessionId);
		if (map == null) {
			map = new HashMap<String, Object>();
			sessionInstances.put(sessionId, map);
		}
		map.put(name, bean);
	}

	public void remove(String name) {
		for (Map<String, Object> map : sessionInstances.values()) {
			map.remove(name);
		}
	}

	// ---------------------------------------------------------------- util

	/**
	 * Returns request from current thread.
	 */
	protected String getHttpSessionId() {
		HttpServletRequest request = RequestContextListener.getRequest();
		if (request == null) {
			throw new PetiteException("No HTTP request bound to the current thread. Maybe RequestContextListener is not available?");
		}
		return request.getSession().getId();
	}

}

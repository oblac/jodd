// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.PetiteContainer;
import jodd.petite.PetiteException;
import jodd.servlet.RequestContextListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Session scope stores unique object instances per single http session.
 * Upon creation, new session listener is registered (dynamically) that will
 * keep track on active sessions. {@link RequestContextListener} is used for accessing
 * the request and the session. Session-scoped beans are stored in the session.
 */
public class SessionScope implements Scope {

	private static final String ATTR_NAME = SessionScope.class.getName() + ".map";

	/**
	 * Session scope.
	 */
	public SessionScope(PetiteContainer petiteContainer) {
		// register session scope on first usage
		ThreadLocalScope threadLocalScope = petiteContainer.resolveScope(ThreadLocalScope.class);
		threadLocalScope.acceptScope(SessionScope.class);
	}

	@SuppressWarnings("unchecked")
	public Object lookup(String name) {
		HttpSession session = getCurrentHttpSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(ATTR_NAME);
		if (map == null) {
			return null;
		}
		return map.get(name);
	}

	@SuppressWarnings("unchecked")
	public void register(String name, Object bean) {
		HttpSession session = getCurrentHttpSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(ATTR_NAME);
		if (map == null) {
			map = new HashMap<String, Object>();
			session.setAttribute(ATTR_NAME, map);
		}
		map.put(name, bean);
	}

	@SuppressWarnings("unchecked")
	public void remove(String name) {
		HttpSession session = getCurrentHttpSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(ATTR_NAME);
		if (map != null) {
			map.remove(name);
		}
	}

	public boolean accept(Scope referenceScope) {
		Class<? extends Scope> refScopeType = referenceScope.getClass();

		if (refScopeType == SingletonScope.class) {
			return true;
		}

		if (refScopeType == SessionScope.class) {
			return true;
		}

		return false;
	}

	// ---------------------------------------------------------------- util

	/**
	 * Returns request from current thread.
	 */
	protected HttpSession getCurrentHttpSession() {
		HttpServletRequest request = RequestContextListener.getRequest();
		if (request == null) {
			throw new PetiteException("No HTTP request bound to the current thread. Is RequestContextListener registered?");
		}
		return request.getSession();
	}

}
// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.scope;

import jodd.petite.BeanData;
import jodd.petite.BeanDefinition;
import jodd.petite.PetiteException;
import jodd.servlet.RequestContextListener;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Request scope.
 */
public class RequestScope implements Scope {

	// ---------------------------------------------------------------- request map

	protected static final String ATTR_NAME = RequestScope.class.getName() + ".MAP.";

	/**
	 * Returns instance map from http request.
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, BeanData> getRequestMap(HttpServletRequest servletRequest) {
		return (Map<String, BeanData>) servletRequest.getAttribute(ATTR_NAME);
	}

	/**
	 * Removes instance map from the request.
	 */
	protected void removeRequestMap(HttpServletRequest servletRequest) {
		servletRequest.removeAttribute(ATTR_NAME);
	}

	/**
	 * Creates instance map and stores it in the request.
	 */
	protected Map<String, BeanData> createRequestMap(HttpServletRequest servletRequest) {
		Map<String, BeanData> map = new HashMap<String, BeanData>();
		servletRequest.setAttribute(ATTR_NAME, map);
		return map;
	}


	// ---------------------------------------------------------------- scope

	public void shutdown() {
	}

	public Object lookup(String name) {
		HttpServletRequest servletRequest = getCurrentHttpRequest();
		Map<String, BeanData> map = getRequestMap(servletRequest);
		if (map == null) {
			return null;
		}

		BeanData beanData = map.get(name);
		if (beanData == null) {
			return null;
		}
		return beanData.getBean();
	}

	public void register(BeanDefinition beanDefinition, Object bean) {
		HttpServletRequest servletRequest = getCurrentHttpRequest();
		Map<String, BeanData> map = getRequestMap(servletRequest);
		if (map == null) {
			map = createRequestMap(servletRequest);
		}

		BeanData beanData = new BeanData(beanDefinition, bean);
		map.put(beanDefinition.getName(), beanData);
	}

	public void remove(String name) {
		HttpServletRequest servletRequest = getCurrentHttpRequest();
		Map<String, BeanData> map = getRequestMap(servletRequest);
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

		if (refScopeType == RequestScope.class) {
			return true;
		}

		return false;
	}

	// ---------------------------------------------------------------- util

	/**
	 * Returns request from current thread.
	 */
	protected HttpServletRequest getCurrentHttpRequest() {
		HttpServletRequest request = RequestContextListener.getRequest();
		if (request == null) {
			throw new PetiteException("No HTTP request bound to the current thread. Is RequestContextListener registered?");
		}
		return request;
	}

}
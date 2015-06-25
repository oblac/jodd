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
		Map<String, BeanData> map = new HashMap<>();
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
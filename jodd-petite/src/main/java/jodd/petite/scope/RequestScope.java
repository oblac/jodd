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
import jodd.petite.PetiteContainer;
import jodd.petite.PetiteException;
import jodd.servlet.RequestContextListener;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Request scope.
 */
public class RequestScope implements Scope {

	private final PetiteContainer pc;

	public RequestScope(final PetiteContainer pc) {
		this.pc = pc;
	}

	/**
	 * Since request map is stored in the requests, app server may want to
	 * persist it during the restart (for example).
	 */
	private class TransientBeanData {

		private final transient BeanData beanData;

		private TransientBeanData(final BeanData beanData) {
			this.beanData = beanData;
		}

		public BeanData get() {
			return beanData;
		}
	}

	// ---------------------------------------------------------------- request map

	protected static final String ATTR_NAME = RequestScope.class.getName() + ".MAP.";

	/**
	 * Returns instance map from http request.
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, TransientBeanData> getRequestMap(final HttpServletRequest servletRequest) {
		return (Map<String, TransientBeanData>) servletRequest.getAttribute(ATTR_NAME);
	}

	/**
	 * Removes instance map from the request.
	 */
	protected void removeRequestMap(final HttpServletRequest servletRequest) {
		servletRequest.removeAttribute(ATTR_NAME);
	}

	/**
	 * Creates instance map and stores it in the request.
	 */
	protected Map<String, TransientBeanData> createRequestMap(final HttpServletRequest servletRequest) {
		Map<String, TransientBeanData> map = new HashMap<>();
		servletRequest.setAttribute(ATTR_NAME, map);
		return map;
	}


	// ---------------------------------------------------------------- scope

	@Override
	public void shutdown() {
	}

	@Override
	public Object lookup(final String name) {
		HttpServletRequest servletRequest = getCurrentHttpRequest();
		Map<String, TransientBeanData> map = getRequestMap(servletRequest);
		if (map == null) {
			return null;
		}

		BeanData beanData = map.get(name).get();
		if (beanData == null) {
			return null;
		}
		return beanData.bean();
	}

	@Override
	public void register(final BeanDefinition beanDefinition, final Object bean) {
		HttpServletRequest servletRequest = getCurrentHttpRequest();
		Map<String, TransientBeanData> map = getRequestMap(servletRequest);
		if (map == null) {
			map = createRequestMap(servletRequest);
		}

		map.put(beanDefinition.name(), new TransientBeanData(new BeanData(pc, beanDefinition, bean)));
	}

	@Override
	public void remove(final String name) {
		HttpServletRequest servletRequest = getCurrentHttpRequest();
		Map<String, TransientBeanData> map = getRequestMap(servletRequest);
		if (map != null) {
			map.remove(name);
		}
	}

	@Override
	public boolean accept(final Scope referenceScope) {
		Class<? extends Scope> refScopeType = referenceScope.getClass();

		if (refScopeType == ProtoScope.class) {
			return true;
		}

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
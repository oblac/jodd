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
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Session scope stores unique object instances per single http session.
 * Upon creation, new session listener is registered (dynamically) that will
 * keep track on active sessions. {@link RequestContextListener} is used for accessing
 * the request and the session.
 */
public class SessionScope extends ShutdownAwareScope {

	private final PetiteContainer pc;

	public SessionScope(final PetiteContainer pc) {
		this.pc = pc;
	}


	// ---------------------------------------------------------------- destory

	protected static final String SESSION_BEANS_NAME = SessionScope.class.getName() + ".SESSION_BEANS.";

	/**
	 * Registers new session destroy callback if not already registered.
	 */
	protected Map<String, BeanData> registerSessionBeans(final HttpSession httpSession) {
	    SessionBeans sessionBeans = new SessionBeans();
		httpSession.setAttribute(SESSION_BEANS_NAME, sessionBeans);
		return sessionBeans.getBeanMap();
	}

	/**
	 * Returns instance map from http session.
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, BeanData> getSessionMap(final HttpSession session) {
		SessionBeans sessionBeans = (SessionBeans) session.getAttribute(SESSION_BEANS_NAME);
		if (sessionBeans == null) {
			return null;
		}
		return sessionBeans.getBeanMap();
	}


	/**
	 * Session beans holder and manager.
	 */
	public class SessionBeans implements HttpSessionBindingListener, Serializable {

		protected Map<String, BeanData> beanMap = new HashMap<>();

		/**
		 * Returns bean map used in this session.
		 */
		public Map<String, BeanData> getBeanMap() {
			return beanMap;
		}

		@Override
		public void valueBound(final HttpSessionBindingEvent event) {
			// do nothing
		}

		/**
		 * Session is destroyed.
		 */
		@Override
		public void valueUnbound(final HttpSessionBindingEvent event) {
			for (BeanData beanData : beanMap.values()) {
				destroyBean(beanData);
			}
		}
	}

	// ---------------------------------------------------------------- scope

	/**
	 * Shutdowns the Session scope. Calls destroyable methods on
	 * all destroyable beans available in this moment.
	 */
	@Override
	public void shutdown() {
		super.shutdown();
	}

	@Override
	public Object lookup(final String name) {
		HttpSession session = getCurrentHttpSession();
		Map<String, BeanData> map = getSessionMap(session);
		if (map == null) {
			return null;
		}

		BeanData beanData = map.get(name);
		if (beanData == null) {
			return null;
		}
		return beanData.bean();
	}

	@Override
	public void register(final BeanDefinition beanDefinition, final Object bean) {
		HttpSession session = getCurrentHttpSession();
		Map<String, BeanData> map = getSessionMap(session);
		if (map == null) {
			map = registerSessionBeans(session);
		}

		final BeanData beanData = new BeanData(pc, beanDefinition, bean);

		map.put(beanDefinition.name(), beanData);

		registerDestroyableBeans(beanData);
	}

	@Override
	public void remove(final String name) {
		if (totalRegisteredDestroyableBeans() == 0) {
			return;
		}
		HttpSession session = getCurrentHttpSession();
		Map<String, BeanData> map = getSessionMap(session);
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
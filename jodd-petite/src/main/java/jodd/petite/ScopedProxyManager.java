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

package jodd.petite;

import jodd.cache.TypeCache;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.petite.proxetta.ProxettaBeanDefinition;
import jodd.petite.scope.Scope;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaFactory;
import jodd.proxetta.pointcuts.AllMethodsPointcut;
import jodd.util.ArraysUtil;
import jodd.util.ClassUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for mixing scopes.
 * 'Mixed scopes' is the situation when 'smaller' scope
 * is injected into the 'bigger' scope. Trivial example
 * is when <b>prototype</b> scoped bean is injected into
 * <b>singleton</b> bean.
 * <p>
 * Mixed scopes are handled using smart factory/proxy-wrapper singletons
 * instead of 'smaller' scoped beans. On each method call of
 * wrapped factory, smaller scoped bean will be looked up and
 * that instance will be used for passing by the method call.
 * <p>
 * Manager also has to determine if scope combination is candidate for
 * mixed scopes.
 */
public class ScopedProxyManager {

	private static final Logger log = LoggerFactory.getLogger(ScopedProxyManager.class);

	protected ProxyAspect aspect = new ProxyAspect(ScopedProxyAdvice.class, new AllMethodsPointcut());

	protected TypeCache<Class> proxyClasses = TypeCache.createDefault();
	protected Map<String, Object> proxies = new HashMap<>();

	public ScopedProxyManager() {
		log.debug("ScopedProxyManager created");
	}

	/**
	 * Returns scoped proxy bean if injection scopes are mixed on some injection point.
	 * May return <code>null</code> if mixing scopes is not detected.
	 */
	public Object lookupValue(final PetiteContainer petiteContainer, final BeanDefinition targetBeanDefinition, final BeanDefinition refBeanDefinition) {
		Scope targetScope = targetBeanDefinition.scope;
		Scope refBeanScope = refBeanDefinition.scope;

		boolean detectMixedScopes = petiteContainer.config().isDetectMixedScopes();
		boolean wireScopedProxy = petiteContainer.config().isWireScopedProxy();

		// when target scope is null then all beans can be injected into it
		// similar to prototype scope
		if (targetScope != null && !targetScope.accept(refBeanScope)) {

			if (!wireScopedProxy) {
				if (detectMixedScopes) {
					throw new PetiteException(createMixingMessage(targetBeanDefinition, refBeanDefinition));
				}
				return null;
			}

			if (detectMixedScopes) {
				if (log.isWarnEnabled()) {
					log.warn(createMixingMessage(targetBeanDefinition, refBeanDefinition));
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug(createMixingMessage(targetBeanDefinition, refBeanDefinition));
				}
			}

			String scopedProxyBeanName = refBeanDefinition.name;

			Object proxy = proxies.get(scopedProxyBeanName);

			if (proxy == null) {
				proxy = createScopedProxyBean(petiteContainer, refBeanDefinition);

				proxies.put(scopedProxyBeanName, proxy);
			}

			return proxy;
		}

		return null;
	}

	/**
	 * Creates mixed scope message.
	 */
	protected String createMixingMessage(final BeanDefinition targetBeanDefinition, final BeanDefinition refBeanDefinition) {
		return "Scopes mixing detected: " +
				refBeanDefinition.name + "@" + refBeanDefinition.scope.getClass().getSimpleName() + " -> " +
				targetBeanDefinition.name + "@" + targetBeanDefinition.scope.getClass().getSimpleName();
	}


	/**
	 * Creates scoped proxy bean for given bean definition.
	 */
	protected Object createScopedProxyBean(final PetiteContainer petiteContainer, final BeanDefinition refBeanDefinition) {

		Class beanType = refBeanDefinition.type;

		Class proxyClass = proxyClasses.get(beanType);

		if (proxyClass == null) {
			// create proxy class only once

			if (refBeanDefinition instanceof ProxettaBeanDefinition) {
				// special case, double proxy!

				ProxettaBeanDefinition pbd =
					(ProxettaBeanDefinition) refBeanDefinition;

				ProxyProxetta proxetta = Proxetta.proxyProxetta().withAspects(ArraysUtil.insert(pbd.proxyAspects, aspect, 0));

				proxetta.setClassNameSuffix("$ScopedProxy");
				proxetta.setVariableClassName(true);

				ProxyProxettaFactory builder = proxetta.proxy().setTarget(pbd.originalTarget);

				proxyClass = builder.define();

				proxyClasses.put(beanType, proxyClass);
			}
			else {
				ProxyProxetta proxetta = Proxetta.proxyProxetta().withAspect(aspect);

				proxetta.setClassNameSuffix("$ScopedProxy");
				proxetta.setVariableClassName(true);

				ProxyProxettaFactory builder = proxetta.proxy().setTarget(beanType);

				proxyClass = builder.define();

				proxyClasses.put(beanType, proxyClass);
			}
		}

		Object proxy;

		try {
			proxy = ClassUtil.newInstance(proxyClass);

			Field field = proxyClass.getField("$__petiteContainer$0");

			field.set(proxy, petiteContainer);

			field = proxyClass.getField("$__name$0");

			field.set(proxy, refBeanDefinition.name);
		} catch (Exception ex) {
			throw new PetiteException(ex);
		}

		return proxy;
	}

}

// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.scope.Scope;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.pointcuts.AllMethodsPointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	protected Map<Class, Class> proxyClasses = new HashMap<Class, Class>();
	protected Map<String, Object> proxies = new HashMap<String, Object>();

	public ScopedProxyManager() {
		if (log.isDebugEnabled()) {
			log.debug("ScopedProxyManager created");
		}
	}

	/**
	 * Returns scoped proxy bean if injection scopes are mixed on some injection point.
	 * May return <code>null</code> if mixing scopes is not detected.
	 */
	public Object lookupValue(PetiteContainer petiteContainer, BeanDefinition targetBeanDefinition, BeanDefinition refBeanDefinition) {
		Scope targetScope = targetBeanDefinition.scope;
		Scope refBeanScope = refBeanDefinition.scope;

		boolean detectMixedScopes = petiteContainer.getConfig().isDetectMixedScopes();
		boolean wireScopedProxy = petiteContainer.getConfig().isWireScopedProxy();

		// when target scope is null then all beans can be injected into it
		// similar to prototype scope
		if (targetScope != null && targetScope.accept(refBeanScope) == false) {

			if (wireScopedProxy == false) {
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
	protected String createMixingMessage(BeanDefinition targetBeanDefinition, BeanDefinition refBeanDefinition) {
		return "Scopes mixing detected: " +
				refBeanDefinition.name + "@" + refBeanDefinition.scope.getClass().getSimpleName() + " -> " +
				targetBeanDefinition.name + "@" + targetBeanDefinition.scope.getClass().getSimpleName();
	}


	/**
	 * Creates scoped proxy bean for given bean definition.
	 */
	protected Object createScopedProxyBean(PetiteContainer petiteContainer, BeanDefinition refBeanDefinition) {

		Class beanType = refBeanDefinition.type;

		Class proxyClass = proxyClasses.get(beanType);

		if (proxyClass == null) {
			// create proxy class only once

			ProxyProxetta proxetta = ProxyProxetta.withAspects(aspect);

			proxetta.setClassNameSuffix("$ScopedProxy");
			proxetta.setVariableClassName(true);

			ProxyProxettaBuilder builder = proxetta.builder(beanType);

			proxyClass = builder.define();

			proxyClasses.put(beanType, proxyClass);
		}

		Object proxy;

		try {
			proxy = proxyClass.newInstance();

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

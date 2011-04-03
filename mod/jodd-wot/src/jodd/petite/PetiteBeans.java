// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.manager.PetiteManager;
import jodd.petite.scope.DefaultScope;
import jodd.petite.scope.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base layer of {@link PetiteContainer Petite Container}.
 * Holds beans and scopes definitions.
 */
public abstract class PetiteBeans {

	private static final Logger log = LoggerFactory.getLogger(PetiteBeans.class);

	/**
	 * Map of all beans definitions.
	 */
	protected final Map<String, BeanDefinition> beans = new HashMap<String, BeanDefinition>();

	/**
	 * Map of all bean scopes.
	 */
	protected final Map<Class<? extends Scope>, Scope> scopes = new HashMap<Class<? extends Scope>, Scope>();

	/**
	 * Petite manager.
	 */
	protected final PetiteManager petiteManager;

	/**
	 * Petite configuration.
	 */
	protected final PetiteConfig petiteConfig;

	protected PetiteBeans(PetiteManager petiteManager, PetiteConfig petiteConfig) {
		this.petiteManager = petiteManager;
		this.petiteConfig = petiteConfig;
	}

	/**
	 * Returns Petite manager.
	 */
	public PetiteManager getManager() {
		return petiteManager;
	}

	/**
	 * Returns {@link PetiteConfig Petite configuration}.
	 * All changes on config should be applied <b>before</b>
	 * beans registration process starts.
	 */
	public PetiteConfig getConfig() {
		return petiteConfig;
	}

	// ---------------------------------------------------------------- scopes

	/**
	 * Resolves scope from scope type.
	 */
	protected Scope resolveScope(Class<? extends Scope> scopeType) {
		Scope scope = scopes.get(scopeType);
		if (scope == null) {
			try {
				scope = scopeType.newInstance();
				registerScope(scopeType, scope);
				scopes.put(scopeType, scope);
			} catch (Exception ex) {
				throw new PetiteException("Unable to create Petite scope: '" + scopeType.getName(), ex);
			}
		}
		return scope;
	}

	/**
	 * Registers new scope. It is not necessary to manually register scopes,
	 * since they become registered on first scope resolving.
	 * However, it is possible to pre-register some scopes, or to replace one scope
	 * type with another. This may be important for testing purposes when
	 * using container-depended scopes.
	 */
	public void registerScope(Class<? extends Scope> scopeType, Scope scope) {
		scopes.put(scopeType, scope);
	}

	// ---------------------------------------------------------------- lookup beans

	/**
	 * Lookups for {@link BeanDefinition bean definition}.
	 * Returns <code>null</code> if bean name doesn't exist.
	 */
	protected BeanDefinition lookupBeanDefinition(String name) {
		return beans.get(name);
	}

	/**
	 * Lookups for existing bean. Throws exception if bean is not found.
	 */
	protected BeanDefinition lookupExistingBeanDefinition(String name) {
		BeanDefinition beanDefinition = lookupBeanDefinition(name);
		if (beanDefinition == null) {
			throw new PetiteException("Bean: '" + name + "' not registered.");
		}
		return beanDefinition;
	}

	/**
	 * Returns <code>true</code> if bean name is registered.
	 */
	public boolean isBeanNameRegistered(String name) {
		return lookupBeanDefinition(name) != null;
	}

	/**
	 * Resolves bean's name from bean annotation or type name. May be used for resolving bean name
	 * of base type during registration of bean subclass.
	 */
	public String resolveBeanName(Class type) {
		return PetiteUtil.resolveBeanName(type, petiteConfig.getUseFullTypeNames());
	}

	// ---------------------------------------------------------------- register beans

	/**
	 * Single point of bean registration. The following rules are applied:
	 * <li>if name is missing, it will be resolved from the class (name or annotation)
	 * <li>if wiring mode is missing, it will be resolved from the class (annotation or default one)
	 * <li>if scope type is missing, it will be resolved from the class (annotation or default one)
	 */
	protected BeanDefinition registerPetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		if (name == null) {
			name = PetiteUtil.resolveBeanName(type, petiteConfig.getUseFullTypeNames());
		}
		if (wiringMode == null) {
			wiringMode = PetiteUtil.resolveBeanWiringMode(type);
		}
		if (wiringMode == WiringMode.DEFAULT) {
			wiringMode = petiteConfig.getDefaultWiringMode();
		}
		if (scopeType == null) {
			scopeType = PetiteUtil.resolveBeanScopeType(type);
		}
		if (scopeType == DefaultScope.class) {
			scopeType = petiteConfig.getDefaultScope();
		}
		BeanDefinition existing = removeBeanDefinition(name);
		if (existing != null) {
			if (petiteConfig.getDetectDuplicatedBeanNames()) {
				throw new PetiteException(
						"Duplicated bean name detected while registering class '" + type.getName() + "'. Petite bean class '" +
						existing.type.getName() + "' is already registered with the name '" + name + "'.");
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Registering bean: " + name +
					" of type: " + type.getSimpleName() +
					" in: " + scopeType.getSimpleName() +
					" using wiring mode: " + wiringMode.toString());
		}

		// registering

		// check if type is valid
		if ((type != null) && (type.isInterface() == true)) {
			throw new PetiteException("Unable to register interface '" + type.getName() + "'.");
		}
		// register
		Scope scope = resolveScope(scopeType);
		BeanDefinition beanDefinition = new BeanDefinition(name, type, scope, wiringMode);
		beans.put(name, beanDefinition);
		return beanDefinition;
	}

	protected void definePetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
		BeanDefinition def = registerPetiteBean(name, type, scopeType, wiringMode);
		def.ctor = petiteManager.resolveCtorInjectionPoint(type);
		def.properties = PropertyInjectionPoint.EMPTY;
		def.methods = MethodInjectionPoint.EMPTY;
		def.initMethods = InitMethodPoint.EMPTY;
	}

	/**
	 * Removes bean and returns definition of removed bean.
	 * All resolvers references are deleted, too.
	 */
	protected BeanDefinition removeBeanDefinition(String name) {
		BeanDefinition bd = beans.remove(name);
		if (bd == null) {
			return null;
		}
		petiteManager.removeResolvers(bd.type);
		bd.scopeRemove();
		return bd;
	}

	// ---------------------------------------------------------------- injection points

	/**
	 * Single point of constructor injection point registration.
	 */
	protected void registerPetiteCtorInjectionPoint(String beanName, Class[] paramTypes, String[] references) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		beanDefinition.ctor = petiteManager.defineCtorInjectionPoint(beanDefinition.type, paramTypes, references);
	}

	/**
	 * Single point of property injection point registration.
	 */
	protected void registerPetitePropertyInjectionPoint(String beanName, String property, String reference) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		PropertyInjectionPoint pip = petiteManager.definePropertyInjectionPoint(
				beanDefinition.type,
				property,
				reference == null ? null : new String[] {reference});
		beanDefinition.addPropertyInjectionPoint(pip);
	}

	/**
	 * Single point of method injection point registration.
	 */
	protected void registerPetiteMethodInjectionPoint(String beanName, String methodName, Class[] arguments, String[] references) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		MethodInjectionPoint mip = petiteManager.defineMethodInjectionPoint(beanDefinition.type, methodName, arguments, references);
		beanDefinition.addMethodInjectionPoint(mip);
	}

	/**
	 * Single point of init method registration.
	 */
	protected void registerPetiteInitMethods(String beanName, String[] beforeMethodNames, String[] afterMethodNames) {
		BeanDefinition beanDefinition = lookupExistingBeanDefinition(beanName);
		InitMethodPoint[] methods = petiteManager.defineInitMethods(beanDefinition.type, beforeMethodNames, afterMethodNames);
		beanDefinition.addInitMethodPoints(methods);
	}

	// ---------------------------------------------------------------- statistics

	/**
	 * Returns total number of registered beans.
	 */
	public int getTotalBeans() {
		return beans.size();
	}

	/**
	 * Returns total number of used scopes.
	 */
	public int getTotalScopes() {
		return scopes.size();
	}

	/**
	 * Returns iterator over all registered beans.
	 */
	public Iterator<BeanDefinition> beansIterator() {
		return beans.values().iterator();
	}

}

// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.scope.DefaultScope;
import jodd.petite.scope.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

	// ---------------------------------------------------------------- lookup

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

	// ---------------------------------------------------------------- register

	/**
	 * Registers bean. The following rules are applied:
	 * <li>if name is missing, it will be resolved from the class (name or annotation)
	 * <li>if wiring mode is missing, it will be resolved from the class (annotation or default one)
	 * <li>if scope type is missing, it will be resolved from the class (annotation or default one)
	 */
	protected BeanDefinition registerPetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode, PetiteConfig pcfg) {
		if (name == null) {
			name = PetiteUtil.resolveBeanName(type, pcfg.getUseFullTypeNames());
		}
		if (wiringMode == null) {
			wiringMode = PetiteUtil.resolveBeanWiringMode(type);
		}
		if (wiringMode == WiringMode.DEFAULT) {
			wiringMode = pcfg.getDefaultWiringMode();
		}
		if (scopeType == null) {
			scopeType = PetiteUtil.resolveBeanScopeType(type);
		}
		if (scopeType == DefaultScope.class) {
			scopeType = pcfg.getDefaultScope();
		}
		BeanDefinition existing = removeBeanDefinition(name);	// todo to remove all references in resolvers?
		if (existing != null) {
			if (pcfg.getDetectDuplicatedBeanNames()) {
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

	/**
	 * Removes bean and returns definition of removed bean.
	 */
	protected BeanDefinition removeBeanDefinition(String name) {
		BeanDefinition bd = beans.remove(name);
		if (bd == null) {
			return null;
		}
		bd.scopeRemove();
		// todo have to remove all references in resolvers!
		return bd;
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

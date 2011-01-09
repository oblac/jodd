// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.util.ClassLoaderUtil;
import jodd.introspector.ClassIntrospector;
import jodd.petite.meta.PetiteInject;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.MadvocException;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Manages all Madvoc action registrations.
 */
public class ActionsManager {

	private static final Logger log = LoggerFactory.getLogger(ActionsManager.class);

	@PetiteInject
	protected ActionMethodParser actionMethodParser;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	protected final Map<String, ActionConfig> configs;

	public ActionsManager() {
		this.configs = new HashMap<String, ActionConfig>();
	}

	/**
	 * Returns all registered action configurations. Should be used with care and
	 * usually only during configuration.
	 */
	public Map<String, ActionConfig> getAllActionConfigurations() {
		return configs;
	}


	// ---------------------------------------------------------------- register variations

	/**
	 * Registers action with provided action signature.
	 */
	public void register(String actionSignature) {
		register(actionSignature, null);
	}

	/**
	 * Registers action with provided action signature.
	 */
	public void register(String actionSignature, String actionPath) {
		int ndx = actionSignature.indexOf('#');
		if (ndx == -1) {
			throw new MadvocException("Madvoc action signature syntax error: '" + actionSignature + "'.");
		}
		String actionClassName = actionSignature.substring(0, ndx);
		String actionMethodName = actionSignature.substring(ndx + 1);
		Class actionClass;
		try {
			actionClass = ClassLoaderUtil.loadClass(actionClassName, this.getClass());
		} catch (ClassNotFoundException cnfex) {
			throw new MadvocException("Madvoc action class not found: '" + actionClassName + "'.", cnfex);
		}
		register(actionClass, actionMethodName, actionPath);
	}

	/**
	 * Registers action with provided action class and method name.
	 */
	public void register(Class actionClass, String actionMethod) {
		register(actionClass, actionMethod, null);
	}

	/**
	 * Registers action with provided action path, class and method name.
	 */
	public void register(Class actionClass, String actionMethod, String actionPath) {
		Method method = ClassIntrospector.lookup(actionClass).getMethod(actionMethod);
		if (method == null) {
			throw new MadvocException("Provided action class '" + actionClass.getSimpleName() + "' doesn't contain public method '" + actionMethod + "'.");
		}
		registerAction(actionClass, method, actionPath);
	}

	public void register(Class actionClass, Method actionMethod, String actionPath) {
		registerAction(actionClass, actionMethod, actionPath);
	}

	public void register(Class actionClass, Method actionMethod) {
		registerAction(actionClass, actionMethod, null);
	}

	// ---------------------------------------------------------------- registration

	/**
	 * Registration single point. Optionally, if action path with the same name already exist,
	 * exception will be thrown.
	 */
	protected void registerAction(Class actionClass, Method actionMethod, String actionPath) {
		ActionConfig cfg = actionMethodParser.parse(actionClass, actionMethod, actionPath);
		if (cfg == null) {
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("Registering Madvoc action: " + cfg.actionPath + " to: " +
					cfg.actionClass.getName() + '#' + cfg.actionMethod.getName());
		}
		boolean isDuplicate = configs.put(cfg.actionPath, cfg) != null;
		if (madvocConfig.isDetectDuplicatePathsEnabled()) {
			if (isDuplicate) {
				throw new MadvocException("Duplicated action path for '" + cfg + "'.");
			}
		}
	}

	// ---------------------------------------------------------------- look-up

	/**
	 * Returns action configurations for provided action path.
	 * Returns <code>null</code> if action path is not registered.
	 */
	public ActionConfig lookup(String actionPath) {
		return configs.get(actionPath);
	}

}

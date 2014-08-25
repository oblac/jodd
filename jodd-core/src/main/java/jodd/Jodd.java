// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.exception.UncheckedException;

import java.lang.reflect.Field;

/**
 * Jodd! This is the module manager for Jodd modules. On the very first access,
 * all modules get loaded and information about available modules is set.
 * In some environments such OSGI, however, classloader lookup does not work.
 * Then you need to manually initialize all Jodd components that are in use.
 */
public class Jodd {

	private static int ndx = 0;

	public static final int CORE 			= ndx++;
	public static final int BEAN 			= ndx++;
	public static final int DB 				= ndx++;
	public static final int HTTP 			= ndx++;
	public static final int INTROSPECTOR 	= ndx++;
	public static final int JSON 			= ndx++;
	public static final int JTX 			= ndx++;
	public static final int LAGARTO 		= ndx++;
	public static final int MADVOC 			= ndx++;
	public static final int MAIL 			= ndx++;
	public static final int PETITE 			= ndx++;
	public static final int PROPS 			= ndx++;
	public static final int PROXETTA 		= ndx++;
	public static final int SERVLET 		= ndx++;
	public static final int UPLOAD 			= ndx++;
	public static final int VTOR 			= ndx++;

	private static final Object[] MODULES = new Object[ndx];
	private static final String[] NAMES = new String[ndx];

	static {
		initAllModules();
	}

	/**
	 * Manual initialization of a module.
	 */
	public static void init(Class joddModuleClass) {
		String name = joddModuleClass.getName();

		int moduleId = -1;

		for (int i = 0; i < NAMES.length; i++) {
			String moduleName = NAMES[i];
			if (name.equals(moduleName)) {
				moduleId = i;
				break;
			}
		}

		if (moduleId == -1) {
			throw new IllegalArgumentException("Invalid module: " + joddModuleClass);
		}

		Object module = MODULES[moduleId];

		if (module != null) {
			if (module.getClass() == joddModuleClass) {
				// already registered
				return;
			}
		}

		MODULES[moduleId] = joddModuleClass;

		updateModuleInstance(moduleId);
	}

	/**
	 * Loads all modules on the classpath by using classloader
	 * of this class.
	 */
	public static void initAllModules() {
		final Field[] fields = Jodd.class.getFields();

		final ClassLoader classLoader = Jodd.class.getClassLoader();

		for (Field field : fields) {
			int index;

			try {
				index = ((Integer) field.get(null)).intValue();
			} catch (IllegalAccessException iaex) {
				throw new UncheckedException(iaex);
			}

			String moduleName = field.getName();

			String packageName = moduleName.toLowerCase();

			moduleName = moduleName.substring(0, 1).toUpperCase() +
					moduleName.substring(1, moduleName.length()).toLowerCase();

			String moduleClass = "jodd." + packageName + ".Jodd" + moduleName;

			NAMES[index] = moduleClass;

			try {
				MODULES[index] = classLoader.loadClass(moduleClass);
			} catch (ClassNotFoundException cnfex) {
				// ignore
			}
		}

		for (int i = 0; i < MODULES.length; i++) {
			updateModuleInstance(i);
		}
	}

	/**
	 * Updates modules instances by creating new modules.
	 * When new module is created, {@link JoddModule#start()}
	 * will be called only once.
	 */
	private static void updateModuleInstance(int moduleId) {
		Object module = MODULES[moduleId];

		if (module == null) {
			return;
		}

		if (module instanceof Class) {
			Class type = (Class) module;
			try {

				module = type.newInstance();
				MODULES[moduleId] = module;

				if (module instanceof JoddModule) {
					((JoddModule) module).start();
				}
			} catch (Exception ex) {
				MODULES[moduleId] = null;
				throw new UncheckedException(ex);
			}
		}
	}

	// ---------------------------------------------------------------- checkers

	/**
	 * Returns <code>true</code> if module is loaded.
	 */
	public static boolean isModuleLoaded(int moduleNdx) {
		return MODULES[moduleNdx] != null;
	}

	/**
	 * Returns module instance if module is loaded. It may return:
	 * <ul>
	 *     <li>null - when module is not registered/li>
	 *     <li>class - when module is registered, but not yet loaded</li>
	 *     <li>object - when module is registered and loaded</li>
	 * </ul>
	 */
	public static Object getModule(int moduleNdx) {
		return MODULES[moduleNdx];
	}

}
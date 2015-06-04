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

package jodd;

import jodd.exception.UncheckedException;

import java.lang.reflect.Field;

/**
 * Jodd! This is the module manager for Jodd modules. On the very first access,
 * all modules get loaded and information about available modules is set.
 * In some environments such OSGI, however, classloader lookup does not work.
 * Then you need to manually initialize all Jodd components that are in use.
 * <p>
 * Each module class contains some static global configuration.
 * Each module class has initialize itself in static block, so first access
 * to the config will also initialize the module. First module initialization
 * will trigger initialization of all modules (as defined in static block
 * of this class). Each module has to have static method <code>init()</code>
 * that register the module here. This method should be used when modules
 * can not be found by classloader.
 * <p>
 * Important: static block and init methods <b>must</b> be declared <b>last</b>
 * in the module class! Also, if module class contains some default instance
 * (as part of the module's configuration), this instance must <b>not</b>
 * use any other configuration in the constructor! Otherwise, that value
 * could not be changed.
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
	 *     <li>null - when module is not registered</li>
	 *     <li>class - when module is registered, but not yet loaded</li>
	 *     <li>object - when module is registered and loaded</li>
	 * </ul>
	 */
	public static Object getModule(int moduleNdx) {
		return MODULES[moduleNdx];
	}

}
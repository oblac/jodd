// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.exception.UncheckedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Jodd! This is a module manager for jodd modules. On the very first access,
 * all modules get loaded and information about available modules is set.
 * After that, you may use the {@link #bind(int, Object...)} method to
 * inter-connect modules; usually done when one module <i>optionally</i> depends
 * on the other.
 */
public class Jodd {

	public static final int CORE 		= 0;
	public static final int BEAN 		= 1;
	public static final int DB 			= 2;
	public static final int HTTP 		= 3;
	public static final int JSON 		= 4;
	public static final int JTX 		= 5;
	public static final int LAGARTO 	= 6;
	public static final int LOG 		= 7;
	public static final int MADVOC 		= 8;
	public static final int MAIL 		= 9;
	public static final int PETITE 		= 10;
	public static final int PROPS 		= 11;
	public static final int PROXETTA 	= 12;
	public static final int SERVLET 	= 13;
	public static final int UPLOAD 		= 14;
	public static final int VTOR 		= 15;

	private static final boolean[] LOADED;
	private static final Object[] MODULES;

	/**
	 * Initializes the Jodd modules.
	 * It does not do anything as all initialization is done
	 * in the static block.
	 */
	static void module() {
	}


	static {
		final Field[] fields = Jodd.class.getFields();

		LOADED = new boolean[fields.length];
		MODULES = new Object[fields.length];

		final ClassLoader classLoader = Jodd.class.getClassLoader();

		for (Field field : fields) {
			int index;

			try {
				index = ((Integer) field.get(null)).intValue();
			} catch (IllegalAccessException iaex) {
				throw new UncheckedException(iaex);
			}

			String moduleName = field.getName();

			moduleName = moduleName.substring(0, 1).toUpperCase() +
					moduleName.substring(1, moduleName.length()).toLowerCase();

			String moduleClass = "jodd.Jodd" + moduleName;

			try {
				MODULES[index] = classLoader.loadClass(moduleClass);
			} catch (ClassNotFoundException cnfex) {
				continue;
			}

			LOADED[index] = true;
		}

		// create module instances after all classes being loaded

		for (int i = 0; i < MODULES.length; i++) {
			Class type = (Class) MODULES[i];

			if (type != null) {
				try {
					MODULES[i] = type.newInstance();
				} catch (Exception ex) {
					throw new UncheckedException(ex);
				}
			}
		}
	}

	// ---------------------------------------------------------------- checkers

	/**
	 * Returns <code>true</code> if module is loaded.
	 */
	public static boolean isModuleLoaded(int moduleNdx) {
		return LOADED[moduleNdx];
	}

	/**
	 * Returns module instance if module is loaded.
	 */
	public static Object getModule(int moduleNdx) {
		return MODULES[moduleNdx];
	}

	// ---------------------------------------------------------------- activate

	/**
	 * Invokes <code>bind(Object)</code> or <code>bind(Object...)</code> method
	 * on target module's instance, if module is loaded. If module is not loaded,
	 * does nothing.
	 */
	public static void bind(final int moduleNdx, final Object... arguments) {
		if (!LOADED[moduleNdx]) {
			// module not loaded
			return;
		}

		Object module = MODULES[moduleNdx];

		try {
			if (arguments.length == 1) {
				Method method = module.getClass().getMethod("bind", Object.class);

				method.invoke(module, arguments[0]);
			} else {
				Method method = module.getClass().getMethod("bind", Object[].class);

				method.invoke(module, arguments);
			}
		} catch (Exception ex) {
			throw new UncheckedException(ex);
		}
	}

}
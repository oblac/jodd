// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import java.util.HashMap;
import java.util.Map;

import jodd.Jodd;
import jodd.bean.loader.BeanLoader;
import jodd.bean.loader.MapBeanLoader;
import jodd.bean.loader.ResultSetBeanLoader;
import jodd.util.ClassLoaderUtil;
import jodd.util.ReflectUtil;

/**
 * Manager for {@link BeanLoader} instances that populates java beans from various sources.
 */
public class BeanLoaderManager {

	// ---------------------------------------------------------------- manager

	protected static final Map<Class, BeanLoader> loaders = new HashMap<Class, BeanLoader>();

    static {
		registerDefaults();
    }

	/**
	 * Unregisters all loaders.
	 */
	public static void unregisterAll() {
		loaders.clear();
	}


	/**
	 * Registers default set of loaders.
	 * <p>
	 * Important note: class that doesn't come with JDK is first being examined
	 * for existence. Examination is done with <code>Class.forName()</code>
	 * If class exists, it will be registered. If not, it will be skipped.
	 *
	 * @see #register
	 */
	public static void registerDefaults() {
		register(java.util.Map.class, new MapBeanLoader());
		register(java.sql.ResultSet.class, new ResultSetBeanLoader());

		if (Jodd.isServletLoaded()) {
			try {
				Class loaderAddon = ClassLoaderUtil.loadClass("jodd.bean.loader.ServletBeanLoaderManagerAddon");

				ReflectUtil.invoke(loaderAddon, "registerDefaults", null);
			} catch (Exception ex) {
				throw new BeanException(ex);
			}
		}
	}

	/**
	 * Registers loader for an objects of specific type.
	 *
	 * @param type	type of object that will be used by loader to populate bean.
	 * @param load	loader object that populates a bean.
	 *
	 * @see #registerDefaults
	 */
	public static void register(Class type, BeanLoader load) {
		loaders.put(type, load);
	}

	public static void unregister(Class type) {
		loaders.remove(type);
	}


	// ---------------------------------------------------------------- lookup


	/**
	 * Returns loader for the specific object type.
	 *
	 * @param type    type of object that will be used by loader to populate bean.
	 *
	 * @return loader for objects of specific type, <code>null</code> if no loader found.
	 */
	public static BeanLoader lookup(Class type) {
		return loaders.get(type);
	}

	/**
	 * Performs more thoroughly search for bean loader. It examines all available
	 * loaders and returns the first that matches the object type.
	 */
	public static BeanLoader lookup(Object source) {
		BeanLoader load = lookup(source.getClass());
		if (load == null) {					// class not found, scan for instanceof matching
			for (Class key : loaders.keySet()) {
				if (key.isInstance(source)) {
					load = lookup(key);
					break;
				}
			}
		}
		return load;
	}

}
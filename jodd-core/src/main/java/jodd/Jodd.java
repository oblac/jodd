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

/**
 * Jodd! This is the simple module manager for Jodd modules. On the very first access,
 * all modules get loaded and information about available modules is set.
 * In some environments such OSGI, however, classloader lookup does not work.
 * Then you need to manually initialize all Jodd components that are in use.
 * <p>
 * Each module must have the following code blocK at the bottom of the class:
 * <pre>{@code
 * static {
 *     init();
 * }
 * public static void init() {
 *     Jodd.initModule();
 * }
 * }</pre>
 *
 * <h2>How Jodd module gets loaded and initialized?</h2>
 * There are two stages Jodd's modules are going through: 1) loading and 2) initialization.
 * <b>Loading</b> is basically detecting if the module exist. Initialization runs some additional
 * optional code block.
 * <p>
 * There are two ways how modules are loaded:
 * <p>
 * By explicitly calling {@link Jodd#initAllModules()} - or by simply accessing
 * the {@link Jodd} class (see {@link Jodd#JODD} :). This method loads all Jodd modules
 * by simply loading each class. When class is loaded, it's static block is executed,
 * and therefore the {@code init()} method as well:
 * <pre>{@literal
 * Jodd.static -> Jodd.initAllModules() -> JoddModule.NAME.static -> JoddModule.NAME.init() -> Jodd.initModule
 * }</pre>
 * <p>
 * Second way is just by using the module's static class. It will load Jodd class as well:
 * <pre>{@literal
 * JoddModule.static -> Jodd.static -> Jodd.initAllModules() -> ...
 * }</pre>
 * <b>Initialization</b> happens once when <b>all</b> modules are loaded. It consist of running the
 * initialization code blocks.
 */
public class Jodd {

	private static boolean initialized = false;

	/**
	 * Jodd modules.
	 */
	public enum JoddModule {
		CORE,
		BEAN,
		DB,
		DECORA,
		HTTP,
		HTML_STAPLER,
		INTROSPECTOR,
		JSON,
		JTX,
		LAGARTO,
		MADVOC,
		MAIL,
		PETITE,
		PROPS,
		PROXETTA,
		SERVLET,
		UPLOAD,
		VTOR;

		static {
			initAllModules();
		}

		private Class<?> moduleClass;

		/**
		 * Loads a module by looking for the module classname on the classpath.
		 */
		private synchronized void load() {
			if (Jodd.isModuleLoaded(this)) {
				return;
			}
			final String moduleClassName = resolveClassName(this);

			try {
				this.moduleClass = Jodd.class.getClassLoader().loadClass(moduleClassName);
			} catch (ClassNotFoundException ignore) {
				// module not found
			}
		}

		/**
		 * Starts a module once all modules are loaded.
		 */
		private synchronized void start() {
			if (!Jodd.isModuleLoaded(this)) {
				return;
			}
			try {
				// create new instance to force loading of the class
				moduleClass.newInstance();
			}
			catch (Exception ignore) {
			}
		}
	}

	static {
		initAllModules();
	}

	/**
	 * Loads and initializes all modules on the classpath.
	 */
	private synchronized static void initAllModules() {
		if (initialized) {
			return;
		}

		for (JoddModule joddModule : JoddModule.values()) {
			joddModule.load();
		}

		for (JoddModule joddModule : JoddModule.values()) {
			joddModule.start();
		}

		initialized = true;
	}

	/**
	 * Returns {@code true} if module is loaded.
	 */
	public static boolean isModuleLoaded(JoddModule module) {
		return module.moduleClass != null;
	}

	/**
	 * @see #initModule(Runnable)
	 */
	public static void initModule() {
		initModule(null);
	}

	/**
	 * Initializes the Jodd module.
	 * Must be called only from the module's class, since it resolves the module from the
	 * calling hierarchy.
	 */
	public static void initModule(Runnable initRunnable) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		for (StackTraceElement stackTraceElement : stackTrace) {
			final String className = stackTraceElement.getClassName();

			for (JoddModule joddModule : JoddModule.values()) {
				if (className.equals(resolveClassName(joddModule))) {
					joddModule.load();

					if (initRunnable != null) {
						initRunnable.run();
					}

					return;
				}
			}
		}

		throw new RuntimeException("Only Jodd module can be initialized!");
	}

	private static String resolveClassName(JoddModule joddModule) {
		String moduleName = joddModule.name();

		String packageName = moduleName.toLowerCase();

		// remove all underscores from package name
		while (true) {
			int ndx = packageName.indexOf('_');

			if (ndx == -1) {
				break;
			}

			packageName = packageName.substring(0, ndx) +
				packageName.substring(ndx + 1);
		}

		moduleName = moduleName.substring(0, 1).toUpperCase() +
			moduleName.substring(1, moduleName.length()).toLowerCase();

		// make CamelCase class name
		while (true) {
			int ndx = moduleName.indexOf('_');

			if (ndx == -1) {
				break;
			}

			moduleName = moduleName.substring(0, ndx) +
				moduleName.substring(ndx + 1, ndx + 2).toUpperCase() +
				moduleName.substring(ndx + 2);
		}

		return "jodd." + packageName + ".Jodd" + moduleName;
	}


	/**
	 * Ascii art of Jodds name. Every serious framework needs one:)
	 */
	public static String JODD =
		"          __          __    __\n" +
		"         / /___  ____/ /___/ /\n" +
		"    __  / / __ \\/ __  / __  / \n" +
		"   / /_/ / /_/ / /_/ / /_/ /  \n" +
		"   \\____/\\____/\\__,_/\\__,_/   \n";

}
// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.config;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.io.findfile.FindClass;
import jodd.madvoc.MadvocException;
import jodd.madvoc.WebApplication;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.result.ActionResult;
import jodd.util.ClassLoaderUtil;
import jodd.util.ReflectUtil;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Method;
import java.net.URL;

/**
 * Default Madvoc configurator uses auto-magic to configure {@link WebApplication}.
 * It searches the class path for all classes which names ends with 'Action' and 'Result'
 * suffixes. Each such class will be loaded and introspected to determine
 * if it represents valid Madvoc entity and then registered into the web application.
 * <p>
 * Action class is scanned for the {@link MadvocAction}. All public methods with {@link Action}
 * are registered as Madvoc actions.
 */
public class AutomagicMadvocConfigurator extends FindClass implements MadvocConfigurator {

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected ResultsManager resultsManager;

	protected String actionClassSuffix;         // default action class suffix, for class path search
	protected String resultClassSuffix;         // default action result class suffix, for class path search
	protected long elapsed;

	public AutomagicMadvocConfigurator() {
		actionClassSuffix = "Action";
		resultClassSuffix = "Result";
		elapsed = 0;
	}

	/**
	 * Configures web application from system classpath
	 * @see #configure(java.net.URL[])
	 */
	public void configure() {
		configure(ClassLoaderUtil.getFullClassPath(AutomagicMadvocConfigurator.class));
	}

	/**
	 * Configures web application from specified classpath. The whole process is done in the following steps:
	 * <ol>
	 * <li>scanning web application classpath</li>
	 * <li>invoking external configurations, if exist</li>
	 * <li>applying defaults</li>
	 * </ol>
	 * @see #configure()
	 */
	public void configure(URL[] classpath) {
		elapsed = System.currentTimeMillis();

		try {
			scanUrls(classpath);
		} catch (Exception ex) {
			throw new MadvocException("Unable to scan classpath.", ex); 
		}
		elapsed = System.currentTimeMillis() - elapsed;
		System.out.println("Madvoc configured in " + elapsed + " ms. Total actions: " + actionsManager.getAllActionConfigurations().size());
	}


	/**
	 * Parses class name that matches madvoc-related names.
	 */
	@Override
	protected void onEntry(EntryData entryData) {
		String entryName = entryData.getName();
		if (entryName.endsWith(actionClassSuffix) == true) {
			try {
				onActionClass(entryName);
			} catch (ClassNotFoundException cnfex) {
				throw new MadvocException("Unable to load Madvoc action class: " + entryName, cnfex);
			}
		} else if (entryName.endsWith(resultClassSuffix) == true) {
			try {
				onResultClass(entryName);
			} catch (ClassNotFoundException cnfex) {
				throw new MadvocException("Unable to load Madvoc result class: " + entryName, cnfex);
			}
		}
	}

	// ---------------------------------------------------------------- class check

	public boolean checkClass(Class clazz) {
		return ((clazz.isAnonymousClass() == false) &&
				(clazz.isArray() == false) &&
				(clazz.isEnum() == false) &&
				(clazz.isInterface() == false) &&
				(clazz.isLocalClass() == false) &&
				(clazz.isMemberClass() == false) &&
				(clazz.isPrimitive() == false)
		);
	}

	// ---------------------------------------------------------------- handlers

	/**
	 * Builds action configuration on founded action class.
	 * Action classes are annotated with {@link jodd.madvoc.meta.MadvocAction} annotation.
	 */
	@SuppressWarnings("NonConstantStringShouldBeStringBuffer")
	protected void onActionClass(String className) throws ClassNotFoundException {
		Class<?> actionClass = ClassLoaderUtil.loadClass(className, this.getClass());

		if (checkClass(actionClass) == false) {
			return; 
		}
		if (actionClass.getAnnotation(MadvocAction.class) == null) {
			return;
		}

		ClassDescriptor cd = ClassIntrospector.lookup(actionClass);
		Method[] allPublicMethods = cd.getAllMethods();
		for (Method method : allPublicMethods) {
			if (method.getAnnotation(Action.class) == null) {
				continue;
			}
			actionsManager.register(actionClass, method);
		}
	}

	/**
	 * Loads madvoc result from founded {@link jodd.madvoc.result.ActionResult} instance.
	 */
	@SuppressWarnings({"unchecked"})
	protected void onResultClass(String className) throws ClassNotFoundException {
		Class resultClass = ClassLoaderUtil.loadClass(className, this.getClass());
		if (resultClass.equals(ActionResult.class)) {
			return;
		}
		if (ReflectUtil.isSubclass(resultClass, ActionResult.class) == true) {
			resultsManager.register(resultClass);
		}
	}

}

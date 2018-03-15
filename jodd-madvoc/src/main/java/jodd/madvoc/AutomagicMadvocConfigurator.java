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

package jodd.madvoc;

import jodd.bean.JoddBean;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.io.findfile.ClassScanner;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocComponentLifecycle;
import jodd.madvoc.component.MadvocContainer;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.ActionAnnotation;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.MadvocComponent;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Default Madvoc configurator uses auto-magic to configure {@link WebApp}.
 * It searches the class path for all classes which names ends with 'Action' and 'Result'
 * suffixes. Each such class will be loaded and introspected to determine
 * if it represents valid Madvoc entity and then registered into the web application.
 * <p>
 * Action class is scanned for the {@link MadvocAction}. All public methods with {@link Action}
 * are registered as Madvoc actions.
 */
public class AutomagicMadvocConfigurator implements MadvocComponentLifecycle.Init, MadvocComponentLifecycle.Start {

	private static final Logger log = LoggerFactory.getLogger(AutomagicMadvocConfigurator.class);
	private final ClassScanner classScanner = new ClassScanner();

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected MadvocContainer madvocContainer;

	protected String actionClassSuffix;         // default action class suffix, for class path search
	protected long elapsed;

	protected final byte[] madvocComponentAnnotation;

	protected List<Runnable> webappConfigurations = new ArrayList<>();
	protected List<Runnable> madvocComponents = new ArrayList<>();

	public AutomagicMadvocConfigurator() {
		actionClassSuffix = "Action";
		madvocComponentAnnotation = ClassScanner.bytecodeSignatureOfType(MadvocComponent.class);
	}

	public AutomagicMadvocConfigurator withScanner(final Consumer<ClassScanner> scannerConsumer) {
		scannerConsumer.accept(classScanner);
		return this;
	}

	@Override
	public void init() {
		elapsed = System.currentTimeMillis();

		classScanner.smartModeEntries();
		classScanner.onEntry(ENTRY_CONSUMER);
		classScanner.scanDefaultClasspath();

		try {
			classScanner.start();
		} catch (Exception ex) {
			throw new MadvocException("Scan classpath error", ex);
		}

		madvocComponents.forEach(Runnable::run);

		elapsed = System.currentTimeMillis() - elapsed;
	}

	@Override
	public void start() {
		long now = System.currentTimeMillis();

		webappConfigurations.forEach(Runnable::run);

		elapsed += (System.currentTimeMillis() - now);
		log.info("Madvoc configured in " + elapsed + " ms. Total actions: " + actionsManager.getActionsCount());
	}


	/**
	 * Parses class name that matches madvoc-related names.
	 */
	private Consumer<ClassScanner.EntryData> ENTRY_CONSUMER = new Consumer<ClassScanner.EntryData>() {
		@Override
		public void accept(final ClassScanner.EntryData entryData) {
			String entryName = entryData.name();

			if (entryName.endsWith(actionClassSuffix)) {
				try {
					onActionClass(entryName);
				} catch (Exception ex) {
					log.debug("Invalid Madvoc action, ignoring: " + entryName);
				}
			} else if (entryData.isTypeSignatureInUse(madvocComponentAnnotation)) {
				try {
					onMadvocComponentClass(entryName);
				} catch (Exception ex) {
					log.debug("Invalid Madvoc component ignoring: {}" + entryName);
				}
			}
		}
	};

	// ---------------------------------------------------------------- class check

	/**
	 * Determines if class should be examined for Madvoc annotations.
	 * Array, anonymous, primitive, interfaces and so on should be
	 * ignored. Sometimes, checking may fail due to e.g. <code>NoClassDefFoundError</code>;
	 * we should continue searching anyway.
	 */
	public boolean checkClass(final Class clazz) {
		try {
			if (clazz.isAnonymousClass()) {
				return false;
			}
			if (clazz.isArray() || clazz.isEnum()) {
				return false;
			}
			if (clazz.isInterface()) {
				return false;
			}
			if (clazz.isLocalClass()) {
				return false;
			}
			if ((clazz.isMemberClass() ^ Modifier.isStatic(clazz.getModifiers()))) {
				return false;
			}
			if (clazz.isPrimitive()) {
				return false;
			}
			int modifiers = clazz.getModifiers();
			if (Modifier.isAbstract(modifiers)) {
				return false;
			}
			return true;
		} catch (Throwable ignore) {
			return false;
		}
	}

	// ---------------------------------------------------------------- handlers

	/**
	 * Builds action runtime configuration on founded action class.
	 * Action classes are annotated with {@link jodd.madvoc.meta.MadvocAction} annotation.
	 */
	@SuppressWarnings("NonConstantStringShouldBeStringBuffer")
	protected void onActionClass(final String className) throws ClassNotFoundException {
		Class<?> actionClass = classScanner.loadClass(className);

		if (actionClass == null) {
			return;
		}

		if (!checkClass(actionClass)) {
			return; 
		}

		if (actionClass.getAnnotation(MadvocAction.class) == null) {
			return;
		}

		ClassDescriptor cd = JoddBean.defaults().getClassIntrospector().lookup(actionClass);

		MethodDescriptor[] allMethodDescriptors = cd.getAllMethodDescriptors();
		for (MethodDescriptor methodDescriptor : allMethodDescriptors) {
			if (!methodDescriptor.isPublic()) {
				continue;
			}
			// just public methods
			Method method = methodDescriptor.getMethod();

			boolean hasAnnotation = false;
			for (ActionAnnotation<?> actionAnnotation : madvocConfig.getActionAnnotationInstances()) {
				if (actionAnnotation.hasAnnotationOn(method)) {
					hasAnnotation = true;
					break;
				}
			}
			if (!hasAnnotation) {
				continue;
			}

			webappConfigurations.add(() -> actionsManager.registerAction(actionClass, method, null));
		}
	}

	/**
	 * Registers new Madvoc component.
	 */
	protected void onMadvocComponentClass(final String className) throws ClassNotFoundException {
		Class componentClass = classScanner.loadClass(className);

		if (componentClass == null) {
			return;
		}

		if (!checkClass(componentClass)) {
			return;
		}

		madvocComponents.add(() -> madvocContainer.registerComponent(componentClass));
	}

}

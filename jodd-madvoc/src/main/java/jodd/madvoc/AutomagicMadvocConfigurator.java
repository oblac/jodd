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

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.MethodDescriptor;
import jodd.io.findfile.ClassScanner;
import jodd.madvoc.component.ActionConfigManager;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocComponentLifecycle;
import jodd.madvoc.component.MadvocContainer;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.MadvocComponent;
import jodd.petite.meta.PetiteInject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
	private final ClassScanner classScanner;

	@PetiteInject
	protected ActionConfigManager actionConfigManager;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected MadvocContainer madvocContainer;

	protected String actionClassSuffix;         // default action class suffix, for class path search
	protected long elapsed;

	protected static final byte[] MADVOC_COMPONENT_ANNOTATION = ClassScanner.bytecodeSignatureOfType(MadvocComponent.class);

	protected List<Runnable> webappConfigurations = new ArrayList<>();
	protected List<Runnable> madvocComponents = new ArrayList<>();

	public AutomagicMadvocConfigurator() {
		actionClassSuffix = "Action";

		classScanner = new ClassScanner();
		classScanner.detectEntriesMode(true);
		classScanner.scanDefaultClasspath();
		registerAsConsumer(classScanner);
	}

	public AutomagicMadvocConfigurator(final ClassScanner classScanner) {
		actionClassSuffix = "Action";

		this.classScanner = classScanner;
		registerAsConsumer(classScanner);
	}

	@Override
	public void init() {
		final long startTime = System.currentTimeMillis();

		try {
			log.info("Scanning...");

			classScanner.start();
		} catch (final Exception ex) {
			throw new MadvocException("Scan classpath error", ex);
		}

		madvocComponents.forEach(Runnable::run);

		log.info("Scanning is complete.");

		elapsed = System.currentTimeMillis() - startTime;
	}

	@Override
	public void start() {
		final long startTime = System.currentTimeMillis();

		webappConfigurations.forEach(Runnable::run);

		elapsed += (System.currentTimeMillis() - startTime);

		log.info(createInfoMessage());
	}

	protected String createInfoMessage() {
		return "Madvoc configured in " + elapsed + " ms. Total actions: " + actionsManager.getActionsCount();
	}

	/**
	 * Parses class name that matches madvoc-related names.
	 */
	protected void registerAsConsumer(final ClassScanner classScanner) {
		classScanner.registerEntryConsumer(classPathEntry -> {
			final String entryName = classPathEntry.name();

			if (entryName.endsWith(actionClassSuffix)) {
				try {
					acceptActionClass(classPathEntry.loadClass());
				} catch (final Exception ex) {
					log.debug("Invalid Madvoc action, ignoring: " + entryName);
				}
			}
			else if (classPathEntry.isTypeSignatureInUse(MADVOC_COMPONENT_ANNOTATION)) {
				try {
					acceptMadvocComponentClass(classPathEntry.loadClass());
				} catch (final Exception ex) {
					log.debug("Invalid Madvoc component ignoring: {}" + entryName);
				}
			}
		});
	}

	// ---------------------------------------------------------------- class check

	/**
	 * Determines if class should be examined for Madvoc annotations.
	 * Array, anonymous, primitive, interfaces and so on should be
	 * ignored. Sometimes, checking may fail due to e.g. <code>NoClassDefFoundError</code>;
	 * we should continue searching anyway.
	 */
	protected boolean checkClass(final Class clazz) {
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
			final int modifiers = clazz.getModifiers();
			if (Modifier.isAbstract(modifiers)) {
				return false;
			}
			return true;
		} catch (final Throwable ignore) {
			return false;
		}
	}

	// ---------------------------------------------------------------- handlers

	/**
	 * Builds action runtime configuration on founded action class.
	 * Action classes are annotated with {@link jodd.madvoc.meta.MadvocAction} annotation.
	 */
	@SuppressWarnings("NonConstantStringShouldBeStringBuffer")
	protected void acceptActionClass(final Class<?> actionClass) {

		if (actionClass == null) {
			return;
		}

		if (!checkClass(actionClass)) {
			return; 
		}

		if (actionClass.getAnnotation(MadvocAction.class) == null) {
			return;
		}

		final ClassDescriptor cd = ClassIntrospector.get().lookup(actionClass);

		final MethodDescriptor[] allMethodDescriptors = cd.getAllMethodDescriptors();
		for (final MethodDescriptor methodDescriptor : allMethodDescriptors) {
			if (!methodDescriptor.isPublic()) {
				continue;
			}
			// just public methods
			final Method method = methodDescriptor.getMethod();

			final boolean hasAnnotation = actionConfigManager.hasActionAnnotationOn(method);

			if (!hasAnnotation) {
				continue;
			}

			webappConfigurations.add(() -> actionsManager.registerAction(actionClass, method, null));
		}
	}

	/**
	 * Registers new Madvoc component.
	 */
	protected void acceptMadvocComponentClass(final Class componentClass) {
		if (componentClass == null) {
			return;
		}

		if (!checkClass(componentClass)) {
			return;
		}

		madvocComponents.add(() -> madvocContainer.registerComponent(componentClass));
	}

}

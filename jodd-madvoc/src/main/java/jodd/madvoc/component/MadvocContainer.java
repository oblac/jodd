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

package jodd.madvoc.component;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.MadvocException;
import jodd.petite.PetiteContainer;
import jodd.props.Props;
import jodd.util.ClassUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Madvoc container. Works internally with {@link jodd.petite.PetiteContainer}.
 */
public class MadvocContainer {

	public static final String MADVOC_PETITE_CONTAINER_NAME = "madpc";

	private static final Logger log = LoggerFactory.getLogger(MadvocContainer.class);

	private final PetiteContainer madpc;
	private final List<String> initListeners = new ArrayList<>();
	private final List<String> readyListeners = new ArrayList<>();
	private final List<String> stopListeners = new ArrayList<>();

	public MadvocContainer() {
		madpc = new PetiteContainer();
		madpc.addSelf(MADVOC_PETITE_CONTAINER_NAME);
	}

	/**
	 * Returns Petite container used for the Madvoc.
	 */
	public PetiteContainer petite() {
		return madpc;
	}

	/**
	 * Registers component instance using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 * @see #registerComponent(Class)
	 */
	public final void registerComponentInstance(Object componentInstance) {
		Class component = componentInstance.getClass();
		String name = resolveBaseComponentName(component);
		registerComponentInstance(name, componentInstance);
	}

	/**
	 * Registers component using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 * @see #registerComponentInstance(Object)
	 */
	public final void registerComponent(Class component) {
		String name = resolveBaseComponentName(component);
		registerComponentInstance(name, component);
	}

	/**
	 * Registers Madvoc component with given name.
	 */
	public final void registerComponentInstance(String name, Class component) {
		log.debug(() -> "Registering Madvoc component '" + name + "' of type " + component.getName());

		madpc.removeBean(name);
		madpc.registerPetiteBean(component, name, null, null, false);

		if (ClassUtil.isTypeOf(component, MadvocListener.Init.class)) {
			if (!initListeners.contains(name)) {
				initListeners.add(name);
			}
		}
		if (ClassUtil.isTypeOf(component, MadvocListener.Ready.class)) {
			if (!readyListeners.contains(name)) {
				readyListeners.add(name);
			}
		}
		if (ClassUtil.isTypeOf(component, MadvocListener.Stop.class)) {
			if (!stopListeners.contains(name)) {
				stopListeners.add(name);
			}
		}
	}

	/**
	 * Registers component instance and wires it with internal container.
	 */
	public final void registerComponentInstance(String name, Object componentInstance) {
		log.debug(() -> "Registering Madvoc component '" + name + "' instance of " + componentInstance.getClass().getName());

		madpc.removeBean(name);
		madpc.addBean(name, componentInstance);

		if (ClassUtil.isInstanceOf(componentInstance, MadvocListener.Init.class)) {
			if (!initListeners.contains(name)) {
				initListeners.add(name);
			}
		}
		if (ClassUtil.isInstanceOf(componentInstance, MadvocListener.Ready.class)) {
			if (!readyListeners.contains(name)) {
				readyListeners.add(name);
			}
		}
		if (ClassUtil.isInstanceOf(componentInstance, MadvocListener.Stop.class)) {
			if (!stopListeners.contains(name)) {
				stopListeners.add(name);
			}
		}
	}


	// ---------------------------------------------------------------- listeners

	/**
	 * Fires the <b>init</b> event.
	 */
	public void fireInitEvent() {
		initListeners.forEach(name ->  {
			MadvocListener.Init listener = (MadvocListener.Init) lookupComponent(name);
			listener.init();
		});
	}
	public void fireReadyEvent() {
		readyListeners.forEach(name ->  {
			MadvocListener.Ready listener = (MadvocListener.Ready) lookupComponent(name);
			listener.ready();
		});
	}


	// ---------------------------------------------------------------- lookup

	/**
	 * Returns registered component or {@code null} if component is not registered.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T lookupComponent(Class<T> component) {
		String name = resolveBaseComponentName(component);
		return (T) madpc.getBean(name);
	}

	/**
	 * Returns existing component. Throws an exception if component is not registered.
	 */
	public <T> T lookupExistingComponent(Class<T> component) {
		T existingComponent = lookupComponent(component);
		if (existingComponent == null) {
			throw new MadvocException("Madvoc component not found: " + component.getName());
		}
		return existingComponent;
	}

	/**
	 * Returns registered component or {@code null} if component does not exist.
	 */
	public Object lookupComponent(String componentName) {
		return madpc.getBean(componentName);
	}

	/**
	 * Resolves the name of the last base non-abstract subclass for provided component.
	 * It iterates all subclasses up to the <code>Object</cde> and declares the last
	 * non-abstract class as base component. Component name will be resolved from the
	 * founded base component.
	 */
	private String resolveBaseComponentName(Class component) {
		Class lastComponent = component;
		while (true) {
			Class superClass = component.getSuperclass();
			if (superClass.equals(Object.class)) {
				break;
			}
			component = superClass;
			if (!Modifier.isAbstract(component.getModifiers())) {
				lastComponent = component;
			}
		}
		return madpc.resolveBeanName(lastComponent);
	}


	// ---------------------------------------------------------------- params

	public void defineParams(Props props) {
		log.debug("Defining Madvoc parameters");

		madpc.defineParameters(props);
	}

}

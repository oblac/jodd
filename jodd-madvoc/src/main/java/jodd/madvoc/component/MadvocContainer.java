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
import jodd.mutable.MutableInteger;
import jodd.petite.PetiteContainer;
import jodd.props.Props;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Madvoc container. Works internally with {@link jodd.petite.PetiteContainer}.
 */
public class MadvocContainer {

	public static final String MADVOC_PETITE_CONTAINER_NAME = "madpc";

	private static final Logger log = LoggerFactory.getLogger(MadvocContainer.class);

	private final PetiteContainer madpc;

	public MadvocContainer() {
		madpc = new PetiteContainer();
		madpc.addSelf(MADVOC_PETITE_CONTAINER_NAME);
	}

	/**
	 * Returns Petite container used for the Madvoc.
	 */
	public PetiteContainer getPetiteContainer() {
		return madpc;
	}

	/**
	 * Registers component using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 *
	 * @see #registerComponentInstance(Object)
	 */
	public void registerComponent(final Class component) {
		String name = resolveBaseComponentName(component);
		registerComponent(name, component);
	}

	public <T> void registerComponent(final Class<T> component, final Consumer<T> consumer) {
		String name = resolveBaseComponentName(component);
		registerComponent(name, component, consumer);
	}

	/**
	 * Registers Madvoc component with given name.
	 */
	public void registerComponent(final String name, final Class component) {
		log.debug(() -> "Madvoc WebApp component: [" + name + "] --> " + component.getName());

		madpc.removeBean(name);
		madpc.registerPetiteBean(component, name, null, null, false, null);
	}

	/**
	 * Registers Madvoc component with given name.
	 */
	public <T> void registerComponent(final String name, final Class<T> component, final Consumer<T> consumer) {
		log.debug(() -> "Madvoc WebApp component: [" + name + "] --> " + component.getName());

		madpc.removeBean(name);
		madpc.registerPetiteBean(component, name, null, null, false, consumer);
	}

	/**
	 * Registers component instance using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 *
	 * @see #registerComponentInstance(String, Object)
	 */
	public void registerComponentInstance(final Object componentInstance) {
		Class component = componentInstance.getClass();
		String name = resolveBaseComponentName(component);
		registerComponentInstance(name, componentInstance);
	}

	/**
	 * Registers component instance and wires it with internal container.
	 * Warning: in this moment we can not guarantee that all other components
	 * are registered, replaced or configuration is update; therefore DO NOT
	 * USE injection, unless you are absolutely sure it works.
	 */
	public void registerComponentInstance(final String name, final Object componentInstance) {
		log.debug(() -> "Madvoc WebApp component: [" + name + "] --> " + componentInstance.getClass().getName());

		madpc.removeBean(name);
		madpc.addBean(name, componentInstance);
	}

	// ---------------------------------------------------------------- listeners

	/**
	 * Fires the Madvoc event.
	 * Warning: since event handlers may register more handlers, we
	 * must collect first the list of components that matches the type
	 * and then to execute.
	 */
	public void fireEvent(final Class listenerType) {
		final Set<String> existing = new HashSet<>();

		while (true) {
			MutableInteger newCount = MutableInteger.of(0);

			madpc.forEachBeanType(listenerType, name -> {
				if (existing.add(name)) {
					// name not found, fire!
					newCount.value++;

					Object listener = lookupComponent(name);
					if (listener != null) {
						MadvocComponentLifecycle.invoke(listener, listenerType);
					}
				}
			});

			if (newCount.value == 0) {
				break;
			}
		}
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Returns registered component or {@code null} if component is not registered.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T lookupComponent(final Class<T> component) {
		String name = resolveBaseComponentName(component);
		return (T) madpc.getBean(name);
	}

	/**
	 * Returns existing component. Throws an exception if component is not registered.
	 */
	public <T> T requestComponent(final Class<T> component) {
		T existingComponent = lookupComponent(component);
		if (existingComponent == null) {
			throw new MadvocException("Madvoc component not found: " + component.getName());
		}
		return existingComponent;
	}

	/**
	 * Returns existing component. Throws an exception if component is not registered.
	 */
	public <T> T requestComponent(final String componentName) {
		T existingComponent = (T) lookupComponent(componentName);
		if (existingComponent == null) {
			throw new MadvocException("Madvoc component not found: " + componentName);
		}
		return existingComponent;
	}

	/**
	 * Returns registered component or {@code null} if component does not exist.
	 */
	public Object lookupComponent(final String componentName) {
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

	public void defineParams(final Props props) {
		madpc.defineParameters(props);
	}

	public void defineParams(final Map params) {
		madpc.defineParameters(params);
	}

}

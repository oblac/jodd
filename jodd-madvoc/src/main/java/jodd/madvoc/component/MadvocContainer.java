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
import java.util.stream.Collectors;

/**
 * Madvoc container. Works internally with {@link jodd.petite.PetiteContainer}.
 */
public class MadvocContainer {

	public static final String MADVOC_PETITE_CONTAINER_NAME = "madpc";

	private static final Logger log = LoggerFactory.getLogger(MadvocContainer.class);

	private final PetiteContainer madpc;
	private final List<MadvocListenerDef> listeners = new ArrayList<>();

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
	public void registerComponentInstance(Object componentInstance) {
		Class component = componentInstance.getClass();
		String name = resolveBaseComponentName(component);
		registerComponentInstance(name, componentInstance);
	}

	/**
	 * Registers component using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 * @see #registerComponentInstance(Object)
	 */
	public void registerComponent(Class component) {
		String name = resolveBaseComponentName(component);
		registerComponentInstance(name, component);
	}

	/**
	 * Registers Madvoc component with given name.
	 */
	public void registerComponentInstance(String name, Class component) {
		log.debug(() -> "Registering Madvoc component '" + name + "' of type " + component.getName());

		madpc.removeBean(name);
		madpc.registerPetiteBean(component, name, null, null, false);

		for (Class eventType : MadvocListener.ALL_TYPES) {
			if (ClassUtil.isTypeOf(component, eventType)) {
				registerEventHandler(eventType, name);
			}
		}
	}

	/**
	 * Registers component instance and wires it with internal container.
	 */
	public void registerComponentInstance(String name, Object componentInstance) {
		log.debug(() -> "Registering Madvoc component '" + name + "' instance of " + componentInstance.getClass().getName());

		madpc.removeBean(name);
		madpc.addBean(name, componentInstance);

		for (Class eventType : MadvocListener.ALL_TYPES) {
			if (ClassUtil.isInstanceOf(componentInstance, eventType)) {
				registerEventHandler(eventType, name);
			}
		}
	}


	// ---------------------------------------------------------------- listeners

	protected static class MadvocListenerDef {
		public final String name;
		public final Class type;

		public MadvocListenerDef(String name, Class type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MadvocListenerDef that = (MadvocListenerDef) o;

			if (!name.equals(that.name)) return false;
			return type.equals(that.type);
		}

		@Override
		public int hashCode() {
			return 31 * name.hashCode() + type.hashCode();
		}
	}

	private final Object listenersLock = new Object();

	/**
	 * Fires the Madvoc event.
	 * Warning: since event handlers may register more handlers, we
	 * must collect first the list of components that matches the type
	 * and then to execute.
	 */
	public void fireEvent(Class listenerType) {

		while(true) {

			List<MadvocListenerDef> listenersToFire;

			synchronized (listenersLock) {
				listenersToFire =
					listeners.stream()
						.filter(def -> def.type == listenerType)
						.collect(Collectors.toList());

				listeners.removeAll(listenersToFire);
			}

			if (listenersToFire.isEmpty()) {
				// no more listeners, exit
				return;
			}

			listenersToFire.forEach(l -> {
				Object listener = lookupComponent(l.name);
				MadvocListener.invoke(listener, listenerType);
			});
		}
	}

	/**
	 * Registers event handler if not already registered.
	 */
	public void registerEventHandler(Class listenerType, String name) {
		synchronized (listenersLock) {
			MadvocListenerDef listenerDef = new MadvocListenerDef(name, listenerType);
			listeners.remove(listenerDef);
			listeners.add(listenerDef);
		}
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

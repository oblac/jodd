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

package jodd.petite;

import jodd.petite.meta.InitMethodInvocationStrategy;
import jodd.petite.scope.Scope;
import jodd.util.ClassUtil;

import java.util.function.Consumer;

/**
 * Petite registry helps with manual registration
 * of Petite beans by allowing fluent interface.
 */
public class PetiteRegistry {

	// ---------------------------------------------------------------- static

	/**
	 * Starts with fluent registration.
	 */
	public static PetiteRegistry of(final PetiteContainer petiteContainer) {
		return new PetiteRegistry(petiteContainer);
	}

	// ---------------------------------------------------------------- ctor

	private final PetiteContainer petiteContainer;

	/**
	 * Creates Petite registry.
	 */
	public PetiteRegistry(final PetiteContainer petiteContainer) {
		this.petiteContainer = petiteContainer;
	}

	/**
	 * Returns {@link PetiteContainer}.
	 */
	public PetiteContainer petiteContainer() {
		return petiteContainer;
	}

	// ---------------------------------------------------------------- register bean

	/**
	 * Starts with bean registration. Example:
	 * <code>bean(Foo.class).name("").scope(...).wiringMode(...).define().register();</code>
	 *
	 * @see PetiteBeans#registerPetiteBean(Class, String, Class, WiringMode, boolean, Consumer)
	 */
	public BeanRegister bean(final Class beanType) {
		return new BeanRegister(beanType);
	}

	public class BeanRegister<T> {

		private final Class<T> beanType;
		private String beanName;
		private Class<? extends Scope> scopeType;
		private WiringMode wiringMode;
		private boolean define;
		private Consumer<T> consumer;

		private BeanRegister(final Class<T> beanType) {
			this.beanType = beanType;
		}

		/**
		 * Defines bean name. If missing, it will be
		 * resolved from type name.
		 */
		public BeanRegister name(final String name) {
			this.beanName = name;
			return this;
		}

		/**
		 * Defines beans scope.
		 */
		public BeanRegister scope(final Class<? extends Scope> scope) {
			this.scopeType = scope;
			return this;
		}

		/**
		 * Defines beans wire mode.
		 */
		public BeanRegister wire(final WiringMode wiringMode) {
			this.wiringMode = wiringMode;
			return this;
		}

		/**
		 * Bean will be defined rather just registered.
		 */
		public BeanRegister define() {
			this.define = true;
			return this;
		}

		public BeanRegister with(final Consumer<T> consumer) {
			this.consumer = consumer;
			return this;
		}

		/**
		 * Registers a bean.
		 */
		public void register() {
			petiteContainer.registerPetiteBean(beanType, beanName, scopeType, wiringMode, define, consumer);
		}
	}

	// ---------------------------------------------------------------- wiring

	/**
	 * Starts with defining injection points (i.e. wiring) for existing bean.
	 */
	public BeanWire wire(final String beanName) {
		petiteContainer.lookupExistingBeanDefinition(beanName);
		return new BeanWire(beanName);
	}

	/**
	 * Bean wiring.
	 */
	public class BeanWire {

		protected final String beanName;

		private BeanWire(final String beanName) {
			this.beanName = beanName;
		}

		// ---------------------------------------------------------------- property

		/**
		 * Wires beans property. Example:
		 * <code>wire("").property("").ref(...).bind();</code>
		 * @see PetiteBeans#registerPetitePropertyInjectionPoint(String, String, String)
		 */
		public BeanWireProperty property(final String propertyName) {
			return new BeanWireProperty(propertyName);
		}

		public class BeanWireProperty {

			protected final String propertyName;
			protected String reference;

			private BeanWireProperty(final String propertyName) {
				this.propertyName = propertyName;
			}

			/**
			 * Defines property reference,
			 */
			public BeanWireProperty ref(final String reference) {
				this.reference = reference;
				return this;
			}

			/**
			 * Registers property injection point.
			 */
			public void bind() {
				petiteContainer.registerPetitePropertyInjectionPoint(beanName, propertyName, reference);
			}
		}

		// ---------------------------------------------------------------- ctor

		/**
		 * Wires beans constructor.
		 * @see PetiteBeans#registerPetiteCtorInjectionPoint(String, Class[], String[])
		 */
		public BeanWireCtor ctor(final Class... ctorArgumentTypes) {
			return new BeanWireCtor(ctorArgumentTypes);
		}

		public class BeanWireCtor {

			protected final Class[] ctorArgumentTypes;
			protected String[] references;

			private BeanWireCtor(Class... ctorArgumentTypes) {
				if (ctorArgumentTypes.length == 0) {
					ctorArgumentTypes = null;
				}
				this.ctorArgumentTypes = ctorArgumentTypes;
			}

			/**
			 * Defines constructor references.
			 */
			public BeanWireCtor ref(String... references) {
				if (references.length == 0) {
					references = null;
				}
				this.references = references;
				return this;
			}

			/**
			 * Registers constructor injection point.
			 */
			public void bind() {
				petiteContainer.registerPetiteCtorInjectionPoint(beanName, ctorArgumentTypes, references);
			}
		}

		// ---------------------------------------------------------------- method
		/**
		 * Wires beans method.
		 * @see PetiteBeans#registerPetiteCtorInjectionPoint(String, Class[], String[])
		 */
		public BeanWireMethod method(final String methodName) {
			return new BeanWireMethod(methodName);
		}

		public class BeanWireMethod {

			protected final String methodName;
			protected Class[] methodArgumentTypes;
			protected String[] references;

			private BeanWireMethod(final String methodName) {
				this.methodName = methodName;
			}

			/**
			 * Defines method argument types.
			 */
			public BeanWireMethod args(Class... methodArgumentTypes) {
				if (methodArgumentTypes.length == 0) {
					methodArgumentTypes = null;
				}
				this.methodArgumentTypes = methodArgumentTypes;
				return this;
			}

			/**
			 * Defines method references.
			 */
			public BeanWireMethod ref(String... references) {
				if (references.length == 0) {
					references = null;
				}
				this.references = references;
				return this;
			}

			/**
			 * Registers method injection point.
			 */
			public void bind() {
				petiteContainer.registerPetiteMethodInjectionPoint(beanName, methodName, methodArgumentTypes, references);
			}
		}

		// ---------------------------------------------------------------- set

		/**
		 * Wires beans set. Example:
		 * <code>wire("").set("").ref(...).bind();</code>
		 * @see PetiteBeans#registerPetitePropertyInjectionPoint(String, String, String)
		 */
		public BeanWireSet set(final String setPropertyName) {
			return new BeanWireSet(setPropertyName);
		}

		public class BeanWireSet {

			protected final String setPropertyName;
			protected String reference;

			private BeanWireSet(final String setPropertyName) {
				this.setPropertyName = setPropertyName;
			}

			/**
			 * Defines set references.
			 */
			public BeanWireSet ref(final String reference) {
				this.reference = reference;
				return this;
			}

			/**
			 * Defines set injection point.
			 */
			public void bind() {
				petiteContainer.registerPetiteSetInjectionPoint(beanName, setPropertyName);
			}
		}
	}

	// ---------------------------------------------------------------- init

	/**
	 * Starts registration of init method.
	 */
	public BeanInit init(final String beanName) {
		petiteContainer.lookupExistingBeanDefinition(beanName);
		return new BeanInit(beanName);
	}

	public class BeanInit {

		protected final String beanName;
		protected String[] methods;
		protected InitMethodInvocationStrategy strategy;

		private BeanInit(final String beanName) {
			this.beanName = beanName;
		}

		/**
		 * Defines init methods.
		 */
		public BeanInit methods(String... methods) {
			if (methods.length == 0) {
				methods = null;
			}
			this.methods = methods;
			return this;
		}

		/**
		 * Defines init method invocation strategy,
		 */
		public BeanInit invoke(final InitMethodInvocationStrategy strategy) {
			this.strategy = strategy;
			return this;
		}

		/**
		 * Registers init methods.
		 */
		public void register() {
			petiteContainer.registerPetiteInitMethods(beanName, strategy, methods);
		}
	}

	// ---------------------------------------------------------------- destroy

	/**
	 * Starts registration of destroy method.
	 */
	public BeanDestroy destroy(final String beanName) {
		petiteContainer.lookupExistingBeanDefinition(beanName);
		return new BeanDestroy(beanName);
	}

	public class BeanDestroy {

		protected final String beanName;
		protected String[] methods;

		private BeanDestroy(final String beanName) {
			this.beanName = beanName;
		}

		/**
		 * Defines destroy methods.
		 */
		public BeanDestroy methods(String... methods) {
			if (methods.length == 0) {
				methods = null;
			}
			this.methods = methods;
			return this;
		}

		/**
		 * Registers destroy methods.
		 */
		public void register() {
			petiteContainer.registerPetiteDestroyMethods(beanName, methods);
		}
	}

	// ---------------------------------------------------------------- provider

	/**
	 * Starts with provider definition.
	 */
	public BeanProvider provider(final String providerName) {
		return new BeanProvider(providerName);
	}

	public class BeanProvider {

		protected final String providerName;
		protected String beanName;
		protected Class type;
		protected String methodName;
		protected Class[] methodArgsTypes;

		private BeanProvider(final String providerName) {
			this.providerName = providerName;
		}

		/**
		 * Defines bean name.
		 */
		public BeanProvider bean(final String beanName) {
			if (type != null) {
				throw new PetiteException("Petite provider type already defined");
			}
			this.beanName = beanName;
			return this;
		}

		/**
		 * Defines bean type.
		 */
		public BeanProvider type(final Class type) {
			if (beanName != null) {
				throw new PetiteException("Petite provider bean name already defined");
			}
			this.type = type;
			return this;
		}

		/**
		 * Defines provider method name.
		 */
		public BeanProvider method(final String methodName) {
			this.methodName = methodName;
			return this;
		}

		/**
		 * Defines method argument types.
		 */
		public BeanProvider args(Class... methodArgsTypes) {
			if (methodArgsTypes.length == 0) {
				methodArgsTypes = ClassUtil.EMPTY_CLASS_ARRAY;
			}
			this.methodArgsTypes = methodArgsTypes;
			return this;
		}

		/**
		 * Registers provider.
		 */
		public void register() {
			if (type != null) {
				petiteContainer.registerPetiteProvider(providerName, type, methodName, methodArgsTypes);
			} else {
				petiteContainer.registerPetiteProvider(providerName, beanName, methodName, methodArgsTypes);
			}
		}
	}

}
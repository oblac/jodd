// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.meta.InitMethodInvocationStrategy;
import jodd.petite.scope.Scope;

/**
 * Petite registry helps with manual registration
 * of Petite beans by allowing fluent interface.
 */
public class PetiteRegistry {

	// ---------------------------------------------------------------- static

	/**
	 * Starts with fluent registration.
	 */
	public static PetiteRegistry petite(PetiteContainer petiteContainer) {
		return new PetiteRegistry(petiteContainer);
	}

	// ---------------------------------------------------------------- ctor

	private final PetiteContainer petiteContainer;

	/**
	 * Creates Petite registry.
	 */
	public PetiteRegistry(PetiteContainer petiteContainer) {
		this.petiteContainer = petiteContainer;
	}

	// ---------------------------------------------------------------- register bean

	/**
	 * Starts with bean registration. Example:
	 * <code>bean(Foo.class).name("").scope(...).wiringMode(...).define().register();</code>
	 *
	 * @see PetiteBeans#registerPetiteBean(String, Class, Class, WiringMode, boolean)
	 */
	public BeanRegister bean(Class beanType) {
		return new BeanRegister(beanType);
	}

	public class BeanRegister {

		protected final Class beanType;
		protected String beanName;
		protected Class<? extends Scope> scopeType;
		protected WiringMode wiringMode;
		protected boolean define;

		private BeanRegister(Class beanType) {
			this.beanType = beanType;
		}

		/**
		 * Defines bean name. If missing, it will be
		 * resolved from type name.
		 */
		public BeanRegister name(String name) {
			this.beanName = name;
			return this;
		}

		/**
		 * Defines beans scope.
		 */
		public BeanRegister scope(Class<? extends Scope> scope) {
			this.scopeType = scope;
			return this;
		}

		/**
		 * Defines beans wire mode.
		 */
		public BeanRegister wire(WiringMode wiringMode) {
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

		/**
		 * Registers a bean.
		 */
		public void register() {
			petiteContainer.registerPetiteBean(beanName, beanType, scopeType, wiringMode, define);
		}
	}

	// ---------------------------------------------------------------- wiring

	/**
	 * Starts with defining injection points (i.e. wiring) for existing bean.
	 */
	public BeanWire wire(String beanName) {
		petiteContainer.lookupExistingBeanDefinition(beanName);
		return new BeanWire(beanName);
	}

	// todo wire(class)

	/**
	 * Bean wiring.
	 */
	public class BeanWire {

		protected final String beanName;

		private BeanWire(String beanName) {
			this.beanName = beanName;
		}

		// ---------------------------------------------------------------- property

		/**
		 * Wires beans property. Example:
		 * <code>wire("").property("").ref(...).bind();</code>
		 * @see PetiteBeans#registerPetitePropertyInjectionPoint(String, String, String)
		 */
		public BeanWireProperty property(String propertyName) {
			return new BeanWireProperty(propertyName);
		}

		public class BeanWireProperty {

			protected final String propertyName;
			protected String reference;

			private BeanWireProperty(String propertyName) {
				this.propertyName = propertyName;
			}

			/**
			 * Defines property reference,
			 */
			public BeanWireProperty ref(String reference) {
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
		public BeanWireCtor ctor(Class... ctorArgumentTypes) {
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
		public BeanWireMethod method(String methodName) {
			return new BeanWireMethod(methodName);
		}

		public class BeanWireMethod {

			protected final String methodName;
			protected Class[] methodArgumentTypes;
			protected String[] references;

			private BeanWireMethod(String methodName) {
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
		public BeanWireSet set(String setPropertyName) {
			return new BeanWireSet(setPropertyName);
		}

		public class BeanWireSet {

			protected final String setPropertyName;
			protected String reference;

			private BeanWireSet(String setPropertyName) {
				this.setPropertyName = setPropertyName;
			}

			/**
			 * Defines set references.
			 */
			public BeanWireSet ref(String reference) {
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
	public BeanInit init(String beanName) {
		petiteContainer.lookupExistingBeanDefinition(beanName);
		return new BeanInit(beanName);
	}

	public class BeanInit {

		protected final String beanName;
		protected String[] methods;
		protected InitMethodInvocationStrategy strategy;

		private BeanInit(String beanName) {
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
		public BeanInit invoke(InitMethodInvocationStrategy strategy) {
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

	// ---------------------------------------------------------------- provider

	/**
	 * Starts with provider definition.
	 */
	public BeanProvider provider(String providerName) {
		return new BeanProvider(providerName);
	}

	public class BeanProvider {

		protected final String providerName;
		protected String beanName;
		protected Class type;
		protected String methodName;
		protected Class[] methodArgsTypes;

		private BeanProvider(String providerName) {
			this.providerName = providerName;
		}

		/**
		 * Defines bean name.
		 */
		public BeanProvider bean(String beanName) {
			if (type != null) {
				throw new PetiteException("Petite provider type already defined");
			}
			this.beanName = beanName;
			return this;
		}

		/**
		 * Defines bean type.
		 */
		public BeanProvider type(Class type) {
			if (beanName != null) {
				throw new PetiteException("Petite provider bean name already defined");
			}
			this.type = type;
			return this;
		}

		/**
		 * Defines provider method name.
		 */
		public BeanProvider method(String methodName) {
			this.methodName = methodName;
			return this;
		}

		/**
		 * Defines method argument types.
		 */
		public BeanProvider args(Class... methodArgsTypes) {
			if (methodArgsTypes.length == 0) {
				methodArgsTypes = null;
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
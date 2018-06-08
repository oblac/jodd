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

import jodd.bean.BeanUtil;
import jodd.introspector.Setter;
import jodd.petite.def.BeanReferences;
import jodd.petite.def.CtorInjectionPoint;
import jodd.petite.def.DestroyMethodPoint;
import jodd.petite.def.InitMethodPoint;
import jodd.petite.def.MethodInjectionPoint;
import jodd.petite.def.PropertyInjectionPoint;
import jodd.petite.def.SetInjectionPoint;
import jodd.petite.def.ValueInjectionPoint;
import jodd.petite.meta.InitMethodInvocationStrategy;

import java.util.Collection;

/**
 * Petite bean is defined by {@link jodd.petite.BeanDefinition bean definition}
 * and it's bean instance. This is a internal bean. It holds data and
 * performs all the operation on the pair of definition and the value.
 */
public class BeanData<T> {

	private final PetiteContainer pc;
	private final BeanDefinition<T> beanDefinition;
	private final T bean;

	public BeanData(final PetiteContainer petiteContainer, final BeanDefinition<T> beanDefinition, final T bean) {
		this.pc = petiteContainer;
		this.beanDefinition = beanDefinition;
		this.bean = bean;
	}

	public BeanData(final PetiteContainer petiteContainer, final BeanDefinition<T> beanDefinition) {
		this.pc = petiteContainer;
		this.beanDefinition = beanDefinition;
		this.bean = (T) newBeanInstance();
	}

	/**
	 * Returns {@link BeanDefinition}.
	 */
	public BeanDefinition<T> definition() {
		return beanDefinition;
	}

	/**
	 * Returns Petite bean instance.
	 */
	public T bean() {
		return bean;
	}

	// ---------------------------------------------------------------- scope

	/**
	 * Registers scope.
	 */
	public void scopeRegister() {
		beanDefinition.scopeRegister(bean);
	}

	// ---------------------------------------------------------------- invoke

	/**
	 * Invokes init methods.
	 */
	public void invokeInitMethods(final InitMethodInvocationStrategy invocationStrategy) {
		for (final InitMethodPoint initMethod : beanDefinition.initMethodPoints()) {
			if (invocationStrategy != initMethod.invocationStrategy) {
				continue;
			}
			try {
				initMethod.method.invoke(bean);
			} catch (Exception ex) {
				throw new PetiteException("Invalid init method: " + initMethod, ex);
			}
		}
	}

	/**
	 * Calls destroy methods on given BeanData. Destroy methods are called
	 * without any order.
	 */
	public void callDestroyMethods() {
		for (final DestroyMethodPoint destroyMethodPoint : beanDefinition.destroyMethodPoints()) {
			try {
				destroyMethodPoint.method.invoke(bean);
			} catch (Exception ex) {
				throw new PetiteException("Invalid destroy method: " + destroyMethodPoint.method, ex);
			}
		}
	}

	public void invokeConsumerIfRegistered() {
		if (beanDefinition.consumer() == null) {
			return;
		}
		beanDefinition.consumer().accept(bean);
	}

	// ---------------------------------------------------------------- new instance

	/**
	 * Creates a new instance.
	 */
	public Object newBeanInstance() {
		if (beanDefinition.ctor == CtorInjectionPoint.EMPTY) {
			throw new PetiteException("No constructor (annotated, single or default) founded as injection point for: " + beanDefinition.type.getName());
		}

		int paramNo = beanDefinition.ctor.references.length;
		Object[] args = new Object[paramNo];

		// wiring
		if (beanDefinition.wiringMode != WiringMode.NONE) {
			for (int i = 0; i < paramNo; i++) {
				args[i] = pc.getBean(beanDefinition.ctor.references[i]);
				if (args[i] == null) {
					if ((beanDefinition.wiringMode == WiringMode.STRICT)) {
						throw new PetiteException(
							"Wiring constructor failed. References '" + beanDefinition.ctor.references[i] +
								"' not found for constructor: " + beanDefinition.ctor.constructor);
					}
				}
			}
		}

		// create instance
		final Object bean;
		try {
			bean = beanDefinition.ctor.constructor.newInstance(args);
		} catch (Exception ex) {
			throw new PetiteException("Failed to create new bean instance '" + beanDefinition.type.getName() + "' using constructor: " + beanDefinition.ctor.constructor, ex);
		}

		return bean;
	}

	// ---------------------------------------------------------------- params

	/**
	 * Injects all parameters.
	 */
	public void injectParams(final ParamManager paramManager, final boolean implicitParamInjection) {
		if (beanDefinition.name == null) {
			return;
		}

		if (implicitParamInjection) {
			// implicit
			final int len = beanDefinition.name.length() + 1;
			for (final String param : beanDefinition.params) {
				final Object value = paramManager.get(param);
				final String destination = param.substring(len);
				try {
					BeanUtil.declared.setProperty(bean, destination, value);
				} catch (Exception ex) {
					throw new PetiteException("Unable to set parameter: '" + param + "' to bean: " + beanDefinition.name, ex);
				}
			}
		}

		// explicit
		for (final ValueInjectionPoint pip : beanDefinition.values) {
			final String value = paramManager.parseKeyTemplate(pip.valueTemplate);

			try {
				BeanUtil.declared.setProperty(bean, pip.property, value);
			} catch (Exception ex) {
				throw new PetiteException("Unable to set value for: '" + pip.valueTemplate + "' to bean: " + beanDefinition.name, ex);
			}
		}
	}

	// ---------------------------------------------------------------- wire

	/**
	 * Wires beans.
	 */
	public void wireBean() {
		if (definition().wiringMode == WiringMode.NONE) {
			return;
		}
		wireProperties();
		wireSets();
		wireMethods();
	}

	protected void wireProperties() {
		for (PropertyInjectionPoint pip : beanDefinition.properties) {
			final BeanReferences refNames = pip.references;

			final Object value = pc.lookupMixingScopedBean(this.definition(), refNames);

			if (value == null) {
				if ((beanDefinition.wiringMode == WiringMode.STRICT)) {
					throw new PetiteException("Wiring failed. Beans references: '" +
						refNames + "' not found for property: " + beanDefinition.type.getName() +
						'#' + pip.propertyDescriptor.getName());
				}
				continue;
			}

			// BeanUtil.setDeclaredProperty(bean, pip.propertyDescriptor.getName(), value);

			final Setter setter = pip.propertyDescriptor.getSetter(true);
			try {
				setter.invokeSetter(bean, value);
			} catch (Exception ex) {
				throw new PetiteException("Wiring failed", ex);
			}
		}
	}

	protected void wireSets() {
		for (final SetInjectionPoint sip : definition().sets) {

			String[] beanNames = pc.resolveBeanNamesForType(sip.targetClass);

			Collection beans = sip.createSet(beanNames.length);

			for (String beanName : beanNames) {
				if (!beanName.equals(definition().name)) {
					Object value = pc.getBean(beanName);
					beans.add(value);
				}
			}

			//BeanUtil.setDeclaredProperty(bean, sip.field.getName(), beans);

			final Setter setter = sip.propertyDescriptor.getSetter(true);
			try {
				setter.invokeSetter(bean, beans);
			}
			catch (Exception ex) {
				throw new PetiteException("Wiring failed", ex);
			}
		}
	}

	/**
	 * Invokes single method injection point on given bean with given bean definition.
	 */
	protected void wireMethods() {
		for (final MethodInjectionPoint methodRef : definition().methods) {
			invokeMethodInjectionPoint(methodRef);
		}
	}

	public Object invokeMethodInjectionPoint(final MethodInjectionPoint methodRef) {

		final BeanReferences[] refNames = methodRef.references;
		final Object[] args = new Object[refNames.length];

		for (int i = 0; i < refNames.length; i++) {
			final BeanReferences refName = refNames[i];

			final Object value = pc.lookupMixingScopedBean(beanDefinition, refName);

			args[i] = value;
			if (value == null) {
				if ((beanDefinition.wiringMode == WiringMode.STRICT)) {
					throw new PetiteException("Wiring failed. Beans references: '" +
						refName + "' not found for method: " + beanDefinition.type.getName() + '#' + methodRef.method.getName());
				}
			}
		}

		try {
			return methodRef.method.invoke(bean, args);
		} catch (Exception ex) {
			throw new PetiteException(ex);
		}
	}

}
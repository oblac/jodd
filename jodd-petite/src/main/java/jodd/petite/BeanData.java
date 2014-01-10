// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

/**
 * Petite bean is defined by {@link jodd.petite.BeanDefinition bean definition}
 * and bean instance.
 */
public class BeanData {

	protected final BeanDefinition beanDefinition;
	protected final Object bean;

	public BeanData(BeanDefinition beanDefinition, Object bean) {
		this.beanDefinition = beanDefinition;
		this.bean = bean;
	}

	/**
	 * Returns Petite bean definition.
	 */
	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	/**
	 * Returns Petite bean instance.
	 */
	public Object getBean() {
		return bean;
	}
}
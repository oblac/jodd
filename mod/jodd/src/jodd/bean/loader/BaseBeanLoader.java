// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import jodd.bean.BeanUtil;
import jodd.bean.BeanUtilBean;

/**
 * Base {@link BeanLoader}.
 */
public abstract class BaseBeanLoader implements BeanLoader {

	private BeanUtilBean beanUtilBean = BeanUtil.getDefaultBeanUtilBean();

	public BeanUtilBean getBeanUtilBean() {
		return beanUtilBean;
	}

	public void setBeanUtilBean(BeanUtilBean beanUtilBean) {
		this.beanUtilBean = beanUtilBean;
	}

	/**
	 * Sets the target bean property with value.
	 */
	protected void setProperty(Object targetBean, String name, Object value) {
		beanUtilBean.setPropertyForcedSilent(targetBean, name, value);
	}
}

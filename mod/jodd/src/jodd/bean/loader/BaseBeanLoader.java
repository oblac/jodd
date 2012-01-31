// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import jodd.bean.BeanUtil;
import jodd.bean.BeanUtilBean;

/**
 * Base {@link BeanLoader}.
 */
public abstract class BaseBeanLoader implements BeanLoader {

	protected BeanUtilBean beanUtilBean = BeanUtil.getDefaultBeanUtilBean();

	public BeanUtilBean getBeanUtilBean() {
		return beanUtilBean;
	}

	public void setBeanUtilBean(BeanUtilBean beanUtilBean) {
		this.beanUtilBean = beanUtilBean;
	}
}

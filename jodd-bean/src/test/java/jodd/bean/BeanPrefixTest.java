// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.JoddBean;
import jodd.bean.data.LifeBean;
import jodd.introspector.CachingIntrospector;
import org.junit.Assert;
import org.junit.Test;

public class BeanPrefixTest {

	@Test
	public void testFieldPrefix1() {
		LifeBean lifeBean = new LifeBean();

		String foo = BeanUtil.getProperty(lifeBean, "foo").toString();

		Assert.assertEquals("foo", foo);

		JoddBean.introspector = new CachingIntrospector(true, true, true, "_");

		foo = BeanUtil.getProperty(lifeBean, "foo").toString();

		Assert.assertEquals("foo", foo);

		JoddBean.introspector = new CachingIntrospector();
	}

	@Test
	public void testFieldPrefix2() {
		LifeBean lifeBean = new LifeBean();

		String bar = BeanUtil.getProperty(lifeBean, "bar").toString();

		Assert.assertEquals("bar", bar);

		BeanUtil.getBeanUtilBean().setIntrospector(new CachingIntrospector(true, true, true, "_"));

		bar = BeanUtil.getProperty(lifeBean, "bar").toString();

		Assert.assertEquals("_bar", bar);

		BeanUtil.getBeanUtilBean().setIntrospector(JoddBean.introspector);
	}
}
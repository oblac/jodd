// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.bean.data.LifeBean;
import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.Introspector;
import jodd.introspector.JoddIntrospector;
import jodd.introspector.PropertyDescriptor;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BeanPrefixTest {

	@Test
	public void testFieldPrefix1() {
		LifeBean lifeBean = new LifeBean();

		String foo = BeanUtil.getProperty(lifeBean, "foo").toString();

		assertEquals("foo", foo);

		JoddIntrospector.introspector = new CachingIntrospector(true, true, true, new String[] {"_"});

		foo = BeanUtil.getProperty(lifeBean, "foo").toString();

		assertEquals("foo", foo);

		ClassDescriptor cd = JoddIntrospector.introspector.lookup(LifeBean.class);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();
		assertEquals(3, pds.length);

		assertEquals("bar", pds[0].getName());
		assertEquals("_bar", pds[0].getFieldDescriptor().getName());

		assertEquals("www", pds[2].getName());
		assertEquals(null, pds[2].getFieldDescriptor());

		JoddIntrospector.introspector = new CachingIntrospector();
	}

	@Test
	public void testFieldPrefix1withEmpty() {
		LifeBean lifeBean = new LifeBean();

		String foo = BeanUtil.getProperty(lifeBean, "foo").toString();

		assertEquals("foo", foo);

		JoddIntrospector.introspector = new CachingIntrospector(true, true, true, new String[] {"_", ""});

		foo = BeanUtil.getProperty(lifeBean, "foo").toString();

		assertEquals("foo", foo);


		ClassDescriptor cd = JoddIntrospector.introspector.lookup(LifeBean.class);

		PropertyDescriptor[] pds = cd.getAllPropertyDescriptors();
		assertEquals(3, pds.length);

		assertEquals("bar", pds[0].getName());
		assertEquals("_bar", pds[0].getFieldDescriptor().getName());

		assertEquals("www", pds[2].getName());
		assertEquals("www", pds[2].getFieldDescriptor().getName());

		JoddIntrospector.introspector = new CachingIntrospector();
	}

	@Test
	public void testFieldPrefix2() {
		LifeBean lifeBean = new LifeBean();

		String bar = BeanUtil.getProperty(lifeBean, "bar").toString();

		assertEquals("bar", bar);

		BeanUtil.getBeanUtilBean().setIntrospector(new CachingIntrospector(true, true, true, new String[] {"_"}));

		bar = BeanUtil.getProperty(lifeBean, "bar").toString();

		assertEquals("_bar", bar);

		BeanUtil.getBeanUtilBean().setIntrospector(JoddIntrospector.introspector);
	}
}
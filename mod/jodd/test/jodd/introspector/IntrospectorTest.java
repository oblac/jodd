// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import junit.framework.TestCase;
import jodd.introspector.test.Abean;
import jodd.introspector.test.Bbean;
import jodd.introspector.test.Ac;
import jodd.introspector.test.Bc;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class IntrospectorTest extends TestCase {

	public void testBasic() {
		ClassDescriptor cd = ClassIntrospector.lookup(Abean.class);
		assertNotNull(cd);
		Method[] getters = cd.getAllBeanGetters();
		assertEquals(2, getters.length);
		assertNotNull(cd.getBeanGetter("fooProp"));
		assertNotNull(cd.getBeanGetter("something"));
		assertNull(cd.getBeanGetter("FooProp"));
		assertNull(cd.getBeanGetter("Something"));
		assertNull(cd.getBeanGetter("notExisting"));

		Method[] setters = cd.getAllBeanSetters();
		assertEquals(1, setters.length);
	}

	public void testExtends() {
		ClassDescriptor cd = ClassIntrospector.lookup(Bbean.class);
		assertNotNull(cd);

		Method[] getters = cd.getAllBeanGetters();
		assertEquals(2, getters.length);
		getters = cd.getAllBeanGetters(true);
		assertEquals(3, getters.length);
		assertNotNull(cd.getBeanGetter("fooProp"));
		assertNotNull(cd.getBeanGetter("something"));
		assertNull(cd.getBeanGetter("FooProp"));
		assertNull(cd.getBeanGetter("Something"));
		assertNull(cd.getBeanGetter("notExisting"));

		assertNotNull(cd.getBeanGetter("boo", true));
		assertNull(cd.getBeanGetter("boo"));

		Method[] setters = cd.getAllBeanSetters();
		assertEquals(1, setters.length);
		setters = cd.getAllBeanSetters(true);
		assertEquals(2, setters.length);
	}

	public void testCtors() {
		ClassDescriptor cd = ClassIntrospector.lookup(Ac.class);
		Constructor[] ctors = cd.getAllCtors();
		assertEquals(1, ctors.length);
   		ctors = cd.getAllCtors(true);
		assertEquals(2, ctors.length);
		assertNotNull(cd.getDefaultCtor(true));
		assertNull(cd.getDefaultCtor());

		Constructor ctor = cd.getCtor(new Class[]{Integer.class}, true);
		assertNotNull(ctor);

		cd = ClassIntrospector.lookup(Bc.class);
		ctors = cd.getAllCtors();
		assertEquals(1, ctors.length);
   		ctors = cd.getAllCtors(true);
		assertEquals(1, ctors.length);
		assertNull(cd.getDefaultCtor());
		assertNull(cd.getDefaultCtor(true));

		ctor = cd.getCtor(new Class[]{Integer.class}, true);
		assertNull(ctor);
		ctor = cd.getCtor(new Class[]{String.class}, true);
		assertNotNull(ctor);

	}
}

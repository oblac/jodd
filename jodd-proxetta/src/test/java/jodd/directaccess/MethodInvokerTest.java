package jodd.directaccess;

import jodd.mutable.MutableInteger;
import jodd.util.ReflectUtil;
import org.junit.Test;

import static jodd.directaccess.MethodInvokerClassBuilder.createNewInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MethodInvokerTest {

	@Test
	public void testMethod0() throws InstantiationException, IllegalAccessException {
		MethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "method0"));

		assertEquals("jodd0", methodInvoker.invoke(new SomeClass()));
	}

	@Test
	public void testMethod1() throws InstantiationException, IllegalAccessException {
		MethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "method1"));

		assertEquals("Hello", methodInvoker.invoke(new SomeClass(), "Hello"));
	}

	@Test
	public void testMethod2() throws InstantiationException, IllegalAccessException {
		MethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "method2"));

		assertEquals("Hello173", methodInvoker.invoke(new SomeClass(), "Hello", Integer.valueOf(173)));
		assertEquals("llunnull", methodInvoker.invoke(new SomeClass(), "llun", null));
	}

	@Test
	public void testMethodBig() throws InstantiationException, IllegalAccessException {
		MethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "methodBig"));

		Object result = methodInvoker.invoke(new SomeClass(),
				"Hello", 323, Double.valueOf(2.33), 4.55, Integer.valueOf(55), null, new MutableInteger(-12), (byte)4);

		assertEquals("Hello3232.334.5555null-124", result);
	}

	@Test
	public void testMethodNone() throws InstantiationException, IllegalAccessException {
		MethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "methodNone"));

		Object result = methodInvoker.invoke(new SomeClass(), null);

		assertNull(result);
	}

	@Test
	public void testMethodInt() throws InstantiationException, IllegalAccessException {
		MethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "methodInt"));

		Object result = methodInvoker.invoke(new SomeClass(), 234, 642);

		assertEquals(876, result);
	}

}
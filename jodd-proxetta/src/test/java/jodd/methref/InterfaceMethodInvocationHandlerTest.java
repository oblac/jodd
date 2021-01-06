package jodd.methref;

import jodd.util.ClassUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterfaceMethodInvocationHandlerTest {
	interface Foo {
		boolean b();
		int i();
		double d();
		char c();
		float f();
	}

	@Test
	void testPrimitives() {
		final Methref methref = new Methref(this.getClass());
		final InterfaceMethodInvocationHandler imih = new InterfaceMethodInvocationHandler(methref);

		Method m = ClassUtil.findMethod(Foo.class, "b");
		assertEquals(false, imih.invoke(null, m, null));

		m = ClassUtil.findMethod(Foo.class, "i");
		assertEquals(0, imih.invoke(null, m, null));

		m = ClassUtil.findMethod(Foo.class, "d");
		assertEquals(0, imih.invoke(null, m, null));

		m = ClassUtil.findMethod(Foo.class, "c");
		assertEquals(0, imih.invoke(null, m, null));

		m = ClassUtil.findMethod(Foo.class, "f");
		assertEquals(0, imih.invoke(null, m, null));
	}
}

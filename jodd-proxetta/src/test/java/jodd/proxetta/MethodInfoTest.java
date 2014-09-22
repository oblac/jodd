// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.mutable.ValueHolder;
import jodd.proxetta.data.Foo;
import jodd.proxetta.data.FooAnn;
import jodd.proxetta.data.FooProxyAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MethodInfoTest {

	@Test
	public void testMethodInfo() {

		final ValueHolder<MethodInfo> valueHolder = new ValueHolder<MethodInfo>();

		ProxyAspect proxyAspect = new ProxyAspect(
				FooProxyAdvice.class,
				new ProxyPointcut() {
					public boolean apply(MethodInfo methodInfo) {
						if (methodInfo.getMethodName().equals("p1")) {
							valueHolder.setValue(methodInfo);
							return true;
						}
						return false;
					}
		});

		ProxyProxetta proxyProxetta = ProxyProxetta.withAspects(proxyAspect);
		proxyProxetta.setClassNameSuffix("$$$Proxetta888");
		ProxyProxettaBuilder pb = proxyProxetta.builder();
		pb.setTarget(Foo.class);
		Foo foo = (Foo) pb.newInstance();

		assertNotNull(foo);

		MethodInfo mi = valueHolder.getValue();

		assertEquals("p1", mi.getMethodName());
		assertEquals(Foo.class.getName().replace('.', '/'), mi.getClassname());
		assertEquals("(java.lang.String)", mi.getDeclaration());
		assertEquals("(Ljava/lang/String;)Ljava/lang/String;", mi.getDescription());
		assertEquals("java.lang.String", mi.getReturnType());
		assertEquals("Ljava/lang/String;", mi.getReturnTypeName());

		assertEquals("java.lang.String p1(java.lang.String)", mi.getSignature());

		assertEquals(1, mi.getArgumentsCount());
		assertEquals("Ljava/lang/String;", mi.getArgumentTypeName(1));

		assertTrue(mi.isTopLevelMethod());
		assertEquals(1, mi.getHierarchyLevel());

		AnnotationInfo[] anns = mi.getArgumentAnnotations(0);

		assertNotNull(anns);
		assertEquals(1, anns.length);
		assertEquals(FooAnn.class.getName(), anns[0].getAnnotationClassname());
	}

}
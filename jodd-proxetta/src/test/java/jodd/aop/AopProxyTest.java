package jodd.aop;

import jodd.aop.fixture.Helloable;
import jodd.aop.fixture.LoggingAspect;
import jodd.aop.fixture.Simple;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AopProxyTest {

	@Test
	public void testAspect() {
		Helloable proxy = AopProxy.proxyOf(new LoggingAspect(new Simple()));

		proxy.hello(3);

		assertEquals("before jodd.aop.fixture.Simple#hello", LoggingAspect.log);
	}
}

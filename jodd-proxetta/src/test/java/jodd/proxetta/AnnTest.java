package jodd.proxetta;

import jodd.proxetta.data.Hero;
import jodd.proxetta.data.HeroProxyAdvice;
import jodd.proxetta.impl.ProxyProxetta;

import jodd.proxetta.pointcuts.AllRealMethodsPointcut;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnTest {

	@Test
	public void testMethodAnnotations() {
		ProxyProxetta proxyProxetta = ProxyProxetta
				.withAspects(
					new ProxyAspect(HeroProxyAdvice.class,
							new AllRealMethodsPointcut() {
								@Override
								public boolean apply(MethodInfo methodInfo) {
									if (!methodInfo.isTopLevelMethod()) {
										return false;
									}
									return super.apply(methodInfo);
								}
							}))
				//.setDebugFolder("/Users/igor/")
				;

		ProxettaBuilder proxettaBuilder = proxyProxetta.builder();
		proxettaBuilder.setTarget(Hero.class);
		Hero hero = (Hero) proxettaBuilder.newInstance();

		assertEquals("BatmanHero37W88.3CatWoman99speeeeedXRAY", hero.name());
	}

}
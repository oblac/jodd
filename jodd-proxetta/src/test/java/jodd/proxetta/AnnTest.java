package jodd.proxetta;

import jodd.proxetta.data.Hero;
import jodd.proxetta.data.HeroProxyAdvice;
import jodd.proxetta.impl.ProxyProxetta;

import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.impl.WrapperProxettaBuilder;
import jodd.proxetta.pointcuts.AllRealMethodsPointcut;
import jodd.util.ReflectUtil;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

public class AnnTest {

	@Test
	public void testMethodAnnotationsProxy() {
		ProxyProxetta proxetta = ProxyProxetta
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

		ProxettaBuilder proxettaBuilder = proxetta.builder();
		proxettaBuilder.setTarget(Hero.class);
		Hero hero = (Hero) proxettaBuilder.newInstance();

		assertEquals("BatmanHero37W88.3CatWoman99speeeeedXRAY", hero.name());
	}

	@Test
	public void testMethodAnnotationsWrapper() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		WrapperProxetta proxetta = WrapperProxetta
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

		WrapperProxettaBuilder proxettaBuilder = proxetta.builder();
		proxettaBuilder.setTarget(Hero.class);
		Object hero = proxettaBuilder.newInstance();

		assertEquals("BatmanHero37W88.3CatWoman99speeeeedXRAY", ReflectUtil.invoke(hero, "name"));
	}

}
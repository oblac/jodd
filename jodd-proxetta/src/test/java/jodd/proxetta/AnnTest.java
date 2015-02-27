// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.data.Hero;
import jodd.proxetta.data.HeroProxyAdvice;
import jodd.proxetta.data.HeroProxyAdvice2;
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
		proxetta.setVariableClassName(true);
		Hero hero = (Hero) proxettaBuilder.newInstance();

		assertEquals("BatmanHero37W88.3CatWoman99speeeeedXRAYnull", hero.name());
	}

	@Test
	public void testClassAnnotationsProxy() {
		ProxyProxetta proxetta = ProxyProxetta
				.withAspects(
					new ProxyAspect(HeroProxyAdvice2.class,
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
		proxetta.setVariableClassName(true);
		Hero hero = (Hero) proxettaBuilder.newInstance();

		assertEquals("SilverHero89W99.222None1000speeeeedXRAYnull", hero.name());
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
		proxetta.setVariableClassName(true);
		Object hero = proxettaBuilder.newInstance();

		assertEquals("BatmanHero37W88.3CatWoman99speeeeedXRAYnull", ReflectUtil.invoke(hero, "name"));
	}

	@Test
	public void testClassAnnotationsWrapper() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		WrapperProxetta proxetta = WrapperProxetta
				.withAspects(
					new ProxyAspect(HeroProxyAdvice2.class,
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
		proxetta.setVariableClassName(true);
		Object hero = proxettaBuilder.newInstance();

		assertEquals("SilverHero89W99.222None1000speeeeedXRAYnull", ReflectUtil.invoke(hero, "name"));
	}

}
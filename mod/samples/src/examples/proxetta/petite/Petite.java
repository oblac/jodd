// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.petite;

import jodd.petite.PetiteContainer;
import jodd.petite.BeanDefinition;
import jodd.petite.WiringMode;
import jodd.petite.scope.Scope;
import jodd.proxetta.ProxyPointcut;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import examples.petite.Foo;
import examples.petite.FooImpl;
import examples.petite.Boo;
import examples.proxetta.LogProxyAdvice;
import jodd.proxetta.impl.ProxyProxetta;

public class Petite {

	public static void main(String[] args) {
		one();
	}

	static ProxyAspect pd4log = new ProxyAspect(LogProxyAdvice.class, new ProxyPointcut() {
		public boolean apply(MethodInfo msign) {
			return true;
		}
	});


	public static void one() {

		PetiteContainer petite = new PetiteContainer() {
			@Override
			protected BeanDefinition registerPetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
				type = ProxyProxetta.withAspects(pd4log).builder(type).define();
				return super.registerPetiteBean(name, type, scopeType, wiringMode);
			}
		};
		petite.getConfig().setDefaultWiringMode(WiringMode.OPTIONAL);


		petite.registerBean("foo", FooImpl.class);
		petite.registerBean(Boo.class);

		Foo foo = (Foo) petite.getBean("foo");
		foo.foo();
	}

}

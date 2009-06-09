// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.petite;

import jodd.petite.WiringMode;
import jodd.petite.PetiteContainer;
import jodd.petite.PetiteException;
import jodd.petite.BeanDefinition;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.scope.Scope;
import jodd.util.ClassLoaderUtil;
import jodd.util.ThreadUtil;

public class PetiteExample {
	public static void main(String[] args) {
		System.out.println("\n\n---1---------------------------------------------------------------");
		//one();
		System.out.println("\n\n---2---------------------------------------------------------------");
		//two();
		System.out.println("\n\n---3---------------------------------------------------------------");
		try {
			//three();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("\n\n---4---------------------------------------------------------------");
		try {
			//four();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("\n\n---5---------------------------------------------------------------");
		five();
	}

	public static void one() {

		// manual registration
		PetiteContainer petite = new PetiteContainer();
		petite.registerBean(FooImpl.class);
		petite.registerBean(Goo.class);
		petite.registerBean(Boo.class);
		petite.registerBean(Zoo.class);

		Foo foo = (Foo) petite.getBean("foo");
		System.out.println("> foo");
		foo.foo();

		foo = (Foo) petite.getBean("foo");
		System.out.println("> foo");
		foo.foo();

		Zoo zoo = (Zoo) petite.getBean("zoo");
		System.out.println("> zoo");
		zoo.zoo();

		zoo = (Zoo) petite.getBean("zoo");
		System.out.println("> zoo (proto scope, new instance)");
		zoo.zoo();
	}


	public static void two() {

		// automagic configuration
		PetiteContainer petite = new PetiteContainer();
		AutomagicPetiteConfigurator petiteCfg = new AutomagicPetiteConfigurator();
		petiteCfg.setIncludedEntries(new String[]{"examples.petite.*"});
		petiteCfg.setExcludedEntries(new String[]{"examples.petite.news.*"});
		petiteCfg.configure(petite);

		System.out.println("> Get foo");
		Foo foo = (Foo) petite.getBean("foo");
		foo.foo();

		System.out.println("> Get zoo");
		Zoo zoo = (Zoo) petite.getBean("zoo");
		zoo.zoo();

		System.out.println("> Remove foo, but reference still exists in zoo");
		petite.removeBean("foo");
		zoo.zoo();
		System.out.println("> Get zoo again, will throw an exception since no foo anymore");
		try {
			zoo = (Zoo) petite.getBean("zoo");
			zoo.zoo();
		} catch (PetiteException pex) {
			System.out.println(pex);
		}

		System.out.println("> Register foo again");
		petite.registerBean(FooImpl.class);
		System.out.println("> Get zoo, foo is new");
		zoo = (Zoo) petite.getBean("zoo");
		zoo.zoo();

	}


	static void five() {

		// create petite container
		final PetiteContainer petite = new PetiteContainer() {

			@Override
			protected BeanDefinition registerPetiteBean(String name, Class type, Class<? extends Scope> scopeType, WiringMode wiringMode) {
				System.out.println(":::: " + type + "            " + type.getClassLoader());
				return super.registerPetiteBean(name, type, scopeType, wiringMode);
			}
		};
		petite.registerBean(FooImpl.class);
		petite.registerBean(Goo.class);
		petite.registerBean(Boo.class);
		petite.registerBean(Zoo.class);

		// get
		System.out.println("> Get ZOO");
		Zoo zoo = (Zoo) petite.getBean("zoo");
		System.out.println(zoo.getClass().getClassLoader());
		zoo.zoo();

		System.out.println("\n\n============ CHANGE FOOIMPL or ZOO and COMPILE IN 10 SEC FROM NOW!\n\n");
		ThreadUtil.sleep(1000);

		Class a = ClassLoaderUtil.findClass("examples.petite.Zoo", ClassLoaderUtil.getFullClassPath(PetiteExample.class));
		System.out.println(a);
		System.out.println(a.getClassLoader());

		System.out.println("\n\n");
		Object result = petite.getBean("zoo");
		System.out.println("GOT> : " + result.getClass());
		System.out.println("GOT> : " + result.getClass().getClassLoader());
		zoo = (Zoo) result;
		zoo.zoo();
	}

}

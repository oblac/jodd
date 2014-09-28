// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import static jodd.proxetta.ProxyTarget.targetClassAnnotation;

public class HeroProxyAdvice2 implements ProxyAdvice {

	public Object execute() throws Exception {
		String name = "";

		String heroName = (String) targetClassAnnotation(HeroName.class.getName(), "value");

		name += heroName;

		Class heroClass = (Class) targetClassAnnotation(HeroName.class.getName(), "power");

		name += heroClass.getSimpleName();

		Integer secret = (Integer) targetClassAnnotation(HeroName.class.getName(), "secret");

		name += secret;

		Character middle = (Character) targetClassAnnotation(HeroName.class.getName(), "middle");

		name += middle;

		Double opacity = (Double) targetClassAnnotation(HeroName.class.getName(), "opacity");

		name += opacity;

		String[] helpers = (String[]) targetClassAnnotation(HeroName.class.getName(), "helpers");

		name += helpers[0];

		int[] enemies = (int[]) targetClassAnnotation(HeroName.class.getName(), "enemies");

		name += enemies[1];

		Hero.POWER power = (Hero.POWER) targetClassAnnotation(HeroName.class.getName(), "power2");

		name += power;

		Hero.POWER[] subpowers = (Hero.POWER[]) targetClassAnnotation(HeroName.class.getName(), "subpowers");

		name += subpowers[0];

		String missing = (String)  targetClassAnnotation(HeroName.class.getName(), "xxxxx");

		name += missing;

		return ProxyTarget.returnValue(name);
	}
}
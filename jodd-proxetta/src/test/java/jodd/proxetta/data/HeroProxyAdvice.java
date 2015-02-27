// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import static jodd.proxetta.ProxyTarget.targetMethodAnnotation;

public class HeroProxyAdvice implements ProxyAdvice {

	public Object execute() throws Exception {
		String name = "";

		String heroName = (String) targetMethodAnnotation(HeroName.class.getName(), "value");

		name += heroName;

		Class heroClass = (Class) targetMethodAnnotation(HeroName.class.getName(), "power");

		name += heroClass.getSimpleName();

		Integer secret = (Integer) targetMethodAnnotation(HeroName.class.getName(), "secret");

		name += secret;

		Character middle = (Character) targetMethodAnnotation(HeroName.class.getName(), "middle");

		name += middle;

		Double opacity = (Double) targetMethodAnnotation(HeroName.class.getName(), "opacity");

		name += opacity;

		String[] helpers = (String[]) targetMethodAnnotation(HeroName.class.getName(), "helpers");

		name += helpers[0];

		int[] enemies = (int[]) targetMethodAnnotation(HeroName.class.getName(), "enemies");

		name += enemies[1];

		Hero.POWER power = (Hero.POWER) targetMethodAnnotation(HeroName.class.getName(), "power2");

		name += power;

		Hero.POWER[] subpowers = (Hero.POWER[]) targetMethodAnnotation(HeroName.class.getName(), "subpowers");

		name += subpowers[0];

		String missing = (String)  targetMethodAnnotation(HeroName.class.getName(), "xxxxx");

		name += missing;

		return ProxyTarget.returnValue(name);
	}
}
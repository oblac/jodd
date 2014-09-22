package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import static jodd.proxetta.ProxyTarget.targetMethodAnnotation;

public class HeroProxyAdvice implements ProxyAdvice {

	public Object execute() throws Exception {
		String name = "";

		String heroName = (String) targetMethodAnnotation(HeroName.class, "value");

		name += heroName;

		Class heroClass = (Class) targetMethodAnnotation(HeroName.class, "power");

		name += heroClass.getSimpleName();

		Integer secret = (Integer) targetMethodAnnotation(HeroName.class, "secret");

		name += secret;

		Character middle = (Character) targetMethodAnnotation(HeroName.class, "middle");

		name += middle;

		Double opacity = (Double) targetMethodAnnotation(HeroName.class, "opacity");

		name += opacity;

		String[] helpers = (String[]) targetMethodAnnotation(HeroName.class, "helpers");

		name += helpers[0];

		int[] enemies = (int[]) targetMethodAnnotation(HeroName.class, "enemies");

		name += enemies[1];

		Hero.POWER power = (Hero.POWER) targetMethodAnnotation(HeroName.class, "power2");

		name += power;

		Hero.POWER[] subpowers = (Hero.POWER[]) targetMethodAnnotation(HeroName.class, "subpowers");

		name += subpowers[0];

		String missing = (String)  targetMethodAnnotation(HeroName.class, "xxxxx");

		name += missing;

		return ProxyTarget.returnValue(name);
	}
}
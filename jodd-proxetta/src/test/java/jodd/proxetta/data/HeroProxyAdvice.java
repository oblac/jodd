package jodd.proxetta.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

public class HeroProxyAdvice implements ProxyAdvice {

	public Object execute() throws Exception {
		String name = "";

		String heroName = (String) ProxyTarget.targetMethodAnnotation(HeroName.class, "value");

		name += heroName;

		Class heroClass = (Class) ProxyTarget.targetMethodAnnotation(HeroName.class, "power");

		name += heroClass.getSimpleName();

		Integer secret = (Integer) ProxyTarget.targetMethodAnnotation(HeroName.class, "secret");

		name += secret;

		Character middle = (Character) ProxyTarget.targetMethodAnnotation(HeroName.class, "middle");

		name += middle;

		Double opacity = (Double) ProxyTarget.targetMethodAnnotation(HeroName.class, "opacity");

		name += opacity;

		String[] helpers = (String[]) ProxyTarget.targetMethodAnnotation(HeroName.class, "helpers");

		name += helpers[0];

		int[] enemies = (int[]) ProxyTarget.targetMethodAnnotation(HeroName.class, "enemies");
		//int[] enemies = new int[] {1,2,3};

		name += enemies[1];

		return ProxyTarget.returnValue(name);
	}
}
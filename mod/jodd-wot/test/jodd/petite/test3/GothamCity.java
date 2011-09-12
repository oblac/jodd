// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test3;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import java.util.Set;

@PetiteBean
public class GothamCity {

	@PetiteInject
	public Set<SuperHero> superHeros;

	public String whoIsThere() {
		String superHeroes = "";
		for (SuperHero superHero : superHeros) {
			superHeroes += superHero.getHeroName() + " ";
		}

		return superHeroes.trim();
	}
}

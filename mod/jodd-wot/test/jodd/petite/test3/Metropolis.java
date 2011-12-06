// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test3;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import java.util.Collection;

@PetiteBean
public class Metropolis implements SuperHero {

	@PetiteInject
	public Collection<SuperHero> superHeros;

	public String whoIsThere() {
		String superHeroes = "";
		for (SuperHero superHero : superHeros) {
			superHeroes += superHero.getHeroName() + " ";
		}

		return superHeroes.trim();
	}

	public String getHeroName() {
		return "delegate";
	}
}

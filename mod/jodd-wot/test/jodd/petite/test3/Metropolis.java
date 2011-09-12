// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test3;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import java.util.Collection;
import java.util.Set;

@PetiteBean
public class Metropolis {

	@PetiteInject
	public Collection<SuperHero> superHeros;

	public String whoIsThere() {
		String superHeroes = "";
		for (SuperHero superHero : superHeros) {
			superHeroes += superHero.getHeroName() + " ";
		}

		return superHeroes.trim();
	}

}

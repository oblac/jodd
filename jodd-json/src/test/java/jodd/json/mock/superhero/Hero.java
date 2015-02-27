// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock.superhero;

import java.util.Arrays;
import java.util.List;

public class Hero {

	private SecretLair lair;
	private SecretIdentity identity;
	private String name;
	private List<SuperPower> powers;

	protected Hero() {
	}

	public Hero(String name, SecretIdentity identity, SecretLair lair, SuperPower... powers) {
		this.name = name;
		this.identity = identity;
		this.lair = lair;
		this.powers = Arrays.asList(powers);
	}

	public SecretLair getLair() {
		return lair;
	}

	public SecretIdentity getIdentity() {
		return identity;
	}

	public String getName() {
		return name;
	}

	private void setLair(SecretLair lair) {
		this.lair = lair;
	}

	private void setIdentity(SecretIdentity identity) {
		this.identity = identity;
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setPowers(List<SuperPower> powers) {
		this.powers = powers;
	}

	public List<SuperPower> getPowers() {
		return powers;
	}
}

// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock.superhero;

import java.util.Arrays;
import java.util.List;

public class Villian {

	private String name;
	private Hero nemesis;
	private SecretLair lair;
	private List<SuperPower> powers;

	protected Villian() {
	}

	public Villian(String name, Hero nemesis, SecretLair lair, SuperPower... powers) {
		this.name = name;
		this.nemesis = nemesis;
		this.lair = lair;
		this.powers = Arrays.asList(powers);
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public Hero getNemesis() {
		return nemesis;
	}

	protected void setNemesis(Hero nemesis) {
		this.nemesis = nemesis;
	}

	public SecretLair getLair() {
		return lair;
	}

	protected void setLair(SecretLair lair) {
		this.lair = lair;
	}

	public List<SuperPower> getPowers() {
		return powers;
	}

	protected void setPowers(List<SuperPower> powers) {
		this.powers = powers;
	}
}

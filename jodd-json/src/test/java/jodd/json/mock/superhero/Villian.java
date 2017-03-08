// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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

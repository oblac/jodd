// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock.superhero;

public class HeatVision implements SuperPower {
	private float power;

	protected HeatVision() {
	}

	public HeatVision(float power) {
		this.power = power;
	}

	public float getPower() {
		return power;
	}
}

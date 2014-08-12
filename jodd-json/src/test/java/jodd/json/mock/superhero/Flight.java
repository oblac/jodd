// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock.superhero;

public class Flight implements SuperPower {

	private float velocity;

	protected Flight() {
	}

	public Flight(float velocity) {
		this.velocity = velocity;
	}

	public float getVelocity() {
		return velocity;
	}

}

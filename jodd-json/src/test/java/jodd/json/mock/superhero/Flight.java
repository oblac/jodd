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

package jodd.proxetta.data;

public class Hero {

	public static enum POWER {
		STRENGTH,
		XRAY,
		SPEED {
			@Override
			public String toString() {
				return "speeeeed";
			}
		}
	}

	@HeroName(
			value = "Batman",
			secret = 37,
			opacity = 88.3,
			helpers = {"CatWoman", "Robin"},
			enemies = {1,99})
	public String name() {
		return null;
	}

}
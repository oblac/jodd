package jodd.proxetta.data;

public class Hero {

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
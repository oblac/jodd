package jodd.proxetta.data;

public @interface HeroName {

	String value();

	Class power() default Hero.class;

	int secret();

	char middle() default 'W';

	double opacity();

	String[] helpers();

	int[] enemies();
}
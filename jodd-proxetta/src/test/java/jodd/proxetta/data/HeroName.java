// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public @interface HeroName {

	String value();

	Class power() default Hero.class;

	int secret();

	char middle() default 'W';

	double opacity();

	String[] helpers();

	int[] enemies();

	Hero.POWER power2() default Hero.POWER.SPEED;

	Hero.POWER[] subpowers() default {Hero.POWER.XRAY};
}
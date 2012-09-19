// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.data;

import jodd.vtor.Vtor;
import jodd.vtor.constraint.MinLength;

public class Too {

	@MinLength(value = 5, profiles = {"-p1"})
	String aaa = "a";

	@MinLength(value = 5, profiles = {"p1", "-p2"})
	String bbb = "b";

	@MinLength(value = 5, profiles = {"-p1", "-p2"})
	String ccc = "c";

	@MinLength(value = 5, profiles = {"-p1", "p2"})
	String ddd = "d";

	@MinLength(value = 5, profiles = {"p1", "p2"})
	String eee = "e";

	@MinLength(value = 5, profiles = {"+p2", "+p3"})
	String fff = "f";

	@MinLength(value = 5, profiles = {Vtor.DEFAULT_PROFILE, "-register"})
	String ggg = "g";
}

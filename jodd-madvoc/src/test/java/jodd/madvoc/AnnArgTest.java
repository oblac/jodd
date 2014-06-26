// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.ActionMethodParser;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AnnArgTest extends MadvocTestCase {

	@Test
	public void testDefaultMethods() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "tst.SuperAction#add");

		assertNotNull(cfg);
	}

}
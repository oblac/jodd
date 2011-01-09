// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import jodd.vtor.data.Too;
import jodd.vtor.data.Zoo;
import junit.framework.TestCase;

import java.util.List;

public class ProfileTest extends TestCase {

	public void testProfiles() {
		Vtor vtor = new Vtor();
		vtor.setValidateAllProfilesByDefault(true);
		Zoo zoo = new Zoo();

		vtor.validate(zoo);
		List<Violation> vlist = vtor.getViolations();
		assertEquals(3, vlist.size());

		vtor.resetViolations();
		vtor.useProfile("default");
		vtor.validate(zoo);
		vlist = vtor.getViolations();
		assertEquals(1, vlist.size());

		vtor.resetViolations();
		vtor.useProfile("p2");
		vtor.validate(zoo);
		vlist = vtor.getViolations();
		assertEquals(2, vlist.size());

		vtor.resetViolations();
		vtor.useProfile("p1");
		vtor.validate(zoo);
		vlist = vtor.getViolations();
		assertEquals(3, vlist.size());

		vtor.resetViolations();
		vtor.resetProfiles();
		vtor.useProfile("p1");
		vtor.validate(zoo);
		vlist = vtor.getViolations();
		assertEquals(2, vlist.size());

		vtor.resetViolations();
		vtor.resetProfiles();
		vtor.useProfile("p2");
		vtor.validate(zoo);
		vlist = vtor.getViolations();
		assertEquals(1, vlist.size());
	}


	public void testDefaultProfiles() {
		Vtor vtor = new Vtor();
		vtor.setValidateAllProfilesByDefault(false);
		Zoo zoo = new Zoo();

		vtor.validate(zoo);
		List<Violation> vlist = vtor.getViolations();
		assertEquals(1, vlist.size());

		vtor.resetViolations();
		vtor.useProfile("default");
		vtor.validate(zoo);
		vlist = vtor.getViolations();
		assertEquals(1, vlist.size());
		assertEquals("ccc", vlist.get(0).getName());

		vtor.resetViolations();
		vtor.resetProfiles();
		vtor.useProfile("p2");
		vtor.validate(zoo);
		vlist = vtor.getViolations();
		assertEquals(1, vlist.size());
		assertEquals("bbb", vlist.get(0).getName());
	}


	public void testMinusProfiles() {
		Vtor vtor = new Vtor();
		vtor.setValidateAllProfilesByDefault(false);
		vtor.useProfile("default");
		Too too = new Too();

		vtor.validate(too);
		List<Violation> vlist = vtor.getViolations();
		assertNull(vlist);

		vtor.resetViolations();
		vtor.resetProfiles();
		vtor.useProfile("p1");
		vtor.validate(too);
		vlist = vtor.getViolations();
		assertEquals(2, vlist.size());

		vtor.resetViolations();
		vtor.resetProfiles();
		vtor.useProfile("p2");
		vtor.validate(too);
		vlist = vtor.getViolations();
		assertEquals(2, vlist.size());

		vtor.resetViolations();
		vtor.resetProfiles();
		vtor.useProfiles("p1", "p2");
		vtor.validate(too);
		vlist = vtor.getViolations();
		assertEquals(1, vlist.size());


	}

}

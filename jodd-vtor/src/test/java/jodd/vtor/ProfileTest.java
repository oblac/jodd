// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.vtor;

import jodd.vtor.constraint.MinLengthConstraint;
import jodd.vtor.fixtures.Too;
import jodd.vtor.fixtures.Zoo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProfileTest {

	@Test
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


	@Test
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


	@Test
	public void testMinusPlusProfiles() {
		Vtor vtor = new Vtor();
		vtor.setValidateAllProfilesByDefault(false);
		vtor.useProfile("default");
		Too too = new Too();

		vtor.validate(too);
		List<Violation> vlist = vtor.getViolations();
		assertEquals(1, vlist.size());
		Violation v = vlist.get(0);
		assertEquals(MinLengthConstraint.class, v.getConstraint().getClass());

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
		vtor.useProfile("p3");
		vtor.validate(too);
		vlist = vtor.getViolations();
		assertNull(vlist);

		vtor.resetViolations();
		vtor.resetProfiles();
		vtor.useProfiles("p1", "p2");
		vtor.validate(too);
		vlist = vtor.getViolations();
		assertEquals(1, vlist.size());

		vtor.resetViolations();
		vtor.resetProfiles();
		vtor.useProfiles("p1", "p2", "p3");
		vtor.validate(too);
		vlist = vtor.getViolations();
		assertEquals(2, vlist.size());
	}

	@Test
	public void testAsJoyAction() {
		Vtor vtor = new Vtor();
		vtor.useProfiles(Vtor.DEFAULT_PROFILE, "register");
		vtor.validate(new Too());
		List<Violation> violations = vtor.getViolations();

		assertNull(violations);
	}

}

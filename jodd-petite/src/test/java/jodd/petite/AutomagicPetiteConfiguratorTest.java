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

package jodd.petite;

import jodd.petite.fixtures.tst.Foo;
import jodd.test.DisabledOnJava;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AutomagicPetiteConfiguratorTest {

	@BeforeEach
	void setUp() {
		Foo.instanceCounter = 0;
	}

	@Test
	@DisabledOnJava(value = 9, description = "Automagic configuration only works with MR-JAR jars as they don't work in exploded mode.")
	void testContainer() {
		PetiteContainer pc = new PetiteContainer();
		AutomagicPetiteConfigurator petiteConfigurator = new AutomagicPetiteConfigurator(pc);

		petiteConfigurator.withScanner(classScanner ->
			classScanner
				.excludeAllEntries(true)
				.includeEntries("jodd.petite.fixtures.*")
				.excludeEntries("jodd.petite.fixtures.data.*", "jodd.petite.fixtures.tst3.*", "jodd.petite.fixtures.tst.Ses")
				.excludeEntries(
					"jodd.petite.fixtures.data.*", "jodd.petite.fixtures.tst6.*", "jodd.petite.fixtures.tst.Ses",
					"*Public*", "*Secret*", "*$*", "jodd.petite.proxy.*", "jodd.petite.fixtures.rainbow.*"));

		petiteConfigurator.configure();

		assertEquals(1, pc.beansCount());
		assertEquals(1, pc.scopesCount());
		assertEquals(0, Foo.instanceCounter);

		Foo foo = pc.getBean("foo");
		assertNotNull(foo);
	}

}

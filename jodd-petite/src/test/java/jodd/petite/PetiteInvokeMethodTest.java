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

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetiteInvokeMethodTest {

	@PetiteBean
	public static class Moon {
		public String name() {
			return "moon";
		}
	}

	public static class Hello {
		public String world1(@PetiteInject Moon moon) {
			return "world" + moon.name();
		}
		public String world2() {
			return "world";
		}
	}

	@Test
	public void invokeMethod() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Moon.class);

		Hello hello = new Hello();
		Method world1 = ClassUtil.findMethod(Hello.class, "world1");
		Method world2 = ClassUtil.findMethod(Hello.class, "world2");

		String string = pc.invokeMethod(hello, world1);
		assertEquals("worldmoon", string);

		string = pc.invokeMethod(hello, world2);
		assertEquals("world", string);

	}
}

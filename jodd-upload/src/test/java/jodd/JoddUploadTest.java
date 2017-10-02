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

package jodd;

import jodd.Jodd.JoddModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoddUploadTest {

	@Test
	public void testLoadedModules() {
		assertTrue (Jodd.isModuleLoaded(JoddModule.BEAN));
		assertFalse(Jodd.isModuleLoaded(JoddModule.HTTP));
		assertFalse(Jodd.isModuleLoaded(JoddModule.MADVOC));
		assertFalse(Jodd.isModuleLoaded(JoddModule.MAIL));
		assertFalse(Jodd.isModuleLoaded(JoddModule.PETITE));
		assertFalse(Jodd.isModuleLoaded(JoddModule.PROPS));
		assertFalse(Jodd.isModuleLoaded(JoddModule.PROXETTA));
		assertFalse(Jodd.isModuleLoaded(JoddModule.SERVLET));
		assertTrue (Jodd.isModuleLoaded(JoddModule.UPLOAD));
		assertFalse(Jodd.isModuleLoaded(JoddModule.VTOR));
	}
}

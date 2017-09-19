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

package jodd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashCodeTest {

	@Test
	public void testhashCode() {
		int hash = HashCode.hash(0, "Hey");
		hash = HashCode.hash(hash, 1);
		hash = HashCode.hash(hash, 1.4);
		hash = HashCode.hash(hash, 9f);
		hash = HashCode.hash(hash, true);
		hash = HashCode.hash(hash, ArraysUtil.ints(1,2,3,4));
		hash = HashCode.hash(hash, new NameValue<>("A", "B"));

		int hash2 = hash;

		hash = HashCode.hash(0, "Hey");
		hash = HashCode.hash(hash, 1);
		hash = HashCode.hash(hash, 1.4);
		hash = HashCode.hash(hash, 9f);
		hash = HashCode.hash(hash, true);
		hash = HashCode.hash(hash, ArraysUtil.ints(1,2,3,4));
		hash = HashCode.hash(hash, new NameValue<>("A", "B"));

		assertEquals(hash, hash2);
	}
}

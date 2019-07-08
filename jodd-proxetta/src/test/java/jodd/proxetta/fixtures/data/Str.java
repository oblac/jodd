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

package jodd.proxetta.fixtures.data;

import java.util.List;

public class Str {

	public String foo() {
		System.out.println("Str.foo");
		return "123";
	}
	public String foo2(List in1, Thread in2) {
		System.out.println("Str.foo2");
		return "123";
	}

	public Integer boo() {
		System.out.println("Str.boo");
		return Integer.valueOf(123);
	}

	public int izoo() {
		System.out.println("Str.izoo");
		return 345;
	}
	public float fzoo() {
		System.out.println("Str.fzoo");
		return 345;
	}
	public double dzoo() {
		System.out.println("Str.dzoo");
		return 345;
	}
	public long lzoo() {
		System.out.println("Str.lzoo");
		return 345;
	}
	public boolean bzoo() {
		System.out.println("Str.bzoo");
		return true;
	}
	public short szoo() {
		System.out.println("Str.bzoo");
		return 1;
	}
	public char czoo() {
		System.out.println("Str.czoo");
		return 'a';
	}
	public byte yzoo() {
		System.out.println("Str.yzoo");
		return 1;
	}

	public void voo() {
		System.out.println("Str.voo");
	}
}

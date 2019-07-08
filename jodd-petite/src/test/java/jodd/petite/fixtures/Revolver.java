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

package jodd.petite.fixtures;

import jodd.petite.meta.PetiteInject;

public class Revolver {

	@PetiteInject
	private String onlyAnnotation;

	private String nothing;

	@PetiteInject("inn")
	private String valued;

	// ----------------------------------------------------------------

	public void noAnnotation(String ex) {}

	public static class NoAnnotation {
		public NoAnnotation(String ex) {}
	}

	// ----------------------------------------------------------------

	@PetiteInject
	public void onlyAnnotation() {}

	public static class OnlyAnnotation {
		@PetiteInject
		public OnlyAnnotation() {}
	}

	// ----------------------------------------------------------------

	@PetiteInject
	public void someArguments(String in1, Integer in2) {}

	public static class SomeArguments {
		@PetiteInject
		public SomeArguments(String in1, Integer in2) {}
	}

	// ----------------------------------------------------------------

	@PetiteInject("innn1, innn2")
	public void someArguments_csv(String in1, Integer in2) {}

	public static class SomeArguments_csv {
		@PetiteInject("innn1, innn2")
		public SomeArguments_csv(String in1, Integer in2) {}
	}

	// ----------------------------------------------------------------

	@PetiteInject("innn1, innn2, inn3")
	public void someArguments_wrongAnnotation(String in1, Integer in2) {}

	public static class SomeArguments_wrongAnnotation {
		@PetiteInject("innn1, innn2, inn3")
		public SomeArguments_wrongAnnotation(String in1, Integer in2) {}
	}

	// ----------------------------------------------------------------

	public void noMethodArgument(
		@PetiteInject String in1,
		@PetiteInject Integer in2
	) {}

	public static class NoMethodArgument {
		public NoMethodArgument(
			@PetiteInject String in1,
			@PetiteInject Integer in2
		) {}
	}

	// ----------------------------------------------------------------

	public void noMethodArgument_partial(
		@PetiteInject("innn1") String in1,
		@PetiteInject Integer in2
	) {}

	public static class NoMethodArgument_partial {
		public NoMethodArgument_partial(
			@PetiteInject("innn1") String in1,
			@PetiteInject Integer in2
		) {}
	}

	// ----------------------------------------------------------------

	@PetiteInject("aaaa, bbbb")
	public void mix(
		@PetiteInject("innn1") String in1,
		Integer in2
	) {}

	public static class Mix {
		@PetiteInject("aaaa, bbbb")
		public Mix(
			@PetiteInject("innn1") String in1,
			Integer in2
		) {}
	}
}
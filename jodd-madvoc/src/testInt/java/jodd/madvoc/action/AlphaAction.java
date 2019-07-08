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

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.madvoc.result.Chain;
import jodd.madvoc.result.Forward;
import jodd.madvoc.result.Redirect;
import jodd.madvoc.result.TextResult;

@MadvocAction
public class AlphaAction {

	// ---------------------------------------------------------------- forward

	@Action
	public Forward view() {
		return Forward.to("ok");
	}

	@Action(alias = "hello")
	public Forward hello() {
		return Forward.to("");
	}

	@Action
	public Forward ciao() {
		return Forward.to("<hello>");
	}

	@Action
	public Forward ciao2() {
		return Forward.to(AlphaAction.class, AlphaAction::hello);
	}

	@Action
	public Forward ciao3() {
		return Forward.to(this, AlphaAction::hello);
	}

	@Action
	public Forward hola() {
		return Forward.to("hello");
	}

	@Action
	public Forward holahoopa() {
		return Forward.to(this, AlphaAction::hola);
	}

	@Action
	public Forward home() {
		return Forward.to(HelloAction.class, HelloAction::world);
	}

	@Action
	public Forward home2() {
		Forward tempResult = Forward.to(HelloAction.class, HelloAction::world);
		return Forward.of(tempResult, "ok");
	}

	// ---------------------------------------------------------------- redirect

	@Action
	public Redirect red1() {
		return Redirect.to("/alpha.html");
	}

	@Action
	public Redirect red2() {
		return Redirect.to(HelloAction.class, HelloAction::view);
	}

	@Action
	public Redirect world() {
		Redirect temp = Redirect.to(HelloAction.class, HelloAction::world);
		return Redirect.of(temp, "?name=Mars&data=173");
	}

	@Action
	public Redirect postme() {
		return Redirect.to(this, AlphaAction::hello);
	}

	// ---------------------------------------------------------------- text

	@Action
	public TextResult txt() {
		return TextResult.of("some text");
	}

	// ---------------------------------------------------------------- no result!

	@Action
	public void noresult() {
	}

	// ---------------------------------------------------------------- chain

	@In
	@Out
	int chain;

	@Action
	public Chain chain() {
		chain++;
		return Chain.to(this, AlphaAction::link);
	}

	@Action
	public void link() {
		chain++;
	}

}
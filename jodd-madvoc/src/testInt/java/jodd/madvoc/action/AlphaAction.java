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
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.result.Result;
import jodd.madvoc.result.ServletRedirectResult;

@MadvocAction
public class AlphaAction {

	final Result result = new Result();

	// ---------------------------------------------------------------- forward

	@Action
	public void view() {
		result.forwardTo("ok");
	}

	@Action(alias = "hello")
	public void hello() {
		result.forward();
	}

	@Action
	public void ciao() {
		result.forwardTo("<hello>");
	}

	@Action
	public void ciao2() {
		result.forwardTo(AlphaAction.class).hello();
	}

	@Action
	public void ciao3() {
		result.forwardTo(this).hello();
	}

	@Action
	public void hola() {
		result.forwardTo("hello");
	}

	@Action
	public void holahoopa() {
		result.forwardTo(this).hola();
	}

	@Action
	public void home() {
		result.forwardTo(HelloAction.class).world();
	}

	@Action
	public void home2() {
		result.forwardTo(HelloAction.class).world();
		result.forwardTo(result, "ok");
	}

	// ---------------------------------------------------------------- redirect

	@Action
	public void red1() {
		result.use(ServletRedirectResult.class).value("/alpha.html");
	}

	@Action
	public void red2() {
		result.redirectTo(HelloAction.class).view();
	}

	@Action
	public void world() {
		result.redirectTo(HelloAction.class).world();
		result.redirectTo(result, "?name=Mars&data=173");
	}

	@Action
	public void postme() {
		result.redirectTo(this).hello();
	}

	// ---------------------------------------------------------------- text

	@Action
	public void txt() {
		result.text("some text");
	}

	// ---------------------------------------------------------------- no result!

	@Action
	public void noresult() {
	}

	// ---------------------------------------------------------------- chain

	@InOut
	int chain;

	@Action
	public void chain() {
		chain++;
		result.chainTo(this).link();
	}

	@Action
	public void link() {
		chain++;
	}

}
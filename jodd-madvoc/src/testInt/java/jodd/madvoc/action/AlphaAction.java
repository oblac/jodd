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

	// ---------------------------------------------------------------- forward

	@Action
	public Result view() {
		return Result.forward().to("ok");
	}

	@Action(alias = "hello")
	public Result hello() {
		return Result.forward();
	}

	@Action
	public Result ciao() {
		return Result.forward().to("<hello>");
	}

	@Action
	public Result ciao2() {
		return Result.forward().to(AlphaAction.class, AlphaAction::hello);
	}

	@Action
	public Result ciao3() {
		return Result.forward().to(this, AlphaAction::hello);
	}

	@Action
	public Result hola() {
		return Result.forward().to("hello");
	}

	@Action
	public Result holahoopa() {
		return Result.forward().to(this, AlphaAction::hola);
	}

	@Action
	public Result home() {
		return Result.forward().to(HelloAction.class, HelloAction::world);
	}

	@Action
	public Result home2() {
		Result tempResult = Result.forward().to(HelloAction.class, HelloAction::world);
		return Result.forward().to(tempResult, "ok");
	}

	// ---------------------------------------------------------------- redirect

	@Action
	public Result red1() {
		return Result.of(ServletRedirectResult.class).with("/alpha.html");
	}

	@Action
	public Result red2() {
		return Result.redirect().to(HelloAction.class, HelloAction::view);
	}

	@Action
	public Result world() {
		Result temp = Result.redirect().to(HelloAction.class, HelloAction::world);
		return Result.redirect().to(temp, "?name=Mars&data=173");
	}

	@Action
	public Result postme() {
		return Result.redirect().to(this, AlphaAction::hello);
	}

	// ---------------------------------------------------------------- text

	@Action
	public Result txt() {
		return Result.text().with("some text");
	}

	// ---------------------------------------------------------------- no result!

	@Action
	public void noresult() {
	}

	// ---------------------------------------------------------------- chain

	@InOut
	int chain;

	@Action
	public Result chain() {
		chain++;
		return Result.chain().to(this, AlphaAction::link);
	}

	@Action
	public void link() {
		chain++;
	}

}
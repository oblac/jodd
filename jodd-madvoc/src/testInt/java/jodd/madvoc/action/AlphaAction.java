// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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
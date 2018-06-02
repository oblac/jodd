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

package jodd.madvoc.fixtures.tst;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.method.DELETE;
import jodd.madvoc.meta.method.POST;

public class BooAction {

	public void foo() {}
	public void view() {}
	public void execute() {}

	@Action("xxx")
	public void foo1() {}

	@Action("{:name}.xxx")
	public void foo2() {}

	@Action(value = Action.NONE)
	public void foo3() {}

	@Action("/xxx")
	public void foo4() {}

	@Action(value = "/xxx")
	@DELETE
	public void foo41() {}

	@Action(value = "/xxx.html", alias = "dude")
	@POST
	public void foo5() {}

	@Action(value = "q{:name}2")
	public void foo6() {}

	@Action(value = "/{:name}.html")
	public void foo7() {}

	@Action
	public void foo8() {}
	@Action(value = "/boo.foo81")
	public void foo81() {}
	@Action
	public void foo82() {}
	@Action("{:name}.json")
	public void foo83() {}

}

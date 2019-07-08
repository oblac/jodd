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

package jodd.madvoc.fixtures.tst2;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.method.POST;

@MadvocAction("/re/")
public class ReAction {

	@Action
	public void hello() {
	}

	@Action("user/{id}/{:name}")
	public void macro() {
	}

	@Action("user/image/{id}/{fmt}/{:name}")
	public void macro2() {
	}

	@Action(value = "users/{id}/{:name}")
	@POST
	public void macro3() {
	}


	@Action("wild{id}cat")
	public void wild1() {
	}
	@Action(value = "wild{id}dog")
	@POST
	public void wild2() {
	}

	@Action(value = "duplo/{id:^[0-9]+}")
	public void duplo2() {
	}

	@Action(value = "duplo/{sid}")
	public void duplo1() {
	}


	// ---------------------------------------------------------------- zqq #30

	String entityName;

	@Action(value = "/{entityName}/dba.delete.do")
	public void zqq1() {}

	@Action(value = "/{entityName}/dba.delete_multi.do")
	public void zqq2() {}

}
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

import jodd.madvoc.AppendingInterceptor;
import jodd.madvoc.MyInterceptorStack;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

@MadvocAction("cpt")
public class IntcptAction {

	@Action
	@InterceptedBy({EchoInterceptor.class, ServletConfigInterceptor.class})
	public void in1() {
	}

	@In
	String foo2;

	@Out
	String foo;

	@Action
	@InterceptedBy({EchoInterceptor.class, ServletConfigInterceptor.class})
	public String in2() {
		foo = foo2;
		return "#in1";
	}

	// ----------------------------------------------------------------

	@Out
	public String value;

	@Action
	@InterceptedBy({ServletConfigInterceptor.class, AppendingInterceptor.class})
	public void inap() {
		value = "appending";
	}

	@Action
	@InterceptedBy({ServletConfigInterceptor.class, AppendingInterceptor.Hey.class})
	public void inap2() {
		value = "appending2";
	}

	@Action
	@InterceptedBy(MyInterceptorStack.class)
	public void inap3() {
		value = "appending3";
	}

}
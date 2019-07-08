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

package jodd.madvoc;

import jodd.madvoc.component.MadvocController;
import jodd.madvoc.config.ActionDefinition;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.ServletDispatcherActionResult;
import jodd.util.ClassUtil;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionRequestRecursionTest {

	@Test
	void testFiltersPassAndInterceptorsPass() throws Exception {
		MyActionRequest actionRequest = createMyActionRequest(
				arr(new FilterPass(1), new FilterPass(2)),
				arr(new InterceptorPass(1), new InterceptorPass(2))
		);

		actionRequest.invoke();
		assertEquals("-F1-F2-I1-I2-A-i2-i1-R-f2-f1", actionRequest.data);
	}

	@Test
	void testFiltersStopAndInterceptorsPass1() throws Exception {
		MyActionRequest actionRequest = createMyActionRequest(
				arr(new FilterStop(), new FilterPass(2)),
				arr(new InterceptorPass(1), new InterceptorPass(2))
		);

		actionRequest.invoke();
		assertEquals("-X", actionRequest.data);
	}

	@Test
	void testFiltersStopAndInterceptorsPass2() throws Exception {
		MyActionRequest actionRequest = createMyActionRequest(
				arr(new FilterPass(1), new FilterStop()),
				arr(new InterceptorPass(1), new InterceptorPass(2))
		);

		actionRequest.invoke();
		assertEquals("-F1-X-f1", actionRequest.data);
	}

	@Test
	void testFiltersPassAndInterceptorsStop1() throws Exception {
		MyActionRequest actionRequest = createMyActionRequest(
				arr(new FilterPass(1), new FilterPass(2)),
				arr(new InterceptorPass(1), new InterceptorStop())
		);

		actionRequest.invoke();
		assertEquals("-F1-F2-I1-x-i1-R-f2-f1", actionRequest.data);
	}

	@Test
	void testFiltersPassAndInterceptorsStop2() throws Exception {
		MyActionRequest actionRequest = createMyActionRequest(
				arr(new FilterPass(1), new FilterPass(2)),
				arr(new InterceptorStop(), new InterceptorPass(2))
		);

		actionRequest.invoke();
		assertEquals("-F1-F2-x-R-f2-f1", actionRequest.data);
	}

	@Test
	void testFiltersPassAndInterceptorsStop3() throws Exception {
		MyActionRequest actionRequest = createMyActionRequest(
				arr(new FilterPass(1), new FilterPass(2)),
				arr(new InterceptorPass(1), new InterceptorStop(), new InterceptorPass(3))
		);

		actionRequest.invoke();
		assertEquals("-F1-F2-I1-x-i1-R-f2-f1", actionRequest.data);
	}

	// ---------------------------------------------------------------- internal

	class MyActionRequest extends ActionRequest {
		public String data = "";
		public MyActionRequest(MadvocController madvocController, String actionPath, ActionRuntime config, Object action, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
			super(madvocController, actionPath, MadvocUtil.splitPathToChunks(actionPath), config, action, servletRequest, servletResponse);
		}
		@Override
		protected Object invokeActionMethod() throws Exception {
			data += "-A";
			return super.invokeActionMethod();
		}
	}

	class Action {
		public void view() {}
	}

	class FilterPass implements ActionFilter {
		final int i;
		public FilterPass(int i) {
			this.i = i;
		}

		@Override
		public Object filter(ActionRequest actionRequest) throws Exception {
			((MyActionRequest)actionRequest).data += "-F" + i;
			Object result = actionRequest.invoke();
			((MyActionRequest)actionRequest).data += "-f" + i;
			return result;
		}
	}
	class FilterStop implements ActionFilter {
		@Override
		public Object filter(ActionRequest actionRequest) {
			((MyActionRequest)actionRequest).data += "-X";
			return "stop";
		}
	}

	class InterceptorPass implements ActionInterceptor {
		final int i;
		public InterceptorPass(int i) {
			this.i = i;
		}

		@Override
		public Object intercept(ActionRequest actionRequest) throws Exception {
			((MyActionRequest)actionRequest).data += "-I"+i;
			Object result = actionRequest.invoke();
			((MyActionRequest)actionRequest).data += "-i"+i;
			return result;
		}
	}
	class InterceptorStop implements ActionInterceptor {
		@Override
		public Object intercept(ActionRequest actionRequest) throws Exception {
			((MyActionRequest)actionRequest).data += "-x";
			return "stop";
		}
	}

	class SimpleMadvocController extends MadvocController {
		@Override
		public void render(ActionRequest actionRequest, Object resultObject) throws Exception {
			((MyActionRequest)actionRequest).data += "-R";
		}
	}

	private MyActionRequest createMyActionRequest(ActionFilter[] actionFilters, ActionInterceptor[] actionInterceptors) {
		SimpleMadvocController madvocController = new SimpleMadvocController();

		Action action = new Action();
		ActionRuntime actionRuntime = new ActionRuntime(
				null,
				Action.class,
				ClassUtil.findMethod(Action.class, "view"),
				actionFilters, actionInterceptors,
				new ActionDefinition("path", "method"),
				ServletDispatcherActionResult.class,
				null,
				false, false, null, null);

		return new MyActionRequest(
				madvocController, "actionPath", actionRuntime, action, null, null);
	}

	private <T> T[] arr(T... array) {
		return array;
	}

}

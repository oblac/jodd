// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.result.BaseActionResult;
import jodd.servlet.DispatcherUtil;

import javax.servlet.http.HttpServletRequest;

public class Book2ActionResult extends BaseActionResult<Book> {

	@Override
	public Class<Book> getResultValueType() {
		return null;
	}

	public void render(ActionRequest actionRequest, Book book) throws Exception {
		HttpServletRequest request = actionRequest.getHttpServletRequest();

		request.setAttribute("book", book);

		String method = actionRequest.getActionConfig().getActionMethod();

		if (method.equalsIgnoreCase("PUT")) {
			DispatcherUtil.forward(request, actionRequest.getHttpServletResponse(), "/book/put.jsp");
		}
	}
}
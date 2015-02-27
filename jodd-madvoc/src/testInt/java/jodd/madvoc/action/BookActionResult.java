// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.result.BaseActionResult;
import jodd.servlet.DispatcherUtil;

import javax.servlet.http.HttpServletRequest;

public class BookActionResult extends BaseActionResult<Book> {

	public void render(ActionRequest actionRequest, Book book) throws Exception {
		HttpServletRequest request = actionRequest.getHttpServletRequest();

		request.setAttribute("book", book);

		String method = actionRequest.getActionConfig().getActionMethod();

		if (method.equalsIgnoreCase("POST")) {
			DispatcherUtil.forward(request, actionRequest.getHttpServletResponse(), "/book/post.jsp");
		} else {
			DispatcherUtil.forward(request, actionRequest.getHttpServletResponse(), "/book/get.jsp");
		}
	}

}
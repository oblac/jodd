// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class Echo2Servlet extends EchoServlet {

	protected void readAll(HttpServletRequest req) throws IOException {
		ref.queryString = req.getQueryString();
		ref.header = copyHeaders(req);
		ref.params = copyParams(req);
	}

}
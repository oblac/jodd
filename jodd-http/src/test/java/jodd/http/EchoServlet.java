// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.StreamUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class EchoServlet extends HttpServlet {

	public static Data ref;

	public static void testinit() {
		ref = new Data();
	}

	public static class Data {
		public boolean get;
		public boolean post;
		public String queryString;
		public String body;
		public Map<String, String> header;
		public Map<String, String> params;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ref.get = true;
		ref.post = false;
		readAll(req);
		write(resp, ref.body);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ref.post = true;
		ref.get = false;
		readAll(req);
		write(resp, ref.body);
	}

	// ---------------------------------------------------------------- write

	protected void write(HttpServletResponse resp, String text) throws IOException {
		if (text != null) {
			resp.setContentLength(text.getBytes(StringPool.UTF_8).length);
			resp.setContentType("text/html;charset=UTF-8");
			resp.getWriter().write(text);
			resp.flushBuffer();
		}
	}

	// ---------------------------------------------------------------- read all

	protected void readAll(HttpServletRequest req) throws IOException {
		ref.body = readRequestBody(req);
		ref.queryString = req.getQueryString();
		ref.header = copyHeaders(req);
	}

	protected String readRequestBody(HttpServletRequest request) throws IOException {
		BufferedReader buff = request.getReader();
		StringWriter out = new StringWriter();
		StreamUtil.copy(buff, out);
		return out.toString();
	}

	protected Map<String, String> copyHeaders(HttpServletRequest req) {
		Enumeration enumeration = req.getHeaderNames();
		Map<String, String> header = new HashMap<String, String>();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement().toString();
			String value = req.getHeader(name);
			header.put(name, value);
		}

		return header;
	}

	protected Map<String, String> copyParams(HttpServletRequest req) {
		String charset = req.getParameter("enc");

		Enumeration enumeration = req.getParameterNames();
		Map<String, String> params = new HashMap<String, String>();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement().toString();
			String value = req.getParameter(name);
			if (charset != null) {
				value = StringUtil.convertCharset(value, StringPool.ISO_8859_1, charset);
			}
			params.put(name, value);
		}

		return params;
	}

}
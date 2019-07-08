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

package jodd.http.fixture;

import jodd.io.StreamUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import org.apache.catalina.core.ApplicationPart;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class EchoServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Data.ref = new Data();
		Data.ref.get = true;
		Data.ref.post = false;
		readAll(req);

		if (Data.ref.cookies != null) {
			for (Cookie cookie : Data.ref.cookies) {
				cookie.setValue(cookie.getValue() + "!");
				resp.addCookie(cookie);
			}
		}

		write(resp, Data.ref.body);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Data.ref = new Data();
		Data.ref.post = true;
		Data.ref.get = false;
		readAll(req);
		write(resp, Data.ref.body);
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
		Data.ref.body = readRequestBody(req);
		Data.ref.queryString = req.getQueryString();
		Data.ref.header = copyHeaders(req);
		Data.ref.cookies = req.getCookies();
	}

	protected String readRequestBody(HttpServletRequest request) throws IOException {
		BufferedReader buff = request.getReader();
		StringWriter out = new StringWriter();
		StreamUtil.copy(buff, out);
		return out.toString();
	}

	protected Map<String, String> copyHeaders(HttpServletRequest req) {
		Enumeration enumeration = req.getHeaderNames();
		Map<String, String> header = new HashMap<>();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement().toString();
			String value = req.getHeader(name);
			header.put(name, value);
		}

		return header;
	}

	protected Map<String, String> copyParams(HttpServletRequest req, String fromEncoding) {
		String charset = req.getParameter("enc");

		Enumeration enumeration = req.getParameterNames();
		Map<String, String> params = new HashMap<>();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement().toString();
			String value = req.getParameter(name);
			if (charset != null) {
				value = StringUtil.convertCharset(value, fromEncoding, charset);
			}
			params.put(name, value);
		}

		return params;
	}

	protected Map<String, String> copyParts(HttpServletRequest req) {
		Map<String, String> parts = new HashMap<>();
		if (req.getContentType() == null) {
			return parts;
		}
		if (req.getContentType() != null && !req.getContentType().toLowerCase().contains("multipart/form-data")) {
			return parts;
		}

		String enc = "UTF-8";

		try {
			Collection<Part> prs = req.getParts();

			for (Part p : prs) {
				parts.put(p.getName(), new String(StreamUtil.readBytes(p.getInputStream()), enc));
			}
		}
		catch (IOException | ServletException e) {
			e.printStackTrace();
		}

		return parts;
	}

	protected Map<String, String> copyFileName(HttpServletRequest req) {
		Map<String, String> parts = new HashMap<>();
		if (req.getContentType() == null) {
			return parts;
		}
		if (req.getContentType() != null && !req.getContentType().toLowerCase().contains("multipart/form-data")) {
			return parts;
		}

		try {
			Collection<Part> prs = req.getParts();

			for (Part p : prs) {
				if (p instanceof ApplicationPart) {
					ApplicationPart ap = (ApplicationPart) p;
					parts.put(p.getName(), ap.getSubmittedFileName());
				}
			}
		}
		catch (IOException | ServletException e) {
			e.printStackTrace();
		}

		return parts;
	}

}
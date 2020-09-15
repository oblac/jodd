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

import jodd.io.IOUtil;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class EchoServlet extends HttpServlet {

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		Data.ref = new Data();
		Data.ref.get = true;
		Data.ref.post = false;
		readAll(req);

		if (Data.ref.cookies != null) {
			for (final Cookie cookie : Data.ref.cookies) {
				cookie.setValue(cookie.getValue() + "!");
				resp.addCookie(cookie);
			}
		}

		write(resp, Data.ref.body);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		Data.ref = new Data();
		Data.ref.post = true;
		Data.ref.get = false;
		readAll(req);
		write(resp, Data.ref.body);
	}

	// ---------------------------------------------------------------- write

	protected void write(final HttpServletResponse resp, final String text) throws IOException {
		if (text != null) {
			resp.setContentLength(text.getBytes(StandardCharsets.UTF_8).length);
			resp.setContentType("text/html;charset=UTF-8");
			resp.getWriter().write(text);
			resp.flushBuffer();
		}
	}

	// ---------------------------------------------------------------- read all

	protected void readAll(final HttpServletRequest req) throws IOException {
		Data.ref.body = readRequestBody(req);
		Data.ref.queryString = req.getQueryString();
		Data.ref.header = copyHeaders(req);
		Data.ref.cookies = req.getCookies();
	}

	protected String readRequestBody(final HttpServletRequest request) throws IOException {
		final BufferedReader buff = request.getReader();
		final StringWriter out = new StringWriter();
		IOUtil.copy(buff, out);
		return out.toString();
	}

	protected Map<String, String> copyHeaders(final HttpServletRequest req) {
		final Enumeration enumeration = req.getHeaderNames();
		final Map<String, String> header = new HashMap<>();

		while (enumeration.hasMoreElements()) {
			final String name = enumeration.nextElement().toString();
			final String value = req.getHeader(name);
			header.put(name, value);
		}

		return header;
	}

	protected Map<String, String> copyParams(final HttpServletRequest req, final String fromEncoding) {
		final String charset = req.getParameter("enc");

		final Enumeration enumeration = req.getParameterNames();
		final Map<String, String> params = new HashMap<>();

		while (enumeration.hasMoreElements()) {
			final String name = enumeration.nextElement().toString();
			String value = req.getParameter(name);
			if (charset != null) {
				value = StringUtil.convertCharset(value, Charset.forName(fromEncoding), Charset.forName(charset));
			}
			params.put(name, value);
		}

		return params;
	}

	protected Map<String, String> copyParts(final HttpServletRequest req) {
		final Map<String, String> parts = new HashMap<>();
		if (req.getContentType() == null) {
			return parts;
		}
		if (req.getContentType() != null && !req.getContentType().toLowerCase().contains("multipart/form-data")) {
			return parts;
		}

		final String enc = "UTF-8";

		try {
			final Collection<Part> prs = req.getParts();

			for (final Part p : prs) {
				parts.put(p.getName(), new String(IOUtil.readBytes(p.getInputStream()), enc));
			}
		}
		catch (final IOException | ServletException e) {
			e.printStackTrace();
		}

		return parts;
	}

	protected Map<String, String> copyFileName(final HttpServletRequest req) {
		final Map<String, String> parts = new HashMap<>();
		if (req.getContentType() == null) {
			return parts;
		}
		if (req.getContentType() != null && !req.getContentType().toLowerCase().contains("multipart/form-data")) {
			return parts;
		}

		try {
			final Collection<Part> prs = req.getParts();

			for (final Part p : prs) {
				if (p instanceof ApplicationPart) {
					final ApplicationPart ap = (ApplicationPart) p;
					parts.put(p.getName(), ap.getSubmittedFileName());
				}
			}
		}
		catch (final IOException | ServletException e) {
			e.printStackTrace();
		}

		return parts;
	}

}

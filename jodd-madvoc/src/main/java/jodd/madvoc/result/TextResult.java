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

package jodd.madvoc.result;

import jodd.io.StreamUtil;
import jodd.madvoc.ActionRequest;
import jodd.util.MimeTypes;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Text result returns a result value, i.e. a string.
 * Useful for JSON responses, when resulting string is built
 * in the action.
 */
public class TextResult extends BaseActionResult<String> {

	public static final String NAME = "text";

	public TextResult() {
		super(NAME);
	}

	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		String encoding = response.getCharacterEncoding();
		response.setContentType(MimeTypes.MIME_TEXT_PLAIN);
		response.setCharacterEncoding(encoding);

		byte[] data = resultValue.getBytes(encoding);
		response.setContentLength(data.length);

		OutputStream out = null;
		try {
			out = response.getOutputStream();
			out.write(data);
		} finally {
			StreamUtil.close(out);
		}
	}
}
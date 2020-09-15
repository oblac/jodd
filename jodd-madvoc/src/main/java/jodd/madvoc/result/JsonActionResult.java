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

import jodd.io.IOUtil;
import jodd.json.JsonSerializer;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.component.MadvocEncoding;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.scope.MadvocContext;
import jodd.net.MimeTypes;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Json results handler.
 */
public class JsonActionResult implements ActionResult {

	@In @MadvocContext
	protected MadvocEncoding madvocEncoding;

	@Override
	public void render(final ActionRequest actionRequest, final Object resultValue) throws Exception {
		final HttpServletResponse response = actionRequest.getHttpServletResponse();

		String encoding = response.getCharacterEncoding();

		if (encoding == null) {
			encoding = madvocEncoding.getEncoding();
		}

		response.setContentType(MimeTypes.MIME_APPLICATION_JSON);
		response.setCharacterEncoding(encoding);

		final String json;
		final int status;
		final String statusMessage;

		if (resultValue instanceof JsonResult) {
			final JsonResult jsonResult = (JsonResult) resultValue;

			json = jsonResult.value();
			status = jsonResult.status();
			statusMessage = jsonResult.message();
		}
		else {
			json = JsonSerializer.create().deep(true).serialize(resultValue);
			status = 200;
			statusMessage = "OK";
		}

		response.setStatus(status);

		// write data

		final byte[] data = json.getBytes(encoding);
		response.setContentLength(data.length);

		OutputStream out = null;
		try {
			out = response.getOutputStream();
			out.write(data);
		} finally {
			IOUtil.close(out);
		}
	}
}

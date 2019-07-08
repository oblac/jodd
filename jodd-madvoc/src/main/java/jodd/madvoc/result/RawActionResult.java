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
import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Raw results directly writes byte context to the output.
 * Content type and charset encoding (e.g. set by Madvoc) is ignored
 * and new values should be set here. Output is closed after writing.
 */
public class RawActionResult implements ActionResult<RawData> {

	@Override
	public void render(final ActionRequest actionRequest, final RawData resultValue) throws IOException {
		if (resultValue == null) {
			return;
		}

		HttpServletResponse response = actionRequest.getHttpServletResponse();

		// reset content type and prepare response
		// since we are using MadvocResponseWrapper, the charset will be reset as well.
		ServletUtil.prepareResponse(response, resultValue.downloadFileName(), resultValue.mimeType(), resultValue.contentLength());

		// write out
		InputStream contentInputStream = resultValue.contentInputStream();
		OutputStream out = response.getOutputStream();

		StreamUtil.copy(contentInputStream, out);

		out.flush();

		StreamUtil.close(contentInputStream);
	}
}

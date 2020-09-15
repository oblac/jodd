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
import jodd.madvoc.ActionRequest;
import jodd.madvoc.component.MadvocEncoding;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.scope.MadvocContext;
import jodd.util.StringPool;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Text result returns a result value, i.e. a string.
 * Useful for JSON responses, when resulting string is built
 * in the action.
 */
public class TextActionResult implements ActionResult {

	@In @MadvocContext
	protected MadvocEncoding madvocEncoding;

	@Override
	public void render(final ActionRequest actionRequest, final Object resultValue) throws Exception {
		final TextResult textResult;

		if (resultValue == null) {
			textResult = TextResult.of(StringPool.EMPTY);
		} else {
			if (resultValue instanceof String) {
				textResult = TextResult.of((String)resultValue);
			}
			else {
				textResult = (TextResult) resultValue;
			}
		}

		final HttpServletResponse response = actionRequest.getHttpServletResponse();

		String encoding = response.getCharacterEncoding();

		if (encoding == null) {
			encoding = madvocEncoding.getEncoding();
		}

		response.setContentType(textResult.contentType());
		response.setCharacterEncoding(encoding);
		response.setStatus(textResult.status());

		String text = textResult.value();

		if (text == null) {
			text = StringPool.EMPTY;
		}

		final byte[] data = text.getBytes(encoding);
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

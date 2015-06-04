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

package jodd.datetime.format;

import jodd.datetime.JDateTime;
import jodd.datetime.DateTimeStamp;

/**
 * Immutable format-formatter pair.
 */
public class JdtFormat {

	protected final String format;
	protected final JdtFormatter formatter;

	public JdtFormat(JdtFormatter formatter, String format) {
		this.format = format;
		this.formatter = formatter;
	}

	/**
	 * Returns format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Returns formatter.
	 */
	public JdtFormatter getFormatter() {
		return formatter;
	}


	/**
	 * Delegates for {@link jodd.datetime.format.JdtFormatter#convert(jodd.datetime.JDateTime, String)}. 
	 */
	public String convert(JDateTime jdt) {
		return formatter.convert(jdt, format);
	}

	/**
	 * Delegates for {@link jodd.datetime.format.JdtFormatter#parse(String, String)}.
	 */
	public DateTimeStamp parse(String value) {
		return formatter.parse(value, format);
	}
}

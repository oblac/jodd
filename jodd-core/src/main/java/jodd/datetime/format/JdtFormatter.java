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

import jodd.datetime.DateTimeStamp;
import jodd.datetime.JDateTime;

import java.io.Serializable;

/**
 * Date time formatter performs conversion both from and to string representation of time.
 * 
 * @see AbstractFormatter
 */
public interface JdtFormatter extends Serializable {

	/**
	 * Converts date time to a string using specified format.
	 *
	 * @param jdt       JDateTime to read from
	 * @param format    format
	 *
	 * @return formatted string with date time information
	 */
	String convert(JDateTime jdt, String format);

	/**
	 * Parses string given in specified format and extracts time information.
	 * It returns a new instance of <code>DateTimeStamp</code> or <code>null</code> if error occurs.
	 *
	 * @param value     string containing date time values
	 * @param format    format
	 *
	 * @return DateTimeStamp instance with populated data
	 */
	DateTimeStamp parse(String value, String format);
}

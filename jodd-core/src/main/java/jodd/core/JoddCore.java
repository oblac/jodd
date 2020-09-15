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

package jodd.core;

import java.security.Security;

/**
 * Jodd library-wide properties.
 */
public class JoddCore {

	static {
		// Starting from Java8 u151, the `Unlimited Strength Jurisdiction Policy Files`
		// are included with Java, but has to be enabled.
		// They are enabled on Java9 by default.
		Security.setProperty("crypto.policy", "unlimited");
	}

	// ---------------------------------------------------------------- settings

	/**
	 * Default prefix for temporary files.
	 */
	public static String tempFilePrefix = "jodd-";

	/**
	 * The encoding used across the Jodd classes, "UTF-8" by default.
	 */
	public static String encoding = "UTF-8";

	/**
	 * Buffer size for various I/O operations.
	 */
	public static int ioBufferSize = 16384;
	/**
	 * Flag that controls the {@code Unsafe} usage (if system detects it). Enabled by default.
	 */
	public static boolean unsafeUsageEnabled = true;

}

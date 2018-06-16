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

package jodd.util;

import jodd.bridge.JavaIncompatible;
import jodd.core.JoddCore;
import jodd.system.SystemUtil;

/**
 * Few methods using infamous <code>java.misc.Unsafe</code>, mostly for private use.
 * See: http://mishadoff.github.io/blog/java-magic-part-4-sun-dot-misc-dot-unsafe/
 *
 * Thanx to Gatling (http://gatling-tool.org)!
 */
@JavaIncompatible
public class UnsafeUtil {

	// IMPORTANT - the order of declaration here is important! we need to detect
	// first the Android, and then to check for the unsafe field.

	private static final boolean IS_ANDROID = SystemUtil.info().isAndroid();
	private static final boolean HAS_UNSAFE = !IS_ANDROID && UnsafeInternal.hasUnsafe();

	/**
	 * Returns <code>true</code> if system has the <code>Unsafe</code>.
	 */
	public static boolean hasUnsafe() {
		return HAS_UNSAFE;
	}

	/**
	 * Returns String characters in most performing way.
	 * If possible, the inner <code>char[]</code> will be returned.
	 * If not, <code>toCharArray()</code> will be called.
	 * Returns <code>null</code> when argument is <code>null</code>.
	 */
	public static char[] getChars(final String string) {
		if (string == null) {
			return null;
		}

		if (!HAS_UNSAFE || !JoddCore.unsafeUsageEnabled) {
			return string.toCharArray();
		}

		return UnsafeInternal.unsafeGetChars(string);
	}

}
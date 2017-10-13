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

package jodd.io.findfile;

import jodd.util.ClassLoaderUtil;

import java.net.URL;
import java.io.File;

/**
 * A class scanner, user-friendly version of {@link ClassFinder} a class scanner.
 * Offers public <code>scan()</code> methods that can be directly used.
 */
public abstract class ClassScanner extends ClassFinder {

	/**
	 * Scans provided classpath.
	 */
	public void scan(URL... urls) {
		scanUrls(urls);
	}

	/**
	 * Scans {@link jodd.util.ClassLoaderUtil#getDefaultClasspath() default class path}.
	 */
	public void scanDefaultClasspath() {
		scan(ClassLoaderUtil.getDefaultClasspath());
	}

	/**
	 * Scans provided paths.
	 */
	public void scan(File... paths) {
		scanPaths(paths);
	}

	/**
	 * Scans provided paths.
	 */
	public void scan(String... paths) {
		scanPaths(paths);
	}

}
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

package jodd.madvoc.macro;

/**
 * Action path macros.
 */
public interface PathMacros {

	/**
	 * Initializes <code>PathMacro</code> and returns
	 * <code>true</code> if macros are found in the action
	 * path. Otherwise, returns <code>false</code> and
	 * the instance can be thrown away.
	 * <p>
	 * Separators is array of 3 strings that defines:
	 * start, dividing string, and end of a macro.
	 */
	boolean init(String actionPath, String[] separators);

	/**
	 * Returns names of all macros.
	 */
	String[] names();

	/**
	 * Returns all patterns. Some elements may be <code>null</code>
	 * if some macro does not define a pattern.
	 */
	String[] patterns();

	/**
	 * Returns macros count.
	 */
	int macrosCount();

	/**
	 * Match provided action path with the path macros,
	 * Returns the number of matched non-macro characters.
	 * Returns -1 if action path is not matched.
	 */
	int match(String actionPath);

	/**
	 * Extracts array of macro values for matched action path
	 * for each {@link #names() name}. It is assumed
	 * that path macro was previously {@link #init(String, String[])} initialized}
	 * on this action path, i.e. input is not validated.
	 * <p>
	 * Returned array string of macro values may contain
	 * <code>null</code> on all ignored macros.
	 */
	String[] extract(String actionPath);

}
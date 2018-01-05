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

import jodd.methref.Methref;

import java.util.function.Consumer;

/**
 * Path result.
 */
public abstract class PathResult {

	private final String path;
	private final Class target;
	private final Methref methref;

	public PathResult(String path) {
		this.path = path;
		this.methref = null;
		this.target = null;
	}

	public <T> PathResult(Class<T> target, Consumer<T> consumer) {
		this.path = null;
		Methref<T> methref = wrapTargetToMethref(target);
		consumer.accept(methref.to());
		this.methref = methref;
		this.target = target;
	}

	/**
	 * Wraps action class and returns <code>MethRef</code> object
	 * (proxified target) so user can choose the method.
	 */
	protected <T> Methref<T> wrapTargetToMethref(Class<T> target) {
		return Methref.on(target);
	}

	/**
	 * Returns path value;
	 */
	public String path() {
		if (methref != null) {
			String methodName = methref.ref();
			return target.getName() + "#" + methodName;
		}
		return path;
	}
}
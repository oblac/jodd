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

package jodd.util.concurrent;

import jodd.exception.UncheckedException;

import java.util.concurrent.Callable;

/**
 * Piece of code that that has no result and may throw an exception.
 * Similar to {@code Runnable} or {@code Callable}.
 */
@FunctionalInterface
public interface Task {

	/**
	 * Wraps a task into a runnable.
	 */
	public static Runnable runnableOf(final Task task) {
		return () -> {
			try {
				task.run();
			} catch (Exception e) {
				throw new UncheckedException(e);
			}
		};
	}

	/**
	 * Wraps a task into a callable that returns {@code null}.
	 */
	public static Callable callableOf(final Task task) {
		return () -> {
			task.run();
			return null;
		};
	}

	/**
	 * Runs a code or throws an exception if unable to do so.
	 *
	 * @throws Exception if unable to execute task
	 */
	void run();
}
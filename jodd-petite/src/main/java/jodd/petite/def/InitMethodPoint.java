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

package jodd.petite.def;

import jodd.petite.meta.InitMethodInvocationStrategy;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Init method point.
 */
public class InitMethodPoint implements Comparable {

	public static final InitMethodPoint[] EMPTY = new InitMethodPoint[0];

	public final Method method;
	public final int order;
	public final InitMethodInvocationStrategy invocationStrategy;

	public InitMethodPoint(final Method method, final int order, final InitMethodInvocationStrategy invocationStrategy) {
		Objects.requireNonNull(method);
		Objects.requireNonNull(invocationStrategy);

		this.method = method;
		this.order = order == 0 ? (Integer.MAX_VALUE >> 1) : (order < 0 ? Integer.MAX_VALUE + order: order);
		this.invocationStrategy = invocationStrategy;
	}

	@Override
	public int compareTo(final Object other) {
		InitMethodPoint that = (InitMethodPoint) other;
		return Integer.compare(this.order, that.order);
	}

}

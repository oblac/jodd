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

package jodd.proxetta;

/**
 * Proxy aspect contains advice and pointcut rules for applying advice.
 */
public class ProxyAspect {

	protected final Class<? extends ProxyAdvice> advice;
	protected final ProxyPointcut pointcut;

	public static ProxyAspect of(final Class<? extends ProxyAdvice> advice, final ProxyPointcut pointcut) {
		return new ProxyAspect(advice, pointcut);
	}

	/**
	 * Creates aspect defined with provided advice and pointcut.
	 */
	public ProxyAspect(final Class<? extends ProxyAdvice> advice, final ProxyPointcut pointcut) {
		this.advice = advice;
		this.pointcut = pointcut;
	}

	/**
	 * Returns proxy advice class.
	 */
	public Class<? extends ProxyAdvice> advice() {
		return advice;
	}

	/**
	 * Returns proxy pointcut.
	 */
	public ProxyPointcut pointcut() {
		return pointcut;
	}


	@Override
	public String toString() {
		return "ProxyAspect{" +
				"advice=" + advice.getName() +
				", pointcut=" + pointcut.getClass().getName() +
				'}';
	}
}

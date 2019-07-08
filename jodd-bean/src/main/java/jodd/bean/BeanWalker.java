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

package jodd.bean;

import java.util.Map;

/**
 * Walker over bean properties.
 */
public class BeanWalker extends BeanVisitorImplBase<BeanWalker> {

	private final BeanWalkerCallback callback;

	/**
	 * Functional callback for walking.
	 */
	public interface BeanWalkerCallback {
		void visitProperty(String name, Object value);
	}

	public BeanWalker(final BeanWalkerCallback callback) {
		this.callback = callback;
	}

	/**
	 * Static ctor.
	 */
	public static BeanWalker walk(final BeanWalkerCallback callback) {
		return new BeanWalker(callback);
	}

	public void source(final Object source) {
		this.source = source;

		isSourceMap = (source instanceof Map);

		visit();
	}

	public void bean(final Object bean) {
		this.source = bean;

		visit();
	}

	public void map(final Map map) {
		this.source = map;

		this.isSourceMap = true;

		visit();
	}

	@Override
	protected boolean visitProperty(final String name, final Object value) {
		callback.visitProperty(name, value);
		return true;
	}

}
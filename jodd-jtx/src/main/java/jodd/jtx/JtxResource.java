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

package jodd.jtx;

import jodd.util.ClassUtil;

/**
 * Stores resource object and its resource manager.
 */
final class JtxResource<E> {

	final JtxTransaction transaction;
	final JtxResourceManager<E> resourceManager;
	private final E resource;

	JtxResource(final JtxTransaction transaction, final JtxResourceManager<E> resourceManager, final E resource) {
		this.transaction = transaction;
		this.resourceManager = resourceManager;
		this.resource = resource;
	}

	/**
	 * Returns <code>true</code> if resource is of provided resource type.
	 */
	public boolean isSameTypeAsResource(final Class type) {
		return ClassUtil.isTypeOf(type, resource.getClass());
	}

	// ---------------------------------------------------------------- delegates

	/**
	 * Delegates to {@link jodd.jtx.JtxResourceManager#commitTransaction(Object)}.
	 */
	void commitTransaction() {
		resourceManager.commitTransaction(resource);
	}

	/**
	 * Delegates to {@link JtxResourceManager#rollbackTransaction(Object)}}.
	 */
	void rollbackTransaction() {
		resourceManager.rollbackTransaction(resource);
	}

	/**
	 * Returns resource instance.
s	 */
	public E getResource() {
		return resource;
	}
}
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

/**
 * Responsible for managing transactions of the
 * resources of the same type under the control of the {@link JtxTransaction transaction}.
 * Resource manager has to be registered in the {@link JtxTransactionManager transaction manager}.
 */
public interface JtxResourceManager<E> {

	/**
	 * Returns associated resource type.
	 */
	Class<E> getResourceType();

	/**
	 * Creates new resource and begins new transaction if specified so by
	 * active flag, usually determined by propagation behavior.
	 * Propagation behavior and timeout may be handled by the Jtx framework,
	 * leaving resource manager to handle isolation and read only flag.
	 */
	E beginTransaction(JtxTransactionMode jtxMode, boolean active);

	/**
	 * Commits resource and closes it if committing was successful.
	 */
	void commitTransaction(E resource);

	/**
	 * Rollback resource and closes it. Resource is closed no matter if rolling back fails.
	 */
	void rollbackTransaction(E resource);

	/**
	 * Closes manager and free its resources.
	 */
	void close();
}

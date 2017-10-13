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
 * Invoke aspect defines method pointcuts that should be replaced and
 * their advice replacements.
 */
public abstract class InvokeAspect {

	/**
	 * Determines if some method should be scanned for pointcuts.
	 * Returns <code>true</code> if method should be scanned.
	 */
	public boolean apply(MethodInfo methodInfo) {
		return true;
	}


	/**
	 * Defines method invocation pointcut and returns replacement advice.
	 * Returns <code>null</code> if method doesn't have to be replaced at all.
	 * <p>
	 * Special case is <code>new</code> instruction. Since <code>new</code> opcode
	 * appears in the bytecode before actual constructor invocation,
	 * description of <code>InvokeInfo</code> is unknown. Therefore, for each
	 * constructor that will be replaced, there must be an advice replacement method
	 * with the same description.
	 */
	public abstract InvokeReplacer pointcut(InvokeInfo invokeInfo);

}

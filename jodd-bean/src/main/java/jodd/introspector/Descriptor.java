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

package jodd.introspector;

/**
 * Common descriptor stuff.
 */
public abstract class Descriptor {

	protected final ClassDescriptor classDescriptor;
	protected final boolean isPublic;

	protected Descriptor(final ClassDescriptor classDescriptor, final boolean isPublic) {
		this.classDescriptor = classDescriptor;
		this.isPublic = isPublic;
	}

	/**
	 * Returns belonging class descriptor.
	 */
	public ClassDescriptor getClassDescriptor() {
		return classDescriptor;
	}

	/**
	 * Returns <code>true</code> if descriptor content is public.
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * Returns <code>true</code> if descriptor content matches required declared flag.
	 */
	public boolean matchDeclared(final boolean declared) {
		if (!declared) {
			return isPublic;
		}
		return true;
	}

	/**
	 * Returns the name of descriptors target.
	 */
	public abstract String getName();

}
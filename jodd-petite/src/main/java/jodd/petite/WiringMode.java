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

package jodd.petite;

/**
 * Wiring modes for Petite beans.
 */
public enum WiringMode {

	/**
	 * Default wiring mode is set by the container.
	 */
	DEFAULT(-1),

	/**
	 * No wiring at all. Petite beans are not injected even when there is an
	 * explicit definition of injection.
	 */
	NONE(0),
	/**
	 * Explicit and strict wiring. Wires explicitly defined injection points.
	 * Throws an exception if wiring can not be satisfied.
	 */
	STRICT(1),
	/**
	 * Explicit and loose wiring. Wires only explicitly defined injection points.
	 * Does not throw exception if wiring can not be satisfied.
	 */
	OPTIONAL(2),
	/**
	 * Auto-wires beans. Beans will be injected for defined injection points
	 * and in all places where naming convention is satisfied.
	 * No exception is thrown if wiring ca not be satisfied.
	 */
	AUTOWIRE(3);

	private final int value;

	WiringMode(final int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		return name();
	}

}

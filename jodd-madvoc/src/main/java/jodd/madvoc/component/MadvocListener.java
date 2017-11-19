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

package jodd.madvoc.component;

/**
 * Madvoc listeners. There are 3 events:
 * <ul>
 *     <li>INIT</li>
 *     <li>READY</li>
 *     <li>SHUTDOWN</li>
 * </ul>
 */
public final class MadvocListener {

	/**
	 * Madvoc INIT handler.
	 * Executed during initialisation phase: default Madvoc components are registered, but user components are not.
	 * Default settings are applied, too.
	 * <p>
	 * DO NOT DEPEND ON CONFIGURATION AND OTHER COMPONENTS UNLESS YOU ARE SURE THEY ARE NOT GOING TO BE OVERWRITTEN.
	 */
	public interface Init {
		/**
		 * Invokes Madvoc initialization.
		 * @see MadvocListener
		 */
		void init();
	}

	/**
	 * Madvoc READY handler.
	 * Invoked when all initialization is done. It is assumed that ALL components are registered.
	 * This phase is used for running any initialization that depends on the complete configuration.
	 */
	public interface Ready {
		/**
		 * Invoked when Madvoc is ready.
		 */
		void ready();
	}

	/**
	 *  Madvoc STOP handler. Invoked on Madvoc shutdown.
	 */
	public interface Stop {
		/**
		 * Invoked on Madvoc shutdown.
		 */
		void stop();
	}

}

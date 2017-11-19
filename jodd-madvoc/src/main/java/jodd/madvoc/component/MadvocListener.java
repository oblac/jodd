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
 * Madvoc events.
 */
public interface MadvocListener {

	Class[] ALL_TYPES = new Class[] {Init.class, Start.class, Ready.class, Stop.class};

	void onEvent();

	/**
	 * Madvoc INIT handler.
	 * Executed during initialisation phase: default Madvoc components are registered, but user components are not.
	 * Default settings are applied, too.
	 * <p>
	 * DO NOT DEPEND ON CONFIGURATION AND OTHER COMPONENTS UNLESS YOU ARE SURE THEY ARE NOT GOING TO BE OVERWRITTEN.
	 */
	interface Init extends MadvocListener {}

	/**
	 * Madvoc START handler.
	 * All components are loaded. User for registering user actions.
	 */
	interface Start extends MadvocListener {}

	/**
	 * Madvoc READY handler.
	 * Invoked when all initialization is done: when both madvoc and user components and actions are loaded.
	 * This phase is used for running any initialization that depends on the complete configuration.
	 */
	interface Ready extends MadvocListener {}

	/**
	 *  Madvoc STOP handler. Invoked on Madvoc shutdown.
	 */
	interface Stop extends MadvocListener {}

}

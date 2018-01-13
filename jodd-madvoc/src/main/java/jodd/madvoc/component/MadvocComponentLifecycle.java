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

import jodd.madvoc.MadvocException;

/**
 * Madvoc component lifecycle events.
 */
public class MadvocComponentLifecycle {

	/**
	 * Madvoc <b>INIT</b> handler.
	 * Components are being registered. Should not request for an instance during this phase, since dependencies
	 * may still not be registered or updated. May be used for registration of additional components.
	 */
	@FunctionalInterface
	public interface Init {
		void init();
	}

	/**
	 * Madvoc <b>START</b> handler.
	 * All components are registered. Web application is being loaded. Should not use any web application
	 * component (action, filter, interceptor, result...) during this phase. New web application components
	 * may be registered.
	 */
	@FunctionalInterface
	public interface Start {
		void start();
	}

	/**
	 * Madvoc <b>READY</b> handler.
	 * Initialization is done: everything is registered.
	 * This phase is used for running any initialization that depends on the complete configuration.
	 */
	@FunctionalInterface
	public interface Ready {
		void ready();
	}

	/**
	 *  Madvoc STOP handler. Invoked on Madvoc shutdown.
	 */
	@FunctionalInterface
	public interface Stop {
		void stop();
	}

	/**
	 * Invoke the listener based on type.
	 * Not very OOP, but works.
	 */
	public static void invoke(final Object listener, final Class listenerType) {
		if (listenerType == Init.class) {
			((Init) listener).init();
			return;
		}
		if (listenerType == Start.class) {
			((Start) listener).start();
			return;
		}
		if (listenerType == Ready.class) {
			((Ready) listener).ready();
			return;
		}
		if (listenerType == Stop.class) {
			((Stop) listener).stop();
			return;
		}
		throw new MadvocException("Invalid listener");
	}



}

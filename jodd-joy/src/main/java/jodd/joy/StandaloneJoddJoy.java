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

package jodd.joy;

import jodd.jtx.JtxPropagationBehavior;
import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;

import java.util.function.Consumer;

/**
 * Standalone runner for Joy web application.
 */
public class StandaloneJoddJoy {

	/**
	 * Runs JOY in standalone mode, with only backend.
	 */
	public void runJoy(final Consumer<JoddJoyRuntime> consumer) {
		final JoddJoy joddJoy = new JoddJoy();

		final JoddJoyRuntime joyRuntime = joddJoy.startOnlyBackend();

		joddJoy.withDb(joyDb -> setJtxManager(joyRuntime.getJtxManager()));

		final JtxTransaction tx = startRwTx();
		final Print print = new Print();
		try {
			print.line("START", 80);
			print.newLine();

			consumer.accept(joyRuntime);

			print.newLine();
			print.line("END", 80);

			if (tx != null) {
				tx.commit();
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
		}

		joddJoy.stop();
	}

	// ---------------------------------------------------------------- jtx

	protected JtxTransactionManager jtxManager;

	/**
	 * Sets transaction manager.
	 */
	private void setJtxManager(final JtxTransactionManager jm) {
		jtxManager = jm;
	}

	/**
	 * Starts new read/write transaction in PROPAGATION_REQUIRED mode.
	 */
	private JtxTransaction startRwTx() {
		if (jtxManager == null) {
			return null;
		}
		return jtxManager.requestTransaction(new JtxTransactionMode(JtxPropagationBehavior.PROPAGATION_REQUIRED, false));
	}

}
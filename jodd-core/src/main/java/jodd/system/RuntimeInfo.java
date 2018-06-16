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

package jodd.system;

import jodd.util.Format;

import java.lang.management.ManagementFactory;

abstract class RuntimeInfo extends OsInfo {

	private Runtime runtime = Runtime.getRuntime();

	/**
	 * Returns MAX memory.
	 */
	public final long getMaxMemory(){
		return runtime.maxMemory();
	}

	/**
	 * Returns TOTAL memory.
	 */
	public final long getTotalMemory(){
		return runtime.totalMemory();
	}

	/**
	 * Returns FREE memory.
	 */
	public final long getFreeMemory(){
		return runtime.freeMemory();
	}

	/**
	 * Returns usable memory.
	 */
	public final long getAvailableMemory(){
		return runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
	}

	/**
	 * Returns PID of current Java process.
	 */
	public final long getCurrentPID() {
		return Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
	}

	@Override
	public String toString() {
		return  super.toString() +
				"\nMax memory:              " + Format.humanReadableByteCount(getMaxMemory(), false) +
				"\nTotal memory:            " + Format.humanReadableByteCount(getTotalMemory(), false) +
				"\nFree memory:             " + Format.humanReadableByteCount(getFreeMemory(), false) +
				"\nAvailableMemory memory:  " + Format.humanReadableByteCount(getAvailableMemory(), false) +
				"\nProcess ID (PID):        " + getCurrentPID();
	}

}

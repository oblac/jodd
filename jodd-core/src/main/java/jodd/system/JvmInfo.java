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

abstract class JvmInfo extends JavaInfo {

	private final String JAVA_VM_NAME = SystemUtil.get("java.vm.name");
	private final String JAVA_VM_VERSION = SystemUtil.get("java.vm.version");
	private final String JAVA_VM_VENDOR = SystemUtil.get("java.vm.vendor");
	private final String JAVA_VM_INFO = SystemUtil.get("java.vm.info");
	private final String JAVA_VM_SPECIFICATION_NAME = SystemUtil.get("java.vm.specification.name");
	private final String JAVA_VM_SPECIFICATION_VERSION = SystemUtil.get("java.vm.specification.version");
	private final String JAVA_VM_SPECIFICATION_VENDOR = SystemUtil.get("java.vm.specification.vendor");

	/**
	 * Returns JVM name.
	 */
	public final String getJvmName() {
		return JAVA_VM_NAME;
	}

	/**
	 * Returns JVM version.
	 */
	public final String getJvmVersion() {
		return JAVA_VM_VERSION;
	}

	/**
	 * Returns VM vendor.
	 */
	public final String getJvmVendor() {
		return JAVA_VM_VENDOR;
	}

	/**
	 * Returns additional VM information.
	 */
	public final String getJvmInfo() {
		return JAVA_VM_INFO;
	}

	public final String getJvmSpecificationName() {
		return JAVA_VM_SPECIFICATION_NAME;
	}

	public final String getJvmSpecificationVersion() {
		return JAVA_VM_SPECIFICATION_VERSION;
	}

	public final String getJvmSpecificationVendor() {
		return JAVA_VM_SPECIFICATION_VENDOR;
	}

	@Override
	public String toString() {
		return  super.toString() +
				"\nJVM name:          " + getJvmName() +
				"\nJVM version:       " + getJvmVersion() +
				"\nJVM vendor:        " + getJvmVendor() +
				"\nJVM info:          " + getJvmInfo() +
				"\nJVM spec. name:    " + getJvmSpecificationName() +
				"\nJVM spec. version: " + getJvmSpecificationVersion() +
				"\nJVM spec. vendor:  " + getJvmSpecificationVendor();
	}

}

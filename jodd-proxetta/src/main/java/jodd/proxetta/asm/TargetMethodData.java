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

package jodd.proxetta.asm;

import static jodd.proxetta.JoddProxetta.methodDivider;

import java.util.List;

/**
 * Holds data for target method that should be wrapped.
 */
final class TargetMethodData {

	final MethodSignatureVisitor msign;
	final String methodName;
	final ProxyAspectData[] proxyData;            // list of ***only*** applied proxies for the target

	TargetMethodData(MethodSignatureVisitor msign, List<ProxyAspectData> aspectList) {
		this.msign = msign;
		this.methodName = msign.getMethodName();
		this.proxyData = aspectList.toArray(new ProxyAspectData[aspectList.size()]);
	}

	// ---------------------------------------------------------------- current

	int currentIndex;

	/**
	 * Selects current proxy.
	 */
	void selectCurrentProxy(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	ProxyAspectData getProxyData() {
		return proxyData[currentIndex];
	}

	// ---------------------------------------------------------------- method names

	private String methodName(int index) {
		return methodName + methodDivider + proxyData[index].aspectIndex;
	}

	/**
	 * Returns the first method name.
	 */
	String firstMethodName() {
		return methodName(0);
	}

	/**
	 * Returns current method name.
	 */
	String methodName() {
		return methodName(currentIndex);
	}

	/**
	 * Returns <code>true</code> for last method in chain.
	 */
	boolean isLastMethodInChain() {
		return currentIndex == (proxyData.length - 1);
	}

	/**
	 * Returns next method name.
	 */
	String nextMethodName() {
		return methodName(currentIndex + 1);
	}

}

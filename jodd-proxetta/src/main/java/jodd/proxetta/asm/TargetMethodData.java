// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import static jodd.proxetta.asm.ProxettaNaming.METHOD_DIVIDER;

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
		return methodName + METHOD_DIVIDER + proxyData[index].aspectIndex;
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

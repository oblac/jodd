// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

/**
 * Proxetta creates <b>the fastest</b> proxy subclasses in an easy, java-friendly way.
 * <p>
 * Proxy is defined by an aspect: advice and pointcut. Proxetta proxy implementation is all about
 * wrapping target methods at defined pointcuts with advice's methods. Advice's methods intercepts
 * target method invocation.
 * <p>
 * Proxetta advices are plain java code that use special 'macro'-alike static methods from <code>ProxyTarget</code> class.
 * These static method invocations will be <b>replaced</b> with appropriate target invocation, once when proxy
 * subclass is created. This unique feature makes generated code not to use reflections, and therefore, very fast.
 */
package jodd.proxetta;
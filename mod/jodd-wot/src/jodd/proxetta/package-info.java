// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

/**
 * <html>
 * <body>
 * <p>
 * Proxetta creates <em><strong>the fastest</strong></em> proxy subclasses in an easy, java-friendly way.
 * </p>
 * <p>
 * Proxy is defined by an aspect: advice and pointcut. Proxetta proxy implementation is all about
 * wrapping target methods at defined pointcuts with advice's methods. Advice's methods intercepts
 * target method invocation.
 * </p>
 * <p>
 * Proxetta advices are plain java code that use special 'macro'-alike static methods from <code>ProxyTarget</code> class.
 * These static method invocations will be <strong>replaced</strong> with appropriate target invocation, once when proxy
 * subclass is created. This unique feature makes generated code not to use reflections, and therefore, very fast.
 * </p>
 * </body>
 * </html>
 */
package jodd.proxetta;
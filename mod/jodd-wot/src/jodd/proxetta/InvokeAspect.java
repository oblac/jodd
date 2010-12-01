// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Invoke aspect defines method pointcuts that should be replaced and
 * their advice replacements.
 */
public abstract class InvokeAspect {

	/**
	 * Determines if some method should be scanned for pointcuts.
	 * Returns <code>true</code> if method should be scanned.
	 */
	public boolean apply(MethodInfo methodInfo) {
		return true;
	}


	/**
	 * Defines method invocation pointcut and returns replacement advice.
	 * Returns <code>null</code> if method doesn't have to be replaced at all.
	 * <p>
	 * Special case is <code>new</code> instruction. Since <code>new</code> opcode
	 * appears in the bytecode before actual constructor invocation,
	 * description of <code>InvokeInfo</code> is unknown. Therefore, for each
	 * constructor that will be replaced, there must be an advice replacement method
	 * with the same description.
	 */
	public abstract InvokeReplacer pointcut(InvokeInfo invokeInfo);

}

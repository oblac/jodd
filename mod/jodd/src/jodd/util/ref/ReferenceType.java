// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.ref;

/**
 * Reference type.
 */
public enum ReferenceType {

	/**
	 * Prevents referent from being reclaimed by the garbage collector.
	 */
	STRONG,

	/**
	 * Referent reclaimed in an LRU fashion when the VM runs low on memory and
	 * no strong references exist.
	 *
	 * @see java.lang.ref.SoftReference
	 */
	SOFT,

	/**
	 * Referent reclaimed when no strong or soft references exist.
	 *
	 * @see java.lang.ref.WeakReference
	 */
	WEAK,

	/**
	 * Similar to weak references except the garbage collector doesn't actually
	 * reclaim the referent. More flexible alternative to finalization.
	 *
	 * @see java.lang.ref.PhantomReference
	 */
	PHANTOM,
}

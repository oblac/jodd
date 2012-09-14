// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;


import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Bags are a specialization of unordered collections that explicitly allow duplicates.
 */
public interface Bag<E> extends Collection<E> {

    /**
     * Returns the number of occurrences (cardinality) of the given
     * object currently in the bag. If the object does not exist in the
     * bag, return 0.
     *
     * @param object  the object to search for
     * @return the number of occurrences of the object, zero if not found
     */
    int getCount(Object object);

    /**
     * <i>(Violation)</i>
     * Adds one copy the specified object to the Bag.
     * <p>
     * If the object is already in the {@link #uniqueSet()} then increment its
     * count as reported by {@link #getCount(Object)}. Otherwise add it to the
     * {@link #uniqueSet()} and report its count as 1.
     * <p>
     * Since this method always increases the size of the bag,
     * according to the {@link Collection#add(Object)} contract, it
     * should always return <code>true</code>.  Since it sometimes returns
     * <code>false</code>, this method violates the contract.
     *
     * @param object  the object to add
     * @return <code>true</code> if the object was not already in the <code>uniqueSet</code>
     */
    boolean add(E object);

    /**
     * Adds <code>nCopies</code> copies of the specified object to the Bag.
     * <p>
     * If the object is already in the {@link #uniqueSet()} then increment its
     * count as reported by {@link #getCount(Object)}. Otherwise add it to the
     * {@link #uniqueSet()} and report its count as <code>nCopies</code>.
     *
     * @param object  the object to add
     * @param nCopies  the number of copies to add
     * @return <code>true</code> if the object was not already in the <code>uniqueSet</code>
     */
    boolean add(E object, int nCopies);

    /**
     * <i>(Violation)</i>
     * Removes all occurrences of the given object from the bag.
     * <p>
     * This will also remove the object from the {@link #uniqueSet()}.
     * <p>
     * According to the {@link Collection#remove(Object)} method,
     * this method should only remove the <i>first</i> occurrence of the
     * given object, not <i>all</i> occurrences.
     *
     * @return <code>true</code> if this call changed the collection
     */
    boolean remove(Object object);

    /**
     * Removes <code>nCopies</code> copies of the specified object from the Bag.
     * <p>
     * If the number of copies to remove is greater than the actual number of
     * copies in the Bag, no error is thrown.
     *
     * @param object  the object to remove
     * @param nCopies  the number of copies to remove
     * @return <code>true</code> if this call changed the collection
     */
    boolean remove(Object object, int nCopies);

    /**
     * Returns a {@link Set} of unique elements in the Bag.
     * <p>
     * Uniqueness constraints are the same as those in {@link java.util.Set}.
     *
     * @return the Set of unique Bag elements
     */
    Set<E> uniqueSet();

    /**
     * Returns the total number of items in the bag across all types.
     *
     * @return the total size of the Bag
     */
    int size();

    /**
     * <i>(Violation)</i>
     * Returns <code>true</code> if the bag contains all elements in
     * the given collection, respecting cardinality.  That is, if the
     * given collection <code>coll</code> contains <code>n</code> copies
     * of a given object, calling {@link #getCount(Object)} on that object must
     * be <code>&gt;= n</code> for all <code>n</code> in <code>coll</code>.
     * <p>
     * The {@link Collection#containsAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * return true if the bag contains at least one of every object contained
     * in the given collection.
     *
     * @param coll  the collection to check against
     * @return <code>true</code> if the Bag contains all the collection
     */
    boolean containsAll(Collection<?> coll);

    /**
     * <i>(Violation)</i>
     * Remove all elements represented in the given collection,
     * respecting cardinality.  That is, if the given collection
     * <code>coll</code> contains <code>n</code> copies of a given object,
     * the bag will have <code>n</code> fewer copies, assuming the bag
     * had at least <code>n</code> copies to begin with.
     *
     * <P>The {@link Collection#removeAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * remove <i>all</i> occurrences of every object contained in the
     * given collection.
     *
     * @param coll  the collection to remove
     * @return <code>true</code> if this call changed the collection
     */
    boolean removeAll(Collection<?> coll);

    /**
     * <i>(Violation)</i>
     * Remove any members of the bag that are not in the given
     * collection, respecting cardinality.  That is, if the given
     * collection <code>coll</code> contains <code>n</code> copies of a
     * given object and the bag has <code>m &gt; n</code> copies, then
     * delete <code>m - n</code> copies from the bag.  In addition, if
     * <code>e</code> is an object in the bag but
     * <code>!coll.contains(e)</code>, then remove <code>e</code> and any
     * of its copies.
     *
     * <P>The {@link Collection#retainAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * keep <i>all</i> occurrences of every object contained in the
     * given collection.
     *
     * @param coll  the collection to retain
     * @return <code>true</code> if this call changed the collection
     */
    boolean retainAll(Collection<?> coll);

    /**
     * Returns an {@link Iterator} over the entire set of members,
     * including copies due to cardinality. This iterator is fail-fast
     * and will not tolerate concurrent modifications.
     *
     * @return iterator over all elements in the Bag
     */
    Iterator<E> iterator();

}
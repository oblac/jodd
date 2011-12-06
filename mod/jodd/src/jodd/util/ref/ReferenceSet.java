// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.ref;

import jodd.util.collection.SetMapAdapter;


/**
 * Reference set build over {@link jodd.util.ref.ReferenceMap}.
 */
public class ReferenceSet<E> extends SetMapAdapter<E> {


	public ReferenceSet(ReferenceType valueReferenceType) {
		super(new ReferenceMap<E, Object>(valueReferenceType, ReferenceType.STRONG));
	}

}

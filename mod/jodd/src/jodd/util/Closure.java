// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Defines a functor interface implemented by classes that do something.
 */
public interface Closure<IN, RESULT> {

    /**
     * Performs an action on the specified input object, returning the result.
     */
    RESULT execute(IN input);
}

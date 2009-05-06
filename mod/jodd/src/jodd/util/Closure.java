// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Defines a functor interface implemented by classes that do something.
 */
public interface Closure<T> {

    /**
     * Performs an action on the specified input object.
     */
    void execute(T input);	
}

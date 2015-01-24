package jodd.mutable;

/**
 * Lazy immutable {@link jodd.mutable.ValueHolder}.
 * @param <T> value type
 */
public interface ValueProvider<T> {

    /**
     * Returns value.
     */
    T getValue();
}
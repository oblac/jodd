package jodd.mutable;

/**
 * Lazy immutable value holder
 * @param <T> Value type
 */
public interface IsValue<T>
{
    T getValue();
}
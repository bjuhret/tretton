package org.example.misc;

import java.util.Objects;

/**
 * Represents a generic immutable pair of values.
 *
 * @param <T1> The type of the first value.
 * @param <T2> The type of the second value.
 */
public class Pair<T1, T2> {

    // Member variables
    private final T1 first;
    private final T2 second;

    /**
     * Constructs a Pair instance with the specified values.
     *
     * @param first The first value.
     * @param second The second value.
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first value in the pair.
     *
     * @return The first value.
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Gets the second value in the pair.
     *
     * @return The second value.
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * Checks if this pair is equal to another object.
     *
     * @param o The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(getFirst(), pair.getFirst()) && Objects.equals(getSecond(), pair.getSecond());
    }

    /**
     * Computes the hash code of this pair.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirst(), getSecond());
    }
}

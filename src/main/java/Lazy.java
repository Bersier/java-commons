import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents a lazy value. It only gets computed when needed for the first time.
 *
 * @author stephanebersier
 */
@SuppressWarnings("unused")
public final class Lazy<T> implements Supplier<T> {
    private Supplier<T> supplier;
    private T value;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * @param supplier used to compute the value; may not return null
     * @param <U>      the type of the value
     * @return a new lazy value
     */
    public static <U> Lazy<U> of(Supplier<U> supplier) {
        return new Lazy<>(supplier);
    }

    /**
     * The value gets computed only on the first call to this function, after which it is recalled from memory.
     *
     * @return the value
     */
    @Override
    public T get() {
        if (!isComputed()) {
            value = supplier.get();
            supplier = null;
        }
        return value;
    }

    /**
     * @return the value only if it has already been computed
     */
    public Optional<T> getIfComputed() {
        return Optional.ofNullable(value);
    }

    /**
     * @return whether the lazy value has already been computed (triggered through the first call to {@link Lazy#get()})
     */
    public boolean isComputed() {
        return value != null;
    }

    /**
     * Two Lazy values are equal either if both have     been computed and their values agree,
     * or if both have not been computed and share the same supplier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lazy)) {
            return false;
        }

        final Lazy<?> that = (Lazy<?>) o;

        if (this.isComputed()) {
            return that.isComputed() && this.get().equals(that.get());
        } else {
            return !that.isComputed() && this.supplier.equals(that.supplier);
        }
    }

    @Override
    public int hashCode() {
        return isComputed() ? get().hashCode() : supplier.hashCode();
    }

    @Override
    public String toString() {
        return "Lazy(" + getIfComputed().map(T::toString).orElse("_") + ")";
    }
}
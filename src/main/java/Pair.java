import java.io.Serializable;
import java.util.function.Function;

/**
 * @author stephanebersier
 */
@SuppressWarnings("unused")
public final class Pair<A, B> implements Serializable {

    private final A first;
    private final B second;

    private Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    /**
     * Pairs two functions together.
     */
    public static <X, A, B> Function<X, Pair<A, B>> u(Function<X, A> f1, Function<X, B> f2) {
        return x -> Pair.of(f1.apply(x), f2.apply(x));
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public <C, D> Pair<C, D> map(Function<A, C> f1, Function<B, D> f2) {
        return Pair.of(f1.apply(first), f2.apply(second));
    }

    public <C, D> Pair<C, D> map(Pair<Function<A, C>, Function<B, D>> fs) {
        return Pair.of(fs.first().apply(first), fs.second().apply(second));
    }

    /**
     * @return a new pair where the order of the elements is inverted
     */
    public Pair<B, A> switched() {
        return Pair.of(second, first);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }

        final Pair<?, ?> that = (Pair<?, ?>) o;

        return (this.first).equals(that.first) && (this.second).equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + first() + ", " + second() + ')';
    }
}
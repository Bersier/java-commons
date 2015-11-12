import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class Either<L, R> {
    private final L l;
    private final R r;

    private Either(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public static <L, R> Either<L, R> inl(L l) {
        return new Either<>(l, null);
    }

    public static <L, R> Either<L, R> inr(R r) {
        return new Either<>(null, r);
    }

    private static void example() {
        final Either<Integer, String> i = inl(1);
        final Either<Integer, String> j = inr("division by zero");

        i.match(
                (Integer n) -> System.out.println("Number: " + n),
                (String s) -> System.out.println("Error:  " + s)
        );
    }

    public <A> A match(Function<L, A> lf, Function<R, A> rf) {
        return r == null ? lf.apply(this.l) : rf.apply(r);
    }

    public void match(Consumer<L> lc, Consumer<R> rc) {
        if (r == null) lc.accept(this.l);
        else rc.accept(r);
    }

    public <LOut, ROut> Either<LOut, ROut> map(Function<L, LOut> lf, Function<R, ROut> rf) {
        return r == null ? inl(lf.apply(this.l)) : inr(rf.apply(r));
    }
}
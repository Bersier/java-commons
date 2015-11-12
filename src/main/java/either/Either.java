package either;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface Either<L, R> {

    static <L, R> Either<L, R> inl(L l) {
        return new Left<>(l);
    }

    static <L, R> Either<L, R> inr(R r) {
        return new Right<>(r);
    }

    <A> A match(Function<L, A> lf, Function<R, A> rf);

    void match(Consumer<L> lc, Consumer<R> rc);

    <LOut, ROut> Either<LOut, ROut> map(Function<L, LOut> lf, Function<R, ROut> rf);
}

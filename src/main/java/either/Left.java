package either;

import java.util.function.Consumer;
import java.util.function.Function;

final class Left<L, R> implements Either<L, R> {
    private final L l;

    Left(L l) {
        this.l = l;
    }

    @Override
    public <A> A match(Function<L, A> lf, Function<R, A> rf) {
        return lf.apply(this.l);
    }

    @Override
    public void match(Consumer<L> lc, Consumer<R> rc) {
        lc.accept(this.l);
    }

    @Override
    public <LOut, ROut> Either<LOut, ROut> map(Function<L, LOut> lf, Function<R, ROut> rf) {
        return Either.inl(lf.apply(this.l));
    }
}
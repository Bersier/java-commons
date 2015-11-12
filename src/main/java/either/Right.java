package either;

import java.util.function.Consumer;
import java.util.function.Function;

final class Right<L, R> implements Either<L, R> {
    private final R r;

    Right(R r) {
        this.r = r;
    }

    @Override
    public <A> A match(Function<L, A> lf, Function<R, A> rf) {
        return rf.apply(r);
    }

    @Override
    public void match(Consumer<L> lc, Consumer<R> rc) {
        rc.accept(r);
    }

    @Override
    public <LOut, ROut> Either<LOut, ROut> map(Function<L, LOut> lf, Function<R, ROut> rf) {
        return Either.inr(rf.apply(r));
    }
}
package net.exoego.stream;

import java.util.Iterator;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.BiFunction;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final class FlatMappers {
    private FlatMappers() {}

    /**
     * <pre class="java">Example:
     * <code class="java">streamA.flatMap(zipper(streamB, (a, b) -&gt; a.foo(b));</code></pre>
     *
     * @param <A>      the
     * @param <B>      the
     * @param <R>      the
     * @param second   The second sequence to be merged
     * @param biMapper A function that specifies how to merge the elements from the two sequences.
     * @return a
     * @throws NullPointerException if any of arguments is null.
     */
    public static <A, B, R> Function<A, Stream<R>> zipper(final Stream<B> second, final BiFunction<A, B, R> biMapper) {
        Objects.requireNonNull(second, "second is null");
        Objects.requireNonNull(biMapper, "biMapper is null");
        Iterator<B> iterator = second.iterator();
        return e -> iterator.hasNext() ? Stream.of(biMapper.apply(e, iterator.next())) : Stream.empty();
    }

    public static IntFunction<IntStream> zipper(final IntStream second, final IntBinaryOperator biMapper) {
        Objects.requireNonNull(second, "second is null");
        Objects.requireNonNull(biMapper, "biMapper is null");
        PrimitiveIterator.OfInt iterator = second.iterator();
        return e -> iterator.hasNext() ? IntStream.of(biMapper.applyAsInt(e, iterator.next())) : IntStream.empty();
    }

    public static LongFunction<LongStream> zipper(final LongStream second, final LongBinaryOperator biMapper) {
        Objects.requireNonNull(second, "second is null");
        Objects.requireNonNull(biMapper, "biMapper is null");
        PrimitiveIterator.OfLong iterator = second.iterator();
        return e -> iterator.hasNext() ? LongStream.of(biMapper.applyAsLong(e, iterator.next())) : LongStream.empty();
    }

    /**
     * <p>Returns a {@code Function} that returns a stream of an element if the element is instance of
     * {@code givenType}, otherwise an empty sequence.</p>
     * <pre class="java">Example:
     * <code class="java">Stream&lt;Number&gt; mixed = ...;
     * Stream&lt;BigInteger&gt; filtered = mixed.flatMap(ofType(BigInteger.class));</code></pre>
     *
     * @param givenType a Class instance to filter stream.
     * @return a {@code Function} that returns a stream of an element if the element is instance of
     * {@code givenType}, otherwise an empty sequence.</p>
     * @throws java.lang.NullPointerException if {@code givenType} is null
     */
    public static <T> Function<? super Object, Stream<T>> ofType(final Class<T> givenType) {
        Objects.requireNonNull(givenType, "givenType is null");
        return e -> givenType.isInstance(e) ? Stream.of(givenType.cast(e)) : Stream.empty();
    }

    public static DoubleFunction<DoubleStream> zipper(final DoubleStream second, final DoubleBinaryOperator biMapper) {
        Objects.requireNonNull(second, "second is null");
        Objects.requireNonNull(biMapper, "biMapper is null");
        PrimitiveIterator.OfDouble iterator = second.iterator();
        return e -> iterator.hasNext()
                ? DoubleStream.of(biMapper.applyAsDouble(e, iterator.next()))
                : DoubleStream.empty();
    }
}

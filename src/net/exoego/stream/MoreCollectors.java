package net.exoego.stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final class MoreCollectors {
    private MoreCollectors() {}

    /**
     * <p>Safer shortcut of {@code Collectors.mapping(mapper, Collectors.toList())}</p>
     *
     * @param mapper a function to be applied to the input elements.
     * @param <T>    the type of the input elements.
     * @param <R>    the type of elements accepted by list.
     * @return a {@code Collector} which collects all the input elements into a {@code List},
     * with applying a mapper {@code Function} to each elements, in encounter order.
     * @throws java.lang.NullPointerException if mapper is null.
     * @see Collectors#mapping(java.util.function.Function, java.util.stream.Collector)
     */
    public static <T, R> Collector<T, ?, List<R>> toListMapped(final Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return Collectors.mapping(mapper, Collectors.toList());
    }

    /**
     * <p>Safer shortcut of {@code Collectors.mapping(mapper, Collectors.toSet())}</p>
     *
     * @param mapper a function to be applied to the input elements.
     * @param <T>    the type of the input elements.
     * @param <R>    the type of elements accepted by set.
     * @return a {@code Collector} which collects all the input elements into a {@code Set},
     * with applying a mapper {@code Function} to each elements, in encounter order.
     * @throws java.lang.NullPointerException if mapper is null.
     * @see Collectors#mapping(java.util.function.Function, java.util.stream.Collector)
     */
    public static <T, R> Collector<T, ?, Set<R>> toSetMapped(final Function<T, R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return Collectors.mapping(mapper, Collectors.toSet());
    }

    /**
     * <p>Returns a {@code Collector} that collects all the input elements into a new {@code Stream}.</p>
     *
     * <p>Example usage of the returned {@code Collector} is to use with
     * {@link Collectors#groupingBy(java.util.function.Function, java.util.stream.Collector)}</p>
     * <pre class="java"><code class="java">Map&lt;Foo, Stream&lt;A&gt;&gt; group = stream.collect(groupingBy(a -&gt; a.foo(), toStream()));</code></pre>
     *
     * @param <T> the type of the input elements.
     * @return a {@code Collector} which collects all the input elements into a {@code Stream}.
     */
    public static <T> Collector<T, ?, Stream<T>> toStream() {
        return Collector.of(Stream::builder, (builder, e) -> builder.accept(e), (left, right) -> {
            right.build().forEach(left::accept);
            return left;
        }, (Stream.Builder<T> builder) -> (builder.build()));
    }

    /**
     * <p>Safer shortcut of {@code Collectors.mapping(mapper, toStream())}</p>
     *
     * @param <T>      the type of the input elements.
     * @param <R>      the type of the resulting collector.
     * @param finisher a function to be applied to the final result of the collected {@code Stream}.
     * @return a collector which collects all the elements into a {@code Stream}, followed by an additional finishing step.
     */
    public static <T, R> Collector<T, ?, R> toStreamThen(final Function<Stream<T>, R> finisher) {
        Objects.requireNonNull(finisher, "finisher is null");
        return Collectors.collectingAndThen(toStream(), finisher);
    }

    public static <T> Collector<T, ?, IntStream> toIntStream(final ToIntFunction<T> keyMapper) {
        return toIntStreamThen(keyMapper, Function.identity());
    }

    public static <T, R> Collector<T, ?, R> toIntStreamThen(
            final ToIntFunction<T> keyMapper, final Function<IntStream, R> resultMapper) {
        Objects.requireNonNull(keyMapper, "keyMapper is null");
        Objects.requireNonNull(resultMapper, "resultMapper is null");
        return Collector.of(IntStream::builder, (builder, e) -> builder.accept(keyMapper.applyAsInt(e)), (l, r) -> {
            r.build().forEach(l::accept);
            return l;
        }, builder -> resultMapper.apply(builder.build()));
    }

    public static <T> Collector<T, ?, LongStream> toLongStream(final ToLongFunction<T> keyMapper) {
        Objects.requireNonNull(keyMapper, "keyMapper is null");
        return Collector.of(LongStream::builder, (builder, e) -> builder.accept(keyMapper.applyAsLong(e)), (l, r) -> {
            r.build().forEach(l::accept);
            return l;
        }, LongStream.Builder::build);
    }

    public static <T> Collector<T, ?, DoubleStream> toDoubleStream(final ToDoubleFunction<T> keyMapper) {
        Objects.requireNonNull(keyMapper, "keyMapper is null");
        return Collector.of(DoubleStream::builder,
                            (builder, e) -> builder.accept(keyMapper.applyAsDouble(e)),
                            (l, r) -> {
                                r.build().forEach(l::accept);
                                return l;
                            },
                            DoubleStream.Builder::build
                           );
    }

    private static <T, K, A, V, M extends Map<K, V>> Collector<T, ?, Stream<Entry<K, V>>> toGroupedEntries(
            final Function<? super T, ? extends K> keyMapper,
            final Collector<? super T, A, V> downstream,
            final Supplier<M> mapFactory) {
        Objects.requireNonNull(keyMapper, "keyMapper is null");
        Objects.requireNonNull(mapFactory, "mapFactory is null");
        Objects.requireNonNull(downstream, "downstream is null");

        return Collectors.collectingAndThen(Collectors.groupingBy(keyMapper, mapFactory, downstream),
                                            map -> map.entrySet().stream());
    }

    public static <T, K, A, V> Collector<T, ?, Stream<Entry<K, V>>> toGroupedEntries(
            final Function<T, ? extends K> keyMapper, final Collector<T, A, V> downstream) {
        return toGroupedEntries(keyMapper, downstream, HashMap::new);
    }

    public static <T, K> Collector<T, ?, Stream<Entry<K, List<T>>>> toGroupedEntries(final Function<T, ? extends K> keyMapper) {
        return toGroupedEntries(keyMapper, Collectors.toList());
    }

    public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> toMapFromEntry() {
        return Collectors.toMap(Entry::getKey, Map.Entry::getValue);
    }

    public static <T, K, A, V, M extends Map<K, V>, R> Collector<T, ?, Stream<R>> groupingThenStreaming(
            final Function<? super T, ? extends K> keyMapper,
            final Collector<? super T, A, V> downstream,
            final BiFunction<K, V, R> finisher,
            final Supplier<M> mapFactory) {
        Collector<T, ?, Stream<Entry<K, V>>> groupedEntries = toGroupedEntries(keyMapper, downstream, mapFactory);
        Objects.requireNonNull(finisher, "finisher is null");

        return Collectors.collectingAndThen(groupedEntries, s -> s.map(e -> finisher.apply(e.getKey(), e.getValue())));
    }

    public static <T, K, A, V, R> Collector<T, ?, Stream<R>> groupingThenStreaming(
            final Function<? super T, ? extends K> keyMapper,
            final Collector<? super T, A, V> downstream,
            final BiFunction<? super K, V, R> finisher) {
        return groupingThenStreaming(keyMapper, downstream, finisher, HashMap::new);
    }

    public static <T, K, R> Collector<T, ?, Stream<R>> groupingThenStreaming(
            final Function<? super T, ? extends K> keyMapper, final BiFunction<? super K, List<T>, R> finisher) {
        return groupingThenStreaming(keyMapper, Collectors.toList(), finisher);
    }

    /**
     * <p>Returns a {@code Collector} that filters elements based on the given type and maps the matched elements to the type.</p>
     *
     * @param givenType a Class instance to filter stream.
     * @return a {@code Collector} that filters elements based on the given type and maps the matched elements to the type.
     * @throws java.lang.NullPointerException if {@code givenType} is null
     */
    public static <T> Collector<? super Object, ?, Stream<T>> ofType(final Class<? extends T> givenType) {
        Objects.requireNonNull(givenType, "givenType is null");
        return Collector.of(Stream::builder, (builder, e) -> {
            if (givenType.isInstance(e)) {
                final T cast = givenType.cast(e);
                builder.accept(cast);
            }
        }, (left, right) -> {
            right.build().forEach(left::accept);
            return left;
        }, (Stream.Builder<T> builder) -> (builder.build()));
    }
}

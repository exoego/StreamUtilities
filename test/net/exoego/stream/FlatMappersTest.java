package net.exoego.stream;

import java.util.List;
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

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class FlatMappersTest {
    public static class ZipperForStream {
        @Test
        public void complete_if_same_length() {
            Stream<String> left = Stream.of("a", "b", "c");
            Stream<Integer> right = Stream.of(1, 2, 3);
            Function<String, Stream<String>> zip = FlatMappers.zipper(right, (a, b) -> a + b);
            assertThat(left.flatMap(zip).toArray(String[]::new), is(new String[]{"a1", "b2", "c3"}));
        }

        @Test
        public void quit_in_the_middle_if_right_is_shorter() {
            Stream<String> left = Stream.of("a", "b", "c", "long", "long");
            Stream<Integer> right = Stream.of(1, 2, 3);
            Function<String, Stream<String>> zip = FlatMappers.zipper(right, (a, b) -> a + b);
            assertThat(left.flatMap(zip).toArray(String[]::new), is(new String[]{"a1", "b2", "c3"}));
        }

        @Test
        public void quit_in_the_middle_if_left_is_shorter() {
            Stream<String> left = Stream.of("a", "b", "c");
            Stream<Integer> right = Stream.of(1, 2, 3, 99999, 99999);
            Function<String, Stream<String>> zip = FlatMappers.zipper(right, (a, b) -> a + b);
            assertThat(left.flatMap(zip).toArray(String[]::new), is(new String[]{"a1", "b2", "c3"}));
        }

        @Test
        public void return_empty_if_left_is_empty() {
            Stream<String> left = Stream.empty();
            Stream<Integer> right = Stream.of(1, 2, 3);
            Function<String, Stream<String>> zip = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zip).toArray(String[]::new), is(new String[]{}));
        }

        @Test
        public void return_empty_if_right_is_empty() {
            Stream<String> left = Stream.of("a", "b", "c");
            Stream<Integer> right = Stream.empty();
            Function<String, Stream<String>> zip = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zip).toArray(String[]::new), is(new String[]{}));
        }

        @Test
        public void return_empty_if_both_are_empty() {
            Stream<String> left = Stream.empty();
            Stream<Integer> right = Stream.empty();
            Function<String, Stream<String>> zip = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zip).toArray(String[]::new), is(new String[]{}));
        }

        @Test(expected = NullPointerException.class)
        public void fail_fast_if_null_stream() {
            Stream<String> right = null;
            FlatMappers.zipper(right, (a, b) -> a + b);
        }

        @Test(expected = NullPointerException.class)
        public void fail_fast_if_null_mapper() {
            BiFunction<Object, String, Object> func = null;
            FlatMappers.zipper(Stream.of(""), func);
        }
    }

    public static class ZipperForIntStream {
        @Test
        public void complete_if_same_length() {
            IntStream left = IntStream.of(1, 2, 3, 4, 5);
            IntStream right = IntStream.of(4, 3, 2, 1, 0);
            IntFunction<IntStream> zipper = FlatMappers.zipper(right, (a, b) -> a + b);
            assertThat(left.flatMap(zipper).toArray(), is(new int[]{5, 5, 5, 5, 5}));
        }

        @Test
        public void quit_in_the_middle_if_right_is_shorter() {
            IntStream left = IntStream.of(1, 2, 3, 4, 5, 99, 99);
            IntStream right = IntStream.of(4, 3, 2, 1, 0);
            IntFunction<IntStream> zipper = FlatMappers.zipper(right, (a, b) -> a * b);
            assertThat(left.flatMap(zipper).toArray(), is(new int[]{4, 6, 6, 4, 0}));
        }

        @Test
        public void quit_in_the_middle_if_left_is_shorter() {
            IntStream left = IntStream.of(1, 2, 3, 4, 5);
            IntStream right = IntStream.of(4, 3, 2, 1, 0, 99, 99);
            IntFunction<IntStream> zipper = FlatMappers.zipper(right, (a, b) -> a * b);
            assertThat(left.flatMap(zipper).toArray(), is(new int[]{4, 6, 6, 4, 0}));
        }

        @Test
        public void return_empty_if_left_is_empty() {
            IntStream left = IntStream.empty();
            IntStream right = IntStream.of(4, 3, 2, 1, 0);
            IntFunction<IntStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new int[]{}));
        }

        @Test
        public void return_empty_if_right_is_empty() {
            IntStream left = IntStream.of(1, 2, 3, 4, 5);
            IntStream right = IntStream.empty();
            IntFunction<IntStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new int[]{}));
        }

        @Test
        public void return_empty_if_both_are_empty() {
            IntStream left = IntStream.empty();
            IntStream right = IntStream.empty();
            IntFunction<IntStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new int[]{}));
        }

        @Test(expected = NullPointerException.class)
        public void fail_fast_if_null_stream() {
            IntStream right = null;
            FlatMappers.zipper(right, (a, b) -> a + b);
        }

        @Test(expected = NullPointerException.class)
        public void fail_fast_if_null_mapper() {
            IntBinaryOperator func = null;
            FlatMappers.zipper(IntStream.empty(), func);
        }
    }

    public static class ZipperForLongStream {
        @Test
        public void complete_if_same_length() {
            LongStream left = LongStream.of(1, 2, 3, 4, 5);
            LongStream right = LongStream.of(4, 3, 2, 1, 0);
            LongFunction<LongStream> zipper = FlatMappers.zipper(right, (a, b) -> a + b);
            assertThat(left.flatMap(zipper).toArray(), is(new long[]{5, 5, 5, 5, 5}));
        }

        @Test
        public void quit_in_the_middle_if_right_is_shorter() {
            LongStream left = LongStream.of(1, 2, 3, 4, 5, 99, 99);
            LongStream right = LongStream.of(4, 3, 2, 1, 0);
            LongFunction<LongStream> zipper = FlatMappers.zipper(right, (a, b) -> a * b);
            assertThat(left.flatMap(zipper).toArray(), is(new long[]{4, 6, 6, 4, 0}));
        }

        @Test
        public void quit_in_the_middle_if_left_is_shorter() {
            LongStream left = LongStream.of(1, 2, 3, 4, 5);
            LongStream right = LongStream.of(4, 3, 2, 1, 0, 99, 99);
            LongFunction<LongStream> zipper = FlatMappers.zipper(right, (a, b) -> a * b);
            assertThat(left.flatMap(zipper).toArray(), is(new long[]{4, 6, 6, 4, 0}));
        }

        @Test
        public void return_empty_if_left_is_empty() {
            LongStream left = LongStream.empty();
            LongStream right = LongStream.of(4, 3, 2, 1, 0);
            LongFunction<LongStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new long[]{}));
        }

        @Test
        public void return_empty_if_right_is_empty() {
            LongStream left = LongStream.of(1, 2, 3, 4, 5);
            LongStream right = LongStream.empty();
            LongFunction<LongStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new long[]{}));
        }

        @Test
        public void return_empty_if_both_are_empty() {
            LongStream left = LongStream.empty();
            LongStream right = LongStream.empty();
            LongFunction<LongStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new long[]{}));
        }

        @Test(expected = NullPointerException.class)
        public void fail_fast_if_null_stream() {
            LongStream right = null;
            FlatMappers.zipper(right, (a, b) -> a + b);
        }

        @Test(expected = NullPointerException.class)
        public void fail_fast_if_null_mapper() {
            LongBinaryOperator func = null;
            FlatMappers.zipper(LongStream.empty(), func);
        }
    }

    public static class ZipperForDoubleStream {
        @Test
        public void complete_if_same_length() {
            DoubleStream left = DoubleStream.of(1, 2, 3, 4, 5);
            DoubleStream right = DoubleStream.of(4, 3, 2, 1, 0);
            DoubleFunction<DoubleStream> zipper = FlatMappers.zipper(right, (a, b) -> a + b);
            assertThat(left.flatMap(zipper).toArray(), is(new double[]{5, 5, 5, 5, 5}));
        }

        @Test
        public void quit_in_the_middle_if_right_is_shorter() {
            DoubleStream left = DoubleStream.of(1, 2, 3, 4, 5, 99, 99);
            DoubleStream right = DoubleStream.of(4, 3, 2, 1, 0);
            DoubleFunction<DoubleStream> zipper = FlatMappers.zipper(right, (a, b) -> a * b);
            assertThat(left.flatMap(zipper).toArray(), is(new double[]{4, 6, 6, 4, 0}));
        }

        @Test
        public void quit_in_the_middle_if_left_is_shorter() {
            DoubleStream left = DoubleStream.of(1, 2, 3, 4, 5);
            DoubleStream right = DoubleStream.of(4, 3, 2, 1, 0, 99, 99);
            DoubleFunction<DoubleStream> zipper = FlatMappers.zipper(right, (a, b) -> a * b);
            assertThat(left.flatMap(zipper).toArray(), is(new double[]{4, 6, 6, 4, 0}));
        }

        @Test
        public void return_empty_if_left_is_empty() {
            DoubleStream left = DoubleStream.empty();
            DoubleStream right = DoubleStream.of(4, 3, 2, 1, 0);
            DoubleFunction<DoubleStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new double[]{}));
        }

        @Test
        public void return_empty_if_right_is_empty() {
            DoubleStream left = DoubleStream.of(1, 2, 3, 4, 5);
            DoubleStream right = DoubleStream.empty();
            DoubleFunction<DoubleStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new double[]{}));
        }

        @Test
        public void return_empty_if_both_are_empty() {
            DoubleStream left = DoubleStream.empty();
            DoubleStream right = DoubleStream.empty();
            DoubleFunction<DoubleStream> zipper = FlatMappers.zipper(right, (a, b) -> {
                throw new IllegalStateException("never thrown !!");
            });
            assertThat(left.flatMap(zipper).toArray(), is(new double[]{}));
        }

        @Test(expected = NullPointerException.class)
        public void fail_fast_if_null_stream() {
            DoubleStream right = null;
            FlatMappers.zipper(right, (a, b) -> a + b);
        }

        @Test(expected = NullPointerException.class)
        public void fail_fast_if_null_mapper() {
            DoubleBinaryOperator func = null;
            FlatMappers.zipper(DoubleStream.empty(), func);
        }

        @Test
        public void OfType_extracts_maps_element_to_a_sequence_of_it_if_it_is_instanceof_given_type() {
            List<Object> mixed = asList(0, "one", 0.5, "three", new int[]{3}, new String[]{"a"});
            Stream<String> filtered = mixed.stream().flatMap(FlatMappers.ofType(String.class));
            assertThat(filtered.collect(toList()), is(asList("one", "three")));
        }

        @Test
        public void OfType_also_should_be_applied_to_Stream_of_not_only_Object_but_also_any_type() {
            List<Number> mixed = asList(0, 0.5, 0.3f, 2, 1L, 4L, -1);
            Stream<Integer> filtered = mixed.stream().flatMap(FlatMappers.ofType(Integer.class));
            assertThat(filtered.collect(toList()), is(asList(0, 2, -1)));
        }

        @Test(expected = NullPointerException.class)
        public void OfType_fail_fast_if_null() {
            FlatMappers.ofType(null);
        }
    }
}

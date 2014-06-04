package net.exoego.stream;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static net.exoego.stream.MoreCollectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class MoreCollectorsTest {
    public static class ToListMapped {
        @Test
        public void toListMapped_returns_a_Collector_that_maps_element_and_collects() {
            Stream<String> src = Stream.of("a", "on", "cat", "tri", "by");
            List<Integer> result = src.collect(toListMapped(String::length));
            assertThat(result, is(asList(1, 2, 3, 3, 2)));
        }

        @Test
        public void use_case_difference_between_ToListMapped_and_ToList() {
            List<String> src = asList("a   @  on@ cat @tri @by".split("@"));

            Map<Integer, List<String>> mapThenCollect = src.stream()
                                                           .map(s -> s.trim())
                                                           .collect(groupingBy((String s) -> s.length(), toList()));
            assertThat(mapThenCollect.get(1), is(asList("a")));
            assertThat(mapThenCollect.get(2), is(asList("on", "by")));
            assertThat(mapThenCollect.get(3), is(asList("cat", "tri")));

            Map<Integer, List<String>> mapWhileCollecting = src.stream()
                                                               .collect(groupingBy(s -> s.length(),
                                                                                   toListMapped(String::trim)));
            assertThat(mapWhileCollecting.get(2), is(asList("by")));
            assertThat(mapWhileCollecting.get(3), is(nullValue()));
            assertThat(mapWhileCollecting.get(4), is(asList("a", "on", "tri")));
            assertThat(mapWhileCollecting.get(5), is(asList("cat")));
        }

        @Test(expected = NullPointerException.class)
        public void toListMapped_fail_fast_if_null_mapper_given() {
            toListMapped(null);
        }
    }

    public static class ToSetMapped {
        @Test
        public void toSetMapped_returns_a_Collector_that_maps_element_and_collects() {
            Stream<String> src = Stream.of("a", "on", "cat", "tri", "by");
            Set<Integer> result = src.collect(toSetMapped(String::length));
            assertThat(result, is(new HashSet<>(asList(1, 2, 3))));
        }

        @Test
        public void use_case_difference_between_ToSetMapped_and_ToSet() {
            List<String> src = asList("a", "on", "cat", "tri", "by", "by", "tri", "cat");
            Function<String, String> bracket = s -> "<" + s + ">";

            Map<Integer, Set<String>> mapThenCollect = src.stream()
                                                          .map(bracket)
                                                          .collect(groupingBy((String s) -> s.length(), toSet()));
            assertThat(mapThenCollect.get(3), is(new HashSet<>(asList("<a>"))));
            assertThat(mapThenCollect.get(4), is(new HashSet<>(asList("<on>", "<by>"))));
            assertThat(mapThenCollect.get(5), is(new HashSet<>(asList("<cat>", "<tri>"))));

            Map<Integer, Set<String>> mapWhileCollecting = src.stream()
                                                              .collect(groupingBy(String::length,
                                                                                  toSetMapped(bracket)));
            assertThat(mapWhileCollecting.get(1), is(new HashSet<>(asList("<a>"))));
            assertThat(mapWhileCollecting.get(2), is(new HashSet<>(asList("<on>", "<by>"))));
            assertThat(mapWhileCollecting.get(3), is(new HashSet<>(asList("<cat>", "<tri>"))));
        }

        @Test(expected = NullPointerException.class)
        public void toSetMapped_fail_fast_if_null_mapper_given() {
            toSetMapped(null);
        }
    }

    public static class ToStream {
        @Test
        public void toStream_returns_a_Collector_that_collects_element_into_Stream() {
            Stream<String> src = Stream.of("cat", "dog", "bird", "dragon");
            Stream<String> result = src.collect(toStream());
            assertThat(result.toArray(String[]::new), is(new String[]{"cat", "dog", "bird", "dragon"}));
        }

        @Test
        public void toStream_with_stream_mapper() {
            Stream<String> result = Stream.of("cat", "dog", "bird", "dragon")
                                          .collect(toStreamThen(stream -> stream.map(e -> e.substring(0, 1))));
            assertThat(result.toArray(String[]::new), is(new String[]{"c", "d", "b", "d"}));
        }

        @Test(expected = NullPointerException.class)
        public void toStream_fail_fast_if_null_mapper_given() {
            toStreamThen(null);
        }

        @Test
        public void combination_with_groupingBy() {
            List<String> split = asList("The quick brown fox jumps over the lazy dog".split(" "));
            Map<Integer, Long> map = split.stream()
                                          .collect(groupingBy(String::length,
                                                              toStreamThen((Stream<String> s) -> s.filter(e -> e.contains(
                                                                      "o")).count())));
            assertThat(map.get(2), is(nullValue()));
            assertThat(map.get(3), is(2L)); // fox, dog
            assertThat(map.get(4), is(1L)); // over
            assertThat(map.get(5), is(1L)); // brown
            assertThat(map.get(6), is(nullValue()));
        }
    }

    public static class ToGroupedEntries {
        @Test
        public void group_elements_into_list_by_default() {
            List<String> src = asList("zero", "one", "two", "three", "four", "five", "six");
            Map<Integer, Integer> map = src.stream()
                                           .collect(toGroupedEntries(String::length))
                                           .collect(toMap(e -> e.getKey(), e -> e.getValue().size()));
            assertThat(map.get(2), is(nullValue()));
            assertThat(map.get(3), is(3)); // one, two, six
            assertThat(map.get(4), is(3)); // zero, four, five
            assertThat(map.get(5), is(1)); // three
            assertThat(map.get(6), is(nullValue()));
        }

        @Test
        public void group_elements_using_downstream_collector() {
            List<String> src = asList("zero", "one", "two", "three", "four", "five", "six", "one", "zero", "three");
            Map<Integer, Integer> map = src.stream()
                                           .collect(toGroupedEntries(String::length, toSet()))
                                           .collect(toMap(Entry::getKey, e -> e.getValue().size()));
            assertThat(map.get(2), is(nullValue()));
            assertThat(map.get(3), is(3)); // one, two, six
            assertThat(map.get(4), is(3)); // zero, four, five
            assertThat(map.get(5), is(1)); // three
            assertThat(map.get(6), is(nullValue()));
        }

        @Test
        public void group_elements_using_downstream_collector2() {
            List<String> src = asList("zero", "one", "two", "three", "four", "five", "six");

            Map<Integer, Long> map = src.stream()
                                        .collect(toGroupedEntries(String::length,
                                                                  toStreamThen(s -> s.filter(e -> e.contains("i"))
                                                                                     .count())))
                                        .collect(toMap(Entry::getKey, Entry::getValue));
            assertThat(map.get(2), is(nullValue()));
            assertThat(map.get(3), is(1L)); // six
            assertThat(map.get(4), is(1L)); // five
            assertThat(map.get(5), is(0L)); // <empty>
            assertThat(map.get(6), is(nullValue()));
        }
    }

    public static class GroupingThenStreaming {
        private final BiFunction<Integer, List<String>, List<String>> addKeyAsString = (key, list) -> {
            list.add(0, String.valueOf(key));
            return list;
        };

        @Test
        public void Returns_a_Collector_that_groups_and_streams_the_results() {
            List<String> src = asList("zero", "one", "two", "three", "four", "five", "six");
            List<List<String>> result = src.stream()
                                           .collect(groupingThenStreaming(String::length, addKeyAsString))
                                           .collect(toList());
            assertThat(result,
                       is(asList(asList("3", "one", "two", "six"),
                                 asList("4", "zero", "four", "five"),
                                 asList("5", "three"))));
        }

        @Test
        public void Returns_a_Collector_that_groups_and_streams_the_results_with_downstream_collector() {
            List<String> src = asList("zero", "one", "two", "three", "four", "five", "six");
            List<List<String>> result = src.stream()
                                           .collect(groupingThenStreaming(String::length, toList(), addKeyAsString))
                                           .collect(toList());
            assertThat(result,
                       is(asList(asList("3", "one", "two", "six"),
                                 asList("4", "zero", "four", "five"),
                                 asList("5", "three"))));
        }

        @Test
        public void Returns_a_Collector_that_groups_and_streams_the_results_2() {
            List<String> src = asList("zero", "one", "two", "three", "four", "five", "six");
            List<List<String>> result = src.stream()
                                           .collect(groupingThenStreaming(String::length,
                                                                          toListMapped(s -> s.concat("!")),
                                                                          addKeyAsString))
                                           .collect(toList());
            assertThat(result,
                       is(asList(asList("3", "one!", "two!", "six!"),
                                 asList("4", "zero!", "four!", "five!"),
                                 asList("5", "three!"))));
        }
    }
}

package net.exoego.stream;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class MoreStreamsTest {
    @Test
    public void Grouped_creates_sequence_where_elements_are_grouped_elements_of_base_stream() {
        Stream<String> alphabetChars = asList("abcdefghijklmnopqrstuvwxyz".split("")).stream();
        Stream<List<String>> grouped = MoreStreams.grouped(alphabetChars, 5);
        List<String> result = grouped.map(l -> String.join("", l)).collect(toList());
        assertThat(result, is(asList("abcde", "fghij", "klmno", "pqrst", "uvwxy", "z")));
    }

    @Test
    public void Grouped_sequence_of_single_list_if_group_size_is_greater_than_stream_size() {
        Stream<String> alphabetChars = asList("abcdefghijklmnopqrstuvwxyz".split("")).stream();
        Stream<List<String>> grouped = MoreStreams.grouped(alphabetChars, 1000);
        List<String> result = grouped.map(l -> String.join("", l)).collect(toList());
        assertThat(result, is(asList("abcdefghijklmnopqrstuvwxyz")));
    }

    @Test(expected = NullPointerException.class)
    public void Grouped_fail_fast_if_0_given() {
        MoreStreams.grouped(Stream.empty(), 0);
    }

    @Test(expected = NullPointerException.class)
    public void Grouped_fail_fast_if_null_stream_given() {
        final Stream<Object> empty = null;
        MoreStreams.grouped(empty, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Grouped_fail_fast_if_negative_given() {
        final int r = new Random().nextInt();
        final int negative = (r == Integer.MIN_VALUE) ? r : Math.negateExact(r);
        MoreStreams.grouped(Stream.empty(), negative);
    }
}

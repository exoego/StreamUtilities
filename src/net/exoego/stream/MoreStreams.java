package net.exoego.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class MoreStreams {
    private MoreStreams() {}

    public static <T> Stream<List<T>> grouped(final Stream<T> baseStream, final int groupSize) {
        Objects.requireNonNull(baseStream, "baseStream is null.");
        if (groupSize <= 0) {
            throw new IllegalArgumentException("groupSize must be greater than 0.");
        }
        final Iterator<T> iterator = baseStream.iterator();
        return Stream.generate(() -> {
            final List<T> ts = new ArrayList<>();
            int i = 0;
            while (i++ <= groupSize && iterator.hasNext()) {
                ts.add(iterator.next());
            }
            return Collections.unmodifiableList(ts);
        });
    }
}

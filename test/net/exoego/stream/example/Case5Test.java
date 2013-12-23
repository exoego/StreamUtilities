package net.exoego.stream.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;
import static net.exoego.stream.MoreCollectors.toGroupedEntries;
import static net.exoego.stream.MoreCollectors.toListMapped;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Case5Test {
    List<Dept> sortDept1(List<Emp> list) {
        return list.stream()
                   .collect(groupingBy(Emp::dept, counting()))
                   .entrySet()
                   .stream()
                   .sorted(comparingLong(Entry::getValue))
                   .<Dept>map(Entry::getKey)
                   .collect(toList());
    }

    List<Dept> sortDept2(List<Emp> list) {
        Collector<Emp, ?, Stream<Entry<Dept, Long>>> collector = //
                collectingAndThen(groupingBy(Emp::dept, counting()), m -> m.entrySet().stream());

        return list.stream()
                   .collect(collector)
                   .sorted(comparingLong(Entry::getValue))
                   .<Dept>map(Entry::getKey)
                   .collect(toList());
    }

    List<Dept> sortDept3(List<Emp> list) {
        return list.stream()
                   .collect(toGroupedEntries(Emp::dept, counting()))
                   .sorted(comparingLong(Entry::getValue))
                   .<Dept>map(Entry::getKey)
                   .collect(toList());
    }

    List<Dept> sortDept4(List<Emp> list) {
        return list.stream()
                   .collect(toGroupedEntries(Emp::dept, counting()))
                   .sorted(comparingLong(Entry::getValue))
                   .collect(mapping(Entry::getKey, toList()));
    }

    List<Dept> sortDept5(List<Emp> list) {
        return list.stream()
                   .collect(toGroupedEntries(Emp::dept, counting()))
                   .sorted(comparingLong(Entry::getValue))
                   .collect(toListMapped(Entry::getKey));
    }

    private List<Emp> EMPLOYEES = null;

    @Before
    public void createEmployees() {
        EMPLOYEES = new ArrayList<>(Arrays.asList(new Emp("john", Dept.DEVELOPMENT, 100),
                                                  new Emp("mike", Dept.DEVELOPMENT, 150),
                                                  new Emp("curt", Dept.DEVELOPMENT, 80),
                                                  new Emp("lucas", Dept.DEVELOPMENT, 90),
                                                  new Emp("cathy", Dept.DEVELOPMENT, 80),
                                                  new Emp("sarah", Dept.OPERATIONS, 80),
                                                  new Emp("emily", Dept.OPERATIONS, 110),
                                                  new Emp("bess", Dept.OPERATIONS, 80),
                                                  new Emp("doug", Dept.HUMAN_RESOURCES, 90),
                                                  new Emp("aaron", Dept.HUMAN_RESOURCES, 120)));
        Collections.shuffle(EMPLOYEES);
    }

    @Test
    public void iteration1() {
        assertThat(sortDept1(EMPLOYEES), is(Arrays.asList(Dept.HUMAN_RESOURCES, Dept.OPERATIONS, Dept.DEVELOPMENT)));
    }

    @Test
    public void iteration2() {
        assertThat(sortDept2(EMPLOYEES), is(Arrays.asList(Dept.HUMAN_RESOURCES, Dept.OPERATIONS, Dept.DEVELOPMENT)));
    }

    @Test
    public void iteration3() {
        assertThat(sortDept3(EMPLOYEES), is(Arrays.asList(Dept.HUMAN_RESOURCES, Dept.OPERATIONS, Dept.DEVELOPMENT)));
    }

    @Test
    public void iteration4() {
        assertThat(sortDept4(EMPLOYEES), is(Arrays.asList(Dept.HUMAN_RESOURCES, Dept.OPERATIONS, Dept.DEVELOPMENT)));
    }

    @Test
    public void iteration5() {
        assertThat(sortDept5(EMPLOYEES), is(Arrays.asList(Dept.HUMAN_RESOURCES, Dept.OPERATIONS, Dept.DEVELOPMENT)));
    }
}
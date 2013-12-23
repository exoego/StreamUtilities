package net.exoego.stream.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.exoego.stream.MoreCollectors;
import org.junit.Before;
import org.junit.Test;

import static java.util.stream.Collectors.*;
import static net.exoego.stream.MoreCollectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class Case6Test {
    private List<Emp> EMPLOYEES = null;

    @Before
    public void createEmployees() {
        EMPLOYEES = new ArrayList<>(Arrays.asList(new Emp("john", Dept.DEVELOPMENT, 1100),
                                                  new Emp("mike", Dept.DEVELOPMENT, 1500),
                                                  new Emp("ted", Dept.DEVELOPMENT, 1400),
                                                  new Emp("curt", Dept.DEVELOPMENT, 800),
                                                  new Emp("lucas", Dept.DEVELOPMENT, 900),
                                                  new Emp("cathy", Dept.DEVELOPMENT, 800),
                                                  new Emp("sarah", Dept.OPERATIONS, 800),
                                                  new Emp("emily", Dept.OPERATIONS, 900),
                                                  new Emp("bess", Dept.OPERATIONS, 800),
                                                  new Emp("doug", Dept.HUMAN_RESOURCES, 900),
                                                  new Emp("steve", Dept.HUMAN_RESOURCES, 1100),
                                                  new Emp("aaron", Dept.HUMAN_RESOURCES, 1200)));
        Collections.shuffle(EMPLOYEES);
    }

    Map<Dept, Long> groupByDeptAndFilter1(List<Emp> list) {
        Collector<Emp, ?, Long> countHighSalary = collectingAndThen(toList(),
                                                                    emps -> emps.stream()
                                                                                .filter(e -> e.salary() > 1000)
                                                                                .count());
        return list.stream().collect(groupingBy(Emp::dept, countHighSalary));
    }

    Map<Dept, Long> groupByDeptAndFilter1_2(List<Emp> list) {
        return list.stream()
                   .collect(groupingBy(Emp::dept,
                                       toStreamThen((Stream<Emp> s) -> s.filter(e -> e.salary() > 1000).count())));
    }

    Map<Dept, Long> groupByDeptAndFilter2(List<Emp> list) {
        return list.stream()
                   .collect(groupingBy(Emp::dept))
                   .entrySet()
                   .stream()
                   .collect(Collectors.toMap(entry -> entry.getKey(),
                                             entry -> entry.getValue()
                                                           .stream()
                                                           .filter(emp -> emp.salary() > 1000)
                                                           .count()));
    }

    Map<Dept, Long> groupByDeptAndFilter2_2(List<Emp> list) {
        return list.stream()
                   .collect(toGroupedEntries(Emp::dept))
                   .collect(Collectors.toMap(entry -> entry.getKey(),
                                             entry -> entry.getValue()
                                                           .stream()
                                                           .filter(emp -> emp.salary() > 1000)
                                                           .count()));
    }

    Map<Dept, Long> groupByDeptAndFilter2_3(List<Emp> list) {
        return list.stream()
                   .collect(toGroupedEntries(Emp::dept,
                                             toStreamThen((Stream<Emp> s) -> s.filter(emp -> emp.salary() > 1000)
                                                                              .count())))
                   .collect(MoreCollectors.toMapFromEntry());
    }

    Map<Dept, Long> groupByDeptAndFilter2_4(List<Emp> list) {
        //groupByDeptAndFilter1_2(list)とまったく同じ
        return list.stream()
                   .collect(groupingBy(Emp::dept,
                                       toStreamThen((Stream<Emp> stream) -> stream.filter(emp -> emp.salary() > 1000)
                                                                                  .count())));
    }

    Map<Dept, Long> groupByDeptAndFilter3(List<Emp> list) {
        Map<Dept, List<Emp>> map = list.stream().collect(groupingBy(Emp::dept));

        Map<Dept, Long> result = new HashMap<>();
        for (Map.Entry<Dept, List<Emp>> entry : map.entrySet()) {
            long count = entry.getValue().stream().filter(emp -> emp.salary() > 1000).count();
            result.put(entry.getKey(), count);
        }

        return result;
    }

    Map<Dept, Long> groupByDeptAndFilter4(List<Emp> list) {
        return list.stream().filter(emp -> emp.salary() > 1000).collect(groupingBy(Emp::dept, Collectors.counting()));
    }

    @Test
    public void iteration1() {
        Map<Dept, Long> highSalaryCountPerDept = groupByDeptAndFilter1(EMPLOYEES);
        expect(highSalaryCountPerDept);
    }

    @Test
    public void iteration2() {
        Map<Dept, Long> highSalaryCountPerDept = groupByDeptAndFilter2(EMPLOYEES);
        expect(highSalaryCountPerDept);
    }

    @Test
    public void iteration3() {
        Map<Dept, Long> highSalaryCountPerDept = groupByDeptAndFilter3(EMPLOYEES);
        expect(highSalaryCountPerDept);
    }

    @Test
    public void wrongSample() {
        Map<Dept, Long> map = groupByDeptAndFilter4(EMPLOYEES);
        assertThat(map.get(Dept.DEVELOPMENT), is(3L));
        assertThat(map.get(Dept.HUMAN_RESOURCES), is(2L));
        assertThat(map.get(Dept.OPERATIONS), is(nullValue()));
    }

    @Test
    public void iteration4() {
        Map<Dept, Long> highSalaryCountPerDept = groupByDeptAndFilter1_2(EMPLOYEES);
        expect(highSalaryCountPerDept);
    }

    @Test
    public void iteration5() {
        Map<Dept, Long> highSalaryCountPerDept = groupByDeptAndFilter2_2(EMPLOYEES);
        expect(highSalaryCountPerDept);
    }

    @Test
    public void iteration6() {
        Map<Dept, Long> highSalaryCountPerDept = groupByDeptAndFilter2_3(EMPLOYEES);
        expect(highSalaryCountPerDept);
    }

    @Test
    public void iteration7() {
        Map<Dept, Long> highSalaryCountPerDept = groupByDeptAndFilter2_4(EMPLOYEES);
        expect(highSalaryCountPerDept);
    }

    private void expect(final Map<Dept, Long> map) {
        assertThat(map.get(Dept.DEVELOPMENT), is(3L));
        assertThat(map.get(Dept.HUMAN_RESOURCES), is(2L));
        assertThat(map.get(Dept.OPERATIONS), is(0L));
    }
}
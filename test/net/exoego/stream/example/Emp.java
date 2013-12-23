package net.exoego.stream.example;

class Emp {
    private final Dept dept;
    private final int salary;
    private final String name;

    public Dept dept() {
        return dept;
    }

    public int salary() {
        return salary;
    }

    @Override
    public String toString() {
        return String.format("%s {salary=%s, dept=%s}", name, salary, dept);
    }

    Emp(final String name, final Dept dept, final int salary) {
        this.dept = dept;
        this.salary = salary;
        this.name = name;
    }
}

package main.java.com.djrapitops.plan.utilities.analysis;

import java.io.Serializable;
import java.util.Collection;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.Stream;

/**
 *
 * @author Rsl1122
 */
public class MathUtils {

    /**
     *
     * @param values
     * @return
     */
    public static double averageInt(Stream<Integer> values) {
        OptionalDouble average = values.mapToInt(i -> i).average();
        if (average.isPresent()) {
            return average.getAsDouble();
        } else {
            return 0;
        }
    }

    /**
     *
     * @param values
     * @return
     */
    public static long averageLong(Collection<Long> values) {
        return averageLong(values.stream());
    }

    /**
     *
     * @param values
     * @return
     */
    public static long averageLong(Stream<Long> values) {
        OptionalDouble average = values.mapToLong(i -> i).average();
        if (average.isPresent()) {
            return (long) average.getAsDouble();
        } else {
            return 0L;
        }
    }

    /**
     *
     * @param values
     * @return
     */
    public static double averageDouble(Stream<Double> values) {
        OptionalDouble average = values.mapToDouble(i -> i).average();
        if (average.isPresent()) {
            return average.getAsDouble();
        } else {
            return 0;
        }
    }

    /**
     *
     * @param total
     * @param size
     * @return
     */
    public static double average(int total, int size) {
        return 1.0 * total / size;
    }

    public static long averageLong(long total, long size) {
        return total / size;
    }

    /**
     *
     * @param values
     * @return
     */
    public static long countTrueBoolean(Stream<Boolean> values) {
        return values.filter(i -> i).count();
    }

    /**
     *
     * @param values
     * @return
     */
    public static int sumInt(Stream<Serializable> values) {
        return values
                .mapToInt(value -> (Integer) value)
                .sum();
    }

    /**
     *
     * @param values
     * @return
     */
    public static long sumLong(Stream<Serializable> values) {
        return values
                .mapToLong(value -> (Long) value)
                .sum();
    }

    /**
     *
     * @param values
     * @return
     */
    public static double sumDouble(Stream<Serializable> values) {
        return values
                .mapToDouble(value -> (Double) value)
                .sum();
    }

    /**
     *
     * @param values
     * @return
     */
    public static int getBiggest(Collection<Integer> values) {
        OptionalInt biggest = values.stream().mapToInt(i -> i).max();

        if (biggest.isPresent()) {
            return biggest.getAsInt();
        } else {
            return 1;
        }
    }

    /**
     *
     * @param values
     * @return
     */
    public static long getBiggestLong(Collection<Long> values) {
        OptionalLong biggest = values.stream().mapToLong(i -> i).max();

        if (biggest.isPresent()) {
            return biggest.getAsLong();
        } else {
            return 1;
        }
    }
}

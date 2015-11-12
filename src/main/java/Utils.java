import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class Utils {
    private static final Logger LOG = java.util.logging.Logger.getAnonymousLogger();

    private Utils() {
    }

    public static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis(), duration.getNano());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse a string according to a given regex.
     *
     * @param regex   the given regex
     * @param toParse String to be parsed
     * @return the resulting Matcher object, already matched (using .matches())
     * @throws java.lang.RuntimeException if regex doesn't match toParse
     */
    public static Matcher parse(String regex, String toParse) {
        return parse(Pattern.compile(regex), toParse);
    }

    /**
     * Parse a string according to a given pattern.
     *
     * @param pattern the given pattern
     * @param toParse String to be parsed
     * @return the resulting Matcher object, already matched (using .matches())
     * @throws java.lang.RuntimeException if pattern doesn't match toParse
     */
    public static Matcher parse(Pattern pattern, String toParse) {
        final Matcher matcher = pattern.matcher(toParse);
        if (!matcher.matches()) {
            throw new RuntimeException("The pattern \"" + pattern + "\" does not match the string \"" + toParse + "\"!");
        }
        return matcher;
    }

    /**
     * Segments, or 'airs' a string, by adding a space every four characters.
     *
     * @param string the string to be segmented
     * @return the segmented string
     */
    public static String segment(String string) {
        final int chunkSize = 4;
        final StringBuilder builder = new StringBuilder();
        final int length = string.length();
        for (int i = 0; i < length; i += chunkSize) {
            builder.append(string.substring(i, Math.min(i + chunkSize, length)));
            builder.append(" ");
        }
        return builder.toString().trim();
    }

    /**
     * Pure function that returns the tail of a list (i.e. a copy of the list without the first element).
     *
     * @throws java.lang.IllegalArgumentException if input list is empty
     */
    public static <T> List<T> tail(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Cannot get tail of empty list!");
        } else {
            final List<T> tail = new ArrayList<>(list.size() - 1);
            for (int i = 1; i < list.size(); i++) {
                tail.add(list.get(i));
            }
            return tail;
        }
    }

    /**
     * Reverse a list.
     * <p>
     * Pure function. Guaranteed to run in O(list.size()), as long as the given list can be traversed in O(list.size()).
     *
     * @param list the list whose reverse we want
     * @return a new list which contains the elements of the given list in reversed order
     */
    public static <T> List<T> reversed(List<T> list) {

        // make an arrayList copy of the given list if it isn't already one (so that we can get O(1) random access)
        final ArrayList<T> arrayList = (list instanceof ArrayList) ? (ArrayList<T>) list : new ArrayList<>(list);

        // add all elements in reverse order to a new list
        final List<T> reversed = new ArrayList<>(list.size());
        for (int i = list.size() - 1; i >= 0; i--) {
            reversed.add(arrayList.get(i));
        }

        return reversed;
    }

    /**
     * Computes an index indicating how different two strings are.
     * Underscores in the first string will be interpreted as digit wildcards
     * whenever doing so reduces the distance between the strings.
     * <p>
     * Matching one char of one string with no corresponding char of the other string increases the distance by one.
     * Matching one char of one string with a different char of the other string also increases the distance by one.
     * The mapping function that matches each char of one string to a char of the other string is order-preserving.
     * <p>
     * Example of a match that minimizes the distance (the dashes represent inserted spaces to improve the fit):
     * <ul>
     * <li><code>Mary--- had a little lamb.</code>
     * <li><code>Marylin had a cut--e lamb.</code>
     * </ul>
     * In this example, a total of 5 chars do not have any equivalent char in the other string (the dashes),
     * and 2 chars are mismatched ("li" from "little" with "cu" from "cute").
     * This therefore brings the total distance between the two strings to 7.
     * <p>
     * The algorithm used is a dynamic programming version of the recursion used in {@link Utils#distanceSlow}.
     */
    public static int distance(String pattern, String string) {
        final int l1Plus1 = pattern.length() + 1;
        final int l2Plus1 = string.length() + 1;

        int[] minDist = new int[l2Plus1];
        for (int j = 0; j < l2Plus1; j++) {
            minDist[j] = j;
        }

        for (int i = 1; i < l1Plus1; i++) {
            final int[] nextMinDist = new int[l2Plus1];
            nextMinDist[0] = i;
            for (int j = 1; j < l2Plus1; j++) {
                nextMinDist[j] = Math.min(
                        distance(pattern.charAt(i - 1), string.charAt(j - 1)) + minDist[j - 1],
                        Math.min(nextMinDist[j - 1], minDist[j]) + 1
                );
            }
            minDist = nextMinDist;
        }

        return minDist[l2Plus1 - 1];
    }

    /**
     * Do not use this method! Its execution time is exponential in the length of the arguments.
     * It only serves as documentation for {@link Utils#distance(String, String)},
     * as the core of the algorithm used is much better represented in {@link Utils#distanceSlow}.
     */
    @SuppressWarnings({"unused", "JavaDoc"})
    private static int distanceSlow(String s1, String s2) {

        // if either string is empty, the distance is the length of the other string
        if (s1.isEmpty()) {
            return s2.length();
        }
        if (s2.isEmpty()) {
            return s1.length();
        }

        /*
         * The minimum distance between 2 non-empty strings is the minimum of three possibilities:
         *
         *  1. Take the minimum distance of the 2 strings without their first char, plus the distance between the 2 first chars.
         *  2. Take the minimum distance of the first string with [the second without its first char],
         *     plus 1 to account for the unmatched first char.
         *  3. The dual of 2.
         */
        return Math.min(
                distance(s1.charAt(0), s2.charAt(0)) + distanceSlow(s1.substring(1), s2.substring(1)),
                Math.min(
                        distanceSlow(s1, s2.substring(1)),
                        distanceSlow(s1.substring(1), s2)
                ) + 1
        );
    }

    /**
     * Simple distance measure between two chars: if they are the same, the distance is 0, else it is 1.
     * Exception: if patternChar == '_', then the distance is also 0 if stringChar is a digit.
     */
    private static int distance(char patternChar, char stringChar) {
        if (patternChar == '_' && (stringChar >= '0' && stringChar <= '9')) {
            return 0;
        } else {
            return patternChar == stringChar ? 0 : 1;
        }
    }

    /**
     * Polls a boolean supplier until it returns true or the maximum number of allowed polls has been reached.
     *
     * @param polled      the given supplier to be polled
     * @param maxTryCount the maximum number of polls
     * @param sleepLength the desired length of the intervals between successive polls
     * @return true as soon as the supplier returns true, false if the maximum number of allowed polls has been reached
     */
    public static boolean tryUntilTrue(Supplier<Boolean> polled, int maxTryCount, Duration sleepLength) {
        return poll(() -> polled.get() ? Optional.of(new Object()) : Optional.empty(), maxTryCount, sleepLength).isPresent();
    }

    /**
     * Polls a boolean supplier until it returns true or the timeout is reached.
     *
     * @param polled      the given supplier to be polled
     * @param timeout     the maximum polling time
     * @param sleepLength the desired length of the intervals between successive polls
     * @return true as soon as the supplier returns true, false if the timeout has been reached
     */
    public static boolean tryUntilTrue(Supplier<Boolean> polled, Duration timeout, Duration sleepLength) {
        return poll(() -> polled.get() ? Optional.of(new Object()) : Optional.empty(), timeout, sleepLength).isPresent();
    }

    /**
     * Polls a supplier until it produces a value or the maximum number of allowed polls has been reached.
     *
     * @param polled      the given supplier to be polled
     * @param maxTryCount the maximum number of polls
     * @param sleepLength the desired length of the sleep intervals between successive polls
     * @param <T>         the value produced by the supplier when it is ready
     * @return the value returned by the given supplier or Optional.empty() if the timeout is reached
     */
    public static <T> Optional<T> poll(Supplier<Optional<T>> polled, int maxTryCount, Duration sleepLength) {
        final Supplier<Boolean> sleeper = new Supplier<Boolean>() {
            private int tryCount = 0;

            @Override
            public Boolean get() {
                tryCount++;

                // stop if the maximum number of allowed polls has been reached
                if (tryCount >= maxTryCount) {
                    return true;
                }

                // sleep
                else {
                    sleep(sleepLength);
                    return false;
                }
            }
        };

        return poll(polled, Duration.ofDays(100), sleeper);
    }

    /**
     * Polls a supplier until it produces a value or the timeout is reached.
     *
     * @param polled      the given supplier to be polled
     * @param timeout     the maximum polling time
     * @param sleepLength the desired length of the sleep intervals between successive polls
     * @param <T>         the value produced by the supplier when it is ready
     * @return the value returned by the given supplier or Optional.empty() if the timeout is reached
     */
    public static <T> Optional<T> poll(Supplier<Optional<T>> polled, Duration timeout, Duration sleepLength) {
        final Instant stopMinusPeriod = Instant.now().plus(timeout).minus(sleepLength);
        final Supplier<Boolean> sleeper = () -> {

            // try to shortcut sleep
            if (Instant.now().compareTo(stopMinusPeriod) > 0) {
                return true;
            }

            // sleep
            else {
                sleep(sleepLength);
                return false;
            }
        };

        return poll(polled, timeout, sleeper);
    }

    /**
     * Busy-polls (i.e. without sleeping) a supplier until it produces a value or the timeout is reached.
     *
     * @param polled  the given supplier to be polled
     * @param timeout the maximum polling time
     * @param <T>     the value produced by the supplier when it is ready
     * @return the value returned by the given supplier or Optional.empty() if the timeout is reached
     */
    public static <T> Optional<T> busyPoll(Supplier<Optional<T>> polled, Duration timeout) {
        return poll(polled, timeout, () -> false);
    }

    /**
     * Polls a supplier until it produces a value or the timeout is reached.
     *
     * @param polled  the given supplier to be polled
     * @param timeout the maximum polling time
     * @param sleeper to be called between polls; returns whether it got interrupted
     * @param <T>     the value produced by the supplier when it is ready
     * @return the value returned by the given supplier or Optional.empty() if the timeout is reached
     */
    public static <T> Optional<T> poll(Supplier<Optional<T>> polled, Duration timeout, Supplier<Boolean> sleeper) {
        final Instant start = Instant.now();
        final Instant stop = start.plus(timeout);

        // used for logging
        Instant nextLogTime = start.plus(Duration.ofMinutes(1));
        int pollCount = 0;

        while (true) {

            // poll and return if successful
            final Optional<T> optional = polled.get();
            if (optional.isPresent()) {
                return optional;
            }

            if (sleeper.get()) {
                return Optional.empty();
            }

            // check if timeout
            final Instant afterSleep = Instant.now();
            if (afterSleep.compareTo(stop) > 0) {
                return Optional.empty();
            }

            // log if it's time
            pollCount++;
            if (afterSleep.compareTo(nextLogTime) > 0) {
                LOG.info("Still polling... (" + pollCount + " polls since last log)");
                nextLogTime = afterSleep.plus(Duration.ofMinutes(1));
                pollCount = 0;
            }
        }
    }

    /**
     * @return the given string if non-empty
     */
    public static Optional<String> nonEmpty(String string) {
        return string == null || string.isEmpty() ? Optional.empty() : Optional.of(string);
    }

    /**
     * @return a map representing the inverse of the given function on the given domain
     */
    public static <S, T> Map<T, S> inverse(Function<S, T> injection, Collection<S> domain) {
        return inverse(injection, domain.parallelStream());
    }

    /**
     * @return a map representing the inverse of the given function on the given domain
     */
    public static <S, T> Map<T, S> inverse(Function<S, T> injection, S[] domain) {
        return inverse(injection, Arrays.stream(domain));
    }

    /**
     * @return a map representing the inverse of the given function on the given domain
     */
    public static <S, T> Map<T, S> inverse(Function<S, T> injection, Stream<S> domain) {
        return domain.collect(Collectors.toMap(injection, x -> x));
    }

    /**
     * @return a map representing the given function on the given domain
     */
    public static <S, T> Map<S, T> asMap(Function<S, T> function, Collection<S> domain) {
        return asMap(function, domain.parallelStream());
    }

    /**
     * @return a map representing the given function on the given domain
     */
    public static <S, T> Map<S, T> asMap(Function<S, T> function, S[] domain) {
        return asMap(function, Arrays.stream(domain));
    }

    /**
     * @return a map representing the given function on the given domain
     */
    public static <S, T> Map<S, T> asMap(Function<S, T> function, Stream<S> domain) {
        return domain.collect(Collectors.toMap(x -> x, function::apply));
    }
}
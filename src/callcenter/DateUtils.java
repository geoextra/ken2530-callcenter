package callcenter;

/**
 * Utilities for working with times and dates
 *
 * @author Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class DateUtils {
    public static double minutesToSeconds(double time) {
        return time * 60;
    }

    public static double secondsToMinutes(double time) {
        return time / 60;
    }

    public static double hoursToSeconds(double time) {
        return minutesToSeconds(time) * 60;
    }

    public static double secondsToHours(double time) {
        return secondsToMinutes(time) / 60;
    }

    public static double daysToSeconds(double time) {
        return hoursToSeconds(time) * 24;
    }

    public static double secondsToDays(double time) {
        return secondsToHours(time) / 24;
    }

    public static double hourOfDay(double time) {
        return secondsToHours(time) % 24;
    }

    public static double startOfDayInSeconds(double time) {
        return daysToSeconds(Math.floor(secondsToDays(time)));
    }

    public static double startOfNextDayInSeconds(double time) {
        return startOfDayInSeconds(time) + daysToSeconds(1);
    }
}

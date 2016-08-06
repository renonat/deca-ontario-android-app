package ca.deca.decaontarioapp.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by Reno on 15-07-01.
 */
public class SocialUtils {

    /**
     * Converts a date string from Twitter into a {DateTime} object
     * The twitter date format is "EEE MMM dd HH:mm:ss Z yyyy"
     *
     * @param rss {String} : The date in twitter format
     * @return {DateTime} : The date as an object
     */
    private static DateTime getTwitterDate(String rss) {
        String form = "EEE MMM dd HH:mm:ss Z yyyy";
        DateTimeFormatter format = DateTimeFormat.forPattern(form).withLocale(Locale.ENGLISH);
        return format.parseDateTime(rss);
    }

    /**
     * Gets the elapsed time since a date.
     * This is a simple representation tailored for small-format display.
     *
     * @param date {DateTime} : The original date
     * @return {String} : The elapsed time in String format
     */
    private static String getOffsetFromDate(DateTime date) {
        DateTime now = DateTime.now();
        // Get the intervals of time between the date and now
        Minutes min = Minutes.minutesBetween(date, now);
        Hours hours = Hours.hoursBetween(date, now);
        Days days = Days.daysBetween(date, now);
        // Turn those numbers into a string
        // Displays the time as the largest unit, else displays the original date
        if (min.getMinutes() < 60) {
            return min.getMinutes() + "m";
        } else if (hours.getHours() < 24) {
            return hours.getHours() + "h";
        } else if (days.getDays() < 7) {
            return days.getDays() + "d";
        } else {
            DateTimeFormatter format = DateTimeFormat.forPattern("MMM d");
            return format.print(date).toUpperCase();
        }
    }

    /**
     * Gets the elapsed time since a date in twitter format.
     * The twitter date format is "EEE MMM dd HH:mm:ss Z yyyy"
     *
     * @param rss {String} : The date in rss twitter format
     * @return {String} : The elapsed time since the date
     */
    public static String getOffsetFromRSS(String rss) {
        return getOffsetFromDate(getTwitterDate(rss));
    }

}

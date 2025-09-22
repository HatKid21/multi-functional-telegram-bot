package com.github.hatkid.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {

    public static long parseDataTimeToEpochMillis(String dateTimeString, String pattern, ZoneId zoneId){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString,formatter);

            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

            long time = zonedDateTime.toInstant().toEpochMilli();
            if (time - System.currentTimeMillis() < 0){
                return -2;
            }
            return time;
        } catch (DateTimeParseException e){
            System.err.println("Error in date parsing " + e.getMessage());
            return -1;
        }

    }

}

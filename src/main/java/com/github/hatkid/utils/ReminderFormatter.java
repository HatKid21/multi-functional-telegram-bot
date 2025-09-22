package com.github.hatkid.utils;

import com.github.hatkid.sql.Reminder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class ReminderFormatter {

    public static String getFormattedReminderList( long chatId){
        StringBuilder stringBuilder = new StringBuilder();
        List<Reminder> reminderList = Reminder.getReminders(chatId);
        if (reminderList == null || reminderList.isEmpty()){
            stringBuilder.append("На данный момент у вас нет активных напоминаний");
            return  stringBuilder.toString();
        }
        reminderList.sort(Comparator.comparingLong(Reminder::getScheduledTime));
        stringBuilder.append("Список напоминаний:").append("\n");
        stringBuilder.append("id\n");
        for (Reminder reminder : reminderList){
            stringBuilder.append("\t");
            long databaseId = reminder.getDatabaseId();
            String reminderText = reminder.getMessageText();
            String date = getFormattedDateTime(reminder.getScheduledTime());
            stringBuilder.append(databaseId).append(". \t");
            stringBuilder.append(date).append(" : ");
            stringBuilder.append(reminderText).append("\n");
        }
        return stringBuilder.toString();
    }

    private static String getFormattedDateTime(long timeMillis){
        Instant instant = Instant.ofEpochMilli(timeMillis);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Moscow"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return zonedDateTime.format(formatter);
    }

}

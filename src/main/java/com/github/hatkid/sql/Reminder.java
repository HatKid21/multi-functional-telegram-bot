package com.github.hatkid.sql;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Reminder {

    private static final Logger LOGGER = Logger.getLogger(Reminder.class.getName());

    private ScheduledFuture<?> scheduledTaskFuture;

    private static final Map<Long, List<Reminder>> REMINDER_MAP = new HashMap<>();

    private long databaseId = -1;
    private long chatId;
    private String messageText;
    private long scheduledTime;

    private final TelegramClient telegramClient;

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public Reminder(TelegramClient telegramClient, long chatId){
        this.telegramClient = telegramClient;
        this.chatId = chatId;
        List<Reminder> list = new ArrayList<>();
        list.add(this);
        REMINDER_MAP.put(chatId,list);
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public void deleteScheduler(){
        scheduledTaskFuture.cancel(false);
        scheduledTaskFuture = null;
        REMINDER_MAP.get(chatId).remove(this);
    }

    public Reminder(TelegramClient telegramClient, long chatId, String messageText, long scheduledTime) {
        this.telegramClient = telegramClient;
        this.chatId = chatId;
        this.messageText = messageText;
        this.scheduledTime = scheduledTime;
        List<Reminder> list = new ArrayList<>();
        list.add(this);
        REMINDER_MAP.put(chatId,list);
    }

    public void activate(){
        if (databaseId == -1){
            this.databaseId = DatabaseManager.insertReminder(this);
        }
        Runnable task = () ->{
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId),messageText);

            try{
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e){
                System.out.println(e.getMessage());
            }
            DatabaseManager.deleteReminder(databaseId);
            REMINDER_MAP.remove(chatId);
        };

        scheduledTaskFuture = executorService.schedule(task,calculateDelay(), TimeUnit.MILLISECONDS);

    }

    public static List<Reminder> getReminders(long chatId){
        return REMINDER_MAP.get(chatId);
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    private long calculateDelay(){
        long currentTime = System.currentTimeMillis();
        return scheduledTime - currentTime;
    }



    public long getChatId() {
        return chatId;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}

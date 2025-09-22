package com.github.hatkid.state;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.UserManager;
import com.github.hatkid.sql.Reminder;
import com.github.hatkid.utils.DateParser;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.time.ZoneId;

public class SetReminderTimeHandler extends BaseStateHandler implements StateHandler{


    public SetReminderTimeHandler(UserManager userManager){
        super(userManager);
    }

    @Override
    public void handle(Message message, long chatId, TelegramBot bot) {
        ZoneId timeZone = ZoneId.of("Europe/Moscow");
        long time = DateParser.parseDataTimeToEpochMillis(message.getText(),"dd.MM.yyyy HH:mm", timeZone);
        String responseMessage;
        if (time == -1){
            responseMessage = "Вы неправильно ввели дату";
        } else if (time == -2){
            responseMessage = "Напоминалка не может сработать в прошлом!";
        }else{
            responseMessage = "Напоминалка успешно создана!";
            Reminder reminder = Reminder.getReminders(chatId).getLast();
            reminder.setScheduledTime(time);
            userManager.getUser(chatId).setState(UserState.IDLE);
            reminder.activate();
        }
        bot.sendMessage(chatId,responseMessage);
    }
}

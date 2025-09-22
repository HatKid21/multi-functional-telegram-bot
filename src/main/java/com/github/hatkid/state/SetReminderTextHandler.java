package com.github.hatkid.state;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.UserManager;
import com.github.hatkid.sql.Reminder;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

public class SetReminderTextHandler extends BaseStateHandler implements StateHandler{


    public SetReminderTextHandler(UserManager userManager){
        super(userManager);
    }

    public void handle(Message message, long chatId, TelegramBot bot) {
        List<Reminder> reminders = Reminder.getReminders(chatId);
        Reminder reminder = reminders.getLast();
        reminder.setMessageText(message.getText());
        String responseMessage = "Теперь введите дату в формате ДД.ММ.ГГГГ ЧЧ:мм";
        bot.sendMessage(chatId,responseMessage);
        userManager.getUser(chatId).setState(UserState.SET_REMINDER_TIME);
    }
}

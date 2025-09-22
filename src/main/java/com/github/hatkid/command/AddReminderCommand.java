package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;
import com.github.hatkid.sql.Reminder;

public class AddReminderCommand extends BaseCommand implements CommandHandler {

    public AddReminderCommand(UserManager userManager){
        super(userManager,"Добавить напоминание");

    }

    @Override
    public void handle(long chatId, TelegramBot bot) {
        User user = userManager.getUser(chatId);
        if(!user.getState().equals(UserState.IDLE)){
            bot.sendMessage(chatId,"Перед использованием этой команды закончите предыдущую.");
            return;
        }
        Reminder reminder = new Reminder(bot.getTelegramClient(),chatId);
        userManager.getUser(chatId).setState(UserState.SET_REMINDER_TEXT);
        reminder.setChatId(chatId);
        String message = "Введите текст для напоминаний";
        bot.sendMessage(chatId,message);
    }

}

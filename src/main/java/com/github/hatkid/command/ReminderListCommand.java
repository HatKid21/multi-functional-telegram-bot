package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.ai.UserManager;
import com.github.hatkid.utils.ReminderFormatter;

public class ReminderListCommand extends BaseCommand implements CommandHandler{

    public ReminderListCommand(UserManager userManager) {
        super(userManager,"Вывести список активных напоминаний");
    }

    @Override
    public void handle(long chatId, TelegramBot bot) {
        String formattedMessage = ReminderFormatter.getFormattedReminderList(chatId);
        bot.sendMessage(chatId,formattedMessage);
    }

}

package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;

public class RemoveReminderCommand extends BaseCommand implements CommandHandler{

    public RemoveReminderCommand(UserManager userManager) {
        super(userManager,"Убрать напоминание");
    }

    @Override
    public void handle(long chatId, TelegramBot bot) {
        User user = userManager.getUser(chatId);
        user.setState(UserState.REMOVE_REMINDER);
        bot.sendMessage(chatId,"Введите id напоминания чтобы его удалить");
    }

}

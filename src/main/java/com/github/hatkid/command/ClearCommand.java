package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;

public class ClearCommand extends BaseCommand implements CommandHandler{

    public ClearCommand(UserManager userManager){
        super(userManager,"Очистить историю");
    }

    @Override
    public void handle(long chatId, TelegramBot bot) {
        User user = userManager.getUser(chatId);
        if (!user.getState().equals(UserState.IDLE)){
            bot.sendMessage(chatId,"Перед использованием этой команды закончите предыдущую.");
            return;
        }

        user.clearContent();

        bot.sendMessage(chatId,"История очищена");

    }

}

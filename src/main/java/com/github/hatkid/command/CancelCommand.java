package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;

public class CancelCommand extends BaseCommand implements CommandHandler{

    public CancelCommand(UserManager userManager){
        super(userManager,"Отмена");
    }

    @Override
    public void handle(long chatId, TelegramBot bot) {
        User user = userManager.getUser(chatId);
        if (user.getState().equals(UserState.IDLE)){
            bot.sendMessage(chatId,"Нечего отменять");
            return;
        }
        String responseMessage  = "Отмена";
        bot.sendMessage(chatId,responseMessage);
        userManager.getUser(chatId).setState(UserState.IDLE);
    }

}

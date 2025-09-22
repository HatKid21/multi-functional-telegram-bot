package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;

public class SetInstructionsCommand extends BaseCommand implements CommandHandler {

    public SetInstructionsCommand(UserManager userManager){
        super(userManager,"Добавить инструкции боту");
    }

    @Override
    public void handle(long chatId, TelegramBot bot) {
        User user = userManager.getUser(chatId);
        UserState userState = user.getState();
        if (!userState.equals(UserState.IDLE)){
            bot.sendMessage(chatId,"Перед использованием этой команды закончите предыдущую.");
            return;
        }
        bot.sendMessage(chatId,"Введите инструкцию");
        user.setState(UserState.SET_BOT_INSTRUCTIONS);
    }

}

package com.github.hatkid.state;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public class SetInstructionsStateHandler extends BaseStateHandler implements StateHandler{

    public SetInstructionsStateHandler(UserManager userManager){
        super(userManager);
    }

    @Override
    public void handle(Message message, long chatId, TelegramBot bot) {
        User user = userManager.getUser(chatId);
        user.getBotSettings().setBotInstructions(message.getText());
        bot.sendMessage(chatId, "Инструкция успешно поставлена!");
        user.setState(UserState.IDLE);
    }
}

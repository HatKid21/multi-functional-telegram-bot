package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.ai.ConfigurationManager;
import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;

public class ClearInstructionsCommand extends BaseCommand implements CommandHandler {

    private static final ConfigurationManager configurationManager = new ConfigurationManager();

    public ClearInstructionsCommand(UserManager userManager) {
        super(userManager, "Очистить инструкцию бота");
    }

    @Override
    public void handle(long chatId, TelegramBot bot) {
        User user = userManager.getUser(chatId);
        user.getBotSettings().setBotInstructions(configurationManager.getDefaultInstruction());
        bot.sendMessage(chatId,"Инструкция ИИ успешно очищена");
    }

}

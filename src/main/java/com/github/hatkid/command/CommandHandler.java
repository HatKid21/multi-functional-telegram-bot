package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;

public interface CommandHandler {

    void handle(long chatId, TelegramBot bot);

    String getDescription();

}

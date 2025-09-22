package com.github.hatkid.state;

import com.github.hatkid.TelegramBot;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface StateHandler {

    void handle(Message message, long chatId, TelegramBot bot);

}

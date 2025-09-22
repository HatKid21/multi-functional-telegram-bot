package com.github.hatkid;

import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;
import com.github.hatkid.command.CommandDispatcher;
import com.github.hatkid.command.CommandHandler;
import com.github.hatkid.sql.DatabaseManager;
import com.github.hatkid.state.StateBasedMessageManager;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private static final Logger LOGGER = Logger.getLogger(TelegramBot.class.getName());

    private final UserManager userManager = new UserManager();

    private static final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    private final CommandDispatcher commandDispatcher = new CommandDispatcher(userManager);

    private final StateBasedMessageManager stateBasedMessageManager = new StateBasedMessageManager(userManager);

    private final TelegramClient telegramClient;

    public TelegramBot(@NotNull String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);

        initializeCommands();
        DatabaseManager.createNewTable();
        DatabaseManager.activateReminders(telegramClient);
    }

    private void initializeCommands() {
        List<BotCommand> commands = new ArrayList<>();
        Set<Map.Entry<String, CommandHandler>> entrySet = commandDispatcher.getEntry();
        for (Map.Entry<String, CommandHandler> entry : entrySet) {
            String commandName = entry.getKey();
            String description = entry.getValue().getDescription();
            commands.add(new BotCommand(commandName, description));
        }
        SetMyCommands setMyCommands = new SetMyCommands(commands);
        try {
            telegramClient.execute(setMyCommands);
        } catch (TelegramApiException e) {
            LOGGER.log(Level.SEVERE, "Initializing commands error", e);
        }
    }

    private void changeAction(SendChatAction sendChatAction) {
        try {
            telegramClient.execute(sendChatAction);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void consume(Update update) {
        virtualThreadExecutor.execute(() ->{
            if (!update.hasMessage()) {
                return;
            }
            long chatId = update.getMessage().getChatId();
            Message message = update.getMessage();

            if (!userManager.proceedUserData(chatId)) {
                userManager.addUser(new User(message.getChat().getUserName(), chatId, UserState.IDLE));
            }

            SendChatAction sendChatAction = new SendChatAction(String.valueOf(chatId), ActionType.TYPING.toString());
            changeAction(sendChatAction);

            if (message.hasText()) {

                if (message.getText().startsWith("/")) {
                    commandDispatcher.dispatch(message.getText(), chatId, this);
                } else {
                    stateBasedMessageManager.dispatch(message, chatId, this);
                }

            } else {
                stateBasedMessageManager.dispatch(message, chatId, this);
            }
        });
    }

    public void sendMessage(long chatId, String message) {
        final int MAX_LENGTH = 4096;
        List<String> messages = new ArrayList<>();
        int start = 0;
        while (start < message.length()) {
            int end = Math.min(start + MAX_LENGTH, message.length());
            messages.add(message.substring(start, end));
            start = end;
        }
        for (String chunk : messages) {
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId), chunk);
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {

                try {
                    sendMessage.setParseMode(null);
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException ex){
                    LOGGER.log(Level.SEVERE, "Error during sendMessageMethod",ex);
                }

            }
        }
    }

    public File getFile(String f_id) {
        File output = null;
        try {
            output = telegramClient.execute(new GetFile(f_id));
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
        return output;
    }

    public TelegramClient getTelegramClient() {
        return telegramClient;
    }
}

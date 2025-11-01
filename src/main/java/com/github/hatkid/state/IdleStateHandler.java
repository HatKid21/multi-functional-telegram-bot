package com.github.hatkid.state;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.ai.ConfigurationManager;
import com.github.hatkid.ai.GeminiBot;
import com.github.hatkid.ai.UserManager;
import com.github.hatkid.utils.BufferManager;
import com.github.hatkid.utils.MessageData;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import swiss.ameri.gemini.api.GenAi;
import swiss.ameri.gemini.gson.GsonJsonParser;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class IdleStateHandler extends BaseStateHandler implements StateHandler{

    private static final Logger LOGGER = Logger.getLogger(IdleStateHandler.class.getName());

    private final GeminiBot geminiBot = new GeminiBot(new GenAi(System.getenv("GEMINI_AI_API_KEY"),new GsonJsonParser()), new ConfigurationManager());

    private final BufferManager bufferManager;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public IdleStateHandler(UserManager userManager){
        super(userManager);
        this.bufferManager = new BufferManager(userManager);
    }

    @Override
    public void handle(Message message, long chatId, TelegramBot bot) {

        if (!bufferManager.hasMessagesInBuffer(chatId)){
            bufferManager.registerUser(chatId);
            scheduler.schedule(() ->    sendRequest(chatId,bot),3, TimeUnit.SECONDS);
        }
            if (message.hasPhoto()){
            bufferManager.addFile(chatId,proceedPhotoInput(bot, message));
        } else if (message.hasVideo()) {
            bufferManager.addFile(chatId,proceedVideoInput(bot,message));
        } else if (message.hasSticker()){
            bufferManager.addFile(chatId,proceedStickerInput(bot,message));
        } else if (message.hasVoice()){
            bufferManager.addFile(chatId,proceedVoiceInput(bot,message));
        } else if (message.hasText()){
            bufferManager.addText(chatId,message.getText());
        } else if (message.hasAnimation()){
            bufferManager.addFile(chatId,proceedAnimationInput(bot,message));
        } else if (message.hasAudio()){
            bufferManager.addFile(chatId,proceedAudioInput(bot,message));
        }

    }

    private void sendRequest(long chatId, TelegramBot telegramBot){
        MessageData messageData = bufferManager.getBufferedMessage(chatId);
        String response = geminiBot.sendRequest(userManager.getUser(chatId),messageData);
        telegramBot.sendMessage(chatId,response);
        userManager.writeUserData(userManager.getUser(chatId));
    }

    private File proceedAudioInput(TelegramBot bot, Message message){
        Audio audio = message.getAudio();
        return bot.getFile(audio.getFileId());
    }

    private File proceedAnimationInput(TelegramBot bot, Message message){
        Animation animation = message.getAnimation();
        return bot.getFile(animation.getFileId());
    }

    private File  proceedVoiceInput(TelegramBot bot,Message message) {
        Voice voice = message.getVoice();
        return bot.getFile(voice.getFileId());
    }

    private File proceedStickerInput(TelegramBot bot,Message message){
        Sticker sticker = message.getSticker();
        return bot.getFile(sticker.getFileId());
    }


    private File proceedVideoInput(TelegramBot bot,Message message){
        Video video = message.getVideo();
        return bot.getFile(video.getFileId());
    }

    private File proceedPhotoInput(TelegramBot bot,Message message){
        PhotoSize photo = message.getPhoto().getLast();
        return bot.getFile(photo.getFileId());
    }

}

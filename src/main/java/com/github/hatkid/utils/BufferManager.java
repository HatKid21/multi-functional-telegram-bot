package com.github.hatkid.utils;

import com.github.hatkid.ai.User;
import com.github.hatkid.ai.UserManager;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BufferManager {

    private final Map<User, MessageData> messageMap = new ConcurrentHashMap<>();

    private final UserManager userManager;

    public BufferManager(UserManager userManager ){
        this.userManager = userManager;
    }

    public void registerUser(long chatId){
        messageMap.put(userManager.getUser(chatId),new MessageData());
    }

    public void addText(long chatID, String text){
        messageMap.get(userManager.getUser(chatID)).addText(text);
    }

    public void addFile(long chatID, File file){
        messageMap.get(userManager.getUser(chatID)).addFile(file);
    }

    public MessageData getBufferedMessage(long chatID){
        return messageMap.remove(userManager.getUser(chatID));
    }

    public boolean hasMessagesInBuffer(long chatId){
        return messageMap.containsKey(userManager.getUser(chatId));
    }

}

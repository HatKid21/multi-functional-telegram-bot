package com.github.hatkid.ai;

import com.github.hatkid.UserState;
import swiss.ameri.gemini.api.Content;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class User {

    private  final AiBotSettings botSettings;
    private final List<Content> contentList = new ArrayList<>();
    private final long chatId;
    private UserState state;
    private final String username;

    public User(String username,long chatId, UserState state){
        this.username = username;
        this.chatId = chatId;
        this.state = state;
        this.botSettings = new AiBotSettings();

    }

    public AiBotSettings getBotSettings() {
        return botSettings;
    }

    public void clearContent(){
        String filePath = "users/" + chatId + ".txt";
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
        }
        contentList.clear();
    }

    public void removeFistContext(){
        contentList.removeFirst();
    }

    public List<Content> getContentList() {
        return contentList;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public UserState getState() {
        return state;
    }

    public void addContent(Content content){
        contentList.add(content);
    }

    public long getChatId() {
        return chatId;
    }

    public String getUsername() {
        return username;
    }
}

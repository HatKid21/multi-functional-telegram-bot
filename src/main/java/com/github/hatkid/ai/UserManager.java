package com.github.hatkid.ai;

import com.github.hatkid.UserState;
import com.github.hatkid.security.EncryptedFileManager;
import com.github.hatkid.security.KeyStoreLoader;
import com.github.hatkid.utils.ContentTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import swiss.ameri.gemini.api.Content;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class UserManager {

    private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());

    private static final String defaultDirectory = "users/";
    private final Gson gson;
    private final Map<Long, User> USERS = new HashMap<>();

    public UserManager() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Content.class,new ContentTypeAdapter(gsonBuilder.create()));
        gson = gsonBuilder.create();
    }

    public void writeUserData(User user) {
        String jsonUserData = gson.toJson(user);
        EncryptedFileManager.writeToFile(String.valueOf(user.getChatId()),jsonUserData, KeyStoreLoader.getSecretKey());
    }

    private User readUserData(long chatId){
        File directory = new File(defaultDirectory);
        if (!directory.exists()){
            directory.mkdirs();
        }
        String json = EncryptedFileManager.decryptFileContent(String.valueOf(chatId),KeyStoreLoader.getSecretKey());
        if (json.isEmpty()){
            return null;
        }
        User user = gson.fromJson(json,User.class);
        user.setState(UserState.IDLE);
        return user;
    }

    public User getUser(long chatId) {
        return USERS.get(chatId);
    }

    public boolean proceedUserData(long chatId){
        if (USERS.get(chatId) != null){
            return true;
        }
        User savedUser = readUserData(chatId);
        if (savedUser != null){
            USERS.put(savedUser.getChatId(),savedUser);
            return true;
        }
        return false;
    }

    public void addUser(User user) {
        USERS.put(user.getChatId(),user);
    }

}

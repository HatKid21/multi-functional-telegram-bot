package com.github.hatkid.utils;

import org.telegram.telegrambots.meta.api.objects.File;

import java.util.ArrayList;
import java.util.List;

public class MessageData {

    private final StringBuilder textData;
    private final List<File> files;

    public MessageData(){
        this.textData = new StringBuilder();
        files = new ArrayList<>();
    }

    public void addText(String textData){
        this.textData.append(textData);
    }

    public void addFile(File file){
        files.add(file);
    }

    public List<File> getFiles() {
        return files;
    }

    public String getTextData() {
        return textData.toString();
    }

}

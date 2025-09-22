package com.github.hatkid.utils;

import org.telegram.telegrambots.meta.api.objects.File;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

public class FileDownloader {

    public static String download(File file){
        try{
            URL url = URI.create(file.getFileUrl(System.getenv("TELEGRAM_BOT_API_KEY"))).toURL();
            InputStream inputStream = url.openStream();
            byte[] fileData = inputStream.readAllBytes();
            inputStream.close();
            return Base64.getEncoder().encodeToString(fileData);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return "";
    }

}

package com.github.hatkid;

import com.github.hatkid.security.KeyStoreLoader;
import com.github.hatkid.security.KeyStoreSetup;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import swiss.ameri.gemini.api.FunctionCall;
import swiss.ameri.gemini.api.FunctionDeclaration;
import swiss.ameri.gemini.api.Schema;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static final String DEFAULT_KEYSTORE_PATH = "secure/keystore.jceks";

    public static void main(String[] args) {

        initKeyStore();

        String botToken = System.getenv("TELEGRAM_BOT_API_KEY");
        if (botToken == null || botToken.isBlank()){
            LOGGER.severe("Environmental variable TELEGRAM_BOT_API_KEY is not set");
            System.exit(1);
        }

        try (TelegramBotsLongPollingApplication botApplication = new TelegramBotsLongPollingApplication()){
            botApplication.registerBot(botToken, new TelegramBot(botToken));
            LOGGER.log(Level.INFO,"Telegram bot started!");
            Thread.currentThread().join();

        } catch (TelegramApiException e){
            LOGGER.log(Level.SEVERE,"Telegram API error:" + e.getLocalizedMessage(), e);
        } catch (Exception e){
            throw new RuntimeException(e);
        }


    }

    private static void initKeyStore(){
        String keyStorePath = System.getenv("KEYSTORE_PATH");
        if (keyStorePath == null){
            keyStorePath = DEFAULT_KEYSTORE_PATH;
        }

        Path keystoreFile = Path.of(keyStorePath);
        if (Files.exists(keystoreFile)){
            try {
                KeyStoreLoader.initializeFromEnvironment();
                LOGGER.info("KeyStore is successfully loaded");
                return;
            } catch (Exception e){
                LOGGER.log(Level.SEVERE,"Error during KeyStore loading : ", e);
                System.exit(1);
            }
        }

        LOGGER.warning("KeyStore file not found " + keyStorePath);

        if (askUserConfirmation()){
            createNewKeyStore(keyStorePath);
        } else{
            LOGGER.info("Storage creation was declined. Exit...");
            System.exit(0);
        }

    }

    private static boolean askUserConfirmation() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Would you like to create a new storage now?" + " (y/N): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    private static void createNewKeyStore(String keyStorePath) {
        try {
            LOGGER.info("KeyStore creation master is running...");

            String[] setupArgs = {keyStorePath};

            KeyStoreSetup.main(setupArgs);

            LOGGER.info("KeyStore is successfully set");
            LOGGER.info("Please set environmental variables for bot");

            showEnvVar(keyStorePath);

            System.exit(0);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"Error during KeyStore creating  : ", e);
            System.exit(1);
        }
    }

    private static void showEnvVar(String keyStorePath){
        System.out.println("KEYSTORE_PATH=" + keyStorePath);
        System.out.println("KEYSTORE_PASSWORD=ваш_пароль_хранилища");
        System.out.println("KEY_ALIAS=ваш_алиас_ключа");
        System.out.println("KEY_PASSWORD=ваш_пароль_ключа");
        System.out.println("TELEGRAM_BOT_API_KEY=ваш_токен_бота");
        System.out.println("GEMINI_AI_API_KEY=ваш_токен_гемини");
    }


}
package com.github.hatkid.security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyStoreSetup {

    private static final Logger LOGGER = Logger.getLogger(KeyStoreSetup.class.getName());

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== KeyStore Setup Utility ===");
            System.out.println("Создание нового хранилища ключей.");

            System.out.print("Введите путь для файла хранилища (например: secure/keystore.jceks): ");
            String keyStorePath = scanner.nextLine().trim();

            System.out.print("Введите пароль для хранилища (будет использоваться для его открытия): ");
            char[] keyStorePassword = scanner.nextLine().toCharArray();

            System.out.print("Введите алиас для ключа (например: telegramBotKey): ");
            String keyAlias = scanner.nextLine().trim();

            System.out.print("Введите пароль для ключа (может совпадать с паролем хранилища): ");
            char[] keyPassword = scanner.nextLine().toCharArray();

            java.nio.file.Path path = java.nio.file.Path.of(keyStorePath);
            if (path.getParent() != null) {
                java.nio.file.Files.createDirectories(path.getParent());
            }

            System.out.println("Генерация AES-256 ключа...");
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom();
            keyGen.init(256, secureRandom);
            SecretKey secretKey = keyGen.generateKey();

            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(null, null); // Создаем пустое хранилище

            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(keyPassword);
            keyStore.setEntry(keyAlias, secretKeyEntry, protectionParam);

            try (FileOutputStream fos = new FileOutputStream(keyStorePath)) {
                keyStore.store(fos, keyStorePassword);
            }

            System.out.println("\nKeyStore успешно создан!");
            System.out.println("Файл: " + keyStorePath);
            System.out.println("Алиас ключа: " + keyAlias);
            System.out.println("Пароль хранилища: " + new String(keyStorePassword));
            System.out.println("Пароль ключа: " + new String(keyPassword));

            System.out.println("\n⚠️ ВАЖНЫЕ ИНСТРУКЦИИ:");
            System.out.println("1. Сохраните пароли в надежном месте (менеджер паролей)");
            System.out.println("2. Добавьте путь к файлу (" + keyStorePath + ") в .gitignore");
            System.out.println("3. Никогда не коммитьте файл хранилища в Git!");
            System.out.println("4. Для запуска бота установите переменные окружения:");
            System.out.println("   export KEYSTORE_PATH=\"" + keyStorePath + "\"");
            System.out.println("   export KEYSTORE_PASSWORD=\"" + new String(keyStorePassword) + "\"");
            System.out.println("   export KEY_ALIAS=\"" + keyAlias + "\"");
            System.out.println("   export KEY_PASSWORD=\"" + new String(keyPassword) + "\"");

        } catch (Exception e) {
            System.err.println("❌ Ошибка при создании KeyStore:");
            LOGGER.log(Level.SEVERE, "Exception : ",e);
            System.exit(1);
        }
    }
}
package com.github.hatkid.security;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.logging.Logger;

public class KeyStoreLoader {

    private static SecretKey secretKey;
    private static final Logger LOGGER = Logger.getLogger(KeyStoreLoader.class.getName());

    public static void initializeFromEnvironment() {
        String keyStorePath = System.getenv("KEYSTORE_PATH");
        String keyStorePassword = System.getenv("KEYSTORE_PASSWORD");
        String keyAlias = System.getenv("KEY_ALIAS");
        String keyPassword = System.getenv("KEY_PASSWORD");

        if (keyStorePath == null || keyStorePassword == null || keyAlias == null || keyPassword == null) {
            throw new IllegalStateException("Not all environmental variables are set: " +
                    "KEYSTORE_PATH, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD");
        }

        initialize(keyStorePath, keyStorePassword.toCharArray(), keyAlias, keyPassword.toCharArray());
    }

    public static void initialize(String keyStorePath, char[] keyStorePassword,
                                  String keyAlias, char[] keyPassword) {
        if (secretKey != null) {
            LOGGER.warning("KeyStoreLoader is already initialized");
            return;
        }

        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            try (FileInputStream fileInputStream = new FileInputStream(keyStorePath)) {
                keyStore.load(fileInputStream, keyStorePassword);
            }

            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyPassword);
            KeyStore.Entry entry = keyStore.getEntry(keyAlias, entryPassword);

            if (entry instanceof KeyStore.SecretKeyEntry) {
                secretKey = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
                LOGGER.info("Secret key is successfully loaded from KeyStore");
            } else {
                throw new KeyStoreException("Note with alias '" + keyAlias + "' is not a secret key");
            }

        } catch (Exception e) {
            throw new RuntimeException("Couldn't load secret key from KeyStore", e);
        }
    }

    public static SecretKey getSecretKey() {
        if (secretKey == null) {
            throw new IllegalStateException("KeyStoreLoader is not initialized. Call initialize() first.");
        }
        return secretKey;
    }

    public static boolean isInitialized() {
        return secretKey != null;
    }
}
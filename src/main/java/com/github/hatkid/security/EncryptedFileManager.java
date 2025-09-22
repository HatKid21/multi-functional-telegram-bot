package com.github.hatkid.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptedFileManager {

    private static final String directoryString = "users/";

    private static final Logger LOGGER = Logger.getLogger(EncryptedFileManager.class.getName());

    public static void writeToFile(String fileName,String stringToEncrypt, SecretKey secretKey){
        byte[] encryptedData = encrypt(stringToEncrypt,secretKey);
        String base64EncodedData = Base64.getEncoder().encodeToString(encryptedData);
        File directoryFile = new File(directoryString);
        if (!directoryFile.exists()){
            directoryFile.mkdirs();
        }
        File outputFile = new File(directoryString + fileName + ".txt");
        try (FileWriter writer = new FileWriter(outputFile)){
            writer.write(base64EncodedData);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static String  decryptFileContent(String fileName,SecretKey secretKey){
        File file = new File(directoryString + fileName + ".txt");
        if (!file.exists()){
            return "";
        }
        try {
            String base64EncodedData = new String(Files.readAllBytes(Path.of(directoryString + fileName + ".txt")));
            byte[] combinedData  = Base64.getDecoder().decode(base64EncodedData);
            int ivSize = 16;
            if (combinedData.length < ivSize){
                LOGGER.log(Level.SEVERE,"Not enough data for IV");
            }
            byte[] iv = new byte[ivSize];
            System.arraycopy(combinedData, 0, iv, 0, ivSize);
            byte[] encryptedData = new byte[combinedData.length - ivSize];
            System.arraycopy(combinedData, ivSize, encryptedData, 0, encryptedData.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            String algorithm = "AES/CBC/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(algorithm);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] originalDataBytes = cipher.doFinal(encryptedData);

            return new String(originalDataBytes, StandardCharsets.UTF_8);

        } catch (NoSuchPaddingException | IllegalBlockSizeException | IOException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static IvParameterSpec generateIv(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] encrypt(String string, SecretKey secretKey){
        byte[] dataToEncrypt = string.getBytes(StandardCharsets.UTF_8);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = generateIv();
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,ivSpec);
            byte[] encryptedData = cipher.doFinal(dataToEncrypt);
            byte[] combined = new byte[ivSpec.getIV().length + encryptedData.length];
            System.arraycopy(ivSpec.getIV(), 0, combined, 0, ivSpec.getIV().length);
            System.arraycopy(encryptedData, 0, combined, ivSpec.getIV().length, encryptedData.length);
            return combined;

        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Runtime:",e);
        } catch (NoSuchPaddingException e) {
            LOGGER.log(Level.SEVERE, "No such ", e);
        } catch (IllegalBlockSizeException e) {
            LOGGER.log(Level.SEVERE, "Illegal ",e);
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.log(Level.SEVERE, "Invalid",e);
        } catch (BadPaddingException e) {
            LOGGER.log(Level.SEVERE, "Bad", e);
        } catch (InvalidKeyException e) {
            LOGGER.log(Level.SEVERE,"InvalidKey ",e);
        }
        return null;
    }


}

package com.github.hatkid.ai;

import com.github.hatkid.functioncall.Function;
import com.github.hatkid.functioncall.FunctionCallManager;
import com.github.hatkid.utils.FileDownloader;
import com.github.hatkid.utils.MessageData;
import org.telegram.telegrambots.meta.api.objects.File;
import swiss.ameri.gemini.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeminiBot {

    private final ConfigurationManager configurationManager;
    //16384
    private static final long MAX_CONTEXT_WINDOW = 16384;
    private static final Logger LOGGER = Logger.getLogger(GeminiBot.class.getName());
    private static final int API_TIMEOUT_SECONDS = 60   ;
    private static final String RESPONSE_MIME = "text/plain";

    private final GenAi geminiAi;

    private final FunctionCallManager functionCallManager;

    public GeminiBot(GenAi geminiAi, ConfigurationManager configurationManager) {
        this.geminiAi = geminiAi;
        this.configurationManager = configurationManager;
        functionCallManager = new FunctionCallManager();
    }

    private void setSafetyConfig(GenerativeModel.GenerativeModelBuilder builder) {
        List<SafetySetting> safetySettings = new ArrayList<>();
        safetySettings.add(SafetySetting.of(SafetySetting.HarmCategory.HARM_CATEGORY_HATE_SPEECH, SafetySetting.HarmBlockThreshold.BLOCK_NONE));
        safetySettings.add(SafetySetting.of(SafetySetting.HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT, SafetySetting.HarmBlockThreshold.BLOCK_NONE));
        safetySettings.add(SafetySetting.of(SafetySetting.HarmCategory.HARM_CATEGORY_HARASSMENT, SafetySetting.HarmBlockThreshold.BLOCK_NONE));
        safetySettings.add(SafetySetting.of(SafetySetting.HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT, SafetySetting.HarmBlockThreshold.BLOCK_NONE));
        for (SafetySetting setting : safetySettings) {
            builder.addSafetySetting(setting);
        }
    }

    private void addTextContent(User user, String message) {
        user.addContent(new Content.TextContent(Content.Role.USER.roleName(), message));
    }

    private void addMediaContent(User user, String textMessage, List<File> files) {
        for (File file : files) {
            String base64File = FileDownloader.download(file);
            String mimeType = "";
            try {
                mimeType = Files.probeContentType(Path.of(file.getFilePath()));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }

            Content userMultiModalContent = Content.textAndMediaContentBuilder()
                    .addMedia(new Content.MediaData(mimeType, base64File))
                    .role(Content.Role.USER)
                    .text(textMessage)
                    .build();
            user.addContent(userMultiModalContent);
        }
    }

    private GenerativeModel correctContextWindow(GenerativeModel generativeModel,User user){
        while(geminiAi.countTokens(generativeModel).join() > MAX_CONTEXT_WINDOW){
            user.removeFistContext();
            GenerativeModel.GenerativeModelBuilder builder = GenerativeModel.builder();
            AiBotSettings botSettings = user.getBotSettings();
            builder.modelName(botSettings.getAiModel());
            setInstructions(builder, botSettings);
            setConfig(builder, botSettings);
            setSafetyConfig(builder);
            setUserContext(builder, user);
            setConfig(builder, botSettings);
            generativeModel = builder.build();
            LOGGER.info("Correcting Context window");
        }
        return generativeModel;
    }

    private void setConfig(GenerativeModel.GenerativeModelBuilder builder, AiBotSettings botSettings) {
        GenerationConfig generationConfig = GenerationConfig.builder()
                .maxOutputTokens(botSettings.getMaxOutputTokens())
                .responseMimeType(RESPONSE_MIME)
                .temperature(botSettings.getTemperature())
                .topK(botSettings.getTopK())
                .topP(botSettings.getTopP())
                .build();
        builder.generationConfig(generationConfig);
    }

    private void setTools(GenerativeModel.GenerativeModelBuilder builder){
        for (Map.Entry<String, Function> entry : functionCallManager.getFunctions().entrySet()){
            builder.addFunctionDeclaration(entry.getValue().getFunctionDeclaration());
        }
    }


    private GenerativeModel createGenerativeModel(User user) {
        GenerativeModel.GenerativeModelBuilder builder = GenerativeModel.builder();
        AiBotSettings botSettings = user.getBotSettings();
        builder.modelName(botSettings.getAiModel());
        setInstructions(builder, botSettings);
        setConfig(builder, botSettings);
        setSafetyConfig(builder);
        setUserContext(builder, user);
        setConfig(builder, botSettings);
        setTools(builder);
        return correctContextWindow(builder.build(),user);
    }


    private void setUserContext(GenerativeModel.GenerativeModelBuilder builder, User user) {
        for (Content content : user.getContentList()) {
            builder.addContent(content);
        }
    }

    private void setInstructions(GenerativeModel.GenerativeModelBuilder builder, AiBotSettings botSettings) {
        String instructionToUse;

        if (botSettings.getBotInstructions() == null) {
            instructionToUse = configurationManager.getDefaultInstruction();
        } else {
            instructionToUse = botSettings.getBotInstructions();
        }
        builder.addSystemInstruction(instructionToUse);
    }

    private String sendFileAndTextRequest(User user, String textMessage, List<File> files) {
        if (textMessage == null || textMessage.isEmpty()) {
            textMessage = ".";
        }
        addMediaContent(user, textMessage, files);
        GenerativeModel generativeModel = createGenerativeModel(user);
        return getResponse(user, generativeModel);
    }

    private String sendTextRequest(User user, String message) {
        addTextContent(user, message);
        GenerativeModel generativeModel = createGenerativeModel(user);
        GenAi.GeneratedContent generatedContent = getFunctionCallResponse(user,generativeModel);
        if (generatedContent != null && generatedContent.functionCall() == null){
            return generatedContent.text();
        }
        GenerativeModel generativeModel1 = createGenerativeModel(user);
        return getResponse(user, generativeModel1);
    }

    public String sendRequest(User user, MessageData messageData) {
        List<File> files = messageData.getFiles();
        String text = messageData.getTextData();
        if (files.isEmpty() && text.isEmpty()) {
            return "Ошибка в формате ввода";
        }
        if (files.isEmpty()) {
            return sendTextRequest(user, text);
        }
        return sendFileAndTextRequest(user, text, files);
    }

    private String getResponse(User user, GenerativeModel generativeModel) {
        CompletableFuture<GenAi.GeneratedContent> future = geminiAi.generateContent(generativeModel);
        GenAi.GeneratedContent response;
        try {
            response = future.get(API_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Thread were interrupted with Gemini API.", e);
            return "Произошла внутренняя ошибка при обработке запроса.";
        } catch (TimeoutException e) {
            LOGGER.log(Level.WARNING, "Time limit exceeded with Gemini API", e);
            return "Извините ИИ не ответил вовремя. Попробуйте позже.";
        } catch (ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Request error with Gemini API", e);
            return "Произошла ошибка при обращении к ИИ.";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error with Gemini API");
            return "Произошла непредвиденная ошибка";
        }
        user.addContent(new Content.TextContent(Content.Role.MODEL.roleName(), response.text()));
        return response.text();
    }

    private GenAi.GeneratedContent getFunctionCallResponse(User user, GenerativeModel generativeModel){
        CompletableFuture<GenAi.GeneratedContent> future = geminiAi.generateContent(generativeModel);
        GenAi.GeneratedContent response;
        try {
            response = future.get(API_TIMEOUT_SECONDS,TimeUnit.SECONDS);
        } catch (Exception e){
            LOGGER.log(Level.SEVERE, "ERROR",e);
            return null;
        }
        FunctionCall functionCall = response.functionCall();
        String functionResponse;
        if (functionCall == null){
            return response;
        }
        functionResponse = functionCallManager.runFunction(functionCall.name());
        Map<String, String> responses = new HashMap<>();
        responses.put(functionCall.name(),functionResponse);

        user.addContent(new Content.FunctionResponseContent(Content.Role.USER.roleName(),new FunctionResponse(functionCall.name(),responses)));
        return response;
    }

}

package com.github.hatkid.ai;

import swiss.ameri.gemini.api.ModelVariant;

public class AiBotSettings {

    private String botInstructions;
    private double temperature = 0.9;
    private int topK = 80;
    private double topP = 0.6;
    private int maxOutputTokens = 8192;
    private String aiModel = "gemini-2.5-flash";

    public AiBotSettings() {

    }

    public String getBotInstructions() {
        return botInstructions;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getTopK() {
        return topK;
    }

    public double getTopP() {
        return topP;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public String  getAiModel() {
        return "models/" +  aiModel;
    }

    public void setBotInstructions(String botInstructions) {
        this.botInstructions = botInstructions;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = "models/" + aiModel;
    }

    public void setAiModel(ModelVariant aiModel) {
        this.aiModel = aiModel.variant();
    }


    public void setMaxOutputTokens(int maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public void setTopP(double topP) {
        this.topP = topP;
    }
}

package com.github.hatkid.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.checkerframework.checker.units.qual.C;
import swiss.ameri.gemini.api.Content;

import java.io.IOException;

public class ContentTypeAdapter extends TypeAdapter<Content> {

    private final Gson gson;

    public ContentTypeAdapter(Gson gson){
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Content value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        JsonElement jsonElement;
        if (value instanceof Content.FunctionCallContent) {
            jsonElement = gson.toJsonTree(value, Content.FunctionCallContent.class);
        } else if (value instanceof Content.FunctionResponseContent) {
            jsonElement = gson.toJsonTree(value, Content.FunctionResponseContent.class);
        } else if (value instanceof Content.TextAndMediaContent) {
            jsonElement = gson.toJsonTree(value, Content.TextAndMediaContent.class);
        } else if (value instanceof Content.TextContent) {
            jsonElement = gson.toJsonTree(value, Content.TextContent.class);
        } else if (value instanceof Content.MediaContent) {
            jsonElement = gson.toJsonTree(value, Content.MediaContent.class);
        } else {
            throw new JsonParseException("Unknown Content type for serialization: " + value.getClass().getName());
        }
        gson.toJson(jsonElement, out);
    }

    @Override
    public Content read(JsonReader in){
        JsonElement jsonElement = JsonParser.parseReader(in);

        if (jsonElement.isJsonNull()) {
            return null;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("functionCall")) {
            return gson.fromJson(jsonObject, Content.FunctionCallContent.class);
        } else if (jsonObject.has("functionResponse")) {
            return gson.fromJson(jsonObject, Content.FunctionResponseContent.class);
        } else if (jsonObject.has("media") && jsonObject.get("media").isJsonArray()) {
            return gson.fromJson(jsonObject, Content.TextAndMediaContent.class);
        } else if (jsonObject.has("media")) {
            return gson.fromJson(jsonObject, Content.MediaContent.class);
        } else if (jsonObject.has("text")) {
            return gson.fromJson(jsonObject, Content.TextContent.class);
        }

        throw new JsonParseException("Could not determine Content type from JSON: " + jsonObject);
    }
}




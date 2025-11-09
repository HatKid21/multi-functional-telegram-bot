package com.github.hatkid.functioncall;

import swiss.ameri.gemini.api.FunctionDeclaration;
import swiss.ameri.gemini.api.Schema;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class WeatherFunction implements Function{

    private static final String URL = "https://wttr.in/%s?ATmFdq";

    private static final FunctionDeclaration functionDeclaration = new FunctionDeclaration("weather",
            "returns current user's date",
            Schema.builder()
                    .type(Schema.Type.OBJECT)
                    .properties(Map.of("location", Schema.builder()
                            .type(Schema.Type.STRING)
                            .description("Location in any of this formats:" +
                                    " city name\n" +
                                    " # any location (+ for spaces)\n" +
                                    "# Unicode name of any location in any language\n" +
                                    " # airport code (3 letters)\n" +
                                    "   # domain name\n" +
                                    " # area codes\n" +
                                    " # GPS coordinates")
                            .build()))
                    .build()
    );

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return functionDeclaration;
    }

    @Override
    public String run(Map<String,?> args) {
        String location = (String) args.get("location");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URL,location)))
                .GET()
                .build();
        String response;
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            response = httpResponse.body();
        } catch (IOException | InterruptedException e) {
            response = "Error in function call:(";
        }
        return response;
    }
}

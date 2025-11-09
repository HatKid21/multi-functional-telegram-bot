package com.github.hatkid.functioncall;

import swiss.ameri.gemini.api.FunctionCall;
import swiss.ameri.gemini.api.FunctionDeclaration;
import swiss.ameri.gemini.api.Schema;

import java.util.Map;

public class DateFunction implements Function{

    private static final FunctionDeclaration functionDeclaration = new FunctionDeclaration("date",
            "returns current user's date",
            Schema.builder().type(Schema.Type.STRING).build());

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return functionDeclaration;
    }

    @Override
    public String run(Map<String, ?> args) {
        return "You have been rickrolled!!";
    }
}

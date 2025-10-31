package com.github.hatkid.functioncall;

import swiss.ameri.gemini.api.FunctionDeclaration;
import swiss.ameri.gemini.api.Schema;

public class DateFunction implements Function{

    private final FunctionDeclaration functionDeclaration = new FunctionDeclaration("date",
            "returns current user's date",
            Schema.builder().type(Schema.Type.STRING).build());

    @Override
    public FunctionDeclaration getFunctionDeclaration() {
        return functionDeclaration;
    }


    //TODO make functions not only return strings
    @Override
    public String run() {
        return "You have been rickrolled!!";
    }
}

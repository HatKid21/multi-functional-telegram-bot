package com.github.hatkid.functioncall;

import swiss.ameri.gemini.api.FunctionDeclaration;

import java.util.Map;

public interface Function {

    FunctionDeclaration getFunctionDeclaration();

    String run(Map<String, ?> args);

}

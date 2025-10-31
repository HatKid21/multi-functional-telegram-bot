package com.github.hatkid.functioncall;

import swiss.ameri.gemini.api.FunctionDeclaration;

public interface Function {

    FunctionDeclaration getFunctionDeclaration();

    String run();

}

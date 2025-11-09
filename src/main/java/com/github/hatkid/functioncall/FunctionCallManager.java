package com.github.hatkid.functioncall;

import swiss.ameri.gemini.api.FunctionCall;

import java.util.HashMap;
import java.util.Map;

public class FunctionCallManager {

    private final Map<String,Function> FUNCTION_MAP;

    public FunctionCallManager(){
        FUNCTION_MAP = new HashMap<>();
        init();
    }

    private void init(){
        DateFunction dateFunction = new DateFunction();
        WeatherFunction weatherFunction = new WeatherFunction();
        FUNCTION_MAP.put(dateFunction.getFunctionDeclaration().name(),dateFunction);
        FUNCTION_MAP.put(weatherFunction.getFunctionDeclaration().name(),weatherFunction);
    }

    public Map<String, Function> getFunctions() {
        return FUNCTION_MAP;
    }

    public String runFunction(FunctionCall functionCall){
        String name = functionCall.name();
        if (FUNCTION_MAP.containsKey(name)){
            return FUNCTION_MAP.get(name).run(functionCall.args());
        } else{
            return "Function with that name is not exist";
        }
    }

}

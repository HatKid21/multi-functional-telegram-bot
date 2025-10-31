package com.github.hatkid.functioncall;

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
        FUNCTION_MAP.put(dateFunction.getFunctionDeclaration().name(),dateFunction);
    }

    public Map<String, Function> getFunctions() {
        return FUNCTION_MAP;
    }

    public String runFunction(String name){
        if (FUNCTION_MAP.containsKey(name)){
            return FUNCTION_MAP.get(name).run();
        } else{
            return "Function with that name is not exist";
        }
    }

}

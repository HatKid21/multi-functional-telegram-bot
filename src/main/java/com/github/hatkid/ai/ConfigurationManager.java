package com.github.hatkid.ai;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigurationManager {

    private Properties applicationProperties;

    public ConfigurationManager(){
        init();
    }

    private void init(){
        Properties properties = new Properties();
        String resourceFileName = "application.properties";

        try (InputStream input = ConfigurationManager.class.getClassLoader().getResourceAsStream(resourceFileName)){
            if (input == null){
                System.out.println("No such file");
                return;
            }
            try(InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)){
                properties.load(reader);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        this.applicationProperties = properties;

    }

    public String getDefaultInstruction(){
        return applicationProperties.getProperty("bot.default-instruction");
    }

}

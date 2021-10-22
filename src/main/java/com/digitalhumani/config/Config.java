package com.digitalhumani.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private Config() {}

    private static Properties props;

    private static void loadConfig() throws IOException{
        props = new Properties();
        InputStream ip = Config.class.getClassLoader().getResourceAsStream("raas.properties");
        props.load(ip);
    }

    public static String getAPIKey() throws IOException{
        if (props == null) {
            loadConfig();
        }
        return props.getProperty("API_KEY");
    }

    public static String getEnvironment() throws IOException{
        if (props == null) {
            loadConfig();
        }
        return props.getProperty("ENVIRONMENT");
    }

    public static String getEnterprise() throws IOException{
        if (props == null) {
            loadConfig();
        }
        return props.getProperty("ENTERPRISE_ID");
    }
}
package org.shadowsocks.config;

public  class ConfigFactory {
    public static Config getConfig(String path){
        return new JsonConfig(path);

    }
}
